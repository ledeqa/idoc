package com.idoc.web.code;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.code.def.TableColumn;
import com.idoc.model.CodeInfo;
import com.idoc.service.code.CodeServiceImpl;
import com.idoc.web.BaseController;

@Controller("codeGenerateController")
public class CodeGenerateController extends BaseController{
	
	@Autowired
	private CodeServiceImpl codeServiceImpl;
	
	@RequestMapping("code/index")
	public String index(){
		return "code/index";
	}
	
	@RequestMapping("/idoc/code/getDatabaseTables.html")
	@ResponseBody
	public Map<String, Object> getDatabaseTables(
			@RequestParam("databaseDriver")String databaseDriver, 
			@RequestParam(value="databaseUrl")String databaseUrl,
			@RequestParam("userName")String userName, 
			@RequestParam("password")String password, 
			@RequestParam("databaseName")String databaseName){
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("databaseDriver", databaseDriver);
		paraMap.put("databaseUrl", databaseUrl);
		paraMap.put("userName", userName);
		paraMap.put("password", password);
		paraMap.put("databaseName", databaseName);
		List<String> list = codeServiceImpl.getDatabaseTables(paraMap);
		if(list != null && list.size() > 0){
			retMap.put("retCode", "200");
			retMap.put("retDesc", "查询成功");
			retMap.put("tableList", list);
		}else{
			retMap.put("retCode", "-1");
		}
		return retMap;
	}
	
	@RequestMapping("/idoc/code/getTableColumns.html")
	@ResponseBody
	public Map<String, Object> getTableColumns(
			@RequestParam("databaseDriver")String databaseDriver, 
			@RequestParam(value="databaseUrl")String databaseUrl,
			@RequestParam("userName")String userName, 
			@RequestParam("password")String password, 
			@RequestParam("databaseName")String databaseName,
			@RequestParam("tableName")String tableName){
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("databaseDriver", databaseDriver);
		paraMap.put("databaseUrl", databaseUrl);
		paraMap.put("userName", userName);
		paraMap.put("password", password);
		paraMap.put("databaseName", databaseName);
		paraMap.put("tableName", tableName);
		List<TableColumn> list = codeServiceImpl.getTableColumns(paraMap);
		if(list != null && list.size() > 0){
			retMap.put("retCode", "200");
			retMap.put("retDesc", "查询成功");
			retMap.put("columnList", list);
		}else{
			retMap.put("retCode", "-1");
		}
		return retMap;
	}
	
	@RequestMapping(value="/idoc/code/dbGenerateCode")
	@ResponseBody
	public Map<String, Object> dbGenerateCode( // 前台校验参数
			@RequestParam("databaseDriver")String databaseDriver, 
			@RequestParam(value="databaseUrl")String databaseUrl,
			@RequestParam("userName")String userName, 
			@RequestParam("password")String password, 
			@RequestParam("databaseName")String databaseName,
			@RequestParam("params")String params, ModelMap map){
		Map<String, Object> paraMap = new HashMap<String, Object>();
		Map<String, Object> retMap = new HashMap<String, Object>();
		paraMap.put("databaseDriver", databaseDriver);
		paraMap.put("databaseUrl", databaseUrl);
		paraMap.put("userName", userName);
		paraMap.put("password", password);
		paraMap.put("databaseName", databaseName);
		paraMap.put("params", params);
		
		List<CodeInfo> sourceCodeInfo = codeServiceImpl.dbGenerateCode(paraMap);
		if(sourceCodeInfo != null && sourceCodeInfo.size() > 0){
			retMap.put("retCode", 200);
			retMap.put("sourceCodeInfo", sourceCodeInfo);
		}else{
			retMap.put("retCode", 400);
		}
		
		return retMap;
	}
	@RequestMapping(value="/code/codeInfo")
	public String codeInfo(){
		return "/code/dbGenerateCode";
	}
	@RequestMapping(value="/idoc/code/classGenerateCode")
	@ResponseBody
	public Map<String, Object> classGenerateCode( // 前台校验参数
			@RequestParam("packageName")String packageName,
			@RequestParam("className")String className,
			@RequestParam("params")String params, ModelMap map){
		Map<String, Object> paraMap = new HashMap<String, Object>();
		Map<String, Object> retMap = new HashMap<String, Object>();
		paraMap.put("packageName", packageName);
		paraMap.put("className", className);
		paraMap.put("params", params);
		
		List<CodeInfo> sourceCodeInfo = null;
		try {
			sourceCodeInfo = codeServiceImpl.classGenerateCode(paraMap);
		} catch(Exception e) {
			e.printStackTrace();
			retMap.put("retCode", 401);
			return retMap;
		}
		
		if(sourceCodeInfo != null && sourceCodeInfo.size() > 0){
			retMap.put("retCode", 200);
			retMap.put("sourceCodeInfo", sourceCodeInfo);
		}else{
			retMap.put("retCode", 400);
		}
		
		return retMap;
	}
	@RequestMapping(value="code/codeInfoForClass")
	public String codeInfoForClass(){
		return "/code/classGenerateCode";
	}
}