package com.idoc.service.flow;

import java.util.Map;

import com.idoc.form.InterfaceForm;
import com.idoc.model.Interface;

public interface StatusFlowService {
	int updateInterfaceStatus(Map<String, Object> paramMap);
	
	int insertInterfaceStatusChangement(long interfaceId, String operation, String toStatus, int reason, String remark);
	
	void createInterfaceFinished(Interface interf, Long projectId);
	
	int audit(long projectId, long interfaceId);
	
	int revise(long projectId, long interfaceId, String proposal);
	
	int auditSuccess(long projectId, long interfaceId, long toStatus);
	
	//int submitToTest(long projectId, long interfaceId);
	
	int startToTest(long projectId, long interfaceId);
	
	
	int returnToTest(long projectId, long interfaceId);
	
	int pressureTest(long projectId, long interfaceId);
	
	int pressureTestFinished(long projectId, long interfaceId);
	
	int returnToPressureTest(long projectId, long interfaceId);
	
	int online(long projectId, long interfaceId, InterfaceForm form);
	
	int iterationInter(long projectId, long interfaceId);
	
	int forceBackToEdit(long projectId, long interfaceId, int reason);

	int submitToTest(long projectId, long interfaceId, Integer flag);

	int tested(long projectId, long interfaceId, Integer flag);
}