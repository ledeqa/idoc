package com.idoc.nostransfer;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Future;

import com.idoc.nostransfer.exception.EcomNosAlreadyExistException;
import com.idoc.nostransfer.exception.EcomNosException;
import com.netease.cloud.services.nos.Nos;
import com.netease.cloud.services.nos.model.DeleteObjectsResult;
import com.netease.cloud.services.nos.model.ObjectListing;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.services.nos.transfer.model.UploadResult;

/**
 * 
 * <p>Title: 电商TransferManager同步接口，可以在方法返回值Future上通过get传递阻塞超时时间</p> 
 * <p>当待处理请求超过线程池大小时，方法调用直接抛出EcomNosExceedConcurrencyException异常</p>
 * 
 * <p>Copyright: Copyright (c) 2014</p> 
 * 
 * <p>Company: www.netease.com</p>
 * 
 * @author 	Barney Woo
 * @date 	2014-11-21
 * @version 1.0
 */
public interface EcomTransferManager
{
	/**
	 * 上传InputStream，size必传
	 * 
	 * @param key 必须
	 * @param input 必须
	 * @param size 必须
	 * @param objectMetadata 不必须
	 * @return
	 * @throws EcomNosException
	 */
	public Future<UploadResult> upload(String key, InputStream input, long size, ObjectMetadata objectMetadata) throws EcomNosException;

	/**
	 * 上传File
	 * 
	 * @param key 必须
	 * @param file 必须
	 * @param objectMetadata 不必须
	 * @return
	 * @throws EcomNosException
	 */
	public Future<UploadResult> upload(String key, File file, ObjectMetadata objectMetadata) throws EcomNosException;
	
	/**
	 * 当文件不存在时上传InputStream，size必传，内部实现只是组合doesObjectExist和upload方法
	 * 当key已经存在时抛出EcomNosAlreadyExistException异常
	 * 
	 * @param key 必须
	 * @param input 必须
	 * @param size 必须
	 * @param objectMetadata 不必须
	 * @return
	 * @throws EcomNosException
	 */
	public Future<UploadResult> uploadIfNotExist(String key, InputStream input, long size, ObjectMetadata objectMetadata) throws EcomNosAlreadyExistException, EcomNosException;

	/**
	 * 当文件不存在时上传File，内部实现只是组合doesObjectExist和upload方法
	 * 当key已经存在时抛出EcomNosAlreadyExistException异常
	 * 
	 * @param key 必须
	 * @param file 必须
	 * @param objectMetadata 不必须
	 * @return
	 * @throws EcomNosException
	 */
	public Future<UploadResult> uploadIfNotExist(String key, File file, ObjectMetadata objectMetadata) throws EcomNosAlreadyExistException, EcomNosException;
	
	/**
	 * 下载文件
	 * 
	 * @param key 必须
	 * @param file 必须
	 * @return
	 * @throws EcomNosException
	 */
	public Future<Boolean> download(String key, File file) throws EcomNosException;
	
	/**
	 * 删除文件
	 * 
	 * @param key 必须
	 * @return
	 * @throws EcomNosException
	 */
	public Future<?> delete(String key) throws EcomNosException;

	/**
	 * 批量删除文件，注意参数数组不要过大，否则容易操作超时
	 * 
	 * @param keys 必须
	 * @return
	 * @throws EcomNosException
	 */
	public Future<DeleteObjectsResult> delete(String[] keys) throws EcomNosException;

	/**
	 * 列举所有文件，该调用为分页查询，每页100条，当marker为null时查询第一页，当marker赋值为上次查询结果的ObjectLising.nextMarker()时则查询下一页
	 * 
	 * @param marker 非必须
	 * @return
	 * @throws EcomNosException
	 */
	public Future<ObjectListing> listObjects(String marker) throws EcomNosException;

	/**
	 *  按照指定前缀列举文件，该调用为分页查询，每页100条，当marker为null时查询第一页，当marker赋值为上次查询结果的ObjectLising.nextMarker()时则查询下一页
	 *  
	 * @param prefix 必须
	 * @param marker 非必须
	 * @return
	 * @throws EcomNosException
	 */
	public Future<ObjectListing> listObjects(String prefix, String marker) throws EcomNosException;
	
	/**
	 * 查询文件是否存在
	 * 
	 * @param key 必须
	 * @return
	 * @throws EcomNosException
	 */
	public Future<Boolean> doesObjectExist(String key) throws EcomNosException;
	
	/**
	 * 查询文件元属性
	 * 
	 * @param key 必须
	 * @return
	 * @throws EcomNosException
	 */
	public Future<ObjectMetadata> getObjectMetaData(String key) throws EcomNosException;
	
	/**
	 * 获取底层NosClient
	 * 
	 * @return
	 */
	public Nos getNos();
}
