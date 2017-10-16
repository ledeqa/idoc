package com.idoc.nostransfer.exception;

/**
 * 
 * <p>Title: nos已经存在异常类</p> 
 * 
 * <p>Copyright: Copyright (c) 2014</p> 
 * 
 * <p>Company: www.netease.com</p>
 * 
 * @author 	Barney Woo
 * @date 	2014-11-21
 * @version 1.0
 */
public class EcomNosAlreadyExistException extends EcomNosException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2633317968753100620L;
	public EcomNosAlreadyExistException()
	{
		super();
	}
	public EcomNosAlreadyExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	public EcomNosAlreadyExistException(String message, Throwable cause)
	{
		super(message, cause);
	}
	public EcomNosAlreadyExistException(String message)
	{
		super(message);
	}
	public EcomNosAlreadyExistException(Throwable cause)
	{
		super(cause);
	}
}
