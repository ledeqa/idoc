
package com.idoc.service.docManage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.idoc.constant.CommonConstant;
import com.idoc.constant.LogConstant;
import com.idoc.dao.docManage.InterVersionDaoImpl;
import com.idoc.dao.docManage.InterfaceDaoImpl;
import com.idoc.dao.docManage.InterfaceOnlineDaoImpl;
import com.idoc.dao.docManage.ModuleDaoImpl;
import com.idoc.dao.docManage.OnlineModuleDaoImpl;
import com.idoc.dao.docManage.OnlinePageDaoImpl;
import com.idoc.dao.docManage.PageDaoImpl;
import com.idoc.dao.docManage.ParamDaoImpl;
import com.idoc.dao.login.LoginDaoImpl;
import com.idoc.dao.productAndProject.ProductUserDaoImpl;
import com.idoc.dao.productAndProject.ProjectDaoImpl;
import com.idoc.form.InterfaceForm;
import com.idoc.model.Dict;
import com.idoc.model.DictParamDisplay;
import com.idoc.model.InterVersion;
import com.idoc.model.Interface;
import com.idoc.model.InterfaceOnline;
import com.idoc.model.Module;
import com.idoc.model.OnlineModule;
import com.idoc.model.OnlinePage;
import com.idoc.model.Page;
import com.idoc.model.Param;
import com.idoc.model.ProjectModel;
import com.idoc.model.Redis;
import com.idoc.model.UserModel;
import com.idoc.service.dict.DictServiceImpl;
import com.idoc.service.flow.StatusFlowService;
import com.idoc.service.interfaceOnline.InterfaceOnlineService;
import com.idoc.shiro.token.TokenManager;
import com.idoc.util.ErrorMessageUtil;
import com.idoc.util.LoginUserInfoUtil;
import com.netease.common.util.StringUtil;

import net.sf.json.JSONObject;

@Service
public class InterfaceServiceImpl {
	
	@Autowired
	private ModuleDaoImpl moduleDaoImpl;
	@Autowired
	private PageDaoImpl pageDaoImpl;
	@Autowired
	private LoginDaoImpl loginDaoImpl;
	@Autowired
	private InterfaceDaoImpl interfaceDaoImpl;
	@Autowired
	private InterfaceOnlineDaoImpl interfaceOnlineDaoImpl;
	@Autowired
	private InterServiceUtilImpl interServiceUtilImpl;
	@Autowired
	private InterVersionDaoImpl interVersionDaoImpl;
	@Autowired
	private ParamDaoImpl paramDaoImpl;
	@Autowired
	private DictServiceImpl dictServiceImpl;
	@Autowired
	private StatusFlowService statusFlowServiceImpl;
	@Autowired
	private OnlineModuleDaoImpl onlineModuleDaoImpl;
	@Autowired
	private OnlinePageDaoImpl onlinePageDaoImpl;
	@Autowired
	private InterfaceOnlineService interfaceOnlineService;
	@Autowired
	private ProductUserDaoImpl productUserDaoImpl;
	@Autowired
	private ProjectDaoImpl projectDaoImpl;
	
	public Interface getNewInterfaceDefaultInfo(Long pageId){
		Interface interf = new Interface();
		// 根据pageId 查到对应的项目，验证pageId是否有效
		ProjectModel project = loginDaoImpl.selectProjectModelByPageId(pageId);
		if(project == null){
			// 页面id不存在响应的项目，报错
			ErrorMessageUtil.put("接口所属的页面不存在！");
			LogConstant.runLog.error("pageId="+pageId + " 所属的项目不存在！");
		}
		// 根据产品id获取产品的所有成员信息 
//		List<ProductUserModel> puList = loginDaoImpl.selectProductUserModelListByProductId(project.getProductId());
//		List<UserModel> productList = new ArrayList<UserModel>();
//		List<UserModel> frontList = new ArrayList<UserModel>();
//		List<UserModel> behindList = new ArrayList<UserModel>();
//		List<UserModel> clientList = new ArrayList<UserModel>();
//		List<UserModel> testList = new ArrayList<UserModel>();
//		
//		if(puList != null && !puList.isEmpty()){
//			for(ProductUserModel pu: puList){
//				if(pu.getRole().getRoleName().equals(CommonConstant.ROLE_PRODUCT_MANAGER)){
//					productList.add(pu.getUser());
//				}else if(pu.getRole().getRoleName().equals(CommonConstant.ROLE_FRONT_MANAGER)){
//					frontList.add(pu.getUser());
//				}else if(pu.getRole().getRoleName().equals(CommonConstant.ROLE_DEVELOPER_MANAGER)){
//					behindList.add(pu.getUser());
//				}else if(pu.getRole().getRoleName().equals(CommonConstant.ROLE_APP_MANAGER)){
//					clientList.add(pu.getUser());
//				}else if(pu.getRole().getRoleName().equals(CommonConstant.ROLE_TESTOR_MANAGER)){
//					testList.add(pu.getUser());
//				}
//			}
//		}
		UserModel creator = TokenManager.getToken();
		
		interf.setCreatorId(creator.getUserId());
		interf.setCreator(creator);
//		interf.setClientPeoples(clientList);
		interf.setCreateTime(new Timestamp(System.currentTimeMillis()));
//		interf.setFrontPeoples(frontList);
//		interf.setBehindPeoples(behindList);
		interf.setInterfaceStatus(10);
		interf.setInterfaceType(1);
		interf.setIsNeedInterfaceTest(1);
		interf.setIsNeedPressureTest(0);
		interf.setPageId(pageId);
//		interf.setReqPeoples(productList);
		interf.setRequestType(1);
		interf.setStatus(1);
//		interf.setTestPeoples(testList);
		return interf;
	}
	
