package com.idoc.web.docManage;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
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
import com.idoc.dao.productAndProject.ProjectDaoImpl;
import com.idoc.form.InterfaceForm;
import com.idoc.memcache.MemcacheServer;
import com.idoc.model.Dict;
import com.idoc.model.DictParamDisplay;
import com.idoc.model.Interface;
import com.idoc.model.Module;
import com.idoc.model.Page;
import com.idoc.model.ProductModel;
import com.idoc.model.ProjectModel;
import com.idoc.model.ProjectModuleModel;
import com.idoc.model.Redis;
import com.idoc.model.Role;
import com.idoc.model.UserModel;
import com.idoc.service.data.AutoGenerateInterTestCodeServiceImpl;
import com.idoc.service.docManage.InterfaceServiceImpl;
import com.idoc.service.docManage.ModuleServiceImpl;
import com.idoc.service.login.LoginIndexServiceImpl;
import com.idoc.service.productAndProject.ProductAndProjectServiceImpl;
import com.idoc.service.role.RoleConfigManagementService;
import com.idoc.shiro.token.TokenManager;
import com.idoc.util.CheckContent;
import com.idoc.util.DataTypeCheckUtil;
import com.idoc.util.ErrorMessageUtil;
import com.idoc.util.LoginUserInfoUtil;
import com.idoc.web.BaseController;
import com.netease.common.util.StringUtil;

@Controller
@RequestMapping("/idoc/inter")
public class InterfaceController extends BaseController {
	
	@Autowired
	private InterfaceServiceImpl interfaceServiceImpl;
	
	@Autowired
	private ModuleServiceImpl moduleServiceImpl;
	
	@Autowired
	private ProjectDaoImpl projectDaoImpl;
	
	@Autowired
	private LoginDaoImpl loginDaoImpl;
	
	@Autowired
	private RoleConfigManagementService roleConfigManagementService;
	
	@Autowired
	private ProductAndProjectServiceImpl productAndProjectServiceImpl;
	
	@Autowired
	private LoginIndexServiceImpl loginIndexServiceImpl;
	
	@Autowired
	private AutoGenerateInterTestCodeServiceImpl autoGenerateInterTestCodeServiceImpl;
	
	@Autowired
	private MemcacheServer memcacheServer;
	
