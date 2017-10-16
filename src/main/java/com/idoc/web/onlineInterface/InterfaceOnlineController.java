package com.idoc.web.onlineInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.constant.CommonConstant;
import com.idoc.constant.LogConstant;
import com.idoc.dao.login.LoginDaoImpl;
import com.idoc.form.DictForm;
import com.idoc.form.OnlineInterfaceForm;
import com.idoc.model.Interface;
import com.idoc.model.InterfaceOnline;
import com.idoc.model.OnlineModule;
import com.idoc.model.OnlinePage;
import com.idoc.model.ProductModel;
import com.idoc.model.Role;
import com.idoc.model.UserModel;
import com.idoc.service.data.ExportInterInfo2WordServiceImpl;
import com.idoc.service.docManage.InterfaceServiceImpl;
import com.idoc.service.interfaceOnline.InterfaceOnlineService;
import com.idoc.service.interfaceOnline.ModuleOnlineService;
import com.idoc.service.login.LoginIndexServiceImpl;
import com.idoc.service.productAndProject.ProductAndProjectServiceImpl;
import com.idoc.service.role.RoleConfigManagementService;
import com.idoc.shiro.token.TokenManager;
import com.idoc.util.DataTypeCheckUtil;
import com.idoc.util.ErrorMessageUtil;
import com.idoc.util.LoginUserInfoUtil;
import com.netease.common.util.StringUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/idoc/onlineInter")
public class InterfaceOnlineController {

	@Autowired
	private ModuleOnlineService moduleOnlineService;
	@Autowired
	private InterfaceOnlineService interfaceOnlineService;
	@Autowired
	private RoleConfigManagementService roleConfigManagementService;
	@Autowired
	private LoginDaoImpl loginDaoImpl;
	@Autowired
	private InterfaceServiceImpl interfaceServiceImpl;
	@Autowired
	private ModuleOnlineService ModuleOnlineService;
	@Autowired
	private ProductAndProjectServiceImpl productAndProjectServiceImpl;
	@Autowired
	private ExportInterInfo2WordServiceImpl exportInterInfo2WordServiceImpl;
	@Autowired
	private LoginIndexServiceImpl loginIndexServiceImpl;

