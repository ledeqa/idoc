package com.idoc.nostransfer.exception;

/**
 * 
 * <p>Title: nos超时异常类</p> 
 * 
 * <p>Copyright: Copyright (c) 2014</p> 
 * 
 * <p>Company: www.netease.com</p>
 * 
 * @author 	Barney Woo
 * @date 	2014-11-21
 * @version 1.0
 */
public class EcomNosTimeoutException extends EcomNosException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6089377683912747298L;

	public EcomNosTimeoutException()
	{
		super();
	}
	public EcomNosTimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	public EcomNosTimeoutException(String message, Throwable cause)
	{
		super(message, cause);
	}
	public EcomNosTimeoutException(String message)
	{
		super(message);
	}
	public EcomNosTimeoutException(Throwable cause)
	{
		super(cause);
	}
}
