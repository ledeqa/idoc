package com.idoc.dao.docManage;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.model.Module;

@Repository("moduleDaoImpl")
public class ModuleDaoImpl extends BaseMysqlDBDaoImpl{
	
	public int insertModule(Module module){
		
		return this.getSqlSession().insert("docManage.insertModule", module);
	}
	
	public int updateModule(Map<String,Object> map){
		return this.getSqlSession().update("docManage.updateModule", map);
	}
	
	public Module selectModuleById(Long moduleId){
		return (Module) this.getSqlSession().selectOne("docManage.selectModuleById", moduleId);
	}
	
	@SuppressWarnings("unchecked")
	public List<Module> selectModuleListByCond(Map<String,Object> map){
		return this.getSqlSession().selectList("docManage.selectModuleListByCond", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Module> selectModuleListByProjectIdAndModuleName(Map<String,Object> map){
		return this.getSqlSession().selectList("docManage.selectModuleListByProjectIdAndModuleName", map);
	}
}