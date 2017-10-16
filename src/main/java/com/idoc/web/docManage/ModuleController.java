package com.idoc.web.docManage;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.constant.LogConstant;
import com.idoc.model.Module;
import com.idoc.service.docManage.ModuleServiceImpl;
import com.idoc.util.ErrorMessageUtil;

@Controller
@RequestMapping("/idoc/module")
public class ModuleController {
	
	@Autowired
	private ModuleServiceImpl moduleServiceImpl;
	
	/**
	 * @param projectId
	 * @param moduleName
	 * 插入模块信息，注意不判断同一个项目下是否存在同名模块
	 * @return
	 */
	@RequestMapping("add.html")
	@ResponseBody
	public Map<String,Object> addModule(String projectId, String moduleName) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(moduleName) || StringUtils.isBlank(projectId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		try {
			Long id = Long.valueOf(projectId);
			Module module = moduleServiceImpl.addModule(id, moduleName);
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
		
		if(StringUtils.isBlank(moduleName) || StringUtils.isBlank(moduleId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		try {
			Long id = Long.valueOf(moduleId);
			moduleServiceImpl.updateModule(id, moduleName);
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
	public Map<String,Object> deleteModule(String moduleId) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(moduleId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		try {
			Long id = Long.valueOf(moduleId);
			moduleServiceImpl.deleteModule(id);
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
