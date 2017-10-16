package com.idoc.service.dict;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idoc.constant.CommonConstant;
import com.idoc.dao.dict.DictDaoImpl;
import com.idoc.dao.docManage.ParamDaoImpl;
import com.idoc.dao.login.LoginDaoImpl;
import com.idoc.dao.pagination.PaginationInfo;
import com.idoc.dao.pagination.PaginationList;
import com.idoc.form.DictForm;
import com.idoc.form.ParamForm;
import com.idoc.model.Dict;
import com.idoc.model.DictVersion;
import com.idoc.model.Param;
import com.idoc.shiro.token.TokenManager;
import com.idoc.util.DateJsonValueProcessor;
import com.idoc.util.ErrorMessageUtil;
import com.idoc.util.LoginUserInfoUtil;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Service
public class DictServiceImpl {
	
	@Autowired
	private DictDaoImpl dictDaoImpl;
	
	@Autowired
	private ParamDaoImpl paramDaoImpl;
	
	@Autowired
	private LoginDaoImpl loginDaoImpl;
	
	public PaginationList<Dict> queryDicts(Map<String,String> paraMap, PaginationInfo paginationInfo) {
	
		return dictDaoImpl.queryDicts(paraMap, paginationInfo);
	}
	
	public List<Dict> queryDictsByProduct(Long productId){
		Dict dict = new Dict();
		dict.setProductId(productId);
		
		return dictDaoImpl.selectDictsByProductId(dict);
	}
	
	public Dict queryDict(Map<String, String> map) {
		Dict dict = dictDaoImpl.queryDict(map);
		dict = recurselyQueryDict(dict);
		return dict;
	}
	
	public Dict queryDictByName(Map<String, String> map) {
		Dict dict = dictDaoImpl.queryDictByName(map);
		return dict;
	}
	/**
	 * 递归查询字典
	 * @param dict
	 * @return
	 */
	public Dict recurselyQueryDict(Dict dict){
		if(dict == null){
			return dict;
		}
		Long dictId = dict.getDictId();
		Map<String, Long> paramMap = new HashMap<String, Long>();
		paramMap.put("dictId", dictId);
		List<Param> paramList = paramDaoImpl.queryParams(paramMap);
		dict.setParams(paramList);
		if(paramList != null){
			for(Param param : paramList){
				Long param_dict_id = param.getDictId();
				if(param_dict_id != null){
					Map<String, String> dictMap = new HashMap<String, String>();
					dictMap.put("dictId", String.valueOf(param_dict_id));
					Dict p_dict = dictDaoImpl.queryDict(dictMap);
					param.setDict(p_dict);
					if(p_dict != null){
						recurselyQueryDict(p_dict);
					}
				}
			}
		}
		return dict;
	}
	/**
	 * 递归保存字典数据
	 */
	@Transactional(rollbackFor={Exception.class})
	public Dict saveDictForm(DictForm dictForm) {
	
		if (dictForm == null) {
			return null;
		}
		Dict dict = new Dict();
		dict.setDictDesc(dictForm.getDictDesc());
		dict.setDictId(dictForm.getDictId());
		dict.setDictName(StringUtils.capitalize(dictForm.getDictName()));
		dict.setProductId(dictForm.getProductId());
		if (dict.getDictId() == null) {
			// 新加dict信息
			dict.setVersion(1L);
			dict.setStatus(1);
			dictDaoImpl.insertDict(dict);
		} else {
			JsonConfig config = new JsonConfig();
			config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor(CommonConstant.DATE_FORMAT_PATTERN));
			// 保存版本信息
//			String username = LoginUserInfoUtil.getUserEnglishName();
//			String userId = loginDaoImpl.queryUserIdByUserName(username);
			Long userId = TokenManager.getUserId();
			Dict oldDict = getDict(dict.getDictId());
			//String dictJson = JSONObject.fromObject(oldDict).toString();
			String dictJson = JSONObject.fromObject(oldDict, config).toString();
			DictVersion dictVersion = new DictVersion();
			dictVersion.setCommitId(userId);
			dictVersion.setDictId(dict.getDictId());
			dictVersion.setSnapshot(dictJson);
			dictVersion.setStatus(1);
			dictVersion.setVersionDesc("极速保存");
			Long currentVersion = dictDaoImpl.selectCurrentDictVersionById(dict.getDictId());
			if(currentVersion == null){
				currentVersion = 1L;
				dictVersion.setVersionNum(currentVersion);
			}else{
				dictVersion.setVersionNum(currentVersion + 1);
			}
			dictDaoImpl.insertDictVersion(dictVersion);
			dict.setVersion(dictVersion.getVersionNum() + 1);
			dictDaoImpl.updateDict(dict);
		}
		ParamForm[] params = dictForm.getParams();
		if (params != null) {
			List<Param> pList = new ArrayList<Param>();
			// 获取已经存在的param id的列表
			List<Long> oldIds = paramDaoImpl.selectDictParamIdsByDictId(dict.getDictId());
			List<Long> sameIds = new ArrayList<Long>();
			if (oldIds == null) {
				oldIds = new ArrayList<Long>();
			}
			Map<String,Object> paramMap = new HashMap<String,Object>();
			for (ParamForm pf : params) {
				Param pm = new Param();
				if (pf.getParamType().equalsIgnoreCase(CommonConstant.PARAM_TYPE_OBJECT) || pf.getParamType().equalsIgnoreCase(CommonConstant.PARAM_TYPE_OBJECT_ARRAY) 
						|| pf.getDictId() != null) {
					// 字典类型 包括匿名字典类型和非匿名字典类型
					if(pf.getDict()!=null){
						Dict subDict = saveDictForm(pf.getDict());
						pm.setDictId(subDict.getDictId());
					}
					if(pf.getDictId()!=null){
						pm.setDictId(pf.getDictId());
					}
				}
				pm.setParamId(pf.getParamId());
				pm.setIsNecessary(pf.getIsNecessary());
				String mock = pf.getMock();
				//mock字段做处理，防止在mock js和mock data时出现错误
				if(StringUtils.isNotEmpty(mock)){
					mock = mock.replaceAll("\"", "");
					mock = mock.replaceAll("“", "").replaceAll("”", "");
				}
				pm.setMock(mock);
				pm.setParamDesc(pf.getParamDesc());
				pm.setParamName(StringUtils.uncapitalize(pf.getParamName()));
				pm.setParamType(pf.getParamType());
				pm.setRemark(pf.getRemark());
				pm.setStatus(1);
//				if(pf.getDict() != null){
//					pm.setDictId(pf.getDict().getDictId());
//				}
				if((pm.getDictId() == null  || pm.getDictId() == 0) && pm.getDict() == null){ // 默认0为null
					pm.setDictId(null);
				}
				if (pf.getParamId() != null && oldIds.contains(pf.getParamId())) {
					// 已经存在的参数，只需要更新参数相关字段和字典信息
					sameIds.add(pf.getParamId());
					paramDaoImpl.updateParam(pm);
				} else {
					if (pm.getParamId() != null) {
						/*ErrorMessageUtil.put("参数错误");
						LogConstant.runLog.error("参数错误 ParamId=" + pm.getParamId() + " 不在旧的参数里");
						throw new RuntimeException("参数错误 ParamId=" + pm.getParamId() + " 不在旧的参数里");*/
						pm.setParamId(null);
					}
					paramDaoImpl.insertParam(pm);
					// 保存字典和参数之间的联系
					paramMap.clear();
					paramMap.put("dictId", dict.getDictId());
					paramMap.put("paramId", pm.getParamId());
					paramDaoImpl.insertDictParam(paramMap);
				}
				pList.add(pm);
			}
			dict.setParams(pList);
			// 删除多余的老的参数
			for(Long id : oldIds){
				if(!sameIds.contains(id)){
					//删除字典关系 和 param数据 物理删除
					paramMap.clear();
					paramMap.put("paramId", id);
					paramDaoImpl.deleteParam(paramMap);
					paramMap.put("dictId", dict.getDictId());
					paramDaoImpl.deleteDictParam(paramMap);
				}
			}
		}
		
