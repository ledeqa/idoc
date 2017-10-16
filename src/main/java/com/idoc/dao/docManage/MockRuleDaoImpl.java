package com.idoc.dao.docManage;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.model.MockRule;
import com.idoc.model.Module;

@Repository("mockRuleDaoImpl")
public class MockRuleDaoImpl extends BaseMysqlDBDaoImpl{
	
	public int insertMockRule(MockRule mockRule){
		return this.getSqlSession().insert("MockRule.insertMockRule", mockRule);
	}
	
	public int updateMockRule(MockRule mockRule){
		return this.getSqlSession().update("MockRule.updateMockRule", mockRule);
	}
	
	public int deleteMockRule(Map<String,Object> map){
		return this.getSqlSession().update("MockRule.deleteMockRule", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<MockRule> selectmockRuleByCond(Map<String,Object> map){
		return this.getSqlSession().selectList("MockRule.selectmockRuleByCond", map);
	}

}
