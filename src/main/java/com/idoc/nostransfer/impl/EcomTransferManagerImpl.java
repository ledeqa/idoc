package com.idoc.nostransfer.impl;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.idoc.constant.LogConstant;
import com.idoc.nostransfer.EcomTransferManager;
import com.idoc.nostransfer.exception.EcomNosAlreadyExistException;
import com.idoc.nostransfer.exception.EcomNosException;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.services.nos.Nos;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.DeleteObjectRequest;
import com.netease.cloud.services.nos.model.DeleteObjectsRequest;
import com.netease.cloud.services.nos.model.DeleteObjectsRequest.KeyVersion;
import com.netease.cloud.services.nos.model.DeleteObjectsResult;
import com.netease.cloud.services.nos.model.ListObjectsRequest;
import com.netease.cloud.services.nos.model.ObjectListing;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.cloud.services.nos.transfer.TransferManager;
import com.netease.cloud.services.nos.transfer.TransferManagerConfiguration;
import com.netease.cloud.services.nos.transfer.internal.DownloadImpl;
import com.netease.cloud.services.nos.transfer.model.UploadResult;

/**
 * 
 * <p>Title: 电商TransferManager同步接口实现类</p> 
 * 
 * <p>Copyright: Copyright (c) 2014</p> 
 * 
 * <p>Company: www.netease.com</p>
 * 
 * @author 	Barney Woo
 * @date 	2014-11-21
 * @version 1.0
 */
public class EcomTransferManagerImpl implements EcomTransferManager
{
	/**
	 * nos configuration
	 */
	private String accessKey = "dc33d5c021dd4ce0b96b89207be51f07";
	private String secretKey = "459c9bc98cec45e98a577bd9de6b9b50";
	//The minimum part size for upload parts. default is 5M
	private Integer minimumUploadPartSize = 5242880;
	//The size threshold, in bytes, for when to use multipart uploads. Uploads over this size will automatically use a multipart upload strategy, while uploads smaller than this threshold will use a single connection to upload the whole object. default is 16M
	private Integer multipartUploadThreshold = 16777216;
	//There are three bucketName: 1)ecomtest; 2)ecomprivate; 3)ecom; "ecomtest" reserved for test data, "ecomprivate" reserved for private online data, "ecom" reserved for public online data which can be accessed by http://ecom.nos.netease.com/${key}
	private String bucketName = "ecom";
	/**
	 * 项目标识，必须
	 */
	private String groupName = null;
	/**
	 * 线程池配置
	 */
	private Integer corePoolSize = 20;
	private Integer maxPoolSize = 20;
	private Integer keepAliveSeconds = 300;
	private Integer queueCapacity = 50;
	
	private String loggerClass = null;
	
	protected TransferManager transferManager = null;
	protected ThreadPoolExecutor executor = null;

	/**
	 * initialize
	 * 
	 * @throws EcomNosException
	 */
	public void init() throws EcomNosException
	{
		LogConstant.debugLog.info(this.getLogPrefix() + "init : " + this.accessKey + "|" + this.secretKey + "|" 
				+ this.minimumUploadPartSize + "|" + this.multipartUploadThreshold+ "|" 
				+ this.bucketName + "|" + this.groupName + "|" 
				+ this.corePoolSize + "|" + this.maxPoolSize + "|" + this.keepAliveSeconds + "|" + this.queueCapacity + "|" 
				+ this.loggerClass);
		
		if (this.groupName == null || "".equals(this.groupName.trim()))
		{
			throw new EcomNosException(this.getLogPrefix() + "groupName can not be empty");
		}
		if (this.bucketName == null || "".equals(this.bucketName.trim()))
		{
			throw new EcomNosException(this.getLogPrefix() + "bucketName can not be empty");
		}
		if (this.minimumUploadPartSize == null || this.minimumUploadPartSize <= 0 || this.multipartUploadThreshold == null || this.multipartUploadThreshold <= 0)
		{
			throw new EcomNosException(this.getLogPrefix() + "minimumUploadPartSize or multipartUploadThreshold is not valid");
		}
		if (this.accessKey == null || "".equals(this.accessKey.trim()) || this.secretKey == null || "".equals(this.secretKey.trim()))
		{
			throw new EcomNosException(this.getLogPrefix() + "accessKey or secretKey is not valid");
		}
		if (this.corePoolSize == null || this.corePoolSize <= 0 || this.maxPoolSize == null || this.maxPoolSize <= 0 
				|| this.keepAliveSeconds == null || this.keepAliveSeconds <= 0 || this.queueCapacity == null || this.queueCapacity <= 0)
		{
			throw new EcomNosException(this.getLogPrefix() + "corePoolSize or corePoolSize or keepAliveSeconds or queueCapacity is not valid");
		}
		
		this.transferManager = new TransferManager(new NosClient(new BasicCredentials(this.accessKey, this.secretKey)));
		
		TransferManagerConfiguration ecomConfiguration = new TransferManagerConfiguration();
		ecomConfiguration.setMinimumUploadPartSize(this.minimumUploadPartSize);
		ecomConfiguration.setMultipartUploadThreshold(this.multipartUploadThreshold);
		this.transferManager.setConfiguration(ecomConfiguration);
		
		this.executor = new ThreadPoolExecutor(this.corePoolSize, this.maxPoolSize, this.keepAliveSeconds, TimeUnit.SECONDS, 
				new LinkedBlockingQueue<Runnable>(this.queueCapacity), 
				new ThreadFactory()
				{
					private int threadCount = 1;
					public Thread newThread(Runnable r) 
					{
						return new Thread(r, "ecom-nos-worker-" + threadCount++);
					}
				}, 
				new ThreadPoolExecutor.AbortPolicy());
	}
	
