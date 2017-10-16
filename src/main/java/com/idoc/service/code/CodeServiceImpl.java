package com.idoc.service.code;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.code.CreateBeanForClass;
import com.idoc.code.def.TableColumn;
import com.idoc.code.factory.CodeGenerateFactory;
import com.idoc.constant.CodeType;
import com.idoc.constant.LogConstant;
import com.idoc.dao.code.CodeDaoImpl;
import com.idoc.model.CodeInfo;
import com.idoc.util.FlileUtil;
import com.idoc.util.ZipCompressor;

@Service("codeServiceImpl")
public class CodeServiceImpl {
	
	@Autowired
	private CodeDaoImpl codeDaoImpl;
	
	public List<String> getDatabaseTables(Map<String, Object> map){
		return codeDaoImpl.getDatabaseTables(map);
	}
	
	public List<TableColumn> getTableColumns(Map<String, Object> map){
		return codeDaoImpl.getTableColumns(map);
	}
	
	public List<CodeInfo> dbGenerateCode(Map<String, Object> map){
		List<CodeInfo> codeInfo = new ArrayList<CodeInfo>();
		if(map == null || map.size() <= 0){
			LogConstant.runLog.info("自动生成代码时，参数为空！");
			return codeInfo;
		}
		String driver = (String) map.get("databaseDriver");
		String url = (String) map.get("databaseUrl");
		String username = (String) map.get("userName");
		String password = (String) map.get("password");
		String entityPackage = "idoc";
		String path = "AutoGener-" + System.currentTimeMillis();
		String params = (String) map.get("params");
		String[] tables = params.split(",");
		for(String table : tables){
			String[] tableParams = table.split(":");
			String tableName = tableParams[0];
			String className = tableParams[1];
			String codeName = tableParams[2];
			LogConstant.runLog.info("自动生成数据库表对应的代码  [tableName]= " + tableName + " [className]= " + className + " [codeName]= " + codeName);
			List<CodeInfo> info = CodeGenerateFactory.codeGenerate(driver, url, username, password, className, tableName, codeName, entityPackage, path);
			if(info != null && info.size() > 0){
				codeInfo.addAll(info);
			}
		}
		String projectPath = CodeGenerateFactory.getProjectPath();
		String codePath = projectPath + "src\\main\\java\\com\\netease\\" + path;
		String zipCodePath = projectPath.substring(0, projectPath.indexOf("WEB-INF")) + "download\\" + path;
		File codeFile = new File(codePath);
		if(codeFile.exists()){
			ZipCompressor zip = new ZipCompressor(zipCodePath + ".zip");
			zip.compressExe(codePath);
			// 删除原文件
			FlileUtil.deleteFile(codeFile);
			CodeInfo download = new CodeInfo();
			download.setCodeType(CodeType.DOWNLOAD);
			String downloadPath = "/download/" + path;
			download.setFilePath(downloadPath + ".zip");
			codeInfo.add(download);
			LogConstant.runLog.info("压缩自动生成代码成功！");
		}
		return codeInfo;
	}
	
	public List<CodeInfo> classGenerateCode(Map<String, Object> map){
		List<CodeInfo> codeInfo = new ArrayList<CodeInfo>();
		if(map == null || map.size() <= 0){
			LogConstant.runLog.info("自动生成代码时，参数为空！");
			return codeInfo;
		}
		String packageName = (String) map.get("packageName");
		String packPath = packageName.replace(".", "\\");
		String className = (String) map.get("className");
		String params = (String) map.get("params");
		String path = "AutoGener-" + System.currentTimeMillis();
		LogConstant.runLog.info("自动生成类属性对应的代码  [packageName]= " + packageName + " [className]= " + className);
		List<CodeInfo> info = CodeGenerateFactory.codeGenerate(packageName, packPath, className, params, path, 0);
		if(info != null && info.size() > 0){
			codeInfo.addAll(info);
		}
		String projectPath = CodeGenerateFactory.getProjectPath();
		String codePath = projectPath + "src\\main\\java\\com\\netease\\" + path;
		String zipCodePath = projectPath.substring(0, projectPath.indexOf("WEB-INF")) + "download\\" + path;
		File codeFile = new File(codePath);
		if(codeFile.exists()){
			ZipCompressor zip = new ZipCompressor(zipCodePath + ".zip");
			zip.compressExe(codePath);
			// 删除原文件
			FlileUtil.deleteFile(codeFile);
			CodeInfo download = new CodeInfo();
			download.setCodeType(CodeType.DOWNLOAD);
			String downloadPath = "/download/" + path;
			download.setFilePath(downloadPath + ".zip");
			codeInfo.add(download);
			LogConstant.runLog.info("压缩自动生成代码成功！");
		}
		return codeInfo;
	}
}