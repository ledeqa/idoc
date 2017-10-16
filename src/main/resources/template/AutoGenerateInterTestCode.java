package XXX; // TODO 1.修改包名

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import XXX; // TODO 4.添加DataListProvider的包名
import com.netease.shockwave.http.HttpHandler;
import com.netease.shockwave.testng.ShockwaveTestNG;

/**
 * ${interfaceName!''}
 * @author Auto Generate
 */
public class AutoGenerateSmokeTestCode extends ShockwaveTestNG {
	
	public static Logger logger = Logger.getLogger(AutoGenerateSmokeTestCode.class);
	private static HttpHandler httpHandler = HttpHandler.getInstance();
	private static int retParamNum = 0;
	private static int retParamTypeCheckedNum = 0;
	
	@BeforeTest
	public void beforeTest() {
	}
	
	@AfterTest
	public void afterTest() {
	}
	
	@BeforeClass
	public void beforeClass() {
	}
	
	@AfterClass
	public void afterClass() {
	}
	
	/**
	 * ${interfaceName!''}
	 * 
	 <#if reqParamList??>
		 <#list reqParamList as req>
	 * @param  ${req.paramName!''}   ${req.paramDesc!''}
		 </#list>
	 </#if>
	 * @throws Exception
	 */
	@Test(dataProvider = "dataList", dataProviderClass = DataListProvider.class, description = "${interfaceName!''}")
	public void processDataList(
			<#if reqParamList??>
				<#list reqParamList as req>
					<#if !req_has_next>
			${req.paramType!''} ${req.paramName!''}
					<#else>
			${req.paramType!''} ${req.paramName!''}, 
					</#if>
				</#list>
			</#if>
	      ) throws Exception {
		String response = requestDataList(
				<#if reqParamList??>
					<#list reqParamList as req>
						<#if !req_has_next>
					${req.paramName!''}
						<#else>
					${req.paramName!''}, 
						</#if>
					</#list>
				</#if>);
		if(StringUtils.isEmpty(response)){
			logger.debug("Interface smoke test failed, the response is empty!");
		}else{
			<#if interfaceType??>
				<#if interfaceType == 1>
			// 接口请求类型为ajax
			Map<String, String> retParamTypeMap = getRetParamTypeMap();
			if(!retParamTypeMap.isEmpty()){
				retParamNum = 0;
				retParamTypeCheckedNum = 0;
				parseJsonData(JSONObject.fromObject(response), retParamTypeMap);
				
				logger.info("------------------------------Test success, the result is -------------------------------");
				logger.info("Request type: ajax");
				logger.info("Response body: \n" + response);
				logger.info("The response return params num: " + retParamNum + "  checked failed num: " + (retParamNum - retParamTypeCheckedNum));
				logger.info("------------------------------------------------------------------------------------------");
			}else{
				logger.info("------------------------------Test success, the result is -------------------------------");
				logger.info("Request type: ajax");
				logger.info("Response body: \n" + response);
				logger.info("------------------------------------------------------------------------------------------");
			}
				<#elseif interfaceType == 2>
			// 接口请求类型为ftl
			logger.info("------------------------------Test success, the result is -------------------------------");
			logger.info("Request type: ftl");
			logger.info("Response body: \n" + response);
			logger.info("------------------------------------------------------------------------------------------");
				</#if>
			</#if>
		}
		//TODO 5.如果有其他逻辑代码可以继续实现
	}
	
	// 解析测试时请求返回的json数据，并判断类型是否和接口文档一致
	private void parseJsonData(JSONObject json, Map<String, String> retParamTypeMap){
		JSONObject jsonObject = json;
		Iterator<?> iter = jsonObject.keys();
		while(iter.hasNext()){
			String key = (String) iter.next();
			Object value = jsonObject.get(key);
			if(value != null){
				if(value instanceof Integer || value instanceof Float || value instanceof Short 
						|| value instanceof Long || value instanceof Byte){
					retParamNum++;
					if(compareParamType(retParamTypeMap, key, "number")){
						retParamTypeCheckedNum++;
					}
					logger.info("type = Number  key = " + key + "   value = " + value);
				}else if(value instanceof Boolean){
					retParamNum++;
					if(compareParamType(retParamTypeMap, key, "boolean")){
						retParamTypeCheckedNum++;
					}
					logger.info("type = Boolean  key = " + key + "   value = " + value);
				}else if(value instanceof String){
					retParamNum++;
					if(compareParamType(retParamTypeMap, key, "string")){
						retParamTypeCheckedNum++;
					}
					logger.info("type = String  key = " + key + "   value = " + value);
				}else if(value instanceof Integer[] || value instanceof Float[] || value instanceof Short[] 
						|| value instanceof Long[] || value instanceof Byte[]){
					retParamNum++;
					if(compareParamType(retParamTypeMap, key, "array[number]")){
						retParamTypeCheckedNum++;
					}
					logger.info("type = Number[]  key = " + key + "   value = " + value);
				}else if(value instanceof Boolean[]){
					retParamNum++;
					if(compareParamType(retParamTypeMap, key, "array[boolean]")){
						retParamTypeCheckedNum++;
					}
					logger.info("type = Boolean[]  key = " + key + "   value = " + value);
				}else if(value instanceof String[]){
					retParamNum++;
					if(compareParamType(retParamTypeMap, key, "array[string]")){
						retParamTypeCheckedNum++;
					}
					logger.info("type = String[]  key = " + key + "   value = " + value);
				}else if(value instanceof JSONObject){
					logger.info("type = JSONObject  key = " + key + "   value = " + value);
					parseJsonData((JSONObject)value, retParamTypeMap);
				}else if(value instanceof JSONArray){
					logger.info("type = JSONArray  key = " + key + "   value = " + value);
					JSONArray jsonArray = jsonObject.getJSONArray(key);
					for(int i = 0; i < jsonArray.size(); i++){
						parseJsonData(jsonArray.getJSONObject(i), retParamTypeMap);
					}
				}else{
					retParamNum++;
					if(compareParamType(retParamTypeMap, key, "null")){
						retParamTypeCheckedNum++;
					}
					logger.info("type = null  key = " + key + "   value = " + value);
				}
			}
		 }
	}
	
	private boolean compareParamType(Map<String, String> retParamTypeMap, String key, String type){
		if(retParamTypeMap.containsKey(key + "0")){
			String paramType = retParamTypeMap.get(key + "0");
			if(paramType.equals(type) || type == null){
				return true;
			}
		}else if(retParamTypeMap.containsKey(key + "1")){
			String paramType = retParamTypeMap.get(key + "1");
			if(paramType.equals(type)){
				return true;
			}
		}
		return false;
	}
	
	private Map<String, String> getRetParamTypeMap(){
		Map<String, String> retParamTypeMap = new HashMap<String, String>();
		<#if retParamTypeList??>
			<#list retParamTypeList as ret>
				<#if ret??>
		retParamTypeMap.put("${ret.paramName!''}", "${ret.paramType!''}");
				</#if>
			</#list>
		</#if>
		return retParamTypeMap;
	}
	
	private String requestDataList(
			<#if reqParamList??>
				<#list reqParamList as req>
					<#if !req_has_next>
				${req.paramType!''} ${req.paramName!''}
					<#else>
				${req.paramType!''} ${req.paramName!''}, 
					</#if>
				</#list>
			</#if>){
		Map<String, String> paramMap = new HashMap<String, String>();
		<#if reqParamList??>
			<#list reqParamList as req>
		paramMap.put("${req.paramName!''}", String.valueOf(${req.paramName!''}));
			</#list>
		</#if>
		Map<String, String> headerMap = new HashMap<String, String>();
		// TODO 2.为了顺利通过登录验证，此处需填上SessionID，可以用自己账号登录后在浏览器调试工具请求Cookie中看到。
		headerMap.put("Cookie", "JSESSIONID=XXX"); 
		<#if interfaceRequsetType??>
			<#if interfaceRequsetType == 1>
		String res = httpHandler.usingGetMethod("${url!''}", paramMap, headerMap);
			<#elseif interfaceRequsetType == 2>
		String res = httpHandler.usingPostMethod("${url!''}", paramMap, headerMap);
			<#elseif interfaceRequsetType == 3>
		String res = httpHandler.usingPutMethod("${url!''}", paramMap, headerMap);
			<#else>
		String res = httpHandler.usingDeleteMethod("${url!''}", paramMap, headerMap);
			</#if>
		</#if>
		
		return res;
	}
}

// 3.将下面代码作为DataProvider类。
package XXX;

import org.testng.annotations.DataProvider;

public class DataListProvider {
	/**
	 * ${interfaceName!''} Data Provider
	 * 
	 <#if reqParamList??>
		 <#list reqParamList as req>
	 * @param  ${req.paramType!''} ${req.paramName!''}  ${req.paramDesc!''}
		 </#list>
	 </#if>
	 */
	@DataProvider(name = "dataList")
	public static Object[][] DataList() {
		return new Object[][]{
			<#if testDataList??>
				<#list testDataList as data>
					<#if !data_has_next>
				${data}
					<#else>
				${data}, 
					</#if>
				</#list>
			</#if>
		};
	}
}