	@RequestMapping("index.html")
	public String index(@RequestParam(value = "projectId", required = false)String project_Id, @RequestParam(value = "interfaceId", required = false)String interfaceId, ModelMap model,
			String responsible, String interName, String interUrl, String requestType){
		LogConstant.debugLog.info("projectId is:" + project_Id + ", interfaceId is: " + interfaceId);
		boolean flag = false;
		if(interfaceId != null){
			flag = DataTypeCheckUtil.isNumber(project_Id, interfaceId);
		}else{
			flag = DataTypeCheckUtil.isNumber(project_Id);
		}
		if(!flag){
			LogConstant.debugLog.info("传递的参数格式有误。");
			model.put("exceptionMsg", "参数格式有误，请检查参数。");
			return "/datatypeError";
		}
		Long projectId = Long.parseLong(project_Id);
		List<Module> modules = moduleServiceImpl.getFullModuleInfoListByProjectId(projectId.toString());
		if(modules!=null){
			if(StringUtils.isNotEmpty(responsible)||StringUtils.isNotEmpty(interName)||StringUtils.isNotEmpty(interUrl)||StringUtils.isNotEmpty(requestType)){
				modules = filterInter(responsible,interName,interUrl,requestType,modules);
				model.put("responsible", responsible);
				model.put("interName", interName);
				model.put("interUrl", interUrl);
				model.put("requestType", requestType);
			}
			model.put("modules", modules);
		}
		
		if(StringUtils.isNotBlank(interfaceId)){
			model.addAttribute("interfaceId", interfaceId);
		}
		model.addAttribute("currUser", LoginUserInfoUtil.getUserEnglishName());
		UserModel currUser = TokenManager.getToken();
		if(currUser != null){
			model.addAttribute("currUserId", currUser.getUserId());
			model.addAttribute("currUserName", currUser.getNickName());
		}
		//获取接口流程需要数据
		String englishName = LoginUserInfoUtil.getUserEnglishName();
		ProjectModel project = productAndProjectServiceImpl.selectProjectModelByProjectId(Long.valueOf(projectId));
		Role role = null;
		if(project != null){
			role = roleConfigManagementService.selectRoleByUserEnglishName(englishName, project.getProductId());
		}else{
			ErrorMessageUtil.put("执行[editInterface]时，获取接口对应项目为空！");
		}
		String roleName = null;
		if(role != null || loginIndexServiceImpl.confirmAdmin(englishName) == 1){
			if(loginIndexServiceImpl.confirmAdmin(englishName) == 1){
				//判断如果当前用户时超级管理员，直接将role的值设置为“管理员”
				model.addAttribute("role", CommonConstant.ROLE_ADMIN);
			}else{
				roleName = role.getRoleName();
				model.addAttribute("role", roleName);
			}
		}else{
			ErrorMessageUtil.put("执行[editInterface]时，获取当前角色为空！");
		}
		Integer productFlow=0;
		ProjectModel projectModel = projectDaoImpl.selectProjectModelByProjectId(projectId);
		if(projectModel != null){
			Long productId=projectModel.getProductId();
			ProductModel productModel = productAndProjectServiceImpl.queryProductModelByProductId(productId);
			productFlow= productAndProjectServiceImpl.getProductFlowById(productId);
			if(productModel != null){
				model.addAttribute("productDomainUrl", productModel.getProductDomainUrl());
				model.addAttribute("productName", productModel.getProductName());
			}
			model.addAttribute("projectName", projectModel.getProjectName());
			model.addAttribute("projectId", projectId);
			model.addAttribute("productId", projectModel.getProductId());
		}
		if(productFlow==1){			
			return "/interface/index";
		}else{
			return "/interface/indexNew";
		}
	}

	
	private List<Module> filterInter(String responsible, String interName,
			String interUrl, String requestType, List<Module> modules) {
		String userId = null;
		if(StringUtils.isNotEmpty(responsible)){
			userId = loginDaoImpl.queryUserIdByNickName(responsible);
		}
		for(int i=0;modules!=null&&i<modules.size();i++){
			Module module = modules.get(i);
			List<Page> pageList = module.getPageList();
			for(int j=0;pageList!=null&&j<pageList.size();j++){
				Page page = pageList.get(j);
				List<Interface> interList = page.getInterfaceList();
				List<Interface> list = new ArrayList<>();
				for(int k=0;interList!=null&&k<interList.size();k++){
					Interface inter = interList.get(k);
					if(StringUtils.isNotEmpty(userId)){
						List<String> userIds = new ArrayList<>();
						Long creatorId = inter.getCreatorId();
						String reqUserIds = inter.getReqPeopleIds();
						String frontUserIds = inter.getFrontPeopleIds();
						String behindUserIds = inter.getBehindPeopleIds();
						String clientUserIds = inter.getClientPeopleIds();
						String testUserIds = inter.getTestPeopleIds();
						userIds.add(creatorId.toString());
						List<String> ids;
						if(StringUtils.isNotEmpty(reqUserIds)){
							String[] reqIds = reqUserIds.split(",");
							ids = Arrays.asList(reqIds);
							userIds.addAll(ids);
						}
						if(StringUtils.isNotEmpty(frontUserIds)){
							String[] frontIds = frontUserIds.split(",");
							ids = Arrays.asList(frontIds);
							userIds.addAll(ids);
						}
						if(StringUtils.isNotEmpty(behindUserIds)){
							String[] behindIds = behindUserIds.split(",");
							ids = Arrays.asList(behindIds);
							userIds.addAll(ids);
						}
						if(StringUtils.isNotEmpty(clientUserIds)){
							String[] clientIds = clientUserIds.split(",");
							ids = Arrays.asList(clientIds);
							userIds.addAll(ids);
						}
						if(StringUtils.isNotEmpty(testUserIds)){
							String[] testIds = testUserIds.split(",");
							ids = Arrays.asList(testIds);
							userIds.addAll(ids);
						}
						int flag = 0;
						for(String user : userIds){
							if(userId.equals(user)){
								flag = 1;
								break;
							}
						}
						if(flag==0){
							list.add(inter);
							continue;
						}
					}
					if(StringUtils.isNotEmpty(interName)&&!inter.getInterfaceName().contains(interName)){
						list.add(inter);
						continue;
					}
					if(StringUtils.isNotEmpty(interUrl)&&!inter.getUrl().contains(interUrl)){
						list.add(inter);
						continue;
					}
					if(StringUtils.isNotEmpty(requestType)&&!String.valueOf(inter.getRequestType()).equals(requestType)){
						list.add(inter);
						continue;
					}
				}
				if(list.size()>0)
					interList.removeAll(list);
			}
		}
		return modules;
	}

