package com.idoc.dao.docManage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.model.Page;


@Repository("pageDaoImpl")
public class PageDaoImpl extends BaseMysqlDBDaoImpl{
	
	@SuppressWarnings("unchecked")
	public List<Page> selectPageListByModuleId(Map<String,Object> map){
		
		return this.getSqlSession().selectList("docManage.selectPageListByModuleId", map);
	}
	
	public Page selectPageById(Long pageId){
		return (Page) this.getSqlSession().selectOne("docManage.selectPageById", pageId);
	}
	
	public int insertPage(Page page){
		
		return this.getSqlSession().insert("docManage.insertPage", page);
	}
	
	public int updatePage(Map<String,Object> map){
		return this.getSqlSession().update("docManage.updatePage", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Page> getPageListByModules(List<Long> moduleIds){
		return this.getSqlSession().selectList("docManage.selectPageListByModules", moduleIds);
	}
	
	public Long selectProductIdByPageId(Long pageId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("pageId", pageId);
		return (Long) this.getSqlSession().selectOne("docManage.selectProductIdByPageId",map);
	}
	
}
