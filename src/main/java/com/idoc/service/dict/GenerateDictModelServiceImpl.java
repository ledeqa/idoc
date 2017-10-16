package com.idoc.service.dict;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.code.factory.CodeGenerateFactory;
import com.idoc.constant.CodeType;
import com.idoc.constant.LogConstant;
import com.idoc.model.CodeInfo;
import com.idoc.model.Dict;
import com.idoc.model.Param;
import com.idoc.model.data.DictParamInfo;
import com.idoc.model.data.ParamInfo;
import com.idoc.util.Config;
import com.idoc.util.FlileUtil;
import com.idoc.util.ZipCompressor;
import com.netease.common.util.StringUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service("generateDictModelServiceImpl")
public class GenerateDictModelServiceImpl {
	private static Configuration configuration = null;
    private String templateFolderPath = "";

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
			} catch (IOException e) {
				LogConstant.debugLog.info("读取配置文件data.propertites出错！");
				e.printStackTrace();
			}
		} else {
			try {
				templateFolderPath = webPath + Config.getConfig("default_template_folder_linux");
			} catch (IOException e) {
				LogConstant.debugLog.info("读取配置文件data.propertites出错！");
				e.printStackTrace();
			}
		}
    }

    public byte[] generateDictModel(String dictId){
		Map<String,Object> dataMap = new HashMap<String,Object>();
        getData(dictId, dataMap);
        try {
			configuration.setDirectoryForTemplateLoading(new File(templateFolderPath));   //FTL文件所存在的位置
		} catch (IOException e) {
			LogConstant.debugLog.info("没找到模板文件目录 " + templateFolderPath);
			e.printStackTrace();
		}
        Template t = null;
        try {
            t = configuration.getTemplate("DictModel.java");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Writer out = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out = new OutputStreamWriter(baos);

        try {
            t.process(dataMap, out);
            out.flush();
            LogConstant.debugLog.info("生成字典Model代码成功！");
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        } finally {
			IOUtils.closeQuietly(out);
		}
        LogConstant.debugLog.info(baos.toString());
		return baos.toByteArray();
	}
    //生成dictClass代码
    public List<CodeInfo> generateDictClass(String dictId) {
    	Long id = Long.valueOf(dictId);
    	Dict dict = dictServiceImpl.getDict(id);
    	List<CodeInfo> codeInfo = new ArrayList<CodeInfo>();
    	String path = "AutoGener-" + System.currentTimeMillis();
    	List<CodeInfo> info = CodeGenerateFactory.codeGenerate("default", "default",dict.getDictName(), getParams(dictId), path, 0);
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
    //获取参数并拼接参数
    private String getParams(String dictId){
    	String params=null;
    	try{
	    	Long id = Long.valueOf(dictId);
			Dict dict = dictServiceImpl.getDict(id);
			if(dict != null){
				List<Param> dictParamList = dict.getParams();
				for(Param param : dictParamList){
					String paramName = param.getParamName();
					String paramType = param.getParamType();
					String remark = param.getRemark();
					params = paramName+":"+paramType+":"+remark+":"+0;
					if(param!=null){
						params +=",";
					}
				}
			}
    	}catch(Exception e){
    		LogConstant.runLog.info("转换dictId类型异常！");
    		e.printStackTrace();
    	}
    	return params;
    }



    private void getData(String dictId, Map<String,Object> dataMap){
    	DictParamInfo dictParamInfo = new DictParamInfo();
    	try{
	    	Long id = Long.valueOf(dictId);
			Dict dict = dictServiceImpl.getDict(id);
			if(dict != null){
				dictParamInfo.setDictName(dict.getDictName());
				List<Param> dictParamList = dict.getParams();
				List<ParamInfo> paramList = new ArrayList<ParamInfo>();
				for(Param param : dictParamList){
					ParamInfo paramInfo = new ParamInfo();
					paramInfo.setParamName(param.getParamName());
					String type = param.getParamType();
					if(!StringUtil.isEmpty(type)){
						if(type.equals("string")){
							type = "String";
						}else if(type.contains("array<")){
							type = type.replace("array", "List");
							dictParamInfo.setHasList(1);
						}
					}
					paramInfo.setParamType(type);
					String desc = param.getParamDesc();
					if(StringUtil.isEmpty(desc)){
						paramInfo.setParamDesc(null);
					}else{
						paramInfo.setParamDesc(desc);
					}

					paramList.add(paramInfo);
				}
				dictParamInfo.setParamList(paramList);
				dataMap.put("dictParamInfo", dictParamInfo);
			}
    	}catch(Exception e){
    		LogConstant.runLog.info("转换dictId类型异常！");
    		e.printStackTrace();
    	}
    }

}