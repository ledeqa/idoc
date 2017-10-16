package com.idoc.dao.docManage;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.model.InterfaceStatusChangement;

@Repository("interfaceStatusChangementDaoImpl")
public class InterfaceStatusChangementDaoImpl extends BaseMysqlDBDaoImpl{
	public int insertInterfaceStatusChangement(InterfaceStatusChangement changement){
		return this.getSqlSession().insert("docManage.insertInterfaceStatusChangement", changement);
	}
	
	@SuppressWarnings("unchecked")
	public List<InterfaceStatusChangement> getInterfaceForceBackReason(List<Long> interfaceIds){
		return this.getSqlSession().selectList("docManage.getInterfaceForceBackReason", interfaceIds);
	}
}