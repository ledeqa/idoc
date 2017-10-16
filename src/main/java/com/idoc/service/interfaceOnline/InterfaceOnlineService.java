package com.idoc.service.interfaceOnline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idoc.constant.CommonConstant;
import com.idoc.constant.LogConstant;
import com.idoc.dao.docManage.InterfaceOnlineDaoImpl;
import com.idoc.dao.docManage.OnlineVersionDaoImpl;
import com.idoc.dao.docManage.ParamDaoImpl;
import com.idoc.dao.login.LoginDaoImpl;
import com.idoc.form.OnlineInterfaceForm;
import com.idoc.model.InterfaceOnline;
import com.idoc.model.OnlinePage;
import com.idoc.model.OnlineVersion;
import com.idoc.model.Param;
import com.idoc.model.UserModel;
import com.idoc.service.dict.DictServiceImpl;
import com.idoc.service.docManage.InterServiceUtilImpl;
import com.idoc.shiro.token.TokenManager;
import com.idoc.util.ErrorMessageUtil;
import com.idoc.util.LoginUserInfoUtil;

import net.sf.json.JSONObject;

@Service("interfaceOnlineService")
public class InterfaceOnlineService {

	@Autowired
	@Resource(name = "interfaceOnlineDaoImpl")
	private InterfaceOnlineDaoImpl interfaceOnlineDaoImpl;
	@Resource(name = "loginDaoImpl")
	private LoginDaoImpl loginDaoImpl;
	@Autowired
	private OnlineVersionDaoImpl onlineVersionDaoImpl;
	@Autowired
	private ParamDaoImpl paramDaoImpl;
	@Autowired
	private InterServiceUtilImpl interServiceUtilImpl;
	@Autowired
	private DictServiceImpl dictServiceImpl;