	@Transactional(rollbackFor=Exception.class, propagation=Propagation.REQUIRED)
	public Interface saveInterfaceInfor(InterfaceForm form, boolean isRollback){ // isRollback是否是回滚历史版本
//		String englishName = LoginUserInfoUtil.getUserEnglishName();
//		String userId = loginDaoImpl.queryUserIdByUserName(englishName);
		Long userId = TokenManager.getUserId();
		if(null == userId){
			ErrorMessageUtil.put("没有找到用户信息，请登录后再操作！");
			LogConstant.runLog.error("userId="+userId + " 没有找到用户信息，请登录后再操作！");
			throw new RuntimeException("userId="+userId + " 没有找到用户信息，请登录后再操作！");
		}
		int flag = form.getFlag();
		// 将接口传递的form信息，转换成interface model
		Interface inter = new Interface();
		inter.setClientPeopleIds(form.getClientPeopleIds());
		inter.setDesc(form.getDesc());
		inter.setRetParamExample(form.getRetParamExample());
		Long editVersion = 0L;
		Long itertVersion = 1L;
		int isFirstSave = 0;
		if(form.getInterfaceId() == null){
			isFirstSave = 1;
			if(flag == 1){
				//新建的接口 第一次保存接口相关信息
				editVersion = 1L;
				inter.setCreatorId(userId);
				// 根据url 找在线的接口文档
				Long productId = pageDaoImpl.selectProductIdByPageId(form.getPageId());
				Map<String,Object> condMap = new HashMap<String,Object>();
				if(form.getOnlineInterfaceId() != null){
					condMap.put("interfaceId", form.getOnlineInterfaceId());
				}else{
					condMap.put("url", form.getUrl());
					condMap.put("productId", productId);
				}
				
				List<InterfaceOnline> onlineInterList = interfaceOnlineDaoImpl.selectOnlineInterfaceListByCond(condMap);
				if(onlineInterList != null && !onlineInterList.isEmpty()){
					if(onlineInterList.size() > 1){
						ErrorMessageUtil.put("url="+form.getUrl() +"在线存在多个接口，请联系开发人员帮助解决");
						LogConstant.runLog.info("url="+form.getUrl() +"在线存在多个接口，请联系开发人员帮助解决");
						return null;
					}
					InterfaceOnline interOnline = onlineInterList.get(0);
					inter.setOnlineInterfaceId(interOnline.getInterfaceId());
					inter.setOnlineVersion(interOnline.getOnlineVersion());
				}
			}
		}else if(flag == 1){
			Long currentEditVersion = interVersionDaoImpl.selectCurrentInterVersionById(form.getInterfaceId());
			if(currentEditVersion == null){
				currentEditVersion = 0L;
			}
			editVersion = currentEditVersion + 1;
			
			itertVersion = interVersionDaoImpl.selectCurrentIterVersionById(form.getInterfaceId());
			if(itertVersion == null){
				itertVersion = 1L;
			}
		}
		
		inter.setInterfaceId(form.getInterfaceId());
		inter.setCreatorId(form.getCreatorId());
		inter.setOnlineInterfaceId(form.getOnlineInterfaceId());
		inter.setExpectOnlineTime(form.getExpectOnlineTime());
		inter.setFrontPeopleIds(form.getFrontPeopleIds());
		inter.setBehindPeopleIds(form.getBehindPeopleIds());
		inter.setInterfaceName(form.getInterfaceName());
		
		if(isRollback){
			inter.setInterfaceStatus(form.getInterfaceStatus());
		}else{
			if(flag == 0)
				inter.setInterfaceStatus(10);//10:暂存中
			else
				inter.setInterfaceStatus(1); // 编辑中
		}
		inter.setInterfaceType(form.getInterfaceType());
		inter.setIsNeedInterfaceTest(form.getIsNeedInterfaceTest());
		inter.setIsNeedPressureTest(form.getIsNeedPressureTest());
		inter.setPageId(form.getPageId());
		inter.setReqPeopleIds(form.getReqPeopleIds());
		inter.setRequestType(form.getRequestType());
		inter.setStatus(1);
		inter.setTestPeopleIds(form.getTestPeopleIds());
		inter.setExpectTestTime(form.getExpectTestTime());
		inter.setUrl(form.getUrl());
		inter.setEditVersion(editVersion);
		inter.setIterVersion(itertVersion);
		inter.setFtlTemplate(form.getFtlTemplate());
		
		if(flag == 0 && isFirstSave == 1){
			//暂时保存
			interfaceDaoImpl.insertInterface(inter);
		}else if(isFirstSave == 1){
			// 保存接口文档信息到数据库
			interfaceDaoImpl.insertInterface(inter);
			// 发送pop消息通知相关人修改负责人信息
			statusFlowServiceImpl.createInterfaceFinished(inter, null);
		}else{
			// 保存版本相关信息 
			Interface oldInter = getInterfaceInfo(form.getInterfaceId());
			Long oldVersion = oldInter.getEditVersion();
			if(oldVersion != 0){
				String interJson = JSONObject.fromObject(oldInter).toString();
				InterVersion interVersion = new InterVersion();
				interVersion.setCommitId(userId);
				interVersion.setInterfaceId(form.getInterfaceId());
				interVersion.setSnapshot(interJson);
				interVersion.setStatus(1);
				if(StringUtils.isBlank(form.getVersionDesc())){
					interVersion.setVersionDesc("极速保存");
				}else{
					interVersion.setVersionDesc(form.getVersionDesc());
				}
				interVersion.setVersionNum(editVersion - 1);
				interVersionDaoImpl.insertInterVersion(interVersion);
			}else if(flag == 1){
				statusFlowServiceImpl.createInterfaceFinished(inter, null);
			}
			if(isRollback){
				interfaceDaoImpl.updateInterfaceTotally(inter);
			}else{
				interfaceDaoImpl.updateInterface(inter);
			}
		}
		
		// reqParams
		if(form.getReqParams()!=null){
			List<Param> reqParams = interServiceUtilImpl.saveReqParams(inter.getInterfaceId(), form.getReqParams(), false, isRollback);
			inter.setReqParams(reqParams);
		}
		// retParams
		if(form.getRetParams()!=null){
			List<Param> retParams = interServiceUtilImpl.saveRetParams(inter.getInterfaceId(), form.getRetParams(), false, isRollback);
			inter.setRetParams(retParams);
		}
		return inter;
	}
	
