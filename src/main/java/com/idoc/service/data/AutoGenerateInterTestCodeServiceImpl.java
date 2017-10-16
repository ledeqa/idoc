package com.idoc.service.data;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.constant.LogConstant;
import com.idoc.dao.docManage.InterfaceDaoImpl;
import com.idoc.dao.docManage.ParamDaoImpl;
import com.idoc.model.Interface;
import com.idoc.model.Param;
import com.idoc.model.data.RetParamType;
import com.idoc.service.dict.DictServiceImpl;
import com.idoc.util.Config;
import com.netease.common.util.StringUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service("autoGenerateInterTestCodeServiceImpl")
public class AutoGenerateInterTestCodeServiceImpl {
	private static Configuration configuration = null;  
    private String templateFolderPath = "";
    private String outputFolderPath = "";
    
    @Autowired
    private InterfaceDaoImpl interfaceDaoImpl;
    
    @Autowired
    private ParamDaoImpl paramDaoImpl;
    
    @Autowired
    private DictServiceImpl dictServiceImpl;
    
    @SuppressWarnings("deprecation")
    @PostConstruct
	public void init(){  
        configuration = new Configuration();  
        configuration.setDefaultEncoding("UTF-8");
		String os = System.getProperty("os.name");
		String webPath = this.getClass().getResource("/").getPath();
		if (os.contains("Windows")) {
			try {
				templateFolderPath = webPath + Config.getConfig("default_template_folder_windows");
				outputFolderPath = webPath + Config.getConfig("default_output_folder_windows");
			} catch (IOException e) {
				LogConstant.debugLog.info("读取配置文件data.propertites出错！");
				e.printStackTrace();
			}
		} else {
			try {
				templateFolderPath = webPath + Config.getConfig("default_template_folder_linux");
				outputFolderPath = webPath + Config.getConfig("default_output_folder_linux");
			} catch (IOException e) {
				LogConstant.debugLog.info("读取配置文件data.propertites出错！");
				e.printStackTrace();
			}
		}
    }
    
