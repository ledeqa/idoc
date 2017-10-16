package com.idoc.dao.docManage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.model.OnlinePage;

@Repository("onlinePageDaoImpl")
public class OnlinePageDaoImpl extends BaseMysqlDBDaoImpl{
	
	@SuppressWarnings("unchecked")
	public List<OnlinePage> selectOnlinePageListByCond(Map<String, Object> map){
		return this.getSqlSession().selectList("OnlinePage.selectOnlinePageListByPage", map);
	}
	
	public OnlinePage selectOnlinePageById(Long pageId){
		return (OnlinePage) this.getSqlSession().selectOne("OnlinePage.selectOnlinePageById", pageId);
	}
	
	public OnlinePage selectOnlinePageByName(String pageName){
		return (OnlinePage) this.getSqlSession().selectOne("OnlinePage.selectOnlinePageByName", pageName);
	}
	
	public int insertOnlinePage(OnlinePage page){
		return this.getSqlSession().insert("OnlinePage.insertOnlinePage", page);
	}
	
	public int updateOnlinePage(Map<String, Object> map){
		return this.getSqlSession().update("OnlinePage.updateOnlinePage", map);
	}
	
	public int deleteOnlinePage(Long pageId){
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("onlinePageId", pageId);
		map.put("delete", "1");
		return this.getSqlSession().delete("OnlinePage.updateOnlinePage", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<OnlinePage> getPageListByModules(List<Long> moduleIds){
		return this.getSqlSession().selectList("OnlinePage.selectOnlinePageListByModules", moduleIds);
	}
}