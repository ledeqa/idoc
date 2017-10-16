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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.constant.LogConstant;
import com.idoc.dao.docManage.InterfaceOnlineDaoImpl;
import com.idoc.dao.docManage.ParamDaoImpl;
import com.idoc.dao.login.LoginDaoImpl;
import com.idoc.model.Dict;
import com.idoc.model.InterfaceOnline;
import com.idoc.model.Param;
import com.idoc.model.UserModel;
import com.idoc.model.data.DictParamInfo;
import com.idoc.model.data.InterfaceInfo;
import com.idoc.model.data.ParamInfo;
import com.idoc.service.dict.DictServiceImpl;
import com.idoc.util.Config;
import com.netease.common.util.StringUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service("exportInterInfo2WordServiceImpl")
public class ExportInterInfo2WordServiceImpl {
	private static Configuration configuration = null;  
    private String templateFolderPath = "";
    private String outputFolderPath = "";
    
    @Autowired
    private InterfaceOnlineDaoImpl interfaceOnlineDaoImpl;
    
    @Autowired
    private LoginDaoImpl loginDaoImpl;
    
    @Autowired
    private ParamDaoImpl paramDaoImpl;
    
    @Autowired
    private DictServiceImpl dictServiceImpl;
    
    @SuppressWarnings("deprecation")
    @PostConstruct
	public void init(){  
        configuration = new Configuration();  
        configuration.setDefaultEncoding("UTF-8");
		LogConstant.debugLog.info("====================================================");
		LogConstant.debugLog.info("初始化输入输出文件路径.......");
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

		LogConstant.debugLog.info("初始化输入输出文件名.......");
		LogConstant.debugLog.info("templateFolderPath : " + templateFolderPath);
		LogConstant.debugLog.info("outputFolderPath : " + outputFolderPath);
		LogConstant.debugLog.info("====================================================");
    }
    
    /**
     * 生成字节流
     * @param interfaceIdList
     * @return
     */
    public byte[] exportInterInfo2ByteStream(List<String> interfaceIdList){
		Map<String,Object> dataMap = new HashMap<String,Object>();  
        getData(interfaceIdList, dataMap);  
        try {
			configuration.setDirectoryForTemplateLoading(new File(templateFolderPath));   //FTL文件所存在的位置  
		} catch (IOException e) {
			LogConstant.debugLog.info("没找到模板文件目录 " + templateFolderPath);
			e.printStackTrace();
		}  
        Template t = null;
        try {  
            t = configuration.getTemplate("ExportInterInfo2Word.xml");
        } catch (IOException e) {
            e.printStackTrace();  
        }
        Writer out = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out = new OutputStreamWriter(baos);  
        
        try {  
            t.process(dataMap, out);
            out.flush();
            LogConstant.debugLog.info("生成Word文档成功！");
        } catch (TemplateException | IOException e) {  
            e.printStackTrace();
        } finally {
			IOUtils.closeQuietly(out);
		}
		return baos.toByteArray();
	}
    
