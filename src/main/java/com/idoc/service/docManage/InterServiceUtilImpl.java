
package com.idoc.service.docManage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.constant.CommonConstant;
import com.idoc.constant.LogConstant;
import com.idoc.dao.docManage.ParamDaoImpl;
import com.idoc.form.ParamForm;
import com.idoc.model.Dict;
import com.idoc.model.Param;
import com.idoc.service.dict.DictServiceImpl;
import com.idoc.util.ErrorMessageUtil;

@Service("interServiceUtilImpl")
public class InterServiceUtilImpl {
	
	@Autowired
	private ParamDaoImpl paramDaoImpl;
	
	@Autowired
	private DictServiceImpl dictServiceImpl;
	
	public List<Param> saveReqParams(Long interfaceId, ParamForm[] reqParams, boolean isOnline, boolean isRollback){
		List<Param> pList = new ArrayList<Param>();
		if(interfaceId == null || reqParams == null || reqParams.length < 0){
			ErrorMessageUtil.put("参数错误");
			LogConstant.runLog.error("参数错误 interfaceId="+interfaceId+" reqParams="+reqParams);
			throw new RuntimeException("参数错误 interfaceId="+interfaceId+" reqParams="+reqParams);
		}
		// 获取已经存在的param id的列表
		List<Long> oldIds = null;
		if(!isOnline){
			oldIds = paramDaoImpl.selectRequestParamIdsByInterfaceId(interfaceId);
		}else{
			oldIds = paramDaoImpl.selectOnlineRequestParamIdsByInterfaceId(interfaceId);
		}
		List<Long> sameIds = new ArrayList<Long>();
		if(oldIds == null){
			oldIds = new ArrayList<Long>();
		}
		Map<String,Object> paramMap = new HashMap<String,Object>();
		for(ParamForm pf : reqParams){
			if(isOnline){
				pf.setParamId(null);
			}
			Param pm = new Param();
			if(pf.getParamType().equalsIgnoreCase(CommonConstant.PARAM_TYPE_OBJECT) 
					|| pf.getParamType().equalsIgnoreCase(CommonConstant.PARAM_TYPE_OBJECT_ARRAY)
					|| pf.getDictId() != null){
				// 字典类型 包括匿名字典类型和非匿名字典类型
				Dict dict = dictServiceImpl.saveDictForm(pf.getDict());
				if(dict != null){
					pm.setDictId(dict.getDictId());
					pm.setDict(dict);
				}
			}
			pm.setParamId(pf.getParamId());
			pm.setIsNecessary(pf.getIsNecessary());
			pm.setMock(pf.getMock());
			pm.setParamDesc(pf.getParamDesc());
			pm.setParamName(pf.getParamName());
			String isDecimal = pf.getParamType();
			if(isDecimal.equals("bigdecimal")||isDecimal=="bigdecimal"){
				isDecimal="double";
			}
			pm.setParamType(isDecimal);
			pm.setRemark(pf.getRemark());
			pm.setStatus(1);
			
			if((pm.getDictId() == null  || pm.getDictId() == 0) && pm.getDict() == null){ // 默认0为null
				pm.setDictId(null);
			}
			if(pf.getParamId() != null && oldIds.contains(pf.getParamId())){
				// 已经存在的参数，只需要更新参数相关字段和字典信息
				sameIds.add(pf.getParamId());
				paramDaoImpl.updateParam(pm);
			}else{
				if(pm.getParamId() != null){
					if(!isRollback){
						ErrorMessageUtil.put("参数错误");
						LogConstant.runLog.error("参数错误 ParamId="+pm.getParamId()+" 不在旧的参数里");
						throw new RuntimeException("参数错误 ParamId="+pm.getParamId()+" 不在旧的参数里");
					}
				}
				paramDaoImpl.insertParam(pm);
				// 保存接口和参数之间的联系
				paramMap.clear();
				paramMap.put("interfaceId", interfaceId);
				paramMap.put("paramId", pm.getParamId());
				if(!isOnline){
					paramDaoImpl.insertRequestParam(paramMap);
				}else{
					paramDaoImpl.insertOnlineRequestParam(paramMap);
				}
			}
			
			pList.add(pm);
		}
		// 删除多余的老的参数
		for(Long id : oldIds){
			if(!sameIds.contains(id)){
				//删除接口关系 和 param数据 物理删除
				paramMap.clear();
				paramMap.put("paramId", id);
				paramDaoImpl.deleteParam(paramMap);
				paramMap.put("interfaceId", interfaceId);
				if(!isOnline){
					paramDaoImpl.deleteRequestParam(paramMap);
				}else{
					paramDaoImpl.deleteOnlineRequestParam(paramMap);
				}
			}
		}
		return pList;
	}
	