	/**
	 * 点击新建接口文档时使用，返回默认的接口相关负责人信息，包括产品，前端，客户端，测试的负责人，产品没有配置相关负责人的不返回
	 * @param pageId
	 * @return
	 */
	@RequestMapping("add.html")
	@ResponseBody
	public Map<String,Object> addInterface(String pageId) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(pageId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			Long id = Long.valueOf(pageId);
			Interface inter = interfaceServiceImpl.getNewInterfaceDefaultInfo(id);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
				retMap.put("inter", inter);
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
	
	@RequestMapping("checkInterfaceUrl.html")
	@ResponseBody
	public Map<String,Object> checkInterfaceUrl(@RequestParam("url") String url,
												@RequestParam("interfaceId") String interfaceId) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		try {
			if(url != null) {
				if(url.equals("")) {
					retMap.put("retCode", 400);
					retMap.put("retDesc", "url不能为空");
					return retMap;
				}
				boolean isValid = CheckContent.isValidUrl(url);
				if(!isValid) {
					String errorContent = "url格式错误";
					if(!url.startsWith("http")) {
						errorContent += ",url需要以http开头";
					}
					retMap.put("retCode", 400);
					retMap.put("retDesc", errorContent);
					return retMap;
				}
				List<Long> interfaceIds = interfaceServiceImpl.selectInterfaceIdByRequestUrl(url);
				if(interfaceIds != null && !interfaceIds.isEmpty()) {
					if(interfaceIds.size() > 1) {
						Long tempInterfaceId = 1l;
						@SuppressWarnings("deprecation")
						Timestamp s=new Timestamp(100, 1, 1, 1, 1, 1, 1);
						for(Long id : interfaceIds){
							Interface tempInter = interfaceServiceImpl.getInterfaceInfo(id);
							Timestamp ts = tempInter.getCreateTime();
							if(ts.after(s)){
								s = ts;
								tempInterfaceId = tempInter.getInterfaceId();
							}
						}
						String existPath = interfaceServiceImpl.getPathByInterfaceId(tempInterfaceId);
						retMap.put("retCode", 402);
						retMap.put("retDesc", "已经存在多个接口使用了该url：" + url + "，最新的接口路径为：" + existPath);
						return retMap;
					}
					Long existId = interfaceIds.get(0);
					if(!interfaceId.equals(existId +"")){
						String existPath = interfaceServiceImpl.getPathByInterfaceId(existId);
						retMap.put("retCode", 402);
						retMap.put("retDesc", "已经存在接口使用了该url，接口id为：" + existId + "，路径为：" + existPath);
						return retMap;
					}
				}
			}
		} catch (Exception e) {
			LogConstant.runLog.error("异常错误",e);
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
			return retMap;
		}
		retMap.put("retCode", 200);
		retMap.put("retDesc", "格式正确");
		return retMap;
	}
	
