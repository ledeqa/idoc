package com.idoc.dao.docManage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.model.InterVersion;

@Repository("interVersionDaoImpl")
public class InterVersionDaoImpl extends BaseMysqlDBDaoImpl{
	
	@SuppressWarnings("unchecked")
	public List<InterVersion> selectInterVersionListById(Long id){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", id);
		return this.getSqlSession().selectList("InterVersion.selectInterVersionById", map);
	}
	
	public InterVersion selectLastInterVersion(Map<String, Object> map){
		return (InterVersion) this.getSqlSession().selectOne("InterVersion.selectLastInterVersion", map);
	}
	
	public int insertInterVersion(InterVersion interVersion){
		return this.getSqlSession().insert("InterVersion.insertInterVersion",interVersion);
	}
	
	public Long selectCurrentInterVersionById(Long id){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", id);
		return (Long) this.getSqlSession().selectOne("InterVersion.selectCurrentInterVersionById", map);
	}
	
	public Long selectCurrentIterVersionById(Long id){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", id);
		return (Long) this.getSqlSession().selectOne("InterVersion.selectCurrentIterVersionById", map);
	}
	
	public InterVersion searchInterfaceVersion(Long versionId){
		return (InterVersion) this.getSqlSession().selectOne("InterVersion.searchInterfaceVersion", versionId);
	}
}