package com.idoc.nostransfer;

import java.io.File;
import java.io.InputStream;

import com.idoc.nostransfer.callback.EcomTransferCallback;
import com.idoc.nostransfer.exception.EcomNosException;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.services.nos.transfer.model.UploadResult;

/**
 * 
 * <p>Title: 电商TransferManager异步接口，由于只有上传和下载时间与文件大小有关，所以这里只提供上传和下载的异步操作</p> 
 * <p>当待处理请求超过线程池大小时，方法调用直接抛出EcomNosExceedConcurrencyException异常；</p>
 * <p>当超过指定的超时时间操作仍然没有完成通过回调接口调用failed方法，传递EcomNosTimeoutException异常；</p>
 * 
 * <p>Copyright: Copyright (c) 2014</p> 
 * 
 * <p>Company: www.netease.com</p>
 * 
 * @author 	Barney Woo
 * @date 	2014-11-21
 * @version 1.0
 */
public interface AsyncEcomTransferManager extends EcomTransferManager
{
	/**
	 * 异步上传File，相比父接口增加超时时间和回调参数
	 * 
	 * @param key 必须
	 * @param file 必须
	 * @param objectMetadata 不必须
	 * @param timeout 不必须，单位ms
	 * @param callback 不必须
	 */
	public void uploadAsync(String key, File file, ObjectMetadata objectMetadata, Integer timeout, EcomTransferCallback<UploadResult> callback) throws EcomNosException;
	
	/**
	 * 异步上传InputStream，size必传，相比父接口增加超时时间和回调参数
	 * 
	 * @param key 必须
	 * @param input 必须
	 * @param size 必须
	 * @param objectMetadata 不必须
	 * @param timeout 不必须，单位ms
	 * @param callback 不必须
	 * @throws EcomNosException 
	 */
	public void uploadAsync(String key, InputStream input, long size, ObjectMetadata objectMetadata, Integer timeout, EcomTransferCallback<UploadResult> callback) throws EcomNosException;
	
	/**
	 * 当文件不存在时异步上传File，相比父接口增加超时时间和回调参数
	 * 
	 * @param key 必须
	 * @param file 必须
	 * @param objectMetadata 不必须
	 * @param timeout 不必须，单位ms
	 * @param callback 不必须
	 */
	public void uploadIfNotExistAsync(String key, File file, ObjectMetadata objectMetadata, Integer timeout, EcomTransferCallback<UploadResult> callback) throws EcomNosException;
	
	/**
	 * 当文件不存在时异步上传InputStream，size必传，相比父接口增加超时时间和回调参数
	 * 
	 * @param key 必须
	 * @param input 必须
	 * @param size 必须
	 * @param objectMetadata 不必须
	 * @param timeout 不必须，单位ms
	 * @param callback 不必须
	 * @throws EcomNosException 
	 */
	public void uploadIfNotExistAsync(String key, InputStream input, long size, ObjectMetadata objectMetadata, Integer timeout, EcomTransferCallback<UploadResult> callback) throws EcomNosException;
	
	/**
	 * 异步下载文件，相比父接口增加超时时间和回调参数
	 * 
	 * @param key 必须
	 * @param file 必须
	 * @param timeout 不必须，单位ms
	 * @param callback 不必须
	 * @return
	 * @throws EcomNosException
	 */
	public void downloadAsync(String key, File file, Integer timeout, EcomTransferCallback<Boolean> callback) throws EcomNosException;

}
