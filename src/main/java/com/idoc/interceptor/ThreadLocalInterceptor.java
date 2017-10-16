package com.idoc.interceptor;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.idoc.constant.CommonConstant;
import com.idoc.constant.LogConstant;
import com.idoc.constant.ThreadLocalConstant;
import com.idoc.memcache.MemcacheServer;
import com.idoc.model.LoginUserInfo;
import com.idoc.util.ErrorMessageUtil;
import com.idoc.util.RequestUtil;

/**
 * 在本地threadLocal中保存request
 */
public class ThreadLocalInterceptor implements HandlerInterceptor {
	@Autowired
	private MemcacheServer memcacheServer;
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) throws Exception {

		ErrorMessageUtil.clear();
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mav) throws Exception {

		List<String> emList = ErrorMessageUtil.get();
		if (emList != null) {
			RequestUtil.setAttribute(ThreadLocalConstant.ERROR_MESSAGE_REQUEST_KEY, emList);
		}
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		System.out.println("request: " + request.getRequestURI());
		//根据实际需求修改cdnBaseUrl的地址
		request.setAttribute("cdnBaseUrl", "http://idoc.qa.lede.com");
		
		String cdnFileVersion = (String)memcacheServer.getCacheData(CommonConstant.INI_CDN_FILE_VERSION);
		if(cdnFileVersion == null){
			cdnFileVersion = "20150417";
		}
		request.setAttribute("cdnFileVersion", cdnFileVersion);
		
		ThreadLocalConstant.requestTL.set(request);
		ThreadLocalConstant.responseTL.set(response);
		LoginUserInfo userInfo = (LoginUserInfo) memcacheServer.getCacheData(request.getSession().getId());
		if(userInfo != null){
			ThreadLocalConstant.sessionTL.set(userInfo);
//			request.setAttribute("userName", userInfo.getUserName());
			request.setAttribute("userCorpMail", userInfo.getEmail());
		}
		
		return true;
	}
	
	/**
	 * 获取字符串的真实长度
	 * 
	 * @param string
	 * @return
	 */
	private int getStringLen(String string) {

		int length = 0;
		if (string != null) {
			try {
				length = string.getBytes("utf-8").length;
			} catch (UnsupportedEncodingException e) {
				LogConstant.debugLog.error("[UserInfoSource]Error occuer when getStringLen [string = " + string + "].", e);
			}
		}
		return length;
	}
}
