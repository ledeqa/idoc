package com.idoc.dao.docManage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.model.OnlineModule;

@Repository("onlineModuleDaoImpl")
public class OnlineModuleDaoImpl extends BaseMysqlDBDaoImpl{
	
	@SuppressWarnings("unchecked")
	public List<OnlineModule> selectOnlineModuleListByCond(Map<String,Object> map){
		return this.getSqlSession().selectList("OnlineModule.selectOnlineModuleList", map);
	}
	
	public OnlineModule selectOnlineModuleById(Long moduleId){
		return (OnlineModule) this.getSqlSession().selectOne("OnlineModule.selectOnlineModuleById", moduleId);
	}
	
	public OnlineModule selectOnlineModuleByName(String moduleName){
		return (OnlineModule) this.getSqlSession().selectOne("OnlineModule.selectOnlineModuleByName", moduleName);
	}
	
	public int insertOnlineModule(OnlineModule module){
		return this.getSqlSession().insert("OnlineModule.insertOnlineModule", module);
	}
	
	public int updateOnlineModule(Map<String,Object> map){
		return this.getSqlSession().update("OnlineModule.updateOnlineModule", map);
	}
	
	public int deleteOnlineModule(Long onlineModuleId){
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("onlineModuleId", onlineModuleId);
		map.put("delete", "1");
		return this.getSqlSession().delete("OnlineModule.updateOnlineModule", map);
	}
	
	
	
}