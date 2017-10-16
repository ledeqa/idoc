package com.idoc.service.mock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.form.ParamForm;
import com.idoc.model.DictParamDisplay;
import com.idoc.model.Interface;
import com.idoc.model.Module;
import com.idoc.model.Page;
import com.idoc.model.Param;
import com.idoc.service.docManage.InterfaceServiceImpl;
import com.idoc.service.docManage.ModuleServiceImpl;
import com.idoc.util.MockjsRunner;

@Service
public class MockService {
	
	@Autowired
	private ModuleServiceImpl moduleServiceImpl;
	
	@Autowired
	private InterfaceServiceImpl interfaceServiceImpl;
	
	public String getMockJsRule(Long interfaceId, ParamForm[] paramFormArr){
		StringBuilder json = new StringBuilder();
        String left = "{";
        String right = "}";
        
        json.append(left);
        
        boolean first = true;

        if(paramFormArr != null){
        	for (ParamForm p : paramFormArr) {
        		if (first) {
        			first = false;
        		} else {
        			json.append(",");
        		}
        		buildMockTemplate(interfaceId, json, p, -1);
        	}
        }
        
        json.append(right);
		
		return json.toString();
	}
	
	public String getMockJsRuleByProjectAndPattern(Long interfaceId, String projectId, String pattern){
		
		if(!isPatternLegal(pattern)){
			return "{\"isOk\":false,\"msg\":\"路径为空，请查看是否接口未填写URL.\"}";
		}
		
		List<Module> moduleList = moduleServiceImpl.getFullModuleInfoListByProjectId(projectId);
		
		List<Interface> interList;
		if(CollectionUtils.isNotEmpty(moduleList)){
			interList = new ArrayList<Interface>();
			//将该项目下的所有接口放入interList
			for(Module module: moduleList){
				List<Page> pageList = module.getPageList();
				if(CollectionUtils.isNotEmpty(pageList)){
					for(Page page: pageList){
						if(CollectionUtils.isNotEmpty(page.getInterfaceList())){
							interList.addAll(page.getInterfaceList());
						}
					}
				}
			}
			
			if(CollectionUtils.isNotEmpty(interList)){
				Interface matchedInter = null;
				for(Interface inter: interList){
					//根据相对路径判断是否是同一个接口
					if(this.isRelativeUrlExactlyMatch(pattern, inter.getUrl())){
						matchedInter = inter;
						break;
					}
				}
				
				if(matchedInter != null){
					matchedInter = interfaceServiceImpl.getInterfaceInfo(matchedInter.getInterfaceId());
					ParamForm[] paramFormArr = null;
					if(CollectionUtils.isNotEmpty(matchedInter.getRetParams())){
						List<Param> retParams = matchedInter.getRetParams();
						paramFormArr = new ParamForm[retParams.size()];
						for(int i = 0; i < retParams.size(); i++){
							paramFormArr[i] = new ParamForm(retParams.get(i));
						}
					}
					
					String result = this.getMockJsRule(interfaceId, paramFormArr);
					
					return result;
				}
			}
		}
		
		return "{\"isOk\":false, \"errMsg\":\"no matched interface\"}";
	}
	
	public String getMockJsData(String path, String rule){
		
		return MockjsRunner.renderMockjsRule(path, rule);
	}
	
	
	public List<String> getWhiteList(String projectId){
		List<String> whiteList = new ArrayList<String>();
		List<Module> moduleList = moduleServiceImpl.getFullModuleInfoListByProjectId(projectId);
		
		if(CollectionUtils.isNotEmpty(moduleList)){
			for(Module module: moduleList){
				List<Page> pageList = module.getPageList();
				if(CollectionUtils.isNotEmpty(pageList)){
					for(Page page: pageList){
						if(CollectionUtils.isNotEmpty(page.getInterfaceList())){
							for(Interface inter: page.getInterfaceList()){
								whiteList.add(this.getRelativeUrl(inter.getUrl()));
							}
						}
					}
				}
			}
		}
		
		return whiteList;
	}
	
	private void buildMockTemplate(Long interfaceId, StringBuilder json, ParamForm param, int index){
		boolean isArrayObject = false;
		int ARRAY_LENGTH = 1;
		
		
		if(param.getParamType().startsWith("array<") && !param.getParamType().equals("array<string>")
				&& !param.getParamType().equals("array<number>") && !param.getParamType().equals("array<boolean>")
				&& !param.getParamType().equals("array<int>") && !param.getParamType().equals("array<float>")
				&& !param.getParamType().equals("array<double>")){
			isArrayObject = true;
		}
		
		if(param.getDict() == null || param.hasMockJSData()){
			json.append(param.getMockJSIdentifier() + ":" + mockJsValue(param));
		}else{
			json.append(param.getMockJSIdentifier() + ":");
			String left = "{";
			String right = "}";
			
			if (isArrayObject) {
                left = "[";
                right = "]";
            }
			
			json.append(left);
			boolean first;
            for (int i = 0; i < ARRAY_LENGTH; i++) {
                first = true;
                if (isArrayObject && i > 0)
                    json.append(",");
                if (isArrayObject)
                    json.append("{");
                if(param.getDict() != null && param.getDict().getParams() != null){
                	for (ParamForm p : param.getDict().getParams()) {
                		//mock数据时隐藏部分字段
                		Long dictId = param.getDict().getDictId();
                		Long paramId = p.getParamId();
                		DictParamDisplay display = new DictParamDisplay();
                		display.setInterfaceId(interfaceId);
                		display.setDictId(dictId);
                		display.setParamId(paramId);
                		DictParamDisplay res = interfaceServiceImpl.queryDictParam(display);
                		if(res != null && res.getStatus()==0){
                			continue;
                		}
                		if (first) {
                			first = false;
                		} else {
                			json.append(",");
                		}
                		buildMockTemplate(interfaceId, json, p, i);
                	}
                }
                if (isArrayObject)
                    json.append("}");
            }
            json.append(right);
		}
		
		
		
	}
	