	public Interface getInterfaceInfo(Long id){
		Map<String,Object> condMap = new HashMap<String,Object>();
		condMap.put("interfaceId",id);
		List<Interface> interList = interfaceDaoImpl.selectInterfaceListByCond(condMap);
		if(interList == null || interList.isEmpty() || interList.size() > 1){
			ErrorMessageUtil.put("id="+id +"查到的接口文档有异常 interList="+interList);
			LogConstant.runLog.info("id="+id +"查到的接口文档有异常 interList="+interList);
			return new Interface();
		}
		Interface inter = interList.get(0);
		// 设置创建者信息
		List<UserModel> cList = loginDaoImpl.selectUserModelListByIds(inter.getCreatorId().toString());
		if(cList != null && !cList.isEmpty()){
			inter.setCreator(cList.get(0));
		}
		// 设置用户相关信息
		inter.setFrontPeoples(loginDaoImpl.selectUserModelListByIds(inter.getFrontPeopleIds()));
		inter.setBehindPeoples(loginDaoImpl.selectUserModelListByIds(inter.getBehindPeopleIds()));
		inter.setClientPeoples(loginDaoImpl.selectUserModelListByIds(inter.getClientPeopleIds()));
		inter.setTestPeoples(loginDaoImpl.selectUserModelListByIds(inter.getTestPeopleIds()));
		inter.setReqPeoples(loginDaoImpl.selectUserModelListByIds(inter.getReqPeopleIds()));
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
	public void removeInterfaceInfor(Long id){
		int num = interfaceDaoImpl.deleteInterface(id);
		if(num <= 0){
			ErrorMessageUtil.put("删除接口失败！");
		}
	}
	
	public void moveInterface(Long interfaceId,Long pageId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", interfaceId);
		map.put("pageId", pageId);
		int num = interfaceDaoImpl.updateInterface(map);
		if(num <= 0){
			ErrorMessageUtil.put("移动接口失败！");
		}
	}
	
	public Interface copyInterface(Long interfaceId,Long pageId){
		Map<String,Object> condMap = new HashMap<String,Object>();
		condMap.put("interfaceId",interfaceId);
		List<Interface> interList = interfaceDaoImpl.selectInterfaceListByCond(condMap);
		if(interList == null || interList.isEmpty() || interList.size() > 1){
			ErrorMessageUtil.put("id="+interfaceId +"查到的接口文档有异常 interList="+interList);
			LogConstant.runLog.info("id="+interfaceId +"查到的接口文档有异常 interList="+interList);
			return new Interface();
		}
		
		UserModel userModel = TokenManager.getToken();
		
		Interface inter = interList.get(0);
		inter.setInterfaceName(inter.getInterfaceName() + "_复制");
		inter.setCreator(userModel);
		inter.setCreatorId(userModel.getUserId());
		inter.setUrl(inter.getUrl() + "." + System.currentTimeMillis() + "_copy");
		inter.setInterfaceId(null);
		inter.setEditVersion(1L);
		inter.setIterVersion(1L);
		inter.setInterfaceStatus(CommonConstant.INTERFACE_STATUS_EDITING);
		inter.setOnlineInterfaceId(null);
		inter.setPageId(pageId);
		interfaceDaoImpl.insertInterface(inter);
		// 设置请求参数信息
		List<Param> reqList = paramDaoImpl.selectRequestParamByInterfaceId(interfaceId);
		if(reqList != null && !reqList.isEmpty()){
			for(Param pm: reqList){
				if(pm.getDictId() != null){
					pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
				}
			}
		}
		if(reqList != null){
			Map<String,Object> paramMap = new HashMap<String,Object>();
			for(Param pm : reqList){
				if(pm.getDictId() != null){
					if(CommonConstant.PARAM_TYPE_OBJECT.equals(pm.getParamType()) || CommonConstant.PARAM_TYPE_OBJECT_ARRAY.equals(pm.getParamType())){
						Dict dict = pm.getDict();
						if(dict != null){
							dict.setDictId(null);
							dictServiceImpl.copyDict(dict);
							pm.setDictId(dict.getDictId());
						}
					}
				}
				pm.setParamId(null);
				paramDaoImpl.insertParam(pm);
				// 保存接口和参数之间的联系
				paramMap.clear();
				paramMap.put("interfaceId", inter.getInterfaceId());
				paramMap.put("paramId", pm.getParamId());
				paramDaoImpl.insertRequestParam(paramMap);
			}
		}
		// 设置返回参数信息
		List<Param> retList = paramDaoImpl.selectReturnParamByInterfaceId(interfaceId);
		if(retList != null && !retList.isEmpty()){
			for(Param pm: retList){
				if(pm.getDictId() != null){
					pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
				}
			}
		}
		if(retList != null){
			Map<String,Object> paramMap = new HashMap<String,Object>();
			for(Param pm : retList){
				if(pm.getDictId() != null){
					if(CommonConstant.PARAM_TYPE_OBJECT.equals(pm.getParamType()) || CommonConstant.PARAM_TYPE_OBJECT_ARRAY.equals(pm.getParamType())){
						Dict dict = pm.getDict();
						if(dict != null){
							dict.setDictId(null);
							dictServiceImpl.copyDict(dict);
							pm.setDictId(dict.getDictId());
						}
					}
				}
				
				pm.setParamId(null);
				paramDaoImpl.insertParam(pm);
				// 保存接口和参数之间的联系
				paramMap.clear();
				paramMap.put("interfaceId", inter.getInterfaceId());
				paramMap.put("paramId", pm.getParamId());
				paramDaoImpl.insertReturnParam(paramMap);
			}
		}
		return inter;
	}
	
	public List<Interface> getInterfaceListByPages(List<Page> pageList){
		if(CollectionUtils.isNotEmpty(pageList)){
			List<Long> pageIds = new ArrayList<Long>();
			for(Page page: pageList){
				pageIds.add(page.getPageId());
			}
			
			List<Interface> interfaceList = interfaceDaoImpl.selectInterfaceListByPages(pageIds);
			
			return interfaceList;
		}
		
		return null;
	}
	
	public List<Interface> getAllInterfaceListByProjectId(Long projectId, String modules){
		List<Interface> interfaceList;
		if(StringUtil.isEmpty(modules)){
			interfaceList = interfaceDaoImpl.getAllInterfaceListByProjectId(projectId);
		}else{
			Map<String, Object> map = new HashMap<String, Object>();
			String[] moduleIds = modules.split(",");
			map.put("projectId", projectId);
			map.put("moduleIdList", Arrays.asList(moduleIds));
			interfaceList = interfaceDaoImpl.getAllInterfaceListByProjectIdAndModules(map);
		}
		if(interfaceList != null && interfaceList.size() > 0){
			Map<String, UserModel> userMap = new HashMap<String, UserModel>();
			List<UserModel> userList = productUserDaoImpl.selectAllUserFromTB_IDOC_USER();
			if(userList != null && userList.size() > 0){
				for(UserModel user : userList){
					userMap.put(String.valueOf(user.getUserId()), user);
				}
				// 设置接口的各角色成员
				for(Interface inter : interfaceList){
					String creatorId = String.valueOf(inter.getCreatorId());
					if(!StringUtil.isEmpty(creatorId)){
						if(userMap.containsKey(creatorId)){
							inter.setCreator(userMap.get(creatorId));
						}
					}
					
					if(!StringUtil.isEmpty(inter.getReqPeopleIds())){
						List<UserModel> reqPeoples = new ArrayList<UserModel>();
						String[] userIds = inter.getReqPeopleIds().split(",");
						for(String userId : userIds){
							if(userMap.containsKey(userId)){
								reqPeoples.add(userMap.get(userId));
							}
						}
						inter.setReqPeoples(reqPeoples);
					}
					
					if(!StringUtil.isEmpty(inter.getFrontPeopleIds())){
						List<UserModel> frontPeoples = new ArrayList<UserModel>();
						String[] userIds = inter.getFrontPeopleIds().split(",");
						for(String userId : userIds){
							if(userMap.containsKey(userId)){
								frontPeoples.add(userMap.get(userId));
							}
						}
						inter.setFrontPeoples(frontPeoples);
					}
					
					if(!StringUtil.isEmpty(inter.getBehindPeopleIds())){
						List<UserModel> behindPeoples = new ArrayList<UserModel>();
						String[] userIds = inter.getBehindPeopleIds().split(",");
						for(String userId : userIds){
							if(userMap.containsKey(userId)){
								behindPeoples.add(userMap.get(userId));
							}
						}
						inter.setBehindPeoples(behindPeoples);
					}
					
					if(!StringUtil.isEmpty(inter.getClientPeopleIds())){
						List<UserModel> clientPeoples = new ArrayList<UserModel>();
						String[] userIds = inter.getClientPeopleIds().split(",");
						for(String userId : userIds){
							if(userMap.containsKey(userId)){
								clientPeoples.add(userMap.get(userId));
							}
						}
						inter.setClientPeoples(clientPeoples);
					}
					
					if(!StringUtil.isEmpty(inter.getTestPeopleIds())){
						List<UserModel> testPeoples = new ArrayList<UserModel>();
						String[] userIds = inter.getTestPeopleIds().split(",");
						for(String userId : userIds){
							if(userMap.containsKey(userId)){
								testPeoples.add(userMap.get(userId));
							}
						}
						inter.setTestPeoples(testPeoples);
					}
					
				}
			}
		}
		return interfaceList;
	}
	
	/**
	 * 根据接口中的角色id查询对应的UserModel
	 * @param users
	 * @return
	 */
	public List<UserModel> getUserListByInterface(String users){
		if(!StringUtil.isEmpty(users)){
			String[] userIds = users.split(",");
			List<Long> userList = new ArrayList<Long>();
			for(String user : userIds){
				try{
					Long userId = Long.parseLong(user);
					userList.add(userId);
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
			}
			List<UserModel> usersList = interfaceDaoImpl.selectUserListByInterface(userList);
			
			return usersList;
		}
		
		return null;
	}
	
	public List<UserModel> getProductUsers(Long productId, String userName, String englishName){
		return loginDaoImpl.selectProductUserModelList(productId, userName, englishName);
	}

	public Interface getInterfaceByOnlineId(Map<String, Object> map) {
		
		return interfaceDaoImpl.selectInterfaceById(map);
	}
	
	@Transactional(rollbackFor=Exception.class, propagation=Propagation.REQUIRED)
	public int importOnlineInterface(String projectId, String onlineModuleId,
			String onlinePageId, String onlineInterfaceId) {
		//检测当前project是否存在该模块
		boolean moduleFlag = false;
		boolean pageFlag = false;
		boolean interfaceFlag = false;
		Module existModule = null;
		Page existPage = null;
		Map<String, Object> map= new HashMap<String, Object>();
		map.put("projectId", projectId);
		OnlineModule onlineModule = onlineModuleDaoImpl.selectOnlineModuleById(Long.parseLong(onlineModuleId));
		String onlineModuleName = onlineModule.getModuleName();
		List<Module> moduleList = moduleDaoImpl.selectModuleListByCond(map);
		if(moduleList != null){
			for(Module module : moduleList){
				String moduleName = module.getModuleName();
				if(moduleName.equals(onlineModuleName)){
					moduleFlag = true;
					existModule= module;
					break;
				}
			}
		}
		if(moduleFlag){
			//检测页面是否存在
			Map<String, Object> pageMap = new HashMap<String, Object>();
			OnlinePage onlinePage = onlinePageDaoImpl.selectOnlinePageById(Long.parseLong(onlinePageId));
			String onlinePageName = onlinePage.getPageName();
			pageMap.put("moduleId", existModule.getModuleId());
			List<Page> pageList = pageDaoImpl.selectPageListByModuleId(pageMap);
			if(pageList != null){
				for(Page page : pageList){
					String pageName = page.getPageName();
					if(pageName.equals(onlinePageName)){
						pageFlag = true;
						existPage = page;
						break;
					}
				}
			}
		}
		if(pageFlag){
			Map<String, Object> interMap = new HashMap<String, Object>();
			interMap.put("pageId", existPage.getPageId());
			List<Interface> interList = interfaceDaoImpl.selectInterfaceListByCond(interMap);
			InterfaceOnline onlineInterface = interfaceOnlineService.getOnlineInterfaceInfo(Long.parseLong(onlineInterfaceId));
			String onlineInterfaceName = onlineInterface.getInterfaceName();
			String onlineInterfaceUrl = onlineInterface.getUrl();
			if(interList != null){
				for(Interface inter : interList){
					String interName = inter.getInterfaceName();
					String interUrl = inter.getUrl();
					if((interName != null && interName.equals(onlineInterfaceName)) || (interUrl != null && interUrl.equals(onlineInterfaceUrl))){
						interfaceFlag = true;
						break;
					}
				}
			}
		}
		if(interfaceFlag){
			return 2;
		}
		if(!moduleFlag){
			OnlineModule online_Module = onlineModuleDaoImpl.selectOnlineModuleById(Long.parseLong(onlineModuleId));
			Module module = new Module();
			module.setModuleName(online_Module.getModuleName());
			module.setProjectId(Long.parseLong(projectId));
			module.setStatus(1);
			moduleDaoImpl.insertModule(module);
			existModule = module;
		}
		if(!pageFlag){
			Page newPage = new Page();
			OnlinePage onlinePage = onlinePageDaoImpl.selectOnlinePageById(Long.parseLong(onlinePageId));
			newPage.setPageName(onlinePage.getPageName());
			newPage.setModuleId(existModule.getModuleId());
			newPage.setStatus(1);
			pageDaoImpl.insertPage(newPage);
			existPage = newPage;
		}
		UserModel userModel = TokenManager.getToken();
		InterfaceOnline onlineInterface = interfaceOnlineDaoImpl.selectOnlineInterfaceById(Long.parseLong(onlineInterfaceId));
		Interface newInter = new Interface();
		newInter.setCreatorId(userModel.getUserId());
		newInter.setCreator(userModel);
		newInter.setPageId(existPage.getPageId());
		newInter.setInterfaceName(onlineInterface.getInterfaceName() + "复制线上");
		newInter.setInterfaceType(onlineInterface.getInterfaceType());
		newInter.setRequestType(onlineInterface.getRequestType());
		newInter.setUrl(onlineInterface.getUrl() + "." + System.currentTimeMillis() + "_copyOnline");
		newInter.setDesc(onlineInterface.getDesc());
		newInter.setIsNeedInterfaceTest(onlineInterface.getIsNeedInterfaceTest());
		newInter.setIsNeedPressureTest(onlineInterface.getIsNeedPressureTest());
		newInter.setReqPeopleIds(onlineInterface.getReqPeopleIds());
		newInter.setFrontPeopleIds(onlineInterface.getFrontPeopleIds());
		newInter.setBehindPeopleIds(onlineInterface.getBehindPeopleIds());
		newInter.setClientPeopleIds(onlineInterface.getClientPeopleIds());
		newInter.setTestPeopleIds(onlineInterface.getTestPeopleIds());
		newInter.setEditVersion(1L);
		newInter.setIterVersion(1L);
		newInter.setStatus(1);
		newInter.setInterfaceStatus(1);
		interfaceDaoImpl.insertInterface(newInter);
		statusFlowServiceImpl.createInterfaceFinished(newInter, Long.parseLong(projectId));
		List<Param> reqParamsList = paramDaoImpl.selectOnlineRequestParamByInterfaceId(Long.parseLong(onlineInterfaceId));
		List<Param> retParamsList = paramDaoImpl.selectOnlineReturnParamByInterfaceId(Long.parseLong(onlineInterfaceId));
		if(reqParamsList != null){
			Map<String,Object> paramMap = new HashMap<String,Object>();
			for(Param pm : reqParamsList){
				pm.setParamId(null);
				paramDaoImpl.insertParam(pm);
				// 保存接口和参数之间的联系
				paramMap.clear();
				paramMap.put("interfaceId", newInter.getInterfaceId());
				paramMap.put("paramId", pm.getParamId());
				paramDaoImpl.insertRequestParam(paramMap);
			}
		}
		if(retParamsList != null){
			Map<String,Object> paramMap = new HashMap<String,Object>();
			for(Param pm : retParamsList){
				pm.setParamId(null);
				paramDaoImpl.insertParam(pm);
				// 保存接口和参数之间的联系
				paramMap.clear();
				paramMap.put("interfaceId", newInter.getInterfaceId());
				paramMap.put("paramId", pm.getParamId());
				paramDaoImpl.insertReturnParam(paramMap);
			}
		}
		/*if(reqParamsList != null){
			int len = reqParamsList.size();
			reqParams = new ParamForm[len];
			for(int i=0; i<len; i++){
				ParamForm p_form = convertParam2ParamForm(reqParamsList.get(i), null);
				reqParams[i] = p_form;
			}
		}
		if(retParamsList != null){
			int len = retParamsList.size();
			retParams = new ParamForm[len];
			for(int i=0; i<len; i++){
				ParamForm p_form = convertParam2ParamForm(retParamsList.get(i), null);
				retParams[i] = p_form;
			}
		}
		Interface insertedInter = saveInterfaceInfor(newInter);*/
		if(newInter.getInterfaceId() != null){
			return 1;
		}else{
			try {
				throw new Exception("导入失败！");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
	}
	
	@Transactional(rollbackFor=Exception.class, propagation=Propagation.REQUIRED)
	public Map<String, Integer> batchImportOnlineInterface(String projectId, String onlineModuleId,
			String onlinePageId) {
		int successNum = 0;
		int failedNum = 0;
		int existNum = 0;
		if(StringUtil.isEmpty(onlinePageId)){ // 添加模块下的所有页面和接口
			List<Long> moduleIds = new ArrayList<Long>();
			moduleIds.add(Long.parseLong(onlineModuleId));
			List<OnlinePage> pages = onlinePageDaoImpl.getPageListByModules(moduleIds);
			
			for(OnlinePage page : pages){
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("pageId", page.getOnlinePageId());
				List<InterfaceOnline> onlineInterfaces = interfaceOnlineDaoImpl.selectOnlineInterfaceByConditionsByPage(map);
				for(InterfaceOnline inter : onlineInterfaces){
					int res = importOnlineInterface(projectId, onlineModuleId, String.valueOf(page.getOnlinePageId()), String.valueOf(inter.getInterfaceId()));
					if(res == 1){
						successNum++;
					}else if( res == 2){
						existNum++;
					}else{
						failedNum++;
					}
				}
			}
		}else{ // 添加页面下的所有接口
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("pageId", onlinePageId);
			List<InterfaceOnline> onlineInterfaces = interfaceOnlineDaoImpl.selectOnlineInterfaceByConditionsByPage(map);
			for(InterfaceOnline inter : onlineInterfaces){
				int res = importOnlineInterface(projectId, onlineModuleId, onlinePageId, String.valueOf(inter.getInterfaceId()));
				if(res == 1){
					successNum++;
				}else if( res == 2){
					existNum++;
				}else{
					failedNum++;
				}
			}
		}
		Map<String, Integer> interfaceNum = new HashMap<String, Integer>();
		interfaceNum.put("successNum", successNum);
		interfaceNum.put("existNum", existNum);
		interfaceNum.put("failedNum", failedNum);
		return interfaceNum;
	}
	
	/*private ParamForm convertParam2ParamForm(Param param, Set<String> dictNameSet){
		if (dictNameSet == null) {
			dictNameSet = new HashSet<String>();
		}
		ParamForm paramForm = new ParamForm();
		paramForm.setParamName(param.getParamName());
		paramForm.setParamDesc(param.getParamDesc());
		paramForm.setParamType(param.getParamType());
		paramForm.setIsNecessary(param.getIsNecessary());
		paramForm.setRemark(param.getRemark());
		paramForm.setMock(param.getMock());
		Dict dict = param.getDict();
		if(dict == null || dictNameSet.contains(String.valueOf(dict.getDictId()))){
			return paramForm;
		}else{
			dictNameSet.add(String.valueOf(dict.getDictId()));
			DictForm dictForm = new DictForm();
			dictForm.setDictName(dict.getDictName());
			dictForm.setDictDesc(dict.getDictDesc());
			dictForm.setProductId(dict.getProductId());
			List<Param> dictParam = dict.getParams();
			if(dictParam != null){
				int len = dictParam.size();
				ParamForm[] forms = new ParamForm[len];
				for(int i=0; i<len; i++){
					Param p = dictParam.get(i);
					ParamForm pForm = convertParam2ParamForm(p, dictNameSet);
					forms[i] = pForm;
				}
				dictForm.setParams(forms);
			}
			paramForm.setDict(dictForm);
		}
		return paramForm;
	}*/
	
	public int modifyMockContent(Long paramId, String mockContent){
		int res = 0;
		if(paramId == null){
			return res;
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("paramId", paramId);
		paramMap.put("mock", mockContent);
		res = paramDaoImpl.updateParam(paramMap);
		return res;
	}
	
	public Dict searchParamDict(Long dictId){
		Dict dict = null;
		if(dictId == null){
			return dict;
		}
		dict = dictServiceImpl.getDict(dictId);
		return dict;
	}
	
	public Dict saveObjectDict(Dict dict){
		if(dict != null){
			dict.setDictId(null);
			dictServiceImpl.copyDict(dict);
		};
		
		return dict;
	}
	
	public int operateDictParamDisplay(DictParamDisplay dictParamDisplay) {

		return interfaceDaoImpl.operateDictParamDisplay(dictParamDisplay);
	}

	public List<DictParamDisplay> queryDictParamList(DictParamDisplay dictParamDisplay) {

		return interfaceDaoImpl.queryDictParamList(dictParamDisplay);
	}

	public DictParamDisplay queryDictParam(DictParamDisplay display) {

		return interfaceDaoImpl.queryDictParam(display);
	}
	
	public Long getInterStatus(Long interId){
		return interfaceDaoImpl.getInterStatus(interId);
	}
	
	public List<Long> selectInterfaceIdByRequestUrl(String requestUrl){
		return interfaceDaoImpl.selectInterfaceIdByRequestUrl(requestUrl);
	}
	
	public List<Redis> selectInterfaceRedis(Long interId){
		return interfaceDaoImpl.selectInterfaceRedis(interId);
	}

	public List<Redis> saveInterfaceRedis(Long interfaceId,
			List<Redis> redisList) {
		if(redisList==null){
			if(selectInterfaceRedis(interfaceId)!=null){
				interfaceDaoImpl.updateInterfaceRedis(interfaceId,null);
			}
			return null;
		}
		for(int i=0;i<redisList.size();i++){
			Redis redis = redisList.get(i);
			redis.setInterfaceId(interfaceId);
			if(redis.getRedisId()==null){
				interfaceDaoImpl.insertInterfaceRedis(redis);
			}else{
				interfaceDaoImpl.updateInterfaceRedis(null,redis);
			}
		}
		return selectInterfaceRedis(interfaceId);
	}

	public int deleteInterfaceRedis(Long redisId) {
		return interfaceDaoImpl.deleteInterfaceRedis(redisId);
	}

	public List<Interface> getInterSimpleInfo(List<String> interfaceIdList) {
		return interfaceDaoImpl.getInterSimpleInfo(interfaceIdList);
	}
	
	/**
	 * 获取接口对应的路径：项目名称-模块名称-页面名称-接口名称.
	 * */
	public String getPathByInterfaceId(Long interfaceId) {
		Interface inter = getInterfaceInfo(interfaceId);
		Long pageId = inter.getPageId();
		Page page = pageDaoImpl.selectPageById(pageId);
		Long moduleId = page.getModuleId();
		Module module = moduleDaoImpl.selectModuleById(moduleId);
		Long projectId = module.getProjectId();
		ProjectModel projectModel = projectDaoImpl.selectProjectModelByProjectId(projectId);
		String path = "项目名称：" + projectModel.getProjectName() +
				"；模块名称：" + module.getModuleName() +
				"；页面名称：" + page.getPageName() +
				"；接口名称：" + inter.getInterfaceName() + "。";
		return path;
	}

	public boolean interNameExist(String interName) {
		Interface inter = interfaceDaoImpl.selectInterfaceByInterName(interName);
		if(inter==null||inter.getInterfaceId()==null){
			return false;
		}else{
			return true;
		}
	}
	
}