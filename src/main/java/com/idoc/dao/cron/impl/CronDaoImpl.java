package com.idoc.dao.cron.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.dao.cron.CronDao;
import com.netease.cron.CronModel;

@Repository
public class CronDaoImpl extends BaseMysqlDBDaoImpl implements CronDao {

	@Override
	public void addCron(CronModel cron) {
		String id = UUID.randomUUID().toString();
		cron.setId(id);
		this.getSqlSession().insert("addCron", cron);
	}
	
	@Override
	public void updateCron(CronModel cron) {

		this.getSqlSession().update("updateCron", cron);
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CronModel> getAllCron(String group) {

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("group", group);
		map.put("withOutAllGroup", false);
		List<CronModel> result = this.getSqlSession().selectList("getAllCron", map);
		return result;
	}
	
	@Override
	public CronModel selectCron(String id) {

		return (CronModel) this.getSqlSession().selectOne("selectCron", id);
	}
	
	@Override
	public void deleteCron(String id) {

		this.getSqlSession().delete("deleteCron", id);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CronModel> getAllCronWithOutGroup(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("withOutAllGroup", true);
		List<CronModel> result = this.getSqlSession().selectList("getAllCron", map);
		return result;
	}
}