    /**
     * 生成文件
     * @param interfaceIdList
     * @return
     */
	public String exportInterInfo2Word(List<String> interfaceIdList){
		Map<String,Object> dataMap = new HashMap<String,Object>();  
        getData(interfaceIdList, dataMap);  
        try {
			configuration.setDirectoryForTemplateLoading(new File(templateFolderPath));   //FTL文件所存在的位置  
		} catch (IOException e) {
			LogConstant.debugLog.info("没找到模板文件目录 " + templateFolderPath);
			e.printStackTrace();
		}  
        Template t = null;
        try {  
            t = configuration.getTemplate("ExportInterInfo2Word.xml");  //文件名  
        } catch (IOException e) {
            e.printStackTrace();  
        }
        File outFile = new File(outputFolderPath + "word-" + UUID.randomUUID() + ".doc");  
        Writer out = null;
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
            LogConstant.debugLog.info(filePath + " 生成Word文档成功！");
        } catch (TemplateException | IOException e) {  
            e.printStackTrace();
        } finally {
			IOUtils.closeQuietly(out);
		}
		return filePath;
	}
	
	private void getData(List<String> interfaceIdList, Map<String, Object> dataMap) {  
        if(interfaceIdList.size() <= 0){
        	return;
        }
        List<InterfaceInfo> interfaceInfoList = new ArrayList<InterfaceInfo>();
        for(String interfaceId : interfaceIdList){
        	Long id = Long.parseLong(interfaceId);
        	InterfaceOnline inter = getInterfaceInfo(id);
        	if(inter == null){
        		LogConstant.debugLog.info("导出word接口信息时，接口[" + id + "]获取的接口信息为空！");
        		continue;
        	}
        	// 取出interface中的信息设置到freeMarker模板类中
        	InterfaceInfo interInfo = new InterfaceInfo();
        	interInfo.setInterfaceName(xmlSpecialCharProcess(inter.getInterfaceName()));
        	interInfo.setRequestType(xmlSpecialCharProcess(getRequestType(inter.getRequestType())));
        	String interfaceType = inter.getInterfaceType() == 1 ? "ajax" : "ftl";
        	interInfo.setInterfaceType(interfaceType);
        	interInfo.setUrl(xmlSpecialCharProcess(inter.getUrl()));
        	interInfo.setDesc("见接口详情");
        	if(inter.getCreator() != null){
        		interInfo.setCreator(xmlSpecialCharProcess(inter.getCreator().getUserName()));
        	}
        	List<DictParamInfo> dictParamList = new ArrayList<DictParamInfo>();
        	Set<Long> dictSet = new HashSet<Long>();
        	List<ParamInfo> reqParamList = new ArrayList<ParamInfo>();
        	List<Param> reqParams = inter.getReqParams();
        	if(reqParams != null){
	        	for(Param req : reqParams){
	        		ParamInfo param = new ParamInfo();
	        		param.setParamName(xmlSpecialCharProcess(req.getParamName()));
	        		param.setParamType(xmlSpecialCharProcess(req.getParamType()));
	        		param.setParamDesc(xmlSpecialCharProcess(req.getParamDesc()));
	        		param.setParamRemark(xmlSpecialCharProcess(req.getRemark()));
	        		String isNecessary = req.getIsNecessary() == 1 ? "是" : "否";
	        		param.setParamIsNecessary(isNecessary);
	        		reqParamList.add(param);
	        		
	        		// 参数是个字典
	        		if(req.getDict() != null){
	        			findDictParams(req.getDict(), dictParamList, dictSet);
	        		}
	        	}
	        	interInfo.setReqParamList(reqParamList);
        	}
        	
        	List<ParamInfo> retParamList = new ArrayList<ParamInfo>();
        	List<Param> retParams = inter.getRetParams();
        	if(retParams != null){
	        	for(Param ret : retParams){
	        		ParamInfo param = new ParamInfo();
	        		param.setParamName(xmlSpecialCharProcess(ret.getParamName()));
	        		param.setParamType(xmlSpecialCharProcess(ret.getParamType()));
	        		param.setParamDesc(xmlSpecialCharProcess(ret.getParamDesc()));
	        		param.setParamRemark(xmlSpecialCharProcess(ret.getRemark()));
	        		String isNecessary = ret.getIsNecessary() == 1 ? "是" : "否";
	        		param.setParamIsNecessary(isNecessary);
	        		retParamList.add(param);
	        		
	        		// 参数是个字典
	        		if(ret.getDict() != null){
	        			findDictParams(ret.getDict(), dictParamList, dictSet);
	        		}
	        	}
	        	interInfo.setRetParamList(retParamList);
        	}
        	interInfo.setDictParamList(dictParamList);
        	interfaceInfoList.add(interInfo);
        }
        dataMap.put("title", "Idoc自动生成的接口信息文档");
        dataMap.put("interfaceInfoList", interfaceInfoList);
    }
	
	private void findDictParams(Dict dict, List<DictParamInfo> dictParamList, Set<Long> dictSet){
		if(dict == null || dictSet.contains(dict.getDictId())){
			return;
		}else{
			dictSet.add(dict.getDictId());
		}
		List<Param> params = dict.getParams();
		if(params.size() <= 0){
			return;
		}
		DictParamInfo dictParam = new DictParamInfo();
		dictParam.setDictName(dict.getDictName());
		List<ParamInfo> paramList = new ArrayList<ParamInfo>();
		for(Param param : params){
			ParamInfo info = new ParamInfo();
			info.setParamName(xmlSpecialCharProcess(param.getParamName()));
    		info.setParamType(xmlSpecialCharProcess(param.getParamType()));
    		info.setParamDesc(xmlSpecialCharProcess(param.getParamDesc()));
    		info.setParamRemark(xmlSpecialCharProcess(param.getRemark()));
    		String isNecessary = param.getIsNecessary() == 1 ? "是" : "否";
    		info.setParamIsNecessary(isNecessary);
    		paramList.add(info);
    		
    		// 如果参数中还有字典，递归找出接口的所有字典
    		if(param.getDict() != null){
    			findDictParams(param.getDict(), dictParamList, dictSet);
    		}
		}
		dictParam.setParamList(paramList);
		dictParamList.add(dictParam);
	}
	
	private InterfaceOnline getInterfaceInfo(Long id){
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("onlineInterfaceId",id);
		InterfaceOnline onlineInter = interfaceOnlineDaoImpl.selectOnlineInterfaceByConditions(paramMap); 
		//设置创建者信息
		Long creatorId = onlineInter.getCreatorId();
		if(creatorId != null){
			UserModel creator = loginDaoImpl.queryUserModelByUserId(creatorId);
			onlineInter.setCreator(creator);
		}
		//设置请求参数信息
		List<Param> reqList = paramDaoImpl.selectOnlineRequestParamByInterfaceId(onlineInter.getInterfaceId());
		if(reqList != null && !reqList.isEmpty()){
			for(Param pm: reqList){
				if(pm.getDictId() != null){
					pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
				}
			}
		}
		onlineInter.setReqParams(reqList);
		//设置返回参数信息
		List<Param> retList = paramDaoImpl.selectOnlineReturnParamByInterfaceId(onlineInter.getInterfaceId());
		if(retList != null && !retList.isEmpty()){
			for(Param pm: retList){
				if(pm.getDictId() != null){
					pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
				}
			}
		}
		onlineInter.setRetParams(retList);
		return onlineInter;
	}
	
	private String getRequestType(int type){
		switch(type){
		case 1:
			return "get";
		case 2:
			return "post";
		case 3:
			return "put";
		case 4:
			return "delete";
		default:
			return "";
		}
	}
	
	private String xmlSpecialCharProcess(String s){
		if(StringUtil.isEmpty(s)){
			return s;
		}
		Pattern p = Pattern.compile("<|>|'|\"|&");
        Matcher m = p.matcher(s);
		if(m.find()){
			s = "<![CDATA[" + s +"]]>";
		}
		return s;
	}
}