	public List<InterfaceOnline> selectOnlineInterfaceByConditionsByPage(String creatorEnglishName, String interfaceName, String url, int requestType, int interfaceType, String selectFromTime, String selectEndTime) {

		Long creatorId = null;
		if (creatorEnglishName != null) {
			creatorId = Long.valueOf(loginDaoImpl.queryUserIdByUserName(creatorEnglishName));
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("creatorId", creatorId);
		map.put("interfaceName", interfaceName);
		map.put("url", url);
		map.put("interfaceType", interfaceType);
		map.put("requestType", requestType);
		map.put("selectFromTime", selectFromTime);
		map.put("selectEndTime", selectEndTime);
		return interfaceOnlineDaoImpl.selectOnlineInterfaceByConditionsByPage(map);
	}

	public int updateOnlineInterface(InterfaceOnline interf) {

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceName", interf.getInterfaceName());
		map.put("creatorId", interf.getCreatorId());
		map.put("interfaceType", interf.getInterfaceType());
		map.put("requestType", interf.getRequestType());
		map.put("url", interf.getUrl());
		map.put("desc", interf.getDesc());
		map.put("isNeedInterfaceTest", interf.getIsNeedInterfaceTest());
		map.put("isNeedPressureTest", interf.getIsNeedPressureTest());
		map.put("expectOnlineTime", interf.getExpectOnlineTime());
		map.put("realOnlineTime", interf.getRealOnlineTime());
		map.put("expectTestTime", interf.getExpectTestTime());
		map.put("realTestTime", interf.getRealTestTime());
		map.put("reqPeopleIds", interf.getReqPeopleIds());
		map.put("frontPeopleIds", interf.getFrontPeopleIds());
		map.put("behindPeopleIds", interf.getBehindPeopleIds());
		map.put("clientPeopleIds", interf.getClientPeopleIds());
		map.put("testPeopleIds", interf.getTestPeopleIds());
		map.put("interfaceStatus", CommonConstant.INTERFACE_STATUS_ONLINE);
		map.put("onlineVersion", interf.getOnlineVersion());
		interfaceOnlineDaoImpl.updateOnlineInterface(map);
		return interfaceOnlineDaoImpl.updateOnlineInterface(map);
	}

	public int deleteOnlineInterface(Long interfaceId) {

		InterfaceOnline interfaceOnline = new InterfaceOnline();
		interfaceOnline.setInterfaceId(interfaceId);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", interfaceId);
		map.put("status", 0);
		return interfaceOnlineDaoImpl.updateOnlineInterface(map);
	}

	public InterfaceOnline selectOnlineInterfaceById(Long interfaceId) {

		return interfaceOnlineDaoImpl.selectOnlineInterfaceById(interfaceId);

	}

	public List<InterfaceOnline> selectOnlineInterfaceListByProductByPage(String url, Long interfaceId, Long ProductId) {

		return interfaceOnlineDaoImpl.selectOnlineInterfaceListByProductByPage(url, interfaceId, ProductId);
	}

	public InterfaceOnline insertOnlineInterface(InterfaceOnline interf) {

		return interfaceOnlineDaoImpl.insertOnlineInterface(interf);
	}
	@Transactional(rollbackFor={Exception.class})
	public InterfaceOnline saveApiDocInterface(InterfaceOnline interOnline){
		InterfaceOnline onlineInter = interfaceOnlineDaoImpl.insertOnlineInterface(interOnline);
		Long interfaceId = onlineInter.getInterfaceId();
		if(interfaceId == null){
			return onlineInter;
		}
		Map<String,Object> paramMap = new HashMap<String,Object>();
		List<Param> reqParams = onlineInter.getReqParams();
		for(Param pm : reqParams){
			pm.setParamId(null);
			paramDaoImpl.insertParam(pm);
			// 保存接口和参数之间的联系
			paramMap.clear();
			paramMap.put("interfaceId", interfaceId);
			paramMap.put("paramId", pm.getParamId());
			paramDaoImpl.insertOnlineRequestParam(paramMap);
		}
		List<Param> retParams = onlineInter.getRetParams();
		for(Param pm : retParams){
			pm.setParamId(null);
			paramDaoImpl.insertParam(pm);
			// 保存接口和参数之间的联系
			paramMap.clear();
			paramMap.put("interfaceId", interfaceId);
			paramMap.put("paramId", pm.getParamId());
			paramDaoImpl.insertOnlineReturnParam(paramMap);
		}
		return interOnline;
	}
	@Transactional(rollbackFor={Exception.class})
	public InterfaceOnline saveOnlineInterfaceInfor(OnlineInterfaceForm form) {

		// 将接口传递的form信息，转换成interface model
		InterfaceOnline inter = new InterfaceOnline();
//		String englishName = LoginUserInfoUtil.getUserEnglishName();
//		String userId = loginDaoImpl.queryUserIdByUserName(englishName);
		Long userId = TokenManager.getUserId();
		if (null == userId) {
			ErrorMessageUtil.put("没有找到用户信息，请登录后再操作！");
			LogConstant.runLog.error("userId=" + userId + " 没有找到用户信息，请登录后再操作！");
			throw new RuntimeException("userId=" + userId + " 没有找到用户信息，请登录后再操作！");
		}
		inter.setDesc(form.getDesc());
		Long editVersion = 1L;
		Long currentEditVersion = onlineVersionDaoImpl.selectCurrentOnlineVersionById(form.getInterfaceId());
		if (currentEditVersion == null) {
			currentEditVersion = 1L;
		}
		editVersion = currentEditVersion + 1;
		inter.setInterfaceId(form.getInterfaceId());
		inter.setExpectOnlineTime(form.getExpectOnlineTime());
		inter.setReqPeopleIds(form.getReqPeopleIds());
		inter.setFrontPeopleIds(form.getFrontPeopleIds());
		inter.setBehindPeopleIds(form.getBehindPeopleIds());
		inter.setClientPeopleIds(form.getClientPeopleIds());
		inter.setTestPeopleIds(form.getTestPeopleIds());
		inter.setInterfaceName(form.getInterfaceName());
		inter.setInterfaceType(form.getInterfaceType());
		inter.setInterfaceStatus(9);
		inter.setIsNeedInterfaceTest(form.getIsNeedInterfaceTest());
		inter.setIsNeedPressureTest(form.getIsNeedPressureTest());
		inter.setOnlinePageId(form.getOnlinePageId());
		inter.setRequestType(form.getRequestType());
		inter.setStatus(1);
		inter.setExpectTestTime(form.getExpectTestTime());
		inter.setUrl(form.getUrl());
		inter.setOnlineVersion(editVersion);
		inter.setFtlTemplate(form.getFtlTemplate());
		inter.setIterVersion(form.getIterVersion());
		// 保存版本相关信息
		InterfaceOnline oldInter = getOnlineInterfaceInfo(form.getInterfaceId());
		String interJson = JSONObject.fromObject(oldInter).toString();
		OnlineVersion interVersion = new OnlineVersion();
		interVersion.setCommitId(userId);
		interVersion.setInterfaceId(form.getInterfaceId());
		interVersion.setSnapshot(interJson);
		interVersion.setStatus(1);
		interVersion.setVersionNum(currentEditVersion);
		if (StringUtils.isBlank(form.getVersionDesc())) {
			interVersion.setVersionDesc("极速保存");
		} else {
			interVersion.setVersionDesc(form.getVersionDesc());
		}
		interfaceOnlineDaoImpl.updateOnlineInter(inter);
		onlineVersionDaoImpl.insertOnlineVersion(interVersion);

		// reqParams
		List<Param> reqParams = interServiceUtilImpl.saveReqParams(form.getInterfaceId(), form.getReqParams(), true, false);
		inter.setReqParams(reqParams);
		// retParams
		List<Param> retParams = interServiceUtilImpl.saveRetParams(form.getInterfaceId(), form.getRetParams(), true, false);
		inter.setRetParams(retParams);
		return inter;
	}

	public InterfaceOnline getOnlineInterfaceInfo(Long id) {

		Map<String,Object> condMap = new HashMap<String,Object>();
		condMap.put("interfaceId", id);
		List<InterfaceOnline> interList = interfaceOnlineDaoImpl.selectOnlineInterfaceListByCond(condMap);
		if (interList == null || interList.isEmpty() || interList.size() > 1) {
			ErrorMessageUtil.put("id=" + id + "查到的接口文档有异常 interList=" + interList);
			LogConstant.runLog.error("id=" + id + "查到的接口文档有异常 interList=" + interList);
			return new InterfaceOnline();
		}
		InterfaceOnline inter = interList.get(0);
		// 设置用户相关信息
		inter.setFrontPeoples(loginDaoImpl.selectUserModelListByIds(inter.getFrontPeopleIds()));
		inter.setCreator(loginDaoImpl.queryUserModelByUserId(inter.getCreatorId()));
		inter.setReqPeoples(loginDaoImpl.selectUserModelListByIds(inter.getReqPeopleIds()));
		inter.setTestPeoples(loginDaoImpl.selectUserModelListByIds(inter.getTestPeopleIds()));
		inter.setClientPeoples(loginDaoImpl.selectUserModelListByIds(inter.getClientPeopleIds()));
		inter.setBehindPeoples(loginDaoImpl.selectUserModelListByIds(inter.getBehindPeopleIds()));
		// 设置请求参数信息
		List<Param> reqList = paramDaoImpl.selectOnlineRequestParamByInterfaceId(inter.getInterfaceId());
		if (reqList != null && !reqList.isEmpty()) {
			for (Param pm : reqList) {
				if (pm.getDictId() != null) {
					pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
				}
			}
		}
		inter.setReqParams(reqList);
		// 设置返回参数信息
		List<Param> retList = paramDaoImpl.selectOnlineReturnParamByInterfaceId(inter.getInterfaceId());
		if (retList != null && !retList.isEmpty()) {
			for (Param pm : retList) {
				if (pm.getDictId() != null) {
					pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
				}
			}
		}
		inter.setRetParams(retList);
		return inter;
	}

	public List<InterfaceOnline> getInterfaceListByPages(List<OnlinePage> pageList){
		if(CollectionUtils.isNotEmpty(pageList)){
			List<Long> pageIds = new ArrayList<Long>();
			for(OnlinePage page: pageList){
				pageIds.add(page.getOnlinePageId());
			}

			List<InterfaceOnline> interfaceList = interfaceOnlineDaoImpl.selectInterfaceListByPages(pageIds);

			return interfaceList;
		}

		return null;
	}

	public InterfaceOnline selectOnlineInterfaceByConditions(Map<String, Object> paramMap) {
		InterfaceOnline onlineInter = interfaceOnlineDaoImpl.selectOnlineInterfaceByConditions(paramMap);
		if(onlineInter==null){
			return null;
		}
		//设置创建者信息
		Long creatorId = onlineInter.getCreatorId();
		if(creatorId != null){
			UserModel creator = loginDaoImpl.queryUserModelByUserId(creatorId);
			onlineInter.setCreator(creator);
		}
		//设置人员信息
		onlineInter.setClientPeoples(loginDaoImpl.selectUserModelListByIds(onlineInter.getClientPeopleIds()));
		onlineInter.setFrontPeoples(loginDaoImpl.selectUserModelListByIds(onlineInter.getFrontPeopleIds()));
		onlineInter.setReqPeoples(loginDaoImpl.selectUserModelListByIds(onlineInter.getReqPeopleIds()));
		onlineInter.setTestPeoples(loginDaoImpl.selectUserModelListByIds(onlineInter.getTestPeopleIds()));
		onlineInter.setBehindPeoples(loginDaoImpl.selectUserModelListByIds(onlineInter.getBehindPeopleIds()));
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
	public void moveInterface(Long onlineInterfaceId, Long onlinePageId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", onlineInterfaceId);
		map.put("onlinePageId", onlinePageId);
		int num = interfaceOnlineDaoImpl.updateOnlineInterface(map);
		if(num <= 0){
			ErrorMessageUtil.put("移动接口失败！");
		}
	}
}
