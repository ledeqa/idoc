package com.idoc.dao.docManage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.model.OnlineVersion;

@Repository("onlineVersionDaoImpl")
public class OnlineVersionDaoImpl extends BaseMysqlDBDaoImpl{
	@SuppressWarnings("unchecked")
	public List<OnlineVersion> selectOnlineVersionListById(Long id){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", id);
		return this.getSqlSession().selectList("OnlineVersion.selectOnlineVersionById", map);
	}
	
	public int insertOnlineVersion(OnlineVersion onlineVersion){
		return this.getSqlSession().insert("OnlineVersion.insertOnlineVersion",onlineVersion);
	}
	
	public Long selectCurrentOnlineVersionById(Long id){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", id);
		return (Long) this.getSqlSession().selectOne("OnlineVersion.selectCurrentOnlineVersionById", map);
	}
	
	public OnlineVersion searchOnlineVersion(Long onlineVersionId){
		return (OnlineVersion) this.getSqlSession().selectOne("OnlineVersion.searchOnlineVersion", onlineVersionId);
	}
}