    public byte[] autoGenerateInterTestCode(String interfaceId){
		Map<String,Object> dataMap = new HashMap<String,Object>();  
        getData(interfaceId, dataMap);  
        try {
			configuration.setDirectoryForTemplateLoading(new File(templateFolderPath));   //FTL文件所存在的位置 
		} catch (IOException e) {
			LogConstant.debugLog.info("没找到模板文件目录 " + templateFolderPath);
			e.printStackTrace();
		}
        Template t = null;
        try {  
            t = configuration.getTemplate("AutoGenerateInterTestCode.java");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Writer out = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out = new OutputStreamWriter(baos);  
        
        try {  
            t.process(dataMap, out);
            out.flush();
            LogConstant.debugLog.info("自动生成接口测试代码成功！");
        } catch (TemplateException | IOException e) {  
            e.printStackTrace();
        } finally {
			IOUtils.closeQuietly(out);
		}
        LogConstant.debugLog.info(baos.toString());
		return baos.toByteArray();
	}
    
    public String autoGenerateInterTestCodeFile(String interfaceId){
		Map<String,Object> dataMap = new HashMap<String,Object>();  
        getData(interfaceId, dataMap);  
        try {
			configuration.setDirectoryForTemplateLoading(new File(templateFolderPath));   //FTL文件所存在的位置 
		} catch (IOException e) {
			LogConstant.debugLog.info("没找到模板文件目录 " + templateFolderPath);
			e.printStackTrace();
		}  
        Template t = null;
        try {  
            t = configuration.getTemplate("AutoGenerateInterTestCode.java");
        } catch (IOException e) {
            e.printStackTrace();  
        }
        Writer out = null;
        File outFile = new File(outputFolderPath + "AutoTest-" + UUID.randomUUID() + ".java");
        try {
        	out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));    
        } catch (FileNotFoundException e) {
        	LogConstant.debugLog.info("没找到文件输出位置 " + outputFolderPath);
            e.printStackTrace();
        }
        String filePath = "";
        try {  
            t.process(dataMap, out);
            out.flush();
            filePath = outFile.getPath();
            LogConstant.debugLog.info("自动生成接口测试代码成功！");
        } catch (TemplateException | IOException e) {  
            e.printStackTrace();
        } finally {
			IOUtils.closeQuietly(out);
		}
		return filePath;
	}
    
    private void getData(String interfaceId, Map<String, Object> dataMap){
    	Long id = Long.parseLong(interfaceId);
    	Interface inter = getInterfaceInfo(id);
		if(inter == null){
    		LogConstant.debugLog.info("自动生成接口测试代码时，接口[" + id + "]获取的接口信息为空！");
    		return;
    	}
		dataMap.put("interfaceName", inter.getInterfaceName());
		dataMap.put("url", inter.getUrl());
		dataMap.put("interfaceRequsetType", inter.getRequestType());
		dataMap.put("interfaceType", inter.getInterfaceType());
		List<Param> reqParamList = inter.getReqParams();
		for(Param req : reqParamList){
			String paramType = req.getParamType();
			if(!StringUtil.isEmpty(paramType)){
				String type = null;
				switch(paramType){
				case "string":
					type = "String";
					break;
				case "number":
					type = "long";
					break;
				case "int":
					type = "int";
					break;
				case "double":
					type = "double";
					break;
				case "float":
					type = "float";
					break;
				case "short":
					type = "short";
					break;
				case "byte":
					type = "byte";
					break;
				case "char":
					type = "char";
					break;
				case "object":
					type = "Object";
					break;
				case "boolean":
					type = "boolean";
					break;
				case "array<string>":
					type = "String[]";
					break;
				case "array<number>":
					type = "long[]";
					break;
				case "array<object>":
					type = "Object[]";
					break;
				case "array<boolean>":
					type = "boolean[]";
					break;
				default:
					if(paramType.startsWith("array<") && paramType.endsWith(">")){
						String classType = paramType.substring(paramType.indexOf('<') + 1, paramType.indexOf('>'));
						if(classType.equals("int") || classType.equals("float") || classType.equals("double") || classType.equals("byte") ||
								classType.equals("char") || classType.equals("long") || classType.equals("short")){
							type = classType + "[]";
						}else{ // 将classType首字母大写
							char[] classTypeChar = classType.trim().toCharArray();
							classTypeChar[0] -= classTypeChar[0] > 96 && classTypeChar[0] < 132 ? 32 : 0;
							type = String.valueOf(classTypeChar) + "[]";
						}
					}else if(!paramType.startsWith("array<") && !paramType.endsWith(">")){
						char[] classTypeChar = paramType.trim().toCharArray();
						classTypeChar[0] -= classTypeChar[0] > 96 && classTypeChar[0] < 132 ? 32 : 0;
						type = String.valueOf(classTypeChar);
					}
					break;
				}
				req.setParamType(type);
			}
		}
		dataMap.put("reqParamList", reqParamList);
		
		List<RetParamType> retParamTypeList = new ArrayList<RetParamType>();
		List<Param> retParamList = inter.getRetParams();
		getRetParamTypeList(retParamList, retParamTypeList);
		dataMap.put("retParamTypeList", retParamTypeList);
		
		List<String> testDataList = new ArrayList<String>();
		int index = 5;
		if(reqParamList.size() > 0){
			for(int i = 0; i < index; i++){
				String testData = "{";
				for(Param req : reqParamList){
					String type = req.getParamType();
					if(!StringUtil.isEmpty(type)){
						switch(type){
						case "int":
							int intValue = 0;
							if(i == index - 2){
								intValue = Integer.MAX_VALUE;
							}else if(i == index -1){
								intValue = Integer.MIN_VALUE;
							}else{
								intValue = RandomUtils.nextInt();
							}
							testData += intValue + ", ";
							break;
						case "float":
							float floatValue = 0;
							if(i == index - 2){
								floatValue = Float.MAX_VALUE;
							}else if(i == index -1){
								floatValue = Float.MIN_VALUE;
							}else{
								floatValue = RandomUtils.nextFloat();
							}
							testData += floatValue + ", ";
							break;
						case "long":
							long longValue = 0;
							if(i == index - 2){
								longValue = Long.MAX_VALUE;
							}else if(i == index -1){
								longValue = Long.MIN_VALUE;
							}else{
								longValue = RandomUtils.nextLong();
							}
							testData += longValue + "L, ";
							break;
						case "short":
							long shortValue = 0;
							if(i == index - 2){
								shortValue = Short.MAX_VALUE;
							}else if(i == index -1){
								shortValue = Short.MIN_VALUE;
							}else{
								shortValue = RandomUtils.nextInt(Short.MAX_VALUE);
							}
							testData += shortValue + ", ";
							break;
						case "double":
							double doubleValue = 0;
							if(i == index - 2){
								doubleValue = Double.MAX_VALUE;
							}else if(i == index -1){
								doubleValue = Double.MIN_VALUE;
							}else{
								doubleValue = RandomUtils.nextDouble();
							}
							testData += doubleValue + ", ";
							break;
						case "char":
							char charValue = (char) RandomUtils.nextInt(127);
							testData += charValue + ", ";
							break;
						case "boolean":
							boolean boolValue = RandomUtils.nextBoolean();
							testData += boolValue + ", ";
							break;
						case "string":
							String stringValue = RandomStringUtils.randomAlphanumeric(i);
							testData += "\"" + stringValue + "\"" + ", ";
							break;
						default :
							testData +=  ", ";
							break;
						}
					}
					
				}
				if(testData.contains(",")){
					testData = testData.substring(0, testData.lastIndexOf(','));
				}
				testData += "}";
				testDataList.add(testData);
			}
		}else{ // 生成空参数，TestNG测试时需要
			String testData = "{}";
			testDataList.add(testData);
		}
		dataMap.put("testDataList", testDataList);
    }
    
    private void getRetParamTypeList(List<Param> retParamList, List<RetParamType> retParamTypeList){
    	if(retParamList == null || retParamList.size() == 0 || retParamTypeList == null){
    		return;
    	}
		for(Param param : retParamList){
			String type = param.getParamType();
			Long dictId = param.getDictId();
			if(dictId == null || dictId == 0){ // 参数无字典
				if(!StringUtil.isEmpty(type)){
					RetParamType ret = new RetParamType();
					switch(type){
					case "int":
					case "float":
					case "long":
					case "short":
					case "double":
					case "number":
						ret.setParamType("number");
						break;
					case "boolean":
					case "Boolean":
						ret.setParamType("boolean");
						break;
					case "string":
					case "String":
						ret.setParamType("string");
						break;
					case "byte":
						ret.setParamType("byte");
						break;
					case "char":
						ret.setParamType("char");
						break;
					default :
						ret.setParamType("null");
						break;
					}
					int isNecessary = param.getIsNecessary();
					if(isNecessary == 0){
						ret.setParamName(param.getParamName() + "0"); // 非必需
					}else{
						ret.setParamName(param.getParamName() + "1");
					}
					retParamTypeList.add(ret);
				}
			}else{
				if(param.getDict() != null)
					getRetParamTypeList(param.getDict().getParams(), retParamTypeList);
			}
		}
    }
    
    private Interface getInterfaceInfo(Long id){
		Map<String,Object> condMap = new HashMap<String,Object>();
		condMap.put("interfaceId",id);
		List<Interface> interList = interfaceDaoImpl.selectInterfaceListByCond(condMap);
		Interface inter = interList.get(0);
		// 设置请求参数信息
		List<Param> reqList = paramDaoImpl.selectRequestParamByInterfaceId(id);
		if(reqList != null && !reqList.isEmpty()){
			for(Param pm: reqList){
				if(pm.getDictId() != null){
					pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
				}
			}
		}
		inter.setReqParams(reqList);
		// 设置返回参数信息
		List<Param> retList = paramDaoImpl.selectReturnParamByInterfaceId(id);
		if(retList != null && !retList.isEmpty()){
			for(Param pm: retList){
				if(pm.getDictId() != null){
					pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
				}
			}
		}
		inter.setRetParams(retList);
		return inter;
	}    
}