	public List<Param> saveRetParams(Long interfaceId, ParamForm[] retParams, boolean isOnline, boolean isRollback){
		List<Param> pList = new ArrayList<Param>();
		if(interfaceId == null || retParams == null || retParams.length < 0){
			ErrorMessageUtil.put("参数错误");
			LogConstant.runLog.error("参数错误 interfaceId="+interfaceId+" reqParams="+retParams);
			throw new RuntimeException("参数错误 interfaceId="+interfaceId+" reqParams="+retParams);
		}
		// 获取已经存在的param id的列表
		List<Long> oldIds = null;
		if(!isOnline){
			oldIds = paramDaoImpl.selectReturnParamIdsByInterfaceId(interfaceId);
		}else{
			oldIds = paramDaoImpl.selectOnlineReturnParamIdsByInterfaceId(interfaceId);
		}
		List<Long> sameIds = new ArrayList<Long>();
		if(oldIds == null){
			oldIds = new ArrayList<Long>();
		}
		Map<String,Object> paramMap = new HashMap<String,Object>();
		for(ParamForm pf : retParams){
			if(isOnline){
				pf.setParamId(null);
			}
			Param pm = new Param();
			if(pf.getParamType().equalsIgnoreCase(CommonConstant.PARAM_TYPE_OBJECT) 
					|| pf.getParamType().equalsIgnoreCase(CommonConstant.PARAM_TYPE_OBJECT_ARRAY)
					|| pf.getDictId() != null){
				// 字典类型 包括匿名字典类型和非匿名字典类型
				Dict dict = dictServiceImpl.saveDictForm(pf.getDict());
				if(dict != null){
					pm.setDictId(dict.getDictId());
					pm.setDict(dict);
				}
			}
			pm.setParamId(pf.getParamId());
			pm.setIsNecessary(pf.getIsNecessary());
			pm.setMock(pf.getMock());
			pm.setParamDesc(pf.getParamDesc());
			pm.setParamName(pf.getParamName());
			pm.setParamType(pf.getParamType());
			pm.setRemark(pf.getRemark());
			pm.setStatus(1);
			
			if((pm.getDictId() == null  || pm.getDictId() == 0) && pm.getDict() == null){ // 默认0为null
				pm.setDictId(null);
			}
			if(pf.getParamId() != null && oldIds.contains(pf.getParamId())){
				// 已经存在的参数，只需要更新参数相关字段和字典信息
				sameIds.add(pf.getParamId());
				paramDaoImpl.updateParam(pm);
			}else{
				if(pm.getParamId() != null){
					if(!isRollback){
						ErrorMessageUtil.put("参数错误");
						LogConstant.runLog.error("参数错误 ParamId="+pm.getParamId()+" 不在旧的参数里");
						throw new RuntimeException("参数错误 ParamId="+pm.getParamId()+" 不在旧的参数里");
					}
				}
				paramDaoImpl.insertParam(pm);
				// 保存接口和参数之间的联系
				paramMap.clear();
				paramMap.put("interfaceId", interfaceId);
				paramMap.put("paramId", pm.getParamId());
				if(!isOnline){
					paramDaoImpl.insertReturnParam(paramMap);
				}else{
					paramDaoImpl.insertOnlineReturnParam(paramMap);
				}
			}
			
			pList.add(pm);
		}
		// 删除多余的老的参数
		for(Long id : oldIds){
			if(!sameIds.contains(id)){
				//删除接口关系 和 param数据 物理删除
				paramMap.clear();
				paramMap.put("paramId", id);
				paramDaoImpl.deleteParam(paramMap);
				paramMap.put("interfaceId", interfaceId);
				if(!isOnline){
					paramDaoImpl.deleteReturnParam(paramMap);
				}else{
					paramDaoImpl.deleteOnlineReturnParam(paramMap);
				}
			}
		}
		return pList;
	}
	
}