	@Override
	public Future<UploadResult> upload(final String key, final InputStream input, final long size, final ObjectMetadata objectMetadata) throws EcomNosException
	{
		LogConstant.debugLog.info(this.getLogPrefix() + "upload : " + key + ", " + size);
		
		this.validateNull(key, "key");
		this.validateNull(input, "input");
		if (size <= 0)
		{
			throw new EcomNosException("size must be positive");
		}
		
		return this.executor.submit(new Callable<UploadResult>(){
			@Override
			public UploadResult call() throws Exception
			{
				ObjectMetadata objectMetadataNew = null;
				if (objectMetadata == null)
				{
					objectMetadataNew = new ObjectMetadata();
				}
				else
				{
					objectMetadataNew = objectMetadata;
				}
				objectMetadataNew.setContentLength(size);
				return transferManager.upload(new PutObjectRequest(bucketName, getKey(key), input, objectMetadataNew)).waitForUploadResult();
			}
		});
	}
	
	@Override
	public Future<UploadResult> upload(final String key, final File file, final ObjectMetadata objectMetadata) throws EcomNosException
	{
		LogConstant.debugLog.info(this.getLogPrefix() + "upload : " + key + ", " + file.getAbsolutePath());

		this.validateNull(key, "key");
		this.validateNull(file, "file");
		
		return this.executor.submit(new Callable<UploadResult>(){
			@Override
			public UploadResult call() throws Exception
			{
				PutObjectRequest request = new PutObjectRequest(bucketName, getKey(key), file);
				request.setMetadata(objectMetadata);
				return transferManager.upload(request).waitForUploadResult();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public Future<Boolean> download(final String key, final File file) throws EcomNosException
	{
		LogConstant.debugLog.info(this.getLogPrefix() + "download : " + key + ", " + file.getAbsolutePath());
		
		this.validateNull(key, "key");
		this.validateNull(file, "file");
		
		DownloadImpl downloadImpl = (DownloadImpl)this.transferManager.download(this.bucketName, this.getKey(key), file);
		return (Future<Boolean>)downloadImpl.getMonitor().getFuture();
	}

	@Override
	public Future<?> delete(final String key) throws EcomNosException
	{
		LogConstant.debugLog.info(this.getLogPrefix() + "delete : " + key);
		
		this.validateNull(key, "key");
		
		return this.executor.submit(new Runnable(){
			@Override
			public void run()
			{
				transferManager.getNosClient().deleteObject(new DeleteObjectRequest(bucketName, getKey(key)));
			}});
	}
	
	@Override
	public Future<DeleteObjectsResult> delete(final String[] key) throws EcomNosException
	{
		LogConstant.debugLog.info(this.getLogPrefix() + "delete : " + Arrays.toString(key));
		
		this.validateNull(key, "keys");
		
		return this.executor.submit(new Callable<DeleteObjectsResult>(){
			@Override
			public DeleteObjectsResult call()
			{
				DeleteObjectsRequest  deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
				List<KeyVersion> keys = new ArrayList<KeyVersion>();
				for (String oneKey : key)
				{
					keys.add(new KeyVersion(getKey(oneKey)));
				}
				deleteObjectsRequest.setKeys(keys);
				return transferManager.getNosClient().deleteObjects(deleteObjectsRequest);
			}});
	}

	@Override
	public Future<ObjectListing> listObjects(final String marker) throws EcomNosException
	{
		LogConstant.debugLog.info(this.getLogPrefix() + "listObjects : " + marker);
		
		return this.executor.submit(new Callable<ObjectListing>(){
			@Override
			public ObjectListing call() throws Exception
			{
				ListObjectsRequest request = new ListObjectsRequest();
				request.setMarker(marker);
				request.setBucketName(bucketName);
				request.setPrefix(groupName);
				return transferManager.getNosClient().listObjects(request);
			}
		});
	}
	
	@Override
	public Future<ObjectListing> listObjects(final String prefix, final String marker) throws EcomNosException
	{
		LogConstant.debugLog.info(this.getLogPrefix() + "listObjects : " + prefix + ", " + marker);
		
		this.validateNull(prefix, "prefix");
		
		return this.executor.submit(new Callable<ObjectListing>(){
			@Override
			public ObjectListing call() throws Exception
			{
				ListObjectsRequest request = new ListObjectsRequest();
				request.setMarker(marker);
				request.setBucketName(bucketName);
				request.setPrefix(getKey(prefix));
				return transferManager.getNosClient().listObjects(request);
			}
		});
	}

	@Override
	public Future<Boolean> doesObjectExist(final String key) throws EcomNosException
	{
		LogConstant.debugLog.info(this.getLogPrefix() + "doesObjectExist : " + key);

		this.validateNull(key, "key");
		
		return this.executor.submit(new Callable<Boolean>(){
			@Override
			public Boolean call() throws Exception
			{
				return transferManager.getNosClient().doesObjectExist(bucketName, getKey(key), null);
			}
		});
	}

	@Override
	public Future<ObjectMetadata> getObjectMetaData(final String key) throws EcomNosException
	{
		LogConstant.debugLog.info(this.getLogPrefix() + "getObjectMetaData : " + key);

		this.validateNull(key, "key");
		
		return this.executor.submit(new Callable<ObjectMetadata>(){
			@Override
			public ObjectMetadata call() throws Exception
			{
				return transferManager.getNosClient().getObjectMetadata(bucketName, getKey(key));
			}
		});
	}

	@Override
	public Nos getNos()
	{
		return this.transferManager.getNosClient();
	}

	@Override
	public Future<UploadResult> uploadIfNotExist(String key, InputStream input, long size, ObjectMetadata objectMetadata) throws EcomNosException
	{
		try
		{
			if (this.doesObjectExist(key).get())
			{
				throw new EcomNosAlreadyExistException();
			}
		}
		catch (EcomNosAlreadyExistException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new EcomNosException(e);
		}
		return this.upload(key, input, size, objectMetadata);
	}

	@Override
	public Future<UploadResult> uploadIfNotExist(String key, File file, ObjectMetadata objectMetadata) throws EcomNosException
	{
		try
		{
			if (this.doesObjectExist(key).get())
			{
				throw new EcomNosAlreadyExistException();
			}
		}
		catch (EcomNosAlreadyExistException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new EcomNosException(e);
		}
		return this.upload(key, file, objectMetadata);
	}
	
	//--------------------------------------------------------------------------
	protected String getKey(String key)
	{
		return this.groupName + "/" + key;
	}
	protected void validateNull(Object obj, String name) throws EcomNosException
	{
		if (obj == null)
		{
			throw new EcomNosException("parameter can not be null : " + name);
		}
	}
	protected String getLogPrefix()
	{
		return "[" + this.getClass().getSimpleName() + "]";
	}
	
	public String getAccessKey()
	{
		return accessKey;
	}
	public EcomTransferManagerImpl setAccessKey(String accessKey)
	{
		this.accessKey = accessKey;
		return this;
	}
	public String getSecretKey()
	{
		return secretKey;
	}
	public EcomTransferManagerImpl setSecretKey(String secretKey)
	{
		this.secretKey = secretKey;
		return this;
	}
	public Integer getMinimumUploadPartSize()
	{
		return minimumUploadPartSize;
	}
	public EcomTransferManagerImpl setMinimumUploadPartSize(Integer minimumUploadPartSize)
	{
		this.minimumUploadPartSize = minimumUploadPartSize;
		return this;
	}
	public Integer getMultipartUploadThreshold()
	{
		return multipartUploadThreshold;
	}
	public EcomTransferManagerImpl setMultipartUploadThreshold(Integer multipartUploadThreshold)
	{
		this.multipartUploadThreshold = multipartUploadThreshold;
		return this;
	}
	public String getBucketName()
	{
		return bucketName;
	}
	public EcomTransferManagerImpl setBucketName(String bucketName)
	{
		this.bucketName = bucketName;
		return this;
	}
	public String getGroupName()
	{
		return groupName;
	}
	public EcomTransferManagerImpl setGroupName(String groupName)
	{
		this.groupName = groupName;
		return this;
	}
	public Integer getCorePoolSize()
	{
		return corePoolSize;
	}
	public EcomTransferManagerImpl setCorePoolSize(Integer corePoolSize)
	{
		this.corePoolSize = corePoolSize;
		return this;
	}
	public Integer getMaxPoolSize()
	{
		return maxPoolSize;
	}
	public EcomTransferManagerImpl setMaxPoolSize(Integer maxPoolSize)
	{
		this.maxPoolSize = maxPoolSize;
		return this;
	}
	public Integer getKeepAliveSeconds()
	{
		return keepAliveSeconds;
	}
	public EcomTransferManagerImpl setKeepAliveSeconds(Integer keepAliveSeconds)
	{
		this.keepAliveSeconds = keepAliveSeconds;
		return this;
	}
	public Integer getQueueCapacity()
	{
		return queueCapacity;
	}
	public EcomTransferManagerImpl setQueueCapacity(Integer queueCapacity)
	{
		this.queueCapacity = queueCapacity;
		return this;
	}
	public String getLoggerClass()
	{
		return loggerClass;
	}
	public void setLoggerClass(String loggerClass)
	{
		this.loggerClass = loggerClass;
	}
}