	private String mockJsValue(ParamForm param){
		String mockValue = param.getMock();
		
		if(StringUtils.isBlank(mockValue)){
			if(StringUtils.isBlank(param.getParamType()) || param.getParamType().equals("byte")){
				return "1";
			}else if(param.getParamType().equals("number")){
				return "123456";
			}else if(param.getParamType().equals("int") || param.getParamType().equals("short") 
					|| param.getParamType().equals("long")){
				return "123456";
			}else if(param.getParamType().equals("double") || param.getParamType().equals("float")){
				return "12.34";
			}else if(param.getParamType().equals("boolean")){
				return "true";
			}else if(param.getParamType().equals("string") || param.getParamType().equals("char")){
				return "\"测试内容12345\"";
			}else if(param.getParamType().equals("date")){
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return "\"" + df.format(new Date()) + "\"";
			}else if(param.getParamType().equals("array<boolean>")){
				return "[true, false]";
			}else if(param.getParamType().equals("array<string>") || param.getParamType().equals("array<char>")){
				return "[\"string1\", \"string2\", \"string3\", \"string4\", \"string5\"]";
			}else if(param.getParamType().equals("array<number>") || param.getParamType().equals("array<byte>")){
				return "[1, 2, 3, 4, 5]";
			}else if(param.getParamType().equals("array<int>") || param.getParamType().equals("array<long>")
					|| param.getParamType().equals("array<short>")){
				return "[1, 2, 3, 4, 5]";
			}else if(param.getParamType().equals("array<double>") || param.getParamType().equals("array<float>")){
				return "[11.11, 22.22, 33.33, 44.44, 55.55]";
			}else if(param.getParamType().startsWith("array<")){
				/*array<object>和array<自定义>*/
				return "[]";
			}else {
				/*Object和自定义*/
				return "{}";
			}
		}
		
		if(StringUtils.isNotBlank(mockValue)){
			if(mockValue.startsWith("[") && mockValue.endsWith("]")){
				return mockValue;
			}else if(mockValue.startsWith("function")){
				return mockValue;
			}else if(param.getParamType().equals("number") || param.getParamType().equals("boolean")
					|| param.getParamType().equals("int") || param.getParamType().equals("byte")
					|| param.getParamType().equals("short") || param.getParamType().equals("long")
					|| param.getParamType().equals("double") || param.getParamType().equals("float")
					|| param.getParamType().equals("char")){
				return mockValue;
			}else if(param.getParamType().equals("array<number>")
					|| param.getParamType().equals("array<boolean>")
					|| param.getParamType().equals("array<string>") || param.getParamType().equals("array<int>")
					|| param.getParamType().equals("array<byte>") || param.getParamType().equals("array<char>")
					|| param.getParamType().equals("array<long>") || param.getParamType().equals("array<double>")
					|| param.getParamType().equals("array<short>") || param.getParamType().equals("array<float>")){
				String retVal = mockValue;
				if(param.getParamType().equals("array<string>")
						&& !retVal.startsWith("\"") && !retVal.startsWith("'")){
					retVal = "\"" + retVal + "\"";
				}
				
				return "[" + retVal + "]";
			}else{
				return "\"" + mockValue + "\"";
			}
		}
		
		return "1";
	}
	
	/**
	 * 判断url是否合法
	 * @param pattern
	 * @return
	 */
	private boolean isPatternLegal(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return false;
        }

        String path = pattern;
        if (path.indexOf("/") == 0) {
            path = path.substring(1);
        }
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }
        if (path.isEmpty()) {
            return false;
        }

        return true;
    }
	
	/**
	 * 对比两个url的相对路径是否相同
	 * @param pattern
	 * @param requestUrl
	 * @return
	 */
	public boolean isRelativeUrlExactlyMatch(String pattern, String requestUrl) {
        if (pattern == requestUrl) return true;
        if (pattern == null || requestUrl == null) return false;

        return getRelativeUrl(pattern).equals(getRelativeUrl(requestUrl));
    }
	
	/**
	 * 获取url的相对路径
	 * @param url
	 * @return
	 */
	public String getRelativeUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        if (url.contains("https://")) {
            url = url.substring(url.indexOf("/", 8));
        } else if (url.contains("http://")) {
        	url = url.substring(url.indexOf("/", 7));
        }
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.charAt(0) != '/' && !url.startsWith("reg:")) {
            url = '/' + url;

        }
        return url;
    }
}
