package com.idoc.nostransfer.callback;

/**
 * 
 * <p>Title: 电商Transfer回调接口</p> 
 * 
 * <p>Copyright: Copyright (c) 2014</p> 
 * 
 * <p>Company: www.netease.com</p>
 * 
 * @author 	Barney Woo
 * @date 	2014-11-21
 * @version 1.0
 */
public interface EcomTransferCallback<T>
{
	/**
	 * 操作完成时回调
	 * 
	 * @param result 操作结果
	 */
	public void completed(String key, T result);
	
	/**
	 * 发生异常时回调
	 * 
	 * @param e 异常
	 */
	public void failed(String key, Exception e);
	
	/**
	 * 操作取消时回调
	 */
	public void canceled(String key);
}
