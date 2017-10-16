package com.idoc.web.mock;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;

import com.idoc.constant.LogConstant;
import com.idoc.constant.ThreadLocalConstant;
import com.idoc.dao.docManage.ParamDaoImpl;
import com.idoc.form.DictForm;
import com.idoc.form.InterfaceForm;
import com.idoc.form.ParamForm;
import com.idoc.model.Dict;
import com.idoc.model.Interface;
import com.idoc.model.LoginUserInfo;
import com.idoc.model.MockRule;
import com.idoc.model.Param;
import com.idoc.service.dict.DictServiceImpl;
import com.idoc.service.docManage.InterfaceServiceImpl;
import com.idoc.service.mock.MockRuleService;
import com.idoc.service.mock.MockService;
import com.idoc.util.HTTPUtils;
import com.idoc.web.BaseController;

@Controller
public class MockDataController extends BaseController {
	
	@Autowired
	private MockService mockService;
	
	@Autowired
	private MockRuleService mockRuleService;
	
	@Autowired
	InterfaceServiceImpl interfaceServiceImpl;
	
	@Autowired
	private ParamDaoImpl paramDaoImpl;
	
	@Autowired
	private DictServiceImpl dictServiceImpl;
	
	@RequestMapping("/idoc/mock/queryMockData.html")
	@ResponseBody
	public Map<String, Object> queryMockjsData(@RequestBody InterfaceForm form){
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		if(form == null){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		try {
			LoginUserInfo user = ThreadLocalConstant.sessionTL.get();
			if(user != null){
				String englishName = user.getEnglishName();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("englishName", englishName);
				map.put("interfaceId", form.getInterfaceId());
				MockRule mockRule = mockRuleService.getMockRuleByCond(map);
				if(mockRule != null){
					retMap.put("retCode", 200);
					retMap.put("mockType", "person");
					retMap.put("mockData", mockRule.getMockRule());
					return retMap;
				}
			}
			
			String mockData = mockService.getMockJsRule(form.getInterfaceId(), form.getRetParams());
			retMap.put("retCode", 200);
			retMap.put("mockType", "common");
			retMap.put("mockData", mockData);
			
//			String reqMockData = mockService.getMockJsRule(form.getReqParams());
//			retMap.put("reqMockData", reqMockData);
//			retMap.put("uuid1", UUID.randomUUID().toString());
//			retMap.put("uuid2", UUID.randomUUID().toString());
		} catch (Exception e) {
			LogConstant.runLog.error("异常错误",e);
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
			return retMap;
		}
		
		return retMap;
	}
	
	@RequestMapping("/idoc/mock/saveUserMockRule.html")
	@ResponseBody
	public Map<String, Object> saveUserMockRule(@RequestParam("interfaceId")String interfaceId,
			                                    @RequestParam("mockRule")String mockRuleStr){
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		if(interfaceId == null){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		LoginUserInfo user = ThreadLocalConstant.sessionTL.get();
		if(user != null ){
			String englishName = user.getEnglishName();
			MockRule mockRule = new MockRule();
			mockRule.setEnglishName(englishName);
			mockRule.setInterfaceId(Long.parseLong(interfaceId));
			mockRule.setMockRule(mockRuleStr);
			int result = mockRuleService.saveMockRule(mockRule);
			if(result == 1){
				retMap.put("retCode", 200);
			}else{
				retMap.put("retCode", 500);
			}
		}else{
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
		}
		
		return retMap;
	}
	
	
	@RequestMapping("/idoc/mock/removeUserMockRule.html")
	@ResponseBody
	public Map<String, Object> removeUserMockRule(@RequestParam("interfaceId")String interfaceId){
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		if(interfaceId == null){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		LoginUserInfo user = ThreadLocalConstant.sessionTL.get();
		if(user != null ){
			String englishName = user.getEnglishName();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("englishName", englishName);
			map.put("interfaceId", interfaceId);
			int result = mockRuleService.deleteMockRule(map);
			if(result == 1){
				retMap.put("retCode", 200);
				
				//获取公共的数据
				List<Param> resList = paramDaoImpl.selectReturnParamByInterfaceId(Long.parseLong(interfaceId));
				if(resList != null && !resList.isEmpty()){
					ParamForm[] returnParams = new ParamForm[resList.size()];
					int i = 0;
					for(Param pm: resList){
						ParamForm paramForm = new ParamForm();
						if(pm.getDictId() != null){
							pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
							paramForm = getParamFormDict(paramForm, pm);
							
						}else{
							paramForm.setParamId(pm.getParamId());
							paramForm.setParamName(pm.getParamName());
							paramForm.setParamType(pm.getParamType());
							paramForm.setMock(pm.getMock());
						}
						returnParams[i] = paramForm;
						i++;
					}
				String mockJsRule = mockService.getMockJsRule(Long.parseLong(interfaceId), returnParams);
				retMap.put("mockRule", mockJsRule);
				}else{
					retMap.put("mockRule", "{}");
				}
			}else{
				retMap.put("retCode", 500);
			}
		}else{
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
		}
		
		return retMap;
	}
	
	@RequestMapping("/mock/getMockData.html")
	public void getMockData(HttpServletResponse response, 
			                @RequestParam(value="userName", required=false, defaultValue="")String userName,
			                @RequestParam("requestUrl")String requestUrl,
			                @RequestParam(value="dateConvert", required=false,defaultValue="false") String dateConvert){
		String mockData = "";
		List<Long> interfaceIds = interfaceServiceImpl.selectInterfaceIdByRequestUrl(requestUrl);
		if(interfaceIds == null || interfaceIds.isEmpty()){
			response.setStatus(404);
		}else{
			Long interfaceId;
			if(interfaceIds.size() >1){
				interfaceId = 1l;
				@SuppressWarnings("deprecation")
				Timestamp s=new Timestamp(100, 1, 1, 1, 1, 1, 1);
				for(Long id : interfaceIds){
					Interface inter = interfaceServiceImpl.getInterfaceInfo(id);
					Timestamp ts = inter.getCreateTime();
					if(ts.after(s)){
						s = ts;
						interfaceId = inter.getInterfaceId();
					}
				}
			}else{
				interfaceId = interfaceIds.get(0);
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("englishName", userName);
			map.put("interfaceId", interfaceId);
			MockRule mockRule = mockRuleService.getMockRuleByCond(map);

			List<Param> resList_0 = null;
			List<String> needConvert = new ArrayList<String>();
			if("true".equals(dateConvert)){
				resList_0 = paramDaoImpl.selectReturnParamByInterfaceId(interfaceId);
				StringBuilder ss = new StringBuilder();
				for(Param pm: resList_0){
					if(pm.getDictId() != null){
						pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
						getParamsNotDictFromDict(needConvert,pm.getDict());
					}else{
						if("date".equals(pm.getParamType())){
							needConvert.add("\"" + pm.getParamName() + "\"");
						}
					}
				}
			}
			if(mockRule != null){
				String path = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/");
				mockData = mockService.getMockJsData(path,mockRule.getMockRule());
			}else{
				//获取公共的数据
				List<Param> resList;
				if("true".equals(dateConvert)){
					resList = resList_0;
				}else{
					resList = paramDaoImpl.selectReturnParamByInterfaceId(interfaceId);
				}
				
				if(resList != null && !resList.isEmpty()){
					ParamForm[] returnParams = new ParamForm[resList.size()];
					int i = 0;
					for(Param pm: resList){
						ParamForm paramForm = new ParamForm();
						if(pm.getDictId() != null){
							pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
							paramForm = getParamFormDict(paramForm, pm);
							
						}else{
							paramForm.setParamId(pm.getParamId());
							paramForm.setParamName(pm.getParamName());
							paramForm.setParamType(pm.getParamType());
							paramForm.setMock(pm.getMock());
						}
						returnParams[i] = paramForm;
						i++;
					}
					
					String mockJsRule = mockService.getMockJsRule(interfaceId, returnParams);
					String path = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/");
					mockData = mockService.getMockJsData(path,mockJsRule);
				}else{
					mockData= "{}";
				}
	
			}
			
			for(String need: needConvert){
				//d = Date();    "firstDate = new Date(" + d.getTime()+ ")"
				Date d = new Date();
				String date = "new Date(" + d.getTime()+ ")";
				int indexNumber = mockData.indexOf(need);
				if(indexNumber > 0){
					String str1 = mockData.substring(0, indexNumber + need.length() + 1);
					String str2 = mockData.substring(indexNumber + need.length() + 3);
					int ind = str2.indexOf("\"");
					String str3 = str2.substring(ind + 1);
					mockData = str1 + date + str3;
				}
			}
			//mockData = mockData.replaceAll( "\\s", "");
			//mockData = mockData.replaceAll( "newDate", "new Date");
			
		}

        response.setCharacterEncoding("UTF-8"); //设置编码格式
        response.setContentType("text/json");   //设置数据格式
        PrintWriter out = null;
		try {
			out = response.getWriter();
	        out.print(mockData); //将mock数据写入流中
	        out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if (out != null) {
                out.close();
            }
        }
	}
	
	@RequestMapping("/mock/getMockDataNew.html")
	public void getMockDataNew(HttpServletResponse response, 
			                @RequestParam(value="userName", required=false, defaultValue="")String userName,
			                @RequestParam("requestUrl")String requestUrl,
			                @RequestParam(value="dateConvert", required=false,defaultValue="false") String dateConvert,
			                @RequestParam(required=false) Integer interfaceType){
		String mockData = "";
		List<Long> interfaceIds = interfaceServiceImpl.selectInterfaceIdByRequestUrl(requestUrl);
		if(interfaceIds == null || interfaceIds.isEmpty()){
			response.setStatus(404);
		}else{
			Long interfaceId;
			if(interfaceIds.size() >1){
				interfaceId = 1l;
				@SuppressWarnings("deprecation")
				Timestamp s=new Timestamp(100, 1, 1, 1, 1, 1, 1);
				for(Long id : interfaceIds){
					Interface inter = interfaceServiceImpl.getInterfaceInfo(id);
					Timestamp ts = inter.getCreateTime();
					if(ts.after(s)){
						s = ts;
						interfaceId = inter.getInterfaceId();
					}
				}
			}else{
				interfaceId = interfaceIds.get(0);
			}
			
			/**
			 * 对于ftl类型的接口，该方法直接返回ftl模板的路径
			 * */
			if(interfaceType != null && interfaceType == 2) {
				Interface inter = interfaceServiceImpl.getInterfaceInfo(interfaceId);
				Integer type = inter.getInterfaceType();
				String content = "";
				if(type == null || type != 2) {
					content = "请求ftl模板路径的接口，类型不是ftl";
				} else {
					content = inter.getFtlTemplate();
				}
				response.setCharacterEncoding("UTF-8"); //设置编码格式
		        response.setContentType("text/json");   //设置数据格式
		        PrintWriter out = null;
				try {
					out = response.getWriter();
			        out.print(content); //将数据写入流中
			        out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
		            if (out != null) {
		                out.close();
		            }
		        }
				return;
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("englishName", userName);
			map.put("interfaceId", interfaceId);
			MockRule mockRule = mockRuleService.getMockRuleByCond(map);

			List<Param> resList_0 = null;
			List<String> needConvert = new ArrayList<String>();
			if("true".equals(dateConvert)){
				resList_0 = paramDaoImpl.selectReturnParamByInterfaceId(interfaceId);
				StringBuilder ss = new StringBuilder();
				for(Param pm: resList_0){
					if(pm.getDictId() != null){
						pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
						getParamsNotDictFromDict(needConvert,pm.getDict());
					}else{
						if("date".equals(pm.getParamType())){
							needConvert.add("\"" + pm.getParamName() + "\"");
						}
					}
				}
			}
			if(mockRule != null){
				String path = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/");
				mockData = mockService.getMockJsData(path,mockRule.getMockRule());
			}else{
				//获取公共的数据
				List<Param> resList;
				if("true".equals(dateConvert)){
					resList = resList_0;
				}else{
					resList = paramDaoImpl.selectReturnParamByInterfaceId(interfaceId);
				}
				
				if(resList != null && !resList.isEmpty()){
					ParamForm[] returnParams = new ParamForm[resList.size()];
					int i = 0;
					for(Param pm: resList){
						ParamForm paramForm = new ParamForm();
						if(pm.getDictId() != null){
							pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
							paramForm = getParamFormDict(paramForm, pm);
							
						}else{
							paramForm.setParamId(pm.getParamId());
							paramForm.setParamName(pm.getParamName());
							paramForm.setParamType(pm.getParamType());
							paramForm.setMock(pm.getMock());
						}
						returnParams[i] = paramForm;
						i++;
					}
					
					String mockJsRule = mockService.getMockJsRule(interfaceId, returnParams);
					String path = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/");
					mockData = mockService.getMockJsData(path,mockJsRule);
				}else{
					mockData= "{}";
				}
	
			}
			
			for(String need: needConvert){
				//d = Date();    "firstDate = new Date(" + d.getTime()+ ")"
				Date d = new Date();
				String date = "new Date(" + d.getTime()+ ")";
				int indexNumber = mockData.indexOf(need);
				if(indexNumber > 0){
					String str1 = mockData.substring(0, indexNumber + need.length() + 1);
					String str2 = mockData.substring(indexNumber + need.length() + 3);
					int ind = str2.indexOf("\"");
					String str3 = str2.substring(ind + 1);
					mockData = str1 + date + str3;
				}
			}
			//mockData = mockData.replaceAll( "\\s", "");
			//mockData = mockData.replaceAll( "newDate", "new Date");
			
		}

        response.setCharacterEncoding("UTF-8"); //设置编码格式
        response.setContentType("text/json");   //设置数据格式
        PrintWriter out = null;
		try {
			out = response.getWriter();
	        out.print(mockData); //将mock数据写入流中
	        out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if (out != null) {
                out.close();
            }
        }
	}
	

	@RequestMapping("/idoc/mock/queryReqMockData.html")
	@ResponseBody
	public Map<String, Object> queryReqMockData(@RequestBody InterfaceForm form){
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		if(form == null){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		try{
			String reqMockData = mockService.getMockJsRule(form.getInterfaceId(), form.getReqParams());
			retMap.put("retCode", 200);
			retMap.put("reqMockData", reqMockData);
			retMap.put("uuid1", UUID.randomUUID().toString());
			retMap.put("uuid2", UUID.randomUUID().toString());
		} catch (Exception e) {
			LogConstant.runLog.error("异常错误",e);
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
			return retMap;
		}
		
		return retMap;
	}
	
	@RequestMapping("/idoc/mock/requestOnServer.html")
	@ResponseBody
	public Map<String, Object> requestOnServer(@RequestParam("url") String url){
		Map<String, Object> retMap = new HashMap<String, Object>();
		try{
			String responseText = HTTPUtils.sendGet(url);
			retMap.put("retCode", 200);
			retMap.put("responseText", responseText);
		}catch(Exception e){
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
		}
		
		return retMap;
	}
	
	@RequestMapping("/mock/createRule.html")
	public String createMockRule(@RequestParam("pRojEctId")String projectId,
								@RequestParam("pattern")String pattern,
								@RequestParam(value = "_c", required = false) String _c,
								@RequestParam(value = "callback", required = false) String callback, Model model){
		String mockRule = mockService.getMockJsRuleByProjectAndPattern(null, projectId, pattern);
		
		if(StringUtils.isNotBlank(_c)){
			mockRule = _c + "(" + mockRule + ")";
		}else if(StringUtils.isNotBlank(callback)){
			mockRule = callback + "(" + mockRule + ")";
		}
		
		model.addAttribute("content", mockRule);
		
		return "/tester/mockRule";
	}
	
	@RequestMapping("/mock/createMockData.html")
	public String createMockData(@RequestParam("pRojEctId")String projectId,
								@RequestParam("pattern")String pattern, Model model){
		String mockRule = mockService.getMockJsRuleByProjectAndPattern(null, projectId, pattern);
		String path = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/");
		
		String mockData = mockService.getMockJsData(path, mockRule);
		model.addAttribute("content", mockData);
		
		return "/tester/mockRule";
	}
	
	@RequestMapping("/mock/createPluginScript.html")
	public String createPluginScript(@RequestParam("projectId") String projectId, Model model){
		List<String> whiteList = mockService.getWhiteList(projectId);
		
		model.addAttribute("whiteList", whiteList);
		model.addAttribute("projectId", projectId);
		
		return "/tester/createPluginScript";
	}
	
	public ParamForm getParamFormDict(ParamForm paramForm, Param pm){
		
		paramForm.setParamId(pm.getParamId());
		paramForm.setParamName(pm.getParamName());
		paramForm.setParamType(pm.getParamType());
		paramForm.setMock(pm.getMock());
		paramForm.setDictId(pm.getDictId());
		Dict dict = pm.getDict();
		if(dict != null){
			DictForm dictForm = new DictForm();
			dictForm.setDictId(dict.getDictId());
			dictForm.setDictName(dict.getDictName());
			if(dict.getParams()!=null && dict.getParams().size()>0){
				List<Param> params = dict.getParams();
				ParamForm[] returnParams = new ParamForm[params.size()];
				int i = 0;
				for(Param param : params){
					ParamForm paramForm1 = new ParamForm();
					if(param.getDictId() != null && param.getDict() != null){
						paramForm1 = getParamFormDict(paramForm1, param);	
					}else{
						paramForm1.setParamId(param.getParamId());
						paramForm1.setParamName(param.getParamName());
						paramForm1.setParamType(param.getParamType());
						paramForm1.setMock(param.getMock());
					}
					returnParams[i] = paramForm1;
					i++;
				}
				dictForm.setParams(returnParams);
			}
			paramForm.setDict(dictForm);
		}
		
		return paramForm;
	}
	
	
	private void getParamsNotDictFromDict(List<String> needConvert, Dict dict) {
		List<Param> params = dict.getParams();
		if(params.size() > 0){
			for(Param param: params){
				if(param.getDict()==null){
					if("date".equals(param.getParamType())){
						needConvert.add("\"" + param.getParamName() + "\"");
					}
				}else{
					getParamsNotDictFromDict(needConvert, param.getDict());
				}
			}
		}
	}
}