	@RequestMapping("checkInterfaceFtl.html")
	@ResponseBody
	public Map<String,Object> checkInterfaceFtl(@RequestParam String ftlTemplate) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		try {
			if(ftlTemplate != null) {
				if(ftlTemplate.equals("")) {
					retMap.put("retCode", 402);
					retMap.put("retDesc", "ftl模板名称不能为空");
					return retMap;
				}
				boolean isValid = CheckContent.isValidFtlTemplateStr(ftlTemplate);
				if(!isValid) {
					retMap.put("retCode", 402);
					retMap.put("retDesc", "ftl模板名称格式错误");
					return retMap;
				}
			}
		} catch (Exception e) {
			LogConstant.runLog.error("异常错误",e);
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
			return retMap;
		}
		retMap.put("retCode", 200);
		retMap.put("retDesc", "格式正确");
		return retMap;
	}
	
	@RequestMapping("checkInterName.html")
	@ResponseBody
	public Map<String,Object> checkInterName(@RequestParam String interName) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		boolean interNameExist=interfaceServiceImpl.interNameExist(interName);
		if(interNameExist){
			retMap.put("retCode", 400);
		}else{
			retMap.put("retCode", 200);
		}
		return retMap;
	}
	
	@RequestMapping("save.html")
	@ResponseBody
	public Map<String,Object> saveInterface(@RequestBody InterfaceForm form) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(form == null){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			String error = ErrorMessageUtil.getErrorMessages();
			/**
			 * 用于判断url是否有效，是否重复
			 * 以及当接口类型为ftl时，ftl模板路径是否有效
			 * */
			String url = form.getUrl();
			@SuppressWarnings("unused")
			Long interfaceId = form.getInterfaceId();
			if(url != null) {
				if(url.equals("")) {
					retMap.put("retCode", 402);
					retMap.put("retDesc", "url不能为空");
					return retMap;
				}
				boolean isValid = CheckContent.isValidUrl(url);
				if(!isValid) {
					String errorContent = "url格式错误";
					if(!url.startsWith("http")) {
						errorContent += ",url需要以http开头";
					}
					retMap.put("retCode", 402);
					retMap.put("retDesc", errorContent);
					return retMap;
				}
				/**
				 * 对于url重复的判断，暂时在save的时候不去进行
				 * 以后会取消注释，进行判断
				 * */
//				List<Long> interfaceIds = interfaceServiceImpl.selectInterfaceIdByRequestUrl(url);
//				if(interfaceIds != null && !interfaceIds.isEmpty()) {
//					if(interfaceIds.size() > 1) {
//						Long tempInterfaceId = 1l;
//						@SuppressWarnings("deprecation")
//						Timestamp s=new Timestamp(100, 1, 1, 1, 1, 1, 1);
//						for(Long id : interfaceIds){
//							Interface tempInter = interfaceServiceImpl.getInterfaceInfo(id);
//							Timestamp ts = tempInter.getCreateTime();
//							if(ts.after(s)){
//								s = ts;
//								tempInterfaceId = tempInter.getInterfaceId();
//							}
//						}
//						String existPath = interfaceServiceImpl.getPathByInterfaceId(tempInterfaceId);
//						retMap.put("retCode", 402);
//						retMap.put("retDesc", "已经存在多个接口使用了该url：" + url + "，最新的接口路径为：" + existPath);
//						return retMap;
//					}
//					Long existId = interfaceIds.get(0);
//					if(interfaceId == null || interfaceId != existId) {
//						String existPath = interfaceServiceImpl.getPathByInterfaceId(existId);
//						retMap.put("retCode", 402);
//						retMap.put("retDesc", "已经存在接口使用了该url，接口id为：" + existId + "，路径为：" + existPath);
//						return retMap;
//					}
//				}
			}
			String ftlTemplate = form.getFtlTemplate();
			if(ftlTemplate != null) {
				if(ftlTemplate.equals("")) {
					if(form.getInterfaceType() == null || form.getInterfaceType() == 2) {
						retMap.put("retCode", 402);
						retMap.put("retDesc", "ftl类型接口，ftl模板名称不能为空");
						return retMap;
					}
				} else {
					boolean isValid = CheckContent.isValidFtlTemplateStr(ftlTemplate);
					if(!isValid) {
						retMap.put("retCode", 402);
						retMap.put("retDesc", "ftl模板名称格式错误");
						return retMap;
					}
				}
			} else {
				if(form.getInterfaceType() != null && form.getInterfaceType() == 2) {
					retMap.put("retCode", 402);
					retMap.put("retDesc", "ftl类型接口，ftl模板名称不能为空");
					return retMap;
				}
			}
			
			Interface inter = interfaceServiceImpl.saveInterfaceInfor(form, false);
			List<Redis> redisList = interfaceServiceImpl.saveInterfaceRedis(inter.getInterfaceId(),form.getRedisList());
			//inter = interfaceServiceImpl.getInterfaceInfo(inter.getInterfaceId());
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
				retMap.put("inter", inter);
				if(form.getInterfaceId() != null){
					String key = form.getInterfaceId().toString();
					memcacheServer.deleteCacheData(key);
				}
				if(redisList!=null && redisList.size()>0){
					retMap.put("redisList", redisList);
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
	
	@RequestMapping("rollbackInterfaceHistory.html")
	@ResponseBody
	public Map<String,Object> rollbackInterfaceHistory(@RequestBody InterfaceForm form) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(form == null){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			String error = ErrorMessageUtil.getErrorMessages();
			Interface inter = interfaceServiceImpl.saveInterfaceInfor(form, true);
			inter = interfaceServiceImpl.getInterfaceInfo(inter.getInterfaceId());
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
				
				retMap.put("inter", inter);
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
	
	@RequestMapping("edit.html")
	@ResponseBody
	public Map<String,Object> editInterface(String projectId, String interfaceId) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(interfaceId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			Long id = Long.valueOf(interfaceId);
			Interface inter = interfaceServiceImpl.getInterfaceInfo(id);
			List<Redis> redisList = interfaceServiceImpl.selectInterfaceRedis(id);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
				retMap.put("inter", inter);
				if(redisList!=null && redisList.size()>0){
					retMap.put("redis", redisList);
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
			Long id = Long.valueOf(interfaceId);
			interfaceServiceImpl.removeInterfaceInfor(id);
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
			LogConstant.runLog.error(e.getMessage(),e);
			e.printStackTrace();
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
			return retMap;
		}
		return retMap;
	}
	
	/**
	 * 移动接口
	 * @param pageId
	 * @return
	 */
	@RequestMapping("move.html")
	@ResponseBody
	public Map<String,Object> moveInterface(String interfaceId,String pageId) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(pageId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			interfaceServiceImpl.moveInterface(Long.valueOf(interfaceId), Long.valueOf(pageId));
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
			LogConstant.runLog.error("异常错误", e);
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
			return retMap;
		}
		return retMap;
	}
	
	@RequestMapping("getProjects.html")
	@ResponseBody
	public Map<String,Object> getModuleList(String projectId, String productId) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(projectId) || StringUtils.isBlank(productId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			//List<Module> moduleList = moduleServiceImpl.getModuleListNoInterByProjectId(projectId);
			List<ProjectModuleModel> projectList = moduleServiceImpl.getProjectModuleListByProductId(productId);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
				retMap.put("projectList", projectList);
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
	
	@RequestMapping("getProjectList.html")
	@ResponseBody
	public Map<String,Object> getModuleList(String productId) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(productId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			List<ProjectModuleModel> projectList = moduleServiceImpl.getProjectModuleListByProductId(productId);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
				retMap.put("projectList", projectList);
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
	
	@RequestMapping("copy.html")
	@ResponseBody
	public Map<String,Object> copyInterface(String interfaceId,String pageId) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(pageId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			Interface inter = interfaceServiceImpl.copyInterface(Long.valueOf(interfaceId), Long.valueOf(pageId));
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
				retMap.put("inter",inter);
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
	
	@RequestMapping("users.html")
	@ResponseBody
	public Map<String,Object> getProductUsers(String productId, String userName, String englishName) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(productId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			Long id = Long.valueOf(productId);
			List<UserModel> users = interfaceServiceImpl.getProductUsers(id, userName, englishName);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
				Map<String, List<UserModel>> userMap = new HashMap<String, List<UserModel>>();
				if(users!=null&&users.size()>0){
					for(UserModel user : users){
						String name = user.getUserName();
						Role role = roleConfigManagementService.selectRoleByUserEnglishName(name, id);
						if(role!=null){
							String roleName = role.getRoleName();
							if(!userMap.containsKey(roleName)){
								List<UserModel> list = new ArrayList<>();
								list.add(user);
								userMap.put(roleName, list);
							}else{
								List<UserModel> list = userMap.get(roleName);
								list.add(user);
							}
						}
					}
				}
				retMap.put("users", userMap);
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
	
	@RequestMapping("importOnlineInterface.html")
	@ResponseBody
	public Map<String, Object> importOnlineInterface(String projectId, String onlineModuleId, 
				String onlinePageId, String onlineInterfaceId){
		Map<String, Object> map = new HashMap<String, Object>();
		int retCode = interfaceServiceImpl.importOnlineInterface(projectId, onlineModuleId, onlinePageId, onlineInterfaceId);
		map.put("retCode", retCode);
		return map;
	}
	
	@RequestMapping("batchImportOnlineInterface.html")
	@ResponseBody
	public Map<String, Object> batchImportOnlineInterface(String projectId, String onlineModuleId, 
				String onlinePageId, String onlineInterfaceId){
		Map<String, Object> map = new HashMap<String, Object>();
		int retCode = 0;
		if(StringUtil.isEmpty(projectId) || StringUtil.isEmpty(onlineModuleId)){
			map.put("retCode", retCode);
			return map;
		}
		if(!StringUtil.isEmpty(onlineModuleId) && !StringUtil.isEmpty(onlinePageId) && !StringUtil.isEmpty(onlineInterfaceId)){
			retCode = interfaceServiceImpl.importOnlineInterface(projectId, onlineModuleId, onlinePageId, onlineInterfaceId);
		}else{
			Map<String, Integer> interfaceNum = interfaceServiceImpl.batchImportOnlineInterface(projectId, onlineModuleId, onlinePageId);
			if(interfaceNum != null){
				retCode = 200;
				map.put("interfaceNum", interfaceNum);
			}
		}
		map.put("retCode", retCode);
		return map;
	}
	
	@RequestMapping("autoGenerateInterTestCode.html")
	public ResponseEntity<byte[]> download(HttpServletRequest request, 
			@RequestParam("interfaceId") String interfaceId,
			@RequestParam("createFileOnServer") boolean createFileOnServer) throws IOException {
		if(StringUtil.isEmpty(interfaceId)){
			return new ResponseEntity<byte[]>("请求的参数为空，自动生成接口测试代码失败！".getBytes("GBK"), HttpStatus.CREATED);
		}
		byte[] flieByteStream = null;
		String path = null;
		if(createFileOnServer){ // 在服务器端生成文件
			path = autoGenerateInterTestCodeServiceImpl.autoGenerateInterTestCodeFile(interfaceId);
			if(StringUtil.isEmpty(path)){
				return new ResponseEntity<byte[]>("后台自动生成接口测试代码失败，请联系管理员！".getBytes("GBK"), HttpStatus.CREATED);
			}
			File file = new File(path);
			flieByteStream = FileUtil.readAsByteArray(file);
		}else{
			flieByteStream = autoGenerateInterTestCodeServiceImpl.autoGenerateInterTestCode(interfaceId);
		}
		if(flieByteStream == null){
			return new ResponseEntity<byte[]>("后台自动生成接口测试代码失败，请联系管理员！".getBytes("GBK"), HttpStatus.CREATED);
		}
        HttpHeaders headers = new HttpHeaders();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String name = "InterfaceSmokeTestCode_" + sdf.format(new Date()) + ".java";
        String fileName = new String(name.getBytes("UTF-8"),"iso-8859-1");
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(flieByteStream, headers, HttpStatus.CREATED);    
	}
	
	/**
	 * 自动生成测试代码页面需要的代码，返回的是代码字符串
	 * @param interfaceId
	 * @return
	 */
	@RequestMapping("autoGenerateInterTestCodePage.html")
	@ResponseBody
	public Map<String, Object> downloadCode(@RequestParam("interfaceId") String interfaceId) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		if(StringUtil.isEmpty(interfaceId)){
			retMap.put("retCode", 400);
			return retMap;
		}
		byte[] codeByte = autoGenerateInterTestCodeServiceImpl.autoGenerateInterTestCode(interfaceId);
		String code = new String(codeByte);
		
		if(StringUtil.isEmpty(code)){
			retMap.put("retCode", 400);
		}else{
			retMap.put("retCode", 200);
			retMap.put("code", code);
		}
        return retMap;
	}
	
	/**
	 * 接口页面处于非编辑状态时，若接口参数的mock字段被修改，保存修改到数据库
	 * @param paramId
	 * @param mock
	 * @return
	 */
	@RequestMapping("modifyMockContent.html")
	@ResponseBody
	public Map<String, Object> modifyMockContent(Long paramId, String mock){
		Map<String, Object> map = new HashMap<String, Object>();
		int res = interfaceServiceImpl.modifyMockContent(paramId, mock);
		if(res > 0){
			map.put("retCode", 200);
			map.put("retDesc", "保存成功");
		}else{
			map.put("retCode", 400);
			map.put("retDesc", "保存失败");
		}
		
		return map;
	}
	
	
	/**
	 * 根据dictId查询字典，复制接口参数时，可能复制的参数没有字典实体。
	 * @param dictId
	 * @return
	 */
	@RequestMapping("searchParamDict.html")
	@ResponseBody
	public Map<String, Object> searchParamDict(Long dictId){
		Map<String, Object> map = new HashMap<String, Object>();
		Dict dict = interfaceServiceImpl.searchParamDict(dictId);
		if(dict != null){
			map.put("retCode", 200);
			map.put("dict", dict);
			map.put("retDesc", "保存成功");
		}else{
			map.put("retCode", 400);
			map.put("retDesc", "保存失败");
		}
		return map;
	}

	/**
	 * 如果复制的参数中包含object类型的字典，在数据库另保存这些字典信息。
	 * @param dict
	 * @return
	 */
	@RequestMapping("saveObjectDict.html")
	@ResponseBody
	public Map<String, Object> saveObjectDict(@RequestBody Dict dict){
		Map<String, Object> map = new HashMap<String, Object>();
		if(dict == null){
			map.put("retCode", 400);
			map.put("retDesc", "保存失败");
			return map;
		}
		Dict saveDict = interfaceServiceImpl.saveObjectDict(dict);
		if(saveDict != null){
			map.put("retCode", 200);
			map.put("dict", saveDict);
			map.put("retDesc", "保存成功");
		}else{
			map.put("retCode", 400);
			map.put("retDesc", "保存失败");
		}
		return map;
	}
	
	 /* 设定字典参数的显示与否
	 * @param dictParamDisplay
	 * @return
	 */
	@RequestMapping("operateDictParam")
	@ResponseBody
	private Map<String, Object> operateDictParamDisplay(@RequestBody DictParamDisplay dictParamDisplay){
		Map<String, Object> map = new HashMap<String, Object>();
		if(dictParamDisplay == null){
			map.put("retCode", -1);
			return map;
		}
		LogConstant.debugLog.info("DictParamDisplay bean:" + dictParamDisplay);
		if(dictParamDisplay.getDictId()==null || dictParamDisplay.getInterfaceId()==null
				|| dictParamDisplay.getParamId()==null){
			LogConstant.debugLog.info("DictParamDisplay bean's params are not complete!");
			map.put("retCode", -1);
			return map;
		}
		int retCode = interfaceServiceImpl.operateDictParamDisplay(dictParamDisplay);
		if(retCode > 0){
			map.put("retCode", 200);
		}else{
			map.put("retCode", "-1");
		}
		return map;
	}
	/**
	 * 查询接口下字典参数是否显示列表
	 */
	@RequestMapping("queryDictParams")
	@ResponseBody
	private Map<String, Object> queryDictParams(@RequestBody DictParamDisplay dictParamDisplay){
		Map<String, Object> map = new HashMap<String, Object>();
		LogConstant.debugLog.info("查询接口下字典列表是否显示的参数为：" + dictParamDisplay);
		if(dictParamDisplay == null){
			map.put("retCode", "-1");
			return map;
		}
		List<DictParamDisplay> list = interfaceServiceImpl.queryDictParamList(dictParamDisplay);
		LogConstant.debugLog.info("查询接口下字典列表是否显示的查询结果为：" + list);
		if(list != null){
			map.put("retCode", 200);
			map.put("retDesc", list);
		}else{
			map.put("retCode", "-1");
			map.put("retDesc", "未查询到相关结果");
		}
		return map;
	}
	@RequestMapping("getEditPeople")
	@ResponseBody
	public Map<String, Object> queryEditPeople(@RequestParam("interfaceId") String interfaceId){
		Map<String, Object> map = new HashMap<String, Object>();
		String editPeople = (String) memcacheServer.getCacheData(interfaceId);
		if(editPeople == null){
			//无人编辑
			map.put("retCode", 200);
		}else{
			map.put("retCode", -1);
			map.put("editPeople", editPeople);
		}
		return map;
	}
	@RequestMapping("setEditPeople")
	@ResponseBody
	public Map<String, Object> setEditPeople(@RequestParam("interfaceId") String interfaceId,@RequestParam("editPeople") String editPeople){
		Map<String, Object> map = new HashMap<String, Object>();
		boolean flag = memcacheServer.setCacheData(interfaceId, editPeople, 60*60*1000);
		if(flag){
			map.put("retCode", 200);
		}else{
			map.put("retCode", -1);
		}
		return map;
	}
	@RequestMapping("cancelEdit")
	@ResponseBody
	public Map<String, Object> cancelEdit(@RequestParam("interfaceId") String interfaceId){
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtil.isEmpty(interfaceId)){
			map.put("retCode", 400);
			return map;
		}
		boolean flag = memcacheServer.deleteCacheData(interfaceId);
		if(flag){
			Long status = interfaceServiceImpl.getInterStatus(Long.parseLong(interfaceId));
			map.put("retCode", 200);
			map.put("status", status);
		}else{
			map.put("retCode", -1);
		}
		return map;
	}
	@RequestMapping("getInterStatus")
	@ResponseBody
	public Map<String, Object> getInterStatus(@RequestParam("interId") String interId){
		Map<String, Object> map = new HashMap<String, Object>();
		Long status = interfaceServiceImpl.getInterStatus(Long.parseLong(interId));
		map.put("retCode", 200);
		map.put("status", status);
		return map;
	}
	@RequestMapping("deleteInterfaceRedis")
	@ResponseBody
	public Map<String, Object> deleteInterfaceRedis(@RequestParam("redisId") String redisId){
		Map<String, Object> map = new HashMap<String, Object>();
		int num = interfaceServiceImpl.deleteInterfaceRedis(Long.parseLong(redisId));
		if(num!=0){
			map.put("retCode", 200);
		}
		return map;
	}
}