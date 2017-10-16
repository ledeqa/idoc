package com.idoc.web.onlineInterface;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.constant.LogConstant;
import com.idoc.model.OnlinePage;
import com.idoc.service.interfaceOnline.PageOnlineService;
import com.idoc.util.ErrorMessageUtil;

@Controller
@RequestMapping("/idoc/pageOnline")
public class PageOnlineController {
	@Autowired
	private PageOnlineService pageOnlineService;
	
	
	@RequestMapping("add.html")
	@ResponseBody
	public Map<String,Object> addModule(String onlineModuleId, String pageName) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(pageName) || StringUtils.isBlank(onlineModuleId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		try {
			Long id = Long.valueOf(onlineModuleId);
			OnlinePage page = pageOnlineService.addOnlinePage(id, pageName);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
				retMap.put("page", page);
			}else{
				retMap.put("retCode", 402);
				retMap.put("retDesc", error);
				// 返回插入失败原因
			}
		} catch (Exception e) {
			LogConstant.runLog.error("异常错误",e);
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
			return retMap;
		}
		return retMap;
	}
	
	@RequestMapping("update.html")
	@ResponseBody
	public Map<String,Object> updatePage(String pageId, String pageName) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(pageName) || StringUtils.isBlank(pageId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		try {
			Long id = Long.valueOf(pageId);
			pageOnlineService.updatePage(id, pageName);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
			}else{
				retMap.put("retCode", 402);
				retMap.put("retDesc", error);
			}
		} catch (Exception e) {
			LogConstant.runLog.error("异常错误",e);
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
			return retMap;
		}
		return retMap;

	}
	
	@RequestMapping("delete.html")
	@ResponseBody
	public Map<String,Object> deletePage(String pageId) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if( StringUtils.isBlank(pageId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		try {
			Long id = Long.valueOf(pageId);
			pageOnlineService.deleteOnlinePage(id);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
			}else{
				retMap.put("retCode", 402);
				retMap.put("retDesc", error);
			}
		} catch (Exception e) {
			LogConstant.runLog.error("异常错误",e);
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
			return retMap;
		}
		return retMap;

	}
	
}