	@RequestMapping("index.html")
	public String index(String productId, ModelMap modelMap, String moduleName, String pageName, String urlName,
			String interType, String requestType, String form_productId){
		if(!DataTypeCheckUtil.isNumber(productId)){
			modelMap.put("exceptionMsg", "参数格式有误，请检查参数。");
			return "/datatypeError";
		}else{
			List<OnlineModule> onlineModules=moduleOnlineService.getFullOnlineModules(productId);
			if (onlineModules!=null) {
				onlineModules = filterOnlineInter(moduleName, pageName, urlName, interType, requestType, onlineModules);
				modelMap.put("modules", onlineModules);
				modelMap.put("productId", productId);
				modelMap.put("form_productId", productId);
				modelMap.put("currUser", LoginUserInfoUtil.getUserEnglishName());
				modelMap.put("moduleName", moduleName);
				modelMap.put("pageName", pageName);
				modelMap.put("urlName", urlName);
				modelMap.put("interType", interType);
				modelMap.put("requestType", requestType);
				UserModel currUser = TokenManager.getToken();
				if(currUser != null){
					modelMap.put("currUserId", currUser.getUserId());
				}
				ProductModel productModel = productAndProjectServiceImpl.queryProductModelByProductId(Long.valueOf(productId));
				if(productModel != null){
					modelMap.addAttribute("productName", productModel.getProductName());
				}
				//获取接口流程需要数据
				String englishName = LoginUserInfoUtil.getUserEnglishName();
				Role role = null;
				role = roleConfigManagementService.selectRoleByUserEnglishName(englishName, Long.parseLong(productId));
				String roleName = null;
				if(role != null || loginIndexServiceImpl.confirmAdmin(englishName) == 1){
					if(loginIndexServiceImpl.confirmAdmin(englishName) == 1){
						//判断如果当前用户时超级管理员，直接将role的值设置为“管理员”
						modelMap.addAttribute("role", CommonConstant.ROLE_ADMIN);
					}else{
						roleName = role.getRoleName();
						modelMap.addAttribute("role", roleName);
					}
				}else{
					ErrorMessageUtil.put("执行[editInterface]时，获取当前角色为空！");
				}
			}
		}
		return "idoc/onlineInter/index";
	}
	@RequestMapping("queryOnlineInters")
	@ResponseBody
	public Map<String, Object> queryOnlineInters(String productId){
		Map<String, Object> map = new HashMap<String, Object>();
		if (productId!=null) {
			List<OnlineModule> onlineModules=moduleOnlineService.getFullOnlineModules(productId);
			if(onlineModules != null && onlineModules.size() > 0){
				map.put("retCode", 200);
				map.put("retContent", onlineModules);
			}
		}else{
			map.put("retCode", -1);
		}
		return map;
	}
	private List<OnlineModule> filterOnlineInter(String moduleName,
			String pageName, String urlName, String interType,
			String requestType, List<OnlineModule> onlineModules) {
		if(StringUtils.isNotEmpty(moduleName)){
			List<OnlineModule> list = new ArrayList<OnlineModule>();
			for(int i=0; i<onlineModules.size(); i++){
				OnlineModule module = onlineModules.get(i);
				String name = module.getModuleName();
				if(!name.contains(moduleName)){
					list.add(module);
				}
			}
			onlineModules.removeAll(list);
		}
		if(StringUtils.isNotEmpty(pageName)){
			for(int i=0; i<onlineModules.size(); i++){
				OnlineModule module = onlineModules.get(i);
				List<OnlinePage> pageList = module.getPageList();
				List<OnlinePage> list = new ArrayList<OnlinePage>();
				if(pageList != null){
					for(OnlinePage page : pageList){
						String name = page.getPageName();
						if(!name.contains(pageName)){
							list.add(page);
						}
					}
				}
				pageList.removeAll(list);
			}
		}
		for(int i=0; i<onlineModules.size(); i++){
			OnlineModule module = onlineModules.get(i);
			List<OnlinePage> pageList = module.getPageList();
			if(pageList != null){
				for(OnlinePage page : pageList){
					List<InterfaceOnline> list = new ArrayList<InterfaceOnline>();
					List<InterfaceOnline> interList = page.getInterfaceList();
					if(interList != null){
						for(InterfaceOnline inter : interList){
							String name = inter.getInterfaceName();
							if(StringUtils.isNotEmpty(urlName) && !name.contains(urlName)){
								list.add(inter);
							}
							int reqType = inter.getRequestType();
							int interfaceType = inter.getInterfaceType();
							if(StringUtils.isNotEmpty(requestType)){
								if(!String.valueOf(reqType).equals(requestType) && !list.contains(module)){
									list.add(inter);
								}
							}
							if(StringUtils.isNotEmpty(interType)){
								if(!String.valueOf(interfaceType).equals(interType) && !list.contains(module)){
									list.add(inter);
								}
							}
						}
						interList.removeAll(list);
					}
				}
			}
		}
		/*  排除模块或页面下没有接口
		List<OnlineModule> moduleLists = new ArrayList<OnlineModule>();
		for(int i=0; i<onlineModules.size(); i++){
			OnlineModule onlineModule = onlineModules.get(i);
			List<OnlinePage> pageList = onlineModule.getPageList();
			List<OnlinePage> pages = new ArrayList<OnlinePage>();
			if(pageList != null){
				for(OnlinePage onlinePage : pageList){
					List<InterfaceOnline> interList = onlinePage.getInterfaceList();
					if(interList == null || interList.size()==0){
						pages.add(onlinePage);
					}
				}
				pageList.removeAll(pages);
			}
			if(pageList == null || pageList.size()==0){
				moduleLists.add(onlineModule);
			}
		}
		onlineModules.removeAll(moduleLists); */
		return onlineModules;
	}

	@RequestMapping("getByManyConditions.html")
	public String search(String productId, ModelMap modelMap, String moduleName, String pageName, String urlName,
			String interType, String requestType){
		if (productId!=null) {
			List<OnlineModule> onlineModules=moduleOnlineService.getFullOnlineModules(productId);
			if (onlineModules!=null) {
				onlineModules = filterOnlineInter(moduleName, pageName, urlName, interType, requestType, onlineModules);
				modelMap.put("modules", onlineModules);
				modelMap.put("productId", productId);
				modelMap.put("currUser", LoginUserInfoUtil.getUserEnglishName());
				UserModel currUser = TokenManager.getToken();
				if(currUser != null){
					modelMap.put("currUserId", currUser.getUserId());
				}
				//获取接口流程需要数据
				String englishName = LoginUserInfoUtil.getUserEnglishName();
				Role role = null;
				role = roleConfigManagementService.selectRoleByUserEnglishName(englishName, Long.parseLong(productId));
				String roleName = null;
				if(role != null){
					roleName = role.getRoleName();
					modelMap.put("role", roleName);
				}else{
					ErrorMessageUtil.put("执行[editInterface]时，获取当前角色为空！");
				}
			}
		}
		return "idoc/onlineInter/index";
	}

