package com.idoc.dao.docManage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.model.Param;

@Repository("paramDaoImpl")
public class ParamDaoImpl extends BaseMysqlDBDaoImpl{
	

	
	public Param insertParam(Param param){
		
		this.getSqlSession().insert("param.insertParam", param);
		return param;
	}
	
	public int insertRequestParam(Map<String,Object> map){
		return this.getSqlSession().insert("param.insertRequestParam", map);
	} 
	
	public int insertReturnParam(Map<String,Object> map){
		return this.getSqlSession().insert("param.insertReturnParam", map);
	}
	public int insertOnlineRequestParam(Map<String,Object> map){
		return this.getSqlSession().insert("param.insertOnlineRequestParam", map);
	} 
	
	public int insertOnlineReturnParam(Map<String,Object> map){
		return this.getSqlSession().insert("param.insertOnlineReturnParam", map);
	}
	
	public int insertDictParam(Map<String,Object> map){
		return this.getSqlSession().insert("param.insertDictParam", map);
	}
	
	public int updateParam(Param param){
		return this.getSqlSession().update("param.updateParam",param);
	}
	
	/**
	 * map作为参数的更新方法
	 * Param作为更新参数陷阱，其中的int字段如果没有赋值，默认值为0，会把数据库中的全改为0
	 * @param param
	 * @return
	 */
	public int updateParam(Map<String, Object> param){
		return this.getSqlSession().update("param.updateParamUseMap",param);
	}
	
	public int deleteParam(Map<String,Object> map){
		return this.getSqlSession().delete("param.deleteParam", map);
	}
	
	public int deleteRequestParam(Map<String,Object> map){
		return this.getSqlSession().delete("param.deleteRequestParam", map);
	}
	
	public int deleteDictParam(Map<String,Object> map){
		return this.getSqlSession().delete("param.deleteDictParam", map);
	}
	
	public int deleteReturnParam(Map<String,Object> map){
		return this.getSqlSession().delete("param.deleteReturntParam", map);
	}
	public int deleteOnlineRequestParam(Map<String,Object> map){
		return this.getSqlSession().delete("param.deleteOnlineRequestParam", map);
	}
	
	public int deleteOnlineReturnParam(Map<String,Object> map){
		return this.getSqlSession().delete("param.deleteOnlineReturntParam", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> selectRequestParamIdsByInterfaceId(Long interfaceId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", interfaceId);
		return this.getSqlSession().selectList("param.selectRequestParamIdsByInterfaceId", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> selectReturnParamIdsByInterfaceId(Long interfaceId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", interfaceId);
		return this.getSqlSession().selectList("param.selectReturnParamIdsByInterfaceId", map);
	}
	@SuppressWarnings("unchecked")
	public List<Long> selectOnlineRequestParamIdsByInterfaceId(Long interfaceId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", interfaceId);
		return this.getSqlSession().selectList("param.selectOnlineRequestParamIdsByInterfaceId", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> selectOnlineReturnParamIdsByInterfaceId(Long interfaceId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", interfaceId);
		return this.getSqlSession().selectList("param.selectOnlineReturnParamIdsByInterfaceId", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> selectDictParamIdsByDictId(Long dictId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("dictId", dictId);
		return this.getSqlSession().selectList("param.selectDictParamIdsByDictId", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Param> selectDictParamsByDictId(Long dictId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("dictId", dictId);
		return this.getSqlSession().selectList("param.selectDictParamsByDictId", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Param> selectRequestParamByInterfaceId(Long interfaceId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", interfaceId);
		return this.getSqlSession().selectList("param.selectRequestParamByInterfaceId", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Param> selectReturnParamByInterfaceId(Long interfaceId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", interfaceId);
		return this.getSqlSession().selectList("param.selectReturnParamByInterfaceId", map);
	}
	@SuppressWarnings("unchecked")
	public List<Param> selectOnlineRequestParamByInterfaceId(Long onlineInterfaceId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", onlineInterfaceId);
		return this.getSqlSession().selectList("param.selectOnlineRequestParamByInterfaceId", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Param> selectOnlineReturnParamByInterfaceId(Long onlineInterfaceId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", onlineInterfaceId);
		return this.getSqlSession().selectList("param.selectOnlineReturnParamByInterfaceId", map);
	}
	
	public int insertDictParamRelation(Param param){
		return this.getSqlSession().insert("param.insertDictParam", param);
	}

	@SuppressWarnings("unchecked")
	public List<Param> queryParams(Map<String, Long> map) {

		return this.getSqlSession().selectList("param.queryParams", map);
	}

	public Param selectDictParamByDictId(Map<String, String> map) {
		
		return (Param) this.getSqlSession().selectOne("param.queryParamById", map);
	}
	
}
