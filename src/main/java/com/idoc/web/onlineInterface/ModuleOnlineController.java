package com.idoc.web.onlineInterface;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.constant.LogConstant;
import com.idoc.model.OnlineModule;
import com.idoc.service.interfaceOnline.ModuleOnlineService;
import com.idoc.util.ErrorMessageUtil;

@Controller
@RequestMapping("/idoc/moduleOnline")
public class ModuleOnlineController {
	
	@Autowired
	private ModuleOnlineService ModuleOnlineService;
	
	/**
	 * @param projectId
	 * @param moduleName
	 * 插入在线模块信息
	 * @return
	 */
	@RequestMapping("add.html")
	@ResponseBody
	public Map<String,Object> addOnlineModule(String productId, String moduleName) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(moduleName) || StringUtils.isBlank(productId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		try {
			Long id = Long.valueOf(productId);
			OnlineModule module = ModuleOnlineService.addOnlineModule(id, moduleName);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
				retMap.put("module", module);
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
	public Map<String,Object> updateModule(String moduleId, String moduleName) {
	
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if (StringUtils.isBlank(moduleName) || StringUtils.isBlank(moduleId)) {
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		try {
			Long id = Long.valueOf(moduleId);
			ModuleOnlineService.updateOnlineModule(id, moduleName);
			String error = ErrorMessageUtil.getErrorMessages();
			if (StringUtils.isBlank(error)) {
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
			} else {
				retMap.put("retCode", 402);
				retMap.put("retDesc", error);
			}
		} catch (Exception e) {
			LogConstant.runLog.error("异常错误", e);
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
			return retMap;
		}
		return retMap;
		
	}
	@RequestMapping("delete.html")
	@ResponseBody
	public Map<String,Object> deleteModule(String moduleId) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(moduleId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		try {
			Long id = Long.valueOf(moduleId);
			ModuleOnlineService.deleteOnlineModule(id);
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