	@RequestMapping("queryOnlineInter.html")
	@ResponseBody
	public Map<String,Object> editInterface(String productId, String interfaceId, String pageId) {

		Map<String,Object> retMap = new HashMap<String,Object>();
		if(StringUtils.isBlank(interfaceId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("productId", Long.parseLong(productId));
			paramMap.put("onlineInterfaceId", Long.parseLong(interfaceId));
			paramMap.put("pageId", Long.parseLong(pageId));
			InterfaceOnline onlineinter = interfaceOnlineService.selectOnlineInterfaceByConditions(paramMap);
			Map<String, Object> interMap = new HashMap<String, Object>();
			interMap.put("onlineInterfaceId", Long.parseLong(interfaceId));
			Interface inter = interfaceServiceImpl.getInterfaceByOnlineId(interMap);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
				retMap.put("inter", onlineinter);
				if(inter != null){
					retMap.put("interInfo", inter);
				}
				if(onlineinter==null){
					retMap.put("retCode", 400);
					retMap.put("retDesc", "操作失败，没有获取到在线接口！");
					retMap.put("inter", null);
				}
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
	@RequestMapping("getModules.html")
	@ResponseBody
	public Map<String,Object> getModuleList(String productId) {

		Map<String,Object> retMap = new HashMap<String,Object>();
		if(StringUtils.isBlank(productId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			List<OnlineModule> moduleList = ModuleOnlineService.getOnlineModuleListNoInterByProductId(productId);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
				retMap.put("moduleList", moduleList);
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
	/**
	 * 移动接口
	 * @param onlineInterfaceId 在线接口id
	 * @param onlinePageId 目标在线页面id
	 * @return
	 */
	@RequestMapping("move.html")
	@ResponseBody
	public Map<String,Object> moveInterface(String onlineInterfaceId,String onlinePageId) {

		Map<String,Object> retMap = new HashMap<String,Object>();
		if(StringUtils.isBlank(onlinePageId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			interfaceOnlineService.moveInterface(Long.valueOf(onlineInterfaceId), Long.valueOf(onlinePageId));
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
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
	@RequestMapping("add.html")
	@ResponseBody
	public Map<String,Object> insertOnlineInterface(HttpServletRequest request) {

		Map<String,Object> retMap = new HashMap<String,Object>();
		InterfaceOnline ret = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			LogConstant.runLog.info(sb.toString());
			String s = sb.toString();
			JSONObject json = JSONObject.fromObject(s);
			InterfaceOnline interfaceOnline = (InterfaceOnline) JSONObject.toBean(json, DictForm.class);
			ret = interfaceOnlineService.insertOnlineInterface(interfaceOnline);

		} catch (IOException e) {
			LogConstant.debugLog.error(e);
		}
		if (ret.getInterfaceId() != null) {
			retMap.put("retCode", 200);
		} else {
			retMap.put("retCode", -1);
		}

		return retMap;
	}

	@RequestMapping("getByProduct.html")
	@ResponseBody
	public Map<String,Object> selectOnlineInterfaceListByProduct(@RequestParam(value="url",required=false) String url,
				@RequestParam(value="interfaceId",required=false) String interfaceId,
				@RequestParam("productId") String productId) {

		Map<String,Object> retMap = new HashMap<String,Object>();
		if (StringUtils.isBlank(url) || StringUtils.isBlank(interfaceId) || StringUtils.isBlank(productId)) {
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		List<InterfaceOnline> interfaceOnlines = interfaceOnlineService.selectOnlineInterfaceListByProductByPage(url, Long.valueOf(interfaceId.trim()), Long.valueOf(productId.trim()));
		if (interfaceOnlines != null && interfaceOnlines.size() > 0) {
			retMap.put("retCode", 200);
			retMap.put("retContent", interfaceOnlines);
		} else {
			retMap.put("retCode", -1);
		}
		return retMap;
	}

	@RequestMapping("getDetail.html")
	public String selectOnlineInterfaceById(@RequestParam("interfaceId") String interfaceId, ModelMap modelMap) {

		if (interfaceId != null) {
			InterfaceOnline interfaceOnline = new InterfaceOnline();
			interfaceOnline = interfaceOnlineService.selectOnlineInterfaceById(Long.valueOf(interfaceId.trim()));

			if (interfaceOnline != null) {
				modelMap.put("interfaceOnline", interfaceOnline);
			}
			return "/idoc/onlineInterfaceDetial";
		} else {
			return "/fail";
		}

	}

	@RequestMapping("edit.html")
	@ResponseBody
	public Map<String,Object> updateOnlineInterface(HttpServletRequest request) {

		Map<String,Object> retMap = new HashMap<String,Object>();
		BufferedReader in;
		int ret = 0;
		try {
			in = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			LogConstant.runLog.info(sb.toString());
			String s = sb.toString();
			JSONObject json = JSONObject.fromObject(s);
			InterfaceOnline interfaceOnline = (InterfaceOnline) JSONObject.toBean(json, DictForm.class);
			if (interfaceOnline != null) {
				ret = interfaceOnlineService.updateOnlineInterface(interfaceOnline);
			}

		} catch (IOException e) {
			LogConstant.debugLog.error(e);
		}
		if (ret > 0) {
			retMap.put("retCode", 200);
		} else {
			retMap.put("retCode", -1);
		}

		return retMap;
	}

	@RequestMapping("update.html")
	@ResponseBody
	public Map<String,Object> updateOnlineInterface(@RequestBody OnlineInterfaceForm form) {

		Map<String,Object> retMap = new HashMap<String,Object>();
		if(form == null){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			String error = ErrorMessageUtil.getErrorMessages();
			InterfaceOnline interfaceOnline = interfaceOnlineService.saveOnlineInterfaceInfor(form);
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");

				retMap.put("inter", interfaceOnline);
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
	@RequestMapping("delete.html")
	@ResponseBody
	public Map<String,Object> deleteInterface(String interfaceId) {

		Map<String,Object> retMap = new HashMap<String,Object>();

		if(StringUtils.isBlank(interfaceId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			//Long id = Long.valueOf(interfaceId);
			//interfaceServiceImpl.removeInterfaceInfor(id);
			int retCode = interfaceOnlineService.deleteOnlineInterface(Long.parseLong(interfaceId));
			//String error = ErrorMessageUtil.getErrorMessages();
			if(retCode > 0){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
			}else{
				retMap.put("retCode", 402);
				retMap.put("retDesc", "接口删除失败！");
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

	@RequestMapping("/exportInterInfo2Word.html")
	public ResponseEntity<byte[]> download(HttpServletRequest request,
			@RequestParam("interfaceIds") String interfaceIds,
			@RequestParam("createFileOnServer") boolean createFileOnServer) throws IOException {
		if(StringUtil.isEmpty(interfaceIds)){
			return new ResponseEntity<byte[]>("请求的参数为空，生成接口信息文档失败！".getBytes("GBK"), HttpStatus.CREATED);
		}
		String[] ids = interfaceIds.split(",");
		if(ids.length <= 0){
			return new ResponseEntity<byte[]>("没有选定要导出的接口，生成接口信息文档失败！".getBytes("GBK"), HttpStatus.CREATED);
		}
		List<String> interfaceIdList = Arrays.asList(ids);
		byte[] flieByteStream = null;
		String path = null;
		if(createFileOnServer){ // 在服务器端生成文件
			path = exportInterInfo2WordServiceImpl.exportInterInfo2Word(interfaceIdList);
			if(StringUtil.isEmpty(path)){
				return new ResponseEntity<byte[]>("后台自动生成接口信息文档失败，请联系管理员！".getBytes("GBK"), HttpStatus.CREATED);
			}
			File file = new File(path);
			flieByteStream = FileUtil.readAsByteArray(file);
		}else{
			flieByteStream = exportInterInfo2WordServiceImpl.exportInterInfo2ByteStream(interfaceIdList);
		}
		if(flieByteStream == null){
			return new ResponseEntity<byte[]>("后台自动生成接口信息文档失败，请联系管理员！".getBytes("GBK"), HttpStatus.CREATED);
		}
        HttpHeaders headers = new HttpHeaders();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String name = "接口信息文档-" + sdf.format(new Date()) + ".doc";
        String fileName = new String(name.getBytes("UTF-8"),"iso-8859-1"); //为了解决中文名称乱码问题
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(flieByteStream, headers, HttpStatus.CREATED);
	}

}
