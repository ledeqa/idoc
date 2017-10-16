package com.idoc.nostransfer.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import com.idoc.constant.LogConstant;
import com.idoc.nostransfer.AsyncEcomTransferManager;
import com.idoc.nostransfer.callback.EcomTransferCallback;
import com.idoc.nostransfer.exception.EcomNosException;
import com.idoc.nostransfer.exception.EcomNosTimeoutException;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.services.nos.transfer.model.UploadResult;

/**
 * 
 * <p>Title: 电商TransferManager异步接口实现类</p> 
 * 
 * <p>Copyright: Copyright (c) 2014</p> 
 * 
 * <p>Company: www.netease.com</p>
 * 
 * @author 	Barney Woo
 * @date 	2014-11-21
 * @version 1.0
 */
public class AsyncEcomTransferManagerImpl extends EcomTransferManagerImpl implements AsyncEcomTransferManager
{
	public AsyncEcomTransferManagerImpl()
	{
		new Thread("EcomFutureChecker"){
			@Override
			public void run()
			{
				LogConstant.debugLog.info(getLogPrefix() + "EcomFutureChecker run...");
				
				while (true)
				{
					try
					{
						Thread.sleep(10);
						check();
					}
					catch (Throwable e)
					{
						LogConstant.debugLog.error("check exception", e);
					}
				}
			}
		}.start();
	}

	@Override
	public void uploadAsync(String key, File file, ObjectMetadata objectMetadata, Integer timeout, EcomTransferCallback<UploadResult> callback) throws EcomNosException
	{
		LogConstant.debugLog.info(this.getLogPrefix() + "upload async : " + key + ", " + file.getAbsolutePath() + ", " + timeout);
		
		this.futureList.add(new FutureWrapper<UploadResult>(this.upload(key, file, objectMetadata), timeout, callback, key));
	}
	
	@Override
	public void uploadAsync(String key, InputStream input, long size, ObjectMetadata objectMetadata, Integer timeout, EcomTransferCallback<UploadResult> callback) throws EcomNosException
	{
		LogConstant.debugLog.info(this.getLogPrefix() + "upload async : " + key + ", " + size + ", " + timeout);
		
		this.futureList.add(new FutureWrapper<UploadResult>(this.upload(key, input, size, objectMetadata), timeout, callback, key));
	}

	@Override
	public void downloadAsync(String key, File file, Integer timeout, EcomTransferCallback<Boolean> callback) throws EcomNosException
	{
		LogConstant.debugLog.info(this.getLogPrefix() + "download async : " + key + ", " + file.getAbsolutePath() + ", " + timeout);
		
		this.futureList.add(new FutureWrapper<Boolean>(this.download(key, file), timeout, callback, key));
	}

	@Override
	public void uploadIfNotExistAsync(String key, File file, ObjectMetadata objectMetadata, Integer timeout, EcomTransferCallback<UploadResult> callback) throws EcomNosException
	{
		LogConstant.debugLog.info(this.getLogPrefix() + "upload if not exist async : " + key + ", " + file.getAbsolutePath() + ", " + timeout);
		
		this.futureList.add(new FutureWrapper<UploadResult>(this.uploadIfNotExist(key, file, objectMetadata), timeout, callback, key));
	}

	@Override
	public void uploadIfNotExistAsync(String key, InputStream input, long size, ObjectMetadata objectMetadata, Integer timeout, EcomTransferCallback<UploadResult> callback) throws EcomNosException
	{
		LogConstant.debugLog.info(this.getLogPrefix() + "upload if not exist async : " + key + ", " + size + ", " + timeout);
		
		this.futureList.add(new FutureWrapper<UploadResult>(this.uploadIfNotExist(key, input, size, objectMetadata), timeout, callback, key));
	}

	//------------------------------------------------------------------------------------
	//implementation
	//------------------------------------------------------------------------------------
	/**
	 * Future集合，定时检查是否完成并通知回调
	 */
	@SuppressWarnings("rawtypes")
	List<FutureWrapper> futureList = new CopyOnWriteArrayList<FutureWrapper>();

