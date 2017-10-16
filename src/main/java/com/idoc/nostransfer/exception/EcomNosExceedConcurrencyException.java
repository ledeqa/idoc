package com.idoc.nostransfer.exception;

/**
 * 
 * <p>Title: nos超出并发限制异常类</p> 
 * 
 * <p>Copyright: Copyright (c) 2014</p> 
 * 
 * <p>Company: www.netease.com</p>
 * 
 * @author 	Barney Woo
 * @date 	2014-11-21
 * @version 1.0
 */
public class EcomNosExceedConcurrencyException extends EcomNosException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2349334481460654236L;
	public EcomNosExceedConcurrencyException()
	{
		super();
	}
	public EcomNosExceedConcurrencyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	public EcomNosExceedConcurrencyException(String message, Throwable cause)
	{
		super(message, cause);
	}
	public EcomNosExceedConcurrencyException(String message)
	{
		super(message);
	}
	public EcomNosExceedConcurrencyException(Throwable cause)
	{
		super(cause);
	}
}
