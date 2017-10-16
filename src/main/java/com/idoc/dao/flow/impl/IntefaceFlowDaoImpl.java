package com.idoc.dao.flow.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.dao.flow.IntefaceFlowDao;

@Repository
public class IntefaceFlowDaoImpl extends BaseMysqlDBDaoImpl implements IntefaceFlowDao {

	@Override
	public int updateInterfaceStatus(Map<String, Object> paramMap) {
		return this.getSqlSession().update("InterfaceFlow.updateInterfaceStatus", paramMap);
	}
}