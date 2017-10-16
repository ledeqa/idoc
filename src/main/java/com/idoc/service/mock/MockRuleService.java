package com.idoc.service.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.dao.docManage.MockRuleDaoImpl;
import com.idoc.model.MockRule;

@Service
public class MockRuleService {
	
	@Autowired
	private MockRuleDaoImpl mockRuleDaoImpl;
	
	public int saveMockRule(MockRule mockRule){
		List<MockRule> lists = new ArrayList<MockRule>();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("englishName", mockRule.getEnglishName());
		map.put("interfaceId", mockRule.getInterfaceId());
		lists = mockRuleDaoImpl.selectmockRuleByCond(map);
		
		int result = 2;
		if(lists==null || lists.isEmpty()){
			mockRule.setStatus(1);
			result = mockRuleDaoImpl.insertMockRule(mockRule);
		}else{
			result = mockRuleDaoImpl.updateMockRule(mockRule);
		}
		return result;
	}
	
	public int deleteMockRule(Map<String,Object> map){
		return mockRuleDaoImpl.deleteMockRule(map);
	}
	
	public MockRule getMockRuleByCond(Map<String,Object> map){
		List<MockRule> lists = new ArrayList<MockRule>();
		lists = mockRuleDaoImpl.selectmockRuleByCond(map);
		if(lists==null || lists.isEmpty()){
			return null;
		}else{
			return lists.get(0);
		}
	}

}
