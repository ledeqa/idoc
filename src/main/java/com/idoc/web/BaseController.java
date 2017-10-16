package com.idoc.web;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.idoc.constant.LogConstant;

/**
 * 所有Controller都需要的操作
 * 
 */
@Controller
public class BaseController {
	
	@ExceptionHandler(Throwable.class)
	public ModelAndView handleException(Throwable e) {
		ModelAndView model = new ModelAndView("/fail");
		StackTraceElement[] traces = e.getStackTrace();
		String msg = e.toString() != null ? e.toString() + "{enter}" : "";
		msg += e.getMessage() != null ? e.getMessage() + "{enter}" : "";
		for(StackTraceElement trace : traces){
			msg += trace.toString() + "{enter}";
		}
		LogConstant.debugLog.error(e.getMessage());
		//LogConstant.debugLog.error(msg, e);
		e.printStackTrace();
		model.addObject("exceptionMsg", msg);
		
		return model;
	}
	
	public boolean verifyPara(String... params) {
		for (String param : params) {
			if (StringUtils.isBlank(param)) {
				return false;
			}
		}
		return true;
	}
	
}