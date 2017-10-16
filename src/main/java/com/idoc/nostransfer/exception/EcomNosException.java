package com.idoc.nostransfer.exception;

/**
 * 
 * <p>Title: nos异常基类</p> 
 * 
 * <p>Copyright: Copyright (c) 2014</p> 
 * 
 * <p>Company: www.netease.com</p>
 * 
 * @author 	Barney Woo
 * @date 	2014-11-21
 * @version 1.0
 */
public class EcomNosException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3634402876302211763L;

	public EcomNosException()
	{
		super();
	}
	public EcomNosException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	public EcomNosException(String message, Throwable cause)
	{
		super(message, cause);
	}
	public EcomNosException(String message)
	{
		super(message);
	}
	public EcomNosException(Throwable cause)
	{
		super(cause);
	}
}