	@SuppressWarnings("rawtypes")
	protected synchronized void check()
	{
//		NosLogFactory.info(this.getLogPrefix() + "check...");
		
		if (!this.futureList.isEmpty())
		{
			Iterator<FutureWrapper> ite = this.futureList.iterator();
			while (ite.hasNext())
			{
				final FutureWrapper wrapper = ite.next();
				boolean isRemoved = false;
				if (wrapper.future.isDone())
				{
					LogConstant.debugLog.info(this.getLogPrefix() + "future is done : " + wrapper.signature);
					
					isRemoved = true;
					if (wrapper.callback != null)
					{
						this.addTask(new Runnable(){
							@SuppressWarnings("unchecked")
							@Override
							public void run()
							{
								try
								{
									wrapper.callback.completed(wrapper.signature, wrapper.future.get());
								}
								catch (Exception e)
								{
									LogConstant.debugLog.error("callback exception", e);
								}
							}
						});
					}
				}
				else if (wrapper.future.isCancelled())
				{
					LogConstant.debugLog.info(this.getLogPrefix() + "future is canceled : " + wrapper.signature);
					
					isRemoved = true;
					if (wrapper.callback != null)
					{
						this.addTask(new Runnable(){
							@Override
							public void run()
							{
								try
								{
									wrapper.callback.canceled(wrapper.signature);
								}
								catch (Exception e)
								{
									LogConstant.debugLog.error("callback exception", e);
								}
							}
						});
					}
				}
				else if (System.currentTimeMillis() - wrapper.startTime > wrapper.timeout)
				{
					isRemoved = true;
					boolean isCanceled = wrapper.future.cancel(true);
					//may be is done
					if (!isCanceled && wrapper.future.isDone())
					{
						LogConstant.debugLog.info(this.getLogPrefix() + "future is done : " + wrapper.signature);
						
						if (wrapper.callback != null)
						{
							this.addTask(new Runnable(){
								@SuppressWarnings("unchecked")
								@Override
								public void run()
								{
									try
									{
										wrapper.callback.completed(wrapper.signature, wrapper.future.get());
									}
									catch (Exception e)
									{
										LogConstant.debugLog.error("callback exception", e);
									}
								}
							});
						}
					}
					else
					{
						LogConstant.debugLog.info(this.getLogPrefix() + "future is timeout : " + wrapper.signature + ", " + wrapper.timeout);
						
						if (wrapper.callback != null)
						{
							this.addTask(new Runnable(){
								@Override
								public void run()
								{
									try
									{
										wrapper.callback.failed(wrapper.signature, new EcomNosTimeoutException());
									}
									catch (Exception e)
									{
										LogConstant.debugLog.error("callback exception", e);
									}
								}
							});
						}
					}
				}
				if (isRemoved)
				{
					this.futureList.remove(wrapper);
				}
			}
		}
	}

	private final static int retryMax = 3;
	private void addTask(Runnable runnable)
	{
		int retry = 0;
		//handle rejection
		boolean isRejected = false;
		do
		{
			try
			{
				this.executor.execute(runnable);
				isRejected = false;
			}
			catch (RejectedExecutionException e)
			{
				isRejected = true;
				retry++;
				//sleep a while
				try
				{
					Thread.sleep(10);
				}
				catch (InterruptedException e1)
				{
				}
			}
		}
		while (isRejected && retry < retryMax);
		
		//thread pool is full, do callback in new thread
		if (isRejected && retry >= retryMax)
		{
			LogConstant.debugLog.info("thread pool is full, make new thread");
			
			new Thread(runnable).start();
		}
	}
	
	/**
	 * 
	 * Future包装类
	 * 
	 * @author bjwwwu
	 *
	 * @param <T>
	 */
	class FutureWrapper<T>
	{
		Future<T> future = null;
		Integer timeout = null;
		EcomTransferCallback<T> callback = null;
		Long startTime = null;
		String signature = null;
		FutureWrapper(Future<T> future, Integer timeout, EcomTransferCallback<T> callback, String signature)
		{
			this.future = future;
			this.timeout = timeout;
			this.callback = callback;
			this.startTime = System.currentTimeMillis();
			this.signature = signature;
		}
	}
}