		return dict;
	}
	/**
	 * 保存字典对象
	 * @param dict
	 * @return
	 */
	@Transactional(rollbackFor={Exception.class})
	public Dict copyDict(Dict dict) {
		if (dict == null) {
			return null;
		}
		if (dict.getDictId() == null) {
			// 新加dict信息
			dict.setVersion(1L);
			dict.setStatus(1);
			dictDaoImpl.insertDict(dict);
		} else {
			JsonConfig config = new JsonConfig();
			config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor(CommonConstant.DATE_FORMAT_PATTERN));
			//保存版本信息
//			String username = LoginUserInfoUtil.getUserEnglishName();
//			String userId = loginDaoImpl.queryUserIdByUserName(username);
			Long userId = TokenManager.getUserId();
			Dict oldDict = getDict(dict.getDictId());
			String dictJson = JSONObject.fromObject(oldDict, config).toString();
			DictVersion dictVersion = new DictVersion();
			dictVersion.setCommitId(userId);
			dictVersion.setDictId(dict.getDictId());
			dictVersion.setSnapshot(dictJson);
			dictVersion.setStatus(1);
			dictVersion.setVersionDesc("极速保存");
			Long currentVersion = dictDaoImpl.selectCurrentDictVersionById(dict.getDictId());
			if(currentVersion == null){
				currentVersion = 1L;
				dictVersion.setVersionNum(currentVersion);
			}else{
				dictVersion.setVersionNum(currentVersion + 1);
			}
			dictDaoImpl.insertDictVersion(dictVersion);
			dict.setVersion(dictVersion.getVersionNum() + 1);
			dictDaoImpl.updateDict(dict);
		}
		List<Param> params = dict.getParams();
		if (params != null) {
			List<Param> pList = new ArrayList<Param>();
			//获取已经存在的param id的列表
			List<Long> oldIds = paramDaoImpl.selectDictParamIdsByDictId(dict.getDictId());
			List<Long> sameIds = new ArrayList<Long>();
			if (oldIds == null) {
				oldIds = new ArrayList<Long>();
			}
			Map<String,Object> paramMap = new HashMap<String,Object>();
			for (Param pf : params) {
				Param pm = new Param();
				if (pf.getParamType().equalsIgnoreCase(CommonConstant.PARAM_TYPE_OBJECT) 
						|| pf.getParamType().equalsIgnoreCase(CommonConstant.PARAM_TYPE_OBJECT_ARRAY) 
						|| pf.getDictId() != null) {
					if(pf.getParamType().equalsIgnoreCase(CommonConstant.PARAM_TYPE_OBJECT) 
							|| pf.getParamType().equalsIgnoreCase(CommonConstant.PARAM_TYPE_OBJECT_ARRAY)){
						//匿名字典类型
						pf.setDictId(null);
						pf.getDict().setDictId(null);
						Dict subDict = copyDict(pf.getDict());
						pm.setDictId(subDict.getDictId());
						pm.setDict(subDict);
					}else{
						//非匿名字典
						pm.setDictId(pf.getDictId());
						pm.setDict(pf.getDict());
					}
				}
				pm.setParamId(pf.getParamId());
				pm.setIsNecessary(pf.getIsNecessary());
				String mock = pf.getMock();
				//mock字段做处理，防止在mock js和mock data时出现错误
				if(StringUtils.isNotEmpty(mock)){
					mock = mock.replaceAll("\"", "");
					mock = mock.replaceAll("“", "").replaceAll("”", "");
				}
				pm.setMock(mock);
				pm.setParamDesc(pf.getParamDesc());
				pm.setParamName(StringUtils.uncapitalize(pf.getParamName()));
				pm.setParamType(pf.getParamType());
				pm.setRemark(pf.getRemark());
				pm.setStatus(1);
				
				if((pm.getDictId() == null  || pm.getDictId() == 0) && pm.getDict() == null){ // 默认0为null
					pm.setDictId(null);
				}
				if (pf.getParamId() != null && oldIds.contains(pf.getParamId())) {
					// 已经存在的参数，只需要更新参数相关字段和字典信息
					sameIds.add(pf.getParamId());
					paramDaoImpl.updateParam(pm);
				} else {
					if (pm.getParamId() != null) {
						/*ErrorMessageUtil.put("参数错误");
						LogConstant.runLog.error("参数错误 ParamId=" + pm.getParamId() + " 不在旧的参数里");
						throw new RuntimeException("参数错误 ParamId=" + pm.getParamId() + " 不在旧的参数里");*/
						pm.setParamId(null);
					}
					paramDaoImpl.insertParam(pm);
					// 保存字典和参数之间的联系
					paramMap.clear();
					paramMap.put("dictId", dict.getDictId());
					paramMap.put("paramId", pm.getParamId());
					paramDaoImpl.insertDictParam(paramMap);
				}
				pList.add(pm);
			}
			dict.setParams(pList);
			// 删除多余的老的参数
			for(Long id : oldIds){
				if(!sameIds.contains(id)){
					//删除字典关系 和 param数据 物理删除
					paramMap.clear();
					paramMap.put("paramId", id);
					paramDaoImpl.deleteParam(paramMap);
					paramMap.put("dictId", dict.getDictId());
					paramDaoImpl.deleteDictParam(paramMap);
				}
			}
		}
		return dict;
	}
	public Dict getDict(Long dictId) {
	
		return getDict(dictId, null);
	}
	/**
	 * 获取字典，同时需要防止出现死循环的情况
	 * @param dictId
	 * @param dictIdSet
	 * @return
	 */
	public Dict getDict(Long dictId, Set<Long> dictIdSet) {
	
		if (dictIdSet == null) {
			dictIdSet = new HashSet<Long>();
		}
		Dict dict = dictDaoImpl.selectDictById(dictId);
		if (dict == null || dictIdSet.contains(dict.getDictId())) {
			return null;
		} else {
			dictIdSet.add(dict.getDictId());
		}
		// 查询dict相关的详细参数信息
		List<Param> list = dictDaoImpl.selectDictParamById(dictId);
		dict.setParams(list);
		if (list != null) {
			for (Param pm : list) {
				if (pm.getDictId() != null) {
					pm.setDict(getDict(pm.getDictId(), dictIdSet));
				}
			}
		}
		return dict;
	}
	
	public void removeDict(Long dictId) {
	
		int num = dictDaoImpl.removeDict(dictId);
		if (num <= 0) {
			ErrorMessageUtil.put("删除字典失败！");
		}
	}

	public PaginationList<Dict> queryDictHistory(Map<String, String> map, PaginationInfo paginationInfo) {
		
		return dictDaoImpl.queryDictHistory(map, paginationInfo);
	}

	public DictVersion queryDictVersion(Map<String, String> map) {

		return dictDaoImpl.queryDictVersion(map);
	}

}