package com.idoc.nostransfer.interceptor;

import java.util.concurrent.RejectedExecutionException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.idoc.constant.LogConstant;
import com.idoc.nostransfer.exception.EcomNosExceedConcurrencyException;
import com.idoc.nostransfer.exception.EcomNosException;
import com.netease.cloud.ClientException;
import com.netease.cloud.ServiceException;

/**
 * 
 * <p>Title: 电商TransferManager拦截类，处理异常</p> 
 * 
 * <p>Copyright: Copyright (c) 2014</p> 
 * 
 * <p>Company: www.netease.com</p>
 * 
 * @author 	Barney Woo
 * @date 	2014-11-21
 * @version 1.0
 */
public class EcomTransferManagerInterceptor implements MethodInterceptor
{
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable
	{
		Object result = null;
		try
		{
			result = invocation.proceed();
		}
		catch (ServiceException e1)
		{
			StringBuffer buf = new StringBuffer();
		    //捕捉nos服务器异常错误
		    buf.append("Error Message:    " + e1.getMessage());    //错误描述信息
		    buf.append(",HTTP Status Code: " + e1.getStatusCode()); //错误http状态码
		    buf.append(",NOS Error Code:   " + e1.getErrorCode());  //NOS服务器定义错误码
		    buf.append(",Error Type:       " + e1.getErrorType());  //NOS服务器定义错误类型
		    buf.append(",Request ID:       " + e1.getRequestId());  //请求ID,非常重要，有利于nos开发人员跟踪异常请求的错误原因
		    LogConstant.debugLog.info("[nos ServiceException]" + buf.toString());
		    throw new EcomNosException(buf.toString(), e1);
		}
		catch(ClientException e2)
		{
		    //捕捉客户端错误
			LogConstant.debugLog.info("[nos ClientException]" + e2);
		    throw new EcomNosException(e2);//客户端错误信息
		}
		catch (RejectedExecutionException e)
		{
			throw new EcomNosExceedConcurrencyException("exceed concurrency limit");
		}
		catch (InterruptedException e3)
		{
			throw new EcomNosException(e3);
		}
		return result;
	}
}
