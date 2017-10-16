package com.idoc.service.flow.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idoc.constant.CommonConstant;
import com.idoc.constant.LogConstant;
import com.idoc.dao.docManage.InterVersionDaoImpl;
import com.idoc.dao.docManage.InterfaceDaoImpl;
import com.idoc.dao.docManage.InterfaceOnlineDaoImpl;
import com.idoc.dao.docManage.InterfaceStatusChangementDaoImpl;
import com.idoc.dao.docManage.ModuleDaoImpl;
import com.idoc.dao.docManage.OnlineModuleDaoImpl;
import com.idoc.dao.docManage.OnlinePageDaoImpl;
import com.idoc.dao.docManage.OnlineVersionDaoImpl;
import com.idoc.dao.docManage.PageDaoImpl;
import com.idoc.dao.docManage.ParamDaoImpl;
import com.idoc.dao.flow.IntefaceFlowDao;
import com.idoc.dao.login.LoginDaoImpl;
import com.idoc.dao.productAndProject.ProjectDaoImpl;
import com.idoc.form.InterfaceForm;
import com.idoc.memcache.MemcacheServer;
import com.idoc.model.InterVersion;
import com.idoc.model.Interface;
import com.idoc.model.InterfaceOnline;
import com.idoc.model.InterfaceStatusChangement;
import com.idoc.model.Module;
import com.idoc.model.OnlineModule;
import com.idoc.model.OnlinePage;
import com.idoc.model.OnlineVersion;
import com.idoc.model.Page;
import com.idoc.model.Param;
import com.idoc.model.ProjectModel;
import com.idoc.model.UserModel;
import com.idoc.service.dict.DictServiceImpl;
import com.idoc.service.docManage.InterServiceUtilImpl;
import com.idoc.service.docManage.InterfaceServiceImpl;
import com.idoc.service.flow.StatusFlowService;
import com.idoc.shiro.token.TokenManager;
import com.idoc.util.ErrorMessageUtil;
import com.idoc.util.ForceBackReason;
import com.idoc.util.InterfaceStatus;
import com.idoc.util.LoginUserInfoUtil;
import com.idoc.util.mail.MailUtil;
import com.netease.common.util.StringUtil;

import net.sf.json.JSONObject;

@Service("statusFlowServiceImpl")
public class StatusFlowServiceImpl implements StatusFlowService {

	@Autowired
	private IntefaceFlowDao intefaceFlowDaoImpl;
	
	@Autowired
	private InterfaceDaoImpl interfaceDaoImpl;
	
	@Autowired
	private PageDaoImpl pageDaoImpl;
	
	@Autowired
	private ModuleDaoImpl moduleDaoImpl;
	
	@Autowired
	private ProjectDaoImpl projectDaoImpl;
	
	@Autowired
	private InterfaceOnlineDaoImpl interfaceOnlineDaoImpl;
	
	@Autowired
	private OnlinePageDaoImpl onlinePageDaoImpl;
	
	@Autowired
	private OnlineModuleDaoImpl onlineModuleDaoImpl;
	
	@Autowired
	private OnlineVersionDaoImpl onlineVersionDaoImpl;
	
	@Autowired
	private LoginDaoImpl loginDaoImpl;
	
	@Autowired
	private ParamDaoImpl paramDaoImpl;
	
	@Autowired
	private DictServiceImpl dictServiceImpl;
	
	@Autowired
	private InterfaceStatusChangementDaoImpl interfaceStatusChangementDaoImpl;
	
	@Autowired
	private InterServiceUtilImpl interServiceUtilImpl;
	
	@Autowired
	private MemcacheServer memcacheServer;
	
	@Autowired
	private InterfaceServiceImpl interfaceServiceImpl;
	
	@Autowired
	private InterVersionDaoImpl interVersionDaoImpl;
	
	private final String interfaceUrl = "http://idoc.qa.lede.com/idoc/inter/index.html";
	/* 
	 * 修改接口状态
	 */
	@Override
	public int updateInterfaceStatus(Map<String, Object> paramMap) {
		return intefaceFlowDaoImpl.updateInterfaceStatus(paramMap);
	}

	/**
	 * 插入接口状态变更
	 * @param interfaceId  接口id
	 * @param operation  操作
	 * @param toStatus  操作后的状态
	 * @param reason  原因
	 * @return
	 */
	@Override
	@Transactional
	public int insertInterfaceStatusChangement(long interfaceId, String operation, String toStatus, int reason, String remark){
		InterfaceStatusChangement changement = new InterfaceStatusChangement();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("interfaceId", interfaceId);
		Interface interf = interfaceDaoImpl.selectInterfaceById(paramMap);
		if(interf == null){
			LogConstant.runLog.info("插入[接口状态改变表]时，接口表中id = [" + interfaceId + "]的接口不存在!");
			return 0;
		}
		changement.setInterfaceId(interfaceId);
		int oldStatus = interf.getInterfaceStatus();
		changement.setInterfaceStatus(oldStatus);
		String englishName = LoginUserInfoUtil.getUserEnglishName();
		Long operatorId = null;
		if(!StringUtil.isEmpty(englishName)){
			UserModel operator = loginDaoImpl.queryUserModelByUserName(englishName);
			if(operator == null){
				LogConstant.runLog.info("插入[接口状态改变表]时，操作者[" + englishName + "]不存在!");
				return 0;
			}
			operatorId = operator.getUserId();
		}
		if((oldStatus == 310&&toStatus.equals("客户端审核通过"))||(oldStatus == 301&&toStatus.equals("前端审核通过"))){
			toStatus = "审核通过";
		}
		changement.setOperatorId(operatorId);
		String changementDesc = "操作：" + operation + "，状态变更：" +InterfaceStatus.getInterfaceStatus(interf.getInterfaceStatus()) 
		+ " --> " + toStatus;
		changement.setChangementDesc(changementDesc);
		changement.setChangementReason(reason);
		changement.setRemark(remark);
		return interfaceStatusChangementDaoImpl.insertInterfaceStatusChangement(changement);
	}
	
	/* 
	 * 创建接口完成后发送popo消息
	 */
	@Override
	public void createInterfaceFinished(Interface interf, Long projectId) {
		if(interf == null){
			LogConstant.runLog.info("执行创建接口发送popo消息时，接口为空!");
			return;
		}
		UserModel creator = loginDaoImpl.queryUserModelByUserId(interf.getCreatorId());
		if(creator == null){
			LogConstant.runLog.info("执行创建接口发送popo消息时，接口创建者为空!");
			return;
		}
		if(projectId == null){
			projectId = interfaceDaoImpl.selectProjectIdByInterfaceId(interf.getInterfaceId());
		}
		String url = interfaceUrl + "?projectId=" + projectId + "&interfaceId=" + interf.getInterfaceId();
		//通知前端/后台/客户端开发负责人分配其他相关负责人
		String frontPeopleIds = interf.getFrontPeopleIds();
		if(StringUtil.isEmpty(frontPeopleIds)){
			LogConstant.runLog.info("执行创建接口发送popo消息时，前端开发负责人为空!");
		}else {
			String[] frontPeople = frontPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
			String frontMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_CREATED_POPO_MESSAGE_TO_FRONT);
			if(StringUtil.isEmpty(frontMsg)){
				frontMsg = "{front}：\r\n\t您好，{creator}创建了新的接口[{interfaceName}]，需要您对该接口分派前端开发相关的负责人。接口地址：{interfaceLink}";
			}else{
				frontMsg = frontMsg.replace("{tab}", "\r\n\t");
			}
			for(String front : frontPeople){
				long frontId = Long.parseLong(front);
				UserModel frontModel = loginDaoImpl.queryUserModelByUserId(frontId);
				if(frontModel == null){
					LogConstant.runLog.info("执行创建接口发送popo消息时，接口前端负责人[" + frontId + "]不存在!");
					continue;
				}
				String sendMsg = frontMsg;
				sendMsg = sendMsg.replace("{front}", frontModel.getUserName());
				sendMsg = sendMsg.replace("{creator}", creator.getUserName());
				sendMsg = sendMsg.replace("{interfaceName}", interf.getInterfaceName());
				sendMsg = sendMsg.replace("{interfaceLink}", url);
//				sendPopoMessage.send(frontModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
			}
		}
		
		String behindPeopleIds = interf.getBehindPeopleIds();
		if(StringUtil.isEmpty(behindPeopleIds)){
			LogConstant.runLog.info("执行创建接口发送popo消息时，后台开发负责人为空!");
		}else {
			String[] behindPeople = behindPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
			String behindMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_CREATED_POPO_MESSAGE_TO_BEHIND);
			if(StringUtil.isEmpty(behindMsg)){
				behindMsg = "{behind}：\r\n\t您好，{creator}创建了新的接口[{interfaceName}]，需要您对该接口分派后台开发相关的负责人。接口地址：{interfaceLink}";
			}else{
				behindMsg = behindMsg.replace("{tab}", "\r\n\t");
			}
			for(String behind : behindPeople){
				long behindId = Long.parseLong(behind);
				UserModel behindModel = loginDaoImpl.queryUserModelByUserId(behindId);
				if(behindModel == null){
					LogConstant.runLog.info("执行创建接口发送popo消息时，接口后台负责人[" + behindId + "]不存在!");
					continue;
				}
				String sendMsg = behindMsg;
				sendMsg = sendMsg.replace("{behind}", behindModel.getUserName());
				sendMsg = sendMsg.replace("{creator}", creator.getUserName());
				sendMsg = sendMsg.replace("{interfaceName}", interf.getInterfaceName());
				sendMsg = sendMsg.replace("{interfaceLink}", url);
//				sendPopoMessage.send(behindModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
			}
		}
		
		String clientPeopleIds = interf.getClientPeopleIds();
		if(StringUtil.isEmpty(clientPeopleIds)){
			LogConstant.runLog.info("执行创建接口发送popo消息时，客户端开发负责人为空!");
		}else {
			String[] clientPeople = clientPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
			String clientMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_CREATED_POPO_MESSAGE_TO_CLIENT);
			if(StringUtil.isEmpty(clientMsg)){
				clientMsg = "{client}：\r\n\t您好，{creator}创建了新的接口[{interfaceName}]，需要您对该接口分派客户端开发相关的负责人。接口地址：{interfaceLink}";
			}else{
				clientMsg = clientMsg.replace("{tab}", "\r\n\t");
			}
			for(String client : clientPeople){
				long clientId = Long.parseLong(client);
				UserModel clientModel = loginDaoImpl.queryUserModelByUserId(clientId);
				if(clientModel == null){
					LogConstant.runLog.info("执行创建接口发送popo消息时，接口客户端负责人[" + clientId + "]不存在!");
					continue;
				}
				String sendMsg = clientMsg;
				sendMsg = sendMsg.replace("{client}", clientModel.getUserName());
				sendMsg = sendMsg.replace("{creator}", creator.getUserName());
				sendMsg = sendMsg.replace("{interfaceName}", interf.getInterfaceName());
				sendMsg = sendMsg.replace("{interfaceLink}", url);
//				sendPopoMessage.send(clientModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
			}
		}
		
		//如果需要测试，通知测试负责人分配其他相关负责人
		if(interf.getIsNeedInterfaceTest() == 1){
			String testPeopleIds = interf.getTestPeopleIds();
			if(StringUtil.isEmpty(testPeopleIds)){
				LogConstant.runLog.info("执行创建接口发送popo消息时，测试开发负责人为空!");
			}else {
				String[] testPeople = testPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
				String testMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_CREATED_POPO_MESSAGE_TO_TESTER);
				if(StringUtil.isEmpty(testMsg)){
					testMsg = "{tester}：\r\n\t您好，{creator}创建了新的接口[{interfaceName}]，需要您对该接口分派测试相关的负责人。接口地址：{interfaceLink}";
				}else{
					testMsg = testMsg.replace("{tab}", "\r\n\t");
				}
				for(String tester : testPeople){
					long testId = Long.parseLong(tester);
					UserModel testModel = loginDaoImpl.queryUserModelByUserId(testId);
					if(testModel == null){
						LogConstant.runLog.info("执行创建接口发送popo消息时，接口测试负责人[" + testId + "]不存在!");
						continue;
					}
					String sendMsg = testMsg;
					sendMsg = sendMsg.replace("{tester}", testModel.getUserName());
					sendMsg = sendMsg.replace("{creator}", creator.getUserName());
					sendMsg = sendMsg.replace("{interfaceName}", interf.getInterfaceName());
					sendMsg = sendMsg.replace("{interfaceLink}", url);
//					sendPopoMessage.send(testModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
				}
			}
		}
	}

	/* 
	 * 发起审核
	 */
	@Override
	public int audit(long projectId, long interfaceId){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("interfaceId", interfaceId);
		Interface interf = interfaceDaoImpl.selectInterfaceById(paramMap);
		if(interf == null){
			LogConstant.runLog.info("执行发起审核时，接口[" + interfaceId + "]不存在!");
			return 0;
		}
		//修改接口状态为‘审核中’
		paramMap.put("interfaceStatus", CommonConstant.INTERFACE_STATUS_AUDITING);
		intefaceFlowDaoImpl.updateInterfaceStatus(paramMap);
		
		long creatorId = interf.getCreatorId();
		UserModel creator = loginDaoImpl.queryUserModelByUserId(creatorId);
		if(creator == null){
			LogConstant.runLog.info("执行审核通过时，接口创建者[" + creatorId + "]不存在!");
			return 0;
		}
		//通知前端/客户端开发负责人分配其他相关负责人
		String url = interfaceUrl + "?projectId=" + projectId + "&interfaceId=" + interfaceId;
		String frontPeopleIds = interf.getFrontPeopleIds();
		if(StringUtil.isEmpty(frontPeopleIds)){
			LogConstant.runLog.info("执行发起审核时，前端开发负责人为空!");
		}else {
			String[] frontPeople = frontPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
			String frontMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_AUDIT_POPO_MESSAGE_TO_FRONT);
			if(StringUtil.isEmpty(frontMsg)){
				frontMsg = "{front}：\r\n\t您好，{creator}创建了新的接口[{interfaceName}]，需要您对接口信息进行审核。审核地址：{interfaceLink}";
			}else{
				frontMsg = frontMsg.replace("{tab}", "\r\n\t");
			}
			for(String front : frontPeople){
				long frontId = Long.parseLong(front);
				UserModel frontModel = loginDaoImpl.queryUserModelByUserId(frontId);
				if(frontModel == null){
					LogConstant.runLog.info("执行发起审核时，接口前端负责人[" + frontId + "]不存在!");
					continue;
				}
				String sendMsg = frontMsg;
				sendMsg = sendMsg.replace("{front}", frontModel.getUserName());
				sendMsg = sendMsg.replace("{creator}", creator.getUserName());
				sendMsg = sendMsg.replace("{interfaceName}", interf.getInterfaceName());
				sendMsg = sendMsg.replace("{interfaceLink}", url);
//				sendPopoMessage.send(frontModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
			}
		}
		
		String clientPeopleIds = interf.getClientPeopleIds();
		if(StringUtil.isEmpty(clientPeopleIds)){
			LogConstant.runLog.info("执行发起审核时，客户端开发负责人为空!");
		}else {
			String[] clientPeople = clientPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
			String clientMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_AUDIT_POPO_MESSAGE_TO_CLIENT);
			if(StringUtil.isEmpty(clientMsg)){
				clientMsg = "{client}：\r\n\t您好，{creator}创建了新的接口[{interfaceName}]，需要您对接口信息进行审核。审核地址：{interfaceLink}";
			}else{
				clientMsg = clientMsg.replace("{tab}", "\r\n\t");
			}
			for(String client : clientPeople){
				long clientId = Long.parseLong(client);
				UserModel clientModel = loginDaoImpl.queryUserModelByUserId(clientId);
				if(clientModel == null){
					LogConstant.runLog.info("执行发起审核时，接口客户端负责人[" + clientId + "]不存在!");
					continue;
				}
				String sendMsg = clientMsg;
				sendMsg = sendMsg.replace("{client}", clientModel.getUserName());
				sendMsg = sendMsg.replace("{creator}", creator.getUserName());
				sendMsg = sendMsg.replace("{interfaceName}", interf.getInterfaceName());
				sendMsg = sendMsg.replace("{interfaceLink}", url);
//				sendPopoMessage.send(clientModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
			}
		}
		return 1;
	}
	
	/* 
	 * 建议修改
	 */
	@Override
	public int revise(long projectId, long interfaceId, String proposal){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("interfaceId", interfaceId);
		Interface interf = interfaceDaoImpl.selectInterfaceById(paramMap);
		if(interf == null){
			LogConstant.runLog.info("执行建议修改时，接口[" + interfaceId + "]不存在!");
			return 0;
		}
		//修改接口状态为‘编辑中’
		paramMap.put("interfaceStatus", CommonConstant.INTERFACE_STATUS_EDITING);
		intefaceFlowDaoImpl.updateInterfaceStatus(paramMap);
		//给接口创建者发送popo消息
		long creator = interf.getCreatorId();
		UserModel creatorModel = loginDaoImpl.queryUserModelByUserId(creator);
		if(creatorModel == null){
			LogConstant.runLog.info("执行建议修改时，接口创建者[" + creator + "]不存在!");
			return 0;
		}
		if(StringUtil.isEmpty(creatorModel.getCorpMail())){
			LogConstant.runLog.info("执行建议修改时，接口创建者邮箱为空!");
			return 0;
		}
		String creatorMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_REVISE_POPO_MESSAGE_TO_CREATOR);
		if(StringUtil.isEmpty(creatorMsg)){
			creatorMsg = "{creator}：\r\n\t您好，您提交的接口[{interfaceName}]未通过审核，修改建议：{proposal} 接口地址：{interfaceLink}，联系人：{audit}";
		}else{
			creatorMsg = creatorMsg.replace("{tab}", "\r\n\t");
		}
		creatorMsg = creatorMsg.replace("{creator}", creatorModel.getUserName());
		creatorMsg = creatorMsg.replace("{interfaceName}", interf.getInterfaceName());
		creatorMsg = creatorMsg.replace("{proposal}", proposal);
		String url = interfaceUrl + "?projectId=" + projectId + "&interfaceId=" + interfaceId;
		creatorMsg = creatorMsg.replace("{interfaceLink}", url); 
		creatorMsg = creatorMsg.replace("{audit}", LoginUserInfoUtil.getUserName());
//		sendPopoMessage.send(creatorModel.getCorpMail(), creatorMsg, SendPopoMessageMethod.POST);
		
		return 1;
	}
	
	/* 
	 * 审核通过
	 */
	@Override
	@Transactional
	public int auditSuccess(long projectId, long interfaceId, long toStatus) {
		int result = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("interfaceId", interfaceId);
		Interface interf = interfaceDaoImpl.selectInterfaceById(paramMap);
		if(interf == null){
			LogConstant.runLog.info("执行审核通过时，接口[" + interfaceId + "]不存在!");
			return result;
		}
		int oldStatus = interf.getInterfaceStatus();
		String frontIds = interf.getFrontPeopleIds();
		String clientIds = interf.getClientPeopleIds();
		if(toStatus == 3||(toStatus == 310&&oldStatus == 301)||(toStatus == 301&&oldStatus == 310)
				||(StringUtils.isEmpty(frontIds)&&toStatus==301)||(StringUtils.isEmpty(clientIds)&&toStatus==310)){
			//修改接口状态为‘审核完成’
			paramMap.put("interfaceStatus", CommonConstant.INTERFACE_STATUS_AUDITED);
			intefaceFlowDaoImpl.updateInterfaceStatus(paramMap);
			//给接口创建者发送popo消息
			long creator = interf.getCreatorId();
			UserModel creatorModel = loginDaoImpl.queryUserModelByUserId(creator);
			if(creatorModel == null){
				LogConstant.runLog.info("执行审核通过时，接口创建者[" + creator + "]不存在!");
				return result;
			}
			if(StringUtil.isEmpty(creatorModel.getCorpMail())){
				LogConstant.runLog.info("执行审核通过时，接口创建者邮箱为空!");
				return result;
			}
			String creatorMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_AUDITED_POPO_MESSAGE_TO_CREATOR);
			if(StringUtil.isEmpty(creatorMsg)){
				creatorMsg = "{creator}：\r\n\t您好，您提交的接口[{interfaceName}]已经审核通过。接口链接：{interfaceLink}，审核人：{audit}";
			}else{
				creatorMsg = creatorMsg.replace("{tab}", "\r\n\t");
			}
			creatorMsg = creatorMsg.replace("{creator}", creatorModel.getUserName());
			creatorMsg = creatorMsg.replace("{interfaceName}", interf.getInterfaceName());
			String url = interfaceUrl + "?projectId=" + projectId + "&interfaceId=" + interfaceId;
			creatorMsg = creatorMsg.replace("{interfaceLink}", url);
			creatorMsg = creatorMsg.replace("{audit}", LoginUserInfoUtil.getUserName());
//			sendPopoMessage.send(creatorModel.getCorpMail(), creatorMsg, SendPopoMessageMethod.POST);
			//如果接口需要测试，给测试人员发送popo消息
			if(interf.getIsNeedInterfaceTest() == 1){
				String testPeopleIds = interf.getTestPeopleIds();
				if(StringUtil.isEmpty(testPeopleIds)){
					LogConstant.runLog.info("执行审核通过时，接口测试人员为空!");
					return result;
				}
				String[] testPeople = testPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
				String testerMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_AUDITED_POPO_MESSAGE_TO_TESTER);
				if(StringUtil.isEmpty(testerMsg)){
					testerMsg = "{tester}：\r\n\t您好，{creator}创建的接口[{interfaceName}]已经审核通过，您可以开始相关的接口测试用例编写。接口地址：{interfaceLink}，审核人：{audit}";
				}else{
					testerMsg = testerMsg.replace("{tab}", "\r\n\t");
				}
				for(String tester : testPeople){
					long testId = Long.parseLong(tester);
					UserModel testerModel = loginDaoImpl.queryUserModelByUserId(testId);
					if(testerModel == null){
						LogConstant.runLog.info("执行审核通过时，接口测试者[" + testId + "]不存在!");
						continue;
					}
					String sendMsg = testerMsg;
					sendMsg = sendMsg.replace("{tester}", testerModel.getUserName());
					sendMsg = sendMsg.replace("{creator}", creatorModel.getUserName());
					sendMsg = sendMsg.replace("{interfaceName}", interf.getInterfaceName());
					sendMsg = sendMsg.replace("{interfaceLink}", url);
					sendMsg = sendMsg.replace("{audit}", LoginUserInfoUtil.getUserName());
//					sendPopoMessage.send(testerModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
				}
			}
			result = 3;
		}else if(toStatus == 310){
			//修改接口状态为前端审核通过
			paramMap.put("interfaceStatus", CommonConstant.INTERFACE_STATUS_FRONT_AUDITED);
			intefaceFlowDaoImpl.updateInterfaceStatus(paramMap);
			//给接口创建者发送popo消息
			long creator = interf.getCreatorId();
			UserModel creatorModel = loginDaoImpl.queryUserModelByUserId(creator);
			if(creatorModel == null){
				LogConstant.runLog.info("执行审核通过时，接口创建者[" + creator + "]不存在!");
				return result;
			}
			if(StringUtil.isEmpty(creatorModel.getCorpMail())){
				LogConstant.runLog.info("执行审核通过时，接口创建者邮箱为空!");
				return result;
			}
			String creatorMsg = "{creator}：\r\n\t您好，您提交的接口[{interfaceName}]前端已经审核通过。接口链接：{interfaceLink}，审核人：{audit}";
			creatorMsg = creatorMsg.replace("{creator}", creatorModel.getUserName());
			creatorMsg = creatorMsg.replace("{interfaceName}", interf.getInterfaceName());
			String url = interfaceUrl + "?projectId=" + projectId + "&interfaceId=" + interfaceId;
			creatorMsg = creatorMsg.replace("{interfaceLink}", url);
			creatorMsg = creatorMsg.replace("{audit}", LoginUserInfoUtil.getUserName());
//			sendPopoMessage.send(creatorModel.getCorpMail(), creatorMsg, SendPopoMessageMethod.POST);
			
			//给客户端人员发送popo消息
			String clientPeopleIds = interf.getClientPeopleIds();
			if(StringUtil.isEmpty(clientPeopleIds)){
				LogConstant.runLog.info("执行审核通过时，接口客户端人员为空!");
				return result;
			}
			String[] clientPeople = clientPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
			String clientMsg = "{client}：\r\n\t您好，{creator}创建的接口[{interfaceName}]前端已经审核通过，请您尽快进行审核。接口地址：{interfaceLink}，审核人：{audit}";
			for(String client : clientPeople){
				long clientId = Long.parseLong(client);
				UserModel clientModel = loginDaoImpl.queryUserModelByUserId(clientId);
				if(clientModel == null){
					LogConstant.runLog.info("执行审核通过时，接口客户端人员[" + clientId + "]不存在!");
					continue;
				}
				String sendMsg = clientMsg;
				sendMsg = sendMsg.replace("{client}", clientModel.getUserName());
				sendMsg = sendMsg.replace("{creator}", creatorModel.getUserName());
				sendMsg = sendMsg.replace("{interfaceName}", interf.getInterfaceName());
				sendMsg = sendMsg.replace("{interfaceLink}", url);
				sendMsg = sendMsg.replace("{audit}", LoginUserInfoUtil.getUserName());
//				sendPopoMessage.send(clientModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
			}
			result = 310;
		}else if(toStatus == 301){
			//修改接口状态为客户端审核通过
			paramMap.put("interfaceStatus", CommonConstant.INTERFACE_STATUS_CLIENT_AUDITED);
			intefaceFlowDaoImpl.updateInterfaceStatus(paramMap);
			//给接口创建者发送popo消息
			long creator = interf.getCreatorId();
			UserModel creatorModel = loginDaoImpl.queryUserModelByUserId(creator);
			if(creatorModel == null){
				LogConstant.runLog.info("执行审核通过时，接口创建者[" + creator + "]不存在!");
				return result;
			}
			if(StringUtil.isEmpty(creatorModel.getCorpMail())){
				LogConstant.runLog.info("执行审核通过时，接口创建者邮箱为空!");
				return result;
			}
			String creatorMsg = "{creator}：\r\n\t您好，您提交的接口[{interfaceName}]客户端已经审核通过。接口链接：{interfaceLink}，审核人：{audit}";
			creatorMsg = creatorMsg.replace("{creator}", creatorModel.getUserName());
			creatorMsg = creatorMsg.replace("{interfaceName}", interf.getInterfaceName());
			String url = interfaceUrl + "?projectId=" + projectId + "&interfaceId=" + interfaceId;
			creatorMsg = creatorMsg.replace("{interfaceLink}", url);
			creatorMsg = creatorMsg.replace("{audit}", LoginUserInfoUtil.getUserName());
//			sendPopoMessage.send(creatorModel.getCorpMail(), creatorMsg, SendPopoMessageMethod.POST);
			
			//给前端人员发送popo消息
			String frontPeopleIds = interf.getFrontPeopleIds();
			if(StringUtil.isEmpty(frontPeopleIds)){
				LogConstant.runLog.info("执行审核通过时，接口前端人员为空!");
				return result;
			}
			String[] frontPeople = frontPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
			String frontMsg = "{front}：\r\n\t您好，{creator}创建的接口[{interfaceName}]客户端已经审核通过，请您尽快进行审核。接口地址：{interfaceLink}，审核人：{audit}";
			for(String front : frontPeople){
				long frontId = Long.parseLong(front);
				UserModel frontModel = loginDaoImpl.queryUserModelByUserId(frontId);
				if(frontModel == null){
					LogConstant.runLog.info("执行审核通过时，接口前端人员[" + frontId + "]不存在!");
					continue;
				}
				String sendMsg = frontMsg;
				sendMsg = sendMsg.replace("{front}", frontModel.getUserName());
				sendMsg = sendMsg.replace("{creator}", creatorModel.getUserName());
				sendMsg = sendMsg.replace("{interfaceName}", interf.getInterfaceName());
				sendMsg = sendMsg.replace("{interfaceLink}", url);
				sendMsg = sendMsg.replace("{audit}", LoginUserInfoUtil.getUserName());
//				sendPopoMessage.send(frontModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
			}
			result = 301;
		}
		return result;
	}

	/*
	 * 提交测试
	 */
	@Override
	public int submitToTest(long projectId, long interfaceId,Integer flag) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("interfaceId", interfaceId);
		Interface interf = interfaceDaoImpl.selectInterfaceById(paramMap);
		if(interf == null){
			LogConstant.runLog.info("执行提交测试时，接口[" + interfaceId + "]不存在!");
			return 0;
		}
		
		//修改接口表中接口状态为‘已提测’，并修改实际提测时间
		if(flag==1){
		    paramMap.put("interfaceStatus", CommonConstant.INTERFACE_STATUS_TOTEST);
		}else{
			paramMap.put("interfaceStatus", CommonConstant.INTERFACE_STATUS_TESTING);
		}
		paramMap.put("realTestTime", new Date());
		intefaceFlowDaoImpl.updateInterfaceStatus(paramMap);
		//给测试人员发送popo消息
		long creatorId = interf.getCreatorId();
		UserModel creator = loginDaoImpl.queryUserModelByUserId(creatorId);
		if(creator == null){
			LogConstant.runLog.info("执行提交测试时，接口创建者[" + creatorId + "]不存在!");
			return 0;
		}
		if(StringUtil.isEmpty(creator.getCorpMail())){
			LogConstant.runLog.info("执行提交测试时，接口创建者邮箱为空!");
			return 0;
		}
		String testPeopleIds = interf.getTestPeopleIds();
		if(StringUtil.isEmpty(testPeopleIds)){
			LogConstant.runLog.info("执行提交测试时，测试人员为空!");
		}else{
			String[] testPeople = testPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
			String testMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_TOTEST_POPO_MESSAGE_TO_TESTER);
			if(StringUtil.isEmpty(testMsg)){
				testMsg = "{tester}：\r\n\t您好，{creator}创建的接口[{interfaceName}]已经提测，您可以开始测试。接口地址：{interfaceLink}";
			}else{
				testMsg = testMsg.replace("{tab}", "\r\n\t");
			}
			
			String url = interfaceUrl + "?projectId=" + projectId + "&interfaceId=" + interfaceId;
			for(String tester : testPeople){
				long testId = Long.parseLong(tester);
				UserModel testModel = loginDaoImpl.queryUserModelByUserId(testId);
				if(testModel == null){
					LogConstant.runLog.info("执行提交测试时，接口测试者[" + testId + "]不存在!");
					continue;
				}
				String sendMsg = testMsg;
				sendMsg = sendMsg.replace("{tester}", testModel.getUserName());
				sendMsg = sendMsg.replace("{creator}", creator.getUserName());
				sendMsg = sendMsg.replace("{interfaceName}", interf.getInterfaceName());
				sendMsg = sendMsg.replace("{interfaceLink}", url);
//				sendPopoMessage.send(testModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
				
				// 提测时发送邮件给相关测试人员
				StringBuilder sb = new StringBuilder();
				sb.append("=================================接口测试提醒=================================</br>");
			    sb.append("<pre>" + testModel.getUserName() + "，您好：</pre>");
			    sb.append("<pre>   <font color=\"red\">" + creator.getUserName() + "</font>创建的接口[<font color=\"red\">" + interf.getInterfaceName() + "</font>]已经提测，您可以开始测试。</pre>");
			    sb.append("<pre>此邮件是由idoc系统发送，收到邮件请尽快处理。如果您已经处理，请忽略此邮件，如果有其他问题</pre>");
			    sb.append("<pre>请联系idoc系统管理员，非常感谢您使用idoc接口管理平台。</pre></br>");
			    sb.append("<a href=\""+ url + "\">点击此处进行处理</a><br><br>");
			    sb.append("============================================================================");
			    
			    MailUtil mailUtil = new MailUtil();
			    mailUtil.setSubject("接口测试提醒");
			    mailUtil.setContent(sb.toString());
			    mailUtil.setTo(testModel.getCorpMail());
//			    mailUtil.send();
			}
		}
		return 1;
	}

	/* 
	 * 开始测试
	 */
	@Override
	public int startToTest(long projectId, long interfaceId) {
		String creatorMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_TESTING_POPO_MESSAGE_TO_CREATOR);
		if(StringUtil.isEmpty(creatorMsg)){
			creatorMsg = "{creator}：\r\n\t您好，接口[{interfaceName}]已经开始测试，接口链接：{interfaceLink}，测试人：{tester}";
		}else{
			creatorMsg = creatorMsg.replace("{tab}", "\r\n\t");
		}
		//发送popo消息给接口创建者
		return sendPopoMessageToCreator(projectId, interfaceId, "开始测试", CommonConstant.INTERFACE_STATUS_TESTING, creatorMsg);
	}

	/* 
	 * 测试完成
	 */
	@Override
	public int tested(long projectId, long interfaceId,Integer flag) {
		String creatorMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_TESTED_POPO_MESSAGE_TO_CREATOR);
		if(StringUtil.isEmpty(creatorMsg)){
			creatorMsg = "{creator}：\r\n\t您好，接口[{interfaceName}]已经测试完成，接口链接：{interfaceLink}，测试人：{tester}";
		}else{
			creatorMsg = creatorMsg.replace("{tab}", "\r\n\t");
		}
		//发送popo消息给接口创建者
		if(flag==1){
			return sendPopoMessageToCreator(projectId, interfaceId, "测试完成", CommonConstant.INTERFACE_STATUS_TESTED, creatorMsg);
		}else{
			return sendPopoMessageToCreator(projectId, interfaceId, "测试完成", CommonConstant.INTERFACE_STATUS_TESTED, creatorMsg);
		}
			
	}

	/* 
	 * 返回测试
	 */
	@Override
	public int returnToTest(long projectId, long interfaceId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("interfaceId", interfaceId);
		Interface interf = interfaceDaoImpl.selectInterfaceById(paramMap);
		if(interf == null){
			LogConstant.runLog.info("执行返回测试时，接口[" + interfaceId + "]不存在!");
			return 0;
		}
		//将接口状态改为‘测试中’
		paramMap.put("interfaceStatus", CommonConstant.INTERFACE_STATUS_TESTING);
		intefaceFlowDaoImpl.updateInterfaceStatus(paramMap);
		return 1;
	}

	/* 
	 * 提交压测
	 */
	@Override
	public int pressureTest(long projectId, long interfaceId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("interfaceId", interfaceId);
		Interface interf = interfaceDaoImpl.selectInterfaceById(paramMap);
		if(interf == null){
			LogConstant.runLog.info("执行提交压测时，接口[" + interfaceId + "]不存在!");
			return 0;
		}
		//将接口状态改为‘压测中’
		paramMap.put("interfaceStatus", CommonConstant.INTERFACE_STATUS_PRESSURE);
		intefaceFlowDaoImpl.updateInterfaceStatus(paramMap);
		
		long creatorId = interf.getCreatorId();
		UserModel creator = loginDaoImpl.queryUserModelByUserId(creatorId);
		if(creator == null){
			LogConstant.runLog.info("执行提交压测时，接口创建者[" + creatorId + "]不存在!");
			return 0;
		}
		//发送popo消息给测试人员
		String testPeopleIds = interf.getTestPeopleIds();
		if(StringUtil.isEmpty(testPeopleIds)){
			LogConstant.runLog.info("执行提交压测时，测试人员为空!");
		}else{
			String[] testPeople = testPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
			String testMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_PRESSURE_POPO_MESSAGE_TO_TESTER);
			if(StringUtil.isEmpty(testMsg)){
				testMsg = "{tester}：\r\n\t您好，接口[{interfaceName}]已经提交压测，压测人员可以开始压力测试。接口地址：{interfaceLink}，提交人：{submit}";
			}else{
				testMsg = testMsg.replace("{tab}", "\r\n\t");
			}
			String url = interfaceUrl + "?projectId=" + projectId + "&interfaceId=" + interfaceId;
			for(String tester : testPeople){
				long testId = Long.parseLong(tester);
				UserModel testModel = loginDaoImpl.queryUserModelByUserId(testId);
				if(testModel == null){
					LogConstant.runLog.info("执行提交压测时，接口测试者[" + testId + "]不存在!");
					continue;
				}
				String sendMsg = testMsg;
				sendMsg = sendMsg.replace("{tester}", testModel.getUserName());
				sendMsg = sendMsg.replace("{interfaceName}", interf.getInterfaceName());
				sendMsg = sendMsg.replace("{interfaceLink}", url);
				sendMsg = sendMsg.replace("{submit}", LoginUserInfoUtil.getUserName());
//				sendPopoMessage.send(testModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
			}
		}
		return 1;
	}

	/* 
	 * 压测完成
	 */
	@Override
	public int pressureTestFinished(long projectId, long interfaceId) {
		String creatorMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_PRESSURED_POPO_MESSAGE_TO_CREATOR);
		if(StringUtil.isEmpty(creatorMsg)){
			creatorMsg = "{creator}：\r\n\t您好，接口[{interfaceName}]已经完成压测，接口链接：{interfaceLink}，测试人：{tester}";
		}else{
			creatorMsg = creatorMsg.replace("{tab}", "\r\n\t");
		}
		//发送popo消息给接口创建者
		return sendPopoMessageToCreator(projectId, interfaceId, "压测完成", CommonConstant.INTERFACE_STATUS_PRESSURED, creatorMsg);
	}

	/* 
	 * 返回压测
	 */
	@Override
	public int returnToPressureTest(long projectId, long interfaceId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("interfaceId", interfaceId);
		Interface interf = interfaceDaoImpl.selectInterfaceById(paramMap);
		if(interf == null){
			LogConstant.runLog.info("执行返回压测时，接口[" + interfaceId + "]不存在!");
			return 0;
		}
		//将接口状态改为‘压测中’
		paramMap.put("interfaceStatus", CommonConstant.INTERFACE_STATUS_PRESSURE);
		intefaceFlowDaoImpl.updateInterfaceStatus(paramMap);
		return 1;
	}

	/* 
	 * 上线
	 */
	@Override
	@Transactional
	public int online(long projectId, long interfaceId, InterfaceForm form) {
		int retCode = insertInterfaceStatusChangement(interfaceId, "上线", "上线", 1, null);
		if(retCode == 0){
			return 0;
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("interfaceId", interfaceId);
		Interface interf = interfaceDaoImpl.selectInterfaceById(paramMap);
		if(interf == null){
			LogConstant.runLog.info("执行上线时，接口[" + interfaceId + "]不存在!");
			return 0;
		}
		
		//如果该接口在[在线接口表]中不存在，需要将其插入在线接口表中。
		long productId = interfaceDaoImpl.selectProductIdByInterfaceId(interfaceId);
		Map<String,Object> condMap = new HashMap<String,Object>();
		if(interf.getOnlineInterfaceId() != null){
			condMap.put("interfaceId", interf.getOnlineInterfaceId());
		}else{  // 如果在线接口id为空，再根据url和产品id查找在线的接口文档是否存在该接口
			condMap.put("url", interf.getUrl());
			condMap.put("productId", productId);
		}
		InterfaceOnline onlineInterface = null;
		List<InterfaceOnline> onlineInterList = interfaceOnlineDaoImpl.selectOnlineInterfaceListByCond(condMap);
		if(onlineInterList != null && !onlineInterList.isEmpty()){
			if(onlineInterList.size() > 1){
				LogConstant.runLog.error("url = [" + interf.getUrl() + "]在线存在多个接口，请联系开发人员帮助解决");
				return 0;
			}
			onlineInterface = onlineInterList.get(0);
		}
		
		long version = 1; // 生成新的版本
		// [在线接口表]不存在该接口，插入在线接口表并更新接口表中相关字段。
		if(onlineInterface == null){
			//如果接口对应的模块没在[在线模块]中，添加到在线模块中。
			Page page = pageDaoImpl.selectPageById(interf.getPageId());
			if(page == null){
				LogConstant.runLog.info("执行上线时，接口[" + interfaceId + "]对应的page[" + interf.getPageId() + "]不存在!");
				return 0;
			}
			long moduleId = page.getModuleId();
			Module module = moduleDaoImpl.selectModuleById(moduleId);
			if(module == null){
				LogConstant.runLog.info("执行上线时，接口[" + interfaceId + "]对应的module[" + moduleId + "]不存在!");
				return 0;
			}
			String moduleName = module.getModuleName();
			Long onlineModuleId = null;
			OnlineModule onlineModule = onlineModuleDaoImpl.selectOnlineModuleByName(moduleName);
			if(onlineModule == null){
				OnlineModule moduleOnline = new OnlineModule();
				moduleOnline.setModuleName(moduleName);
				moduleOnline.setProductId(productId);
				moduleOnline.setStatus(1);
				onlineModuleDaoImpl.insertOnlineModule(moduleOnline);
				onlineModuleId = moduleOnline.getOnlineModuleId();
			}else{
				onlineModuleId = onlineModule.getOnlineModuleId();
			}
			
			//如果接口对应的页面没在[在线页面中]，添加到在线页面中。
			String pageName = page.getPageName();
			Long onlinePageId = null;
			OnlinePage onlinePage = onlinePageDaoImpl.selectOnlinePageByName(pageName);
			if(onlinePage == null){
				OnlinePage pageOnline = new OnlinePage();
				pageOnline.setOnlineModuleId(onlineModuleId);
				pageOnline.setPageName(pageName);
				pageOnline.setStatus(1);
				onlinePageDaoImpl.insertOnlinePage(pageOnline);
				onlinePageId = pageOnline.getOnlinePageId();
			}else{
				onlinePageId = onlinePage.getOnlinePageId();
			}
			
			//插入接口到在线接口表
			Date date = new Date();
			Timestamp tsOnline = new Timestamp(date.getTime());
			InterfaceOnline interfaceOnline = new InterfaceOnline();
			interfaceOnline.setProductId(productId);
			interfaceOnline.setOnlinePageId(onlinePageId);
			interfaceOnline.setInterfaceName(interf.getInterfaceName());
			interfaceOnline.setCreatorId(interf.getCreatorId());
			interfaceOnline.setInterfaceType(interf.getInterfaceType());
			interfaceOnline.setRequestType(interf.getRequestType());
			interfaceOnline.setUrl(interf.getUrl());
			interfaceOnline.setDesc(interf.getDesc());
			interfaceOnline.setIsNeedInterfaceTest(interf.getIsNeedInterfaceTest());
			interfaceOnline.setIsNeedPressureTest(interf.getIsNeedPressureTest());
			interfaceOnline.setExpectOnlineTime(interf.getExpectOnlineTime());
			interfaceOnline.setRealOnlineTime(tsOnline);
			interfaceOnline.setExpectTestTime(interf.getExpectTestTime());
			interfaceOnline.setRealTestTime(interf.getRealTestTime());
			interfaceOnline.setReqPeopleIds(interf.getReqPeopleIds());
			interfaceOnline.setFrontPeopleIds(interf.getFrontPeopleIds());
			interfaceOnline.setBehindPeopleIds(interf.getBehindPeopleIds());
			interfaceOnline.setClientPeopleIds(interf.getClientPeopleIds());
			interfaceOnline.setTestPeopleIds(interf.getTestPeopleIds());
			interfaceOnline.setInterfaceStatus(CommonConstant.INTERFACE_STATUS_ONLINE);
			interfaceOnline.setOnlineVersion(version);
			interfaceOnline.setIterVersion(interf.getIterVersion());
			interfaceOnline.setStatus(1);
			interfaceOnline.setFtlTemplate(interf.getFtlTemplate());
			interfaceOnlineDaoImpl.insertOnlineInterface(interfaceOnline);
			onlineInterface = interfaceOnline;
			
			//修改[接口表]中的online_interface_id、online_version和realOnlineTime字段
			Map<String, Object> updateInterfaceVersionMap = new HashMap<String, Object>();
			updateInterfaceVersionMap.put("interfaceId", interfaceId);
			updateInterfaceVersionMap.put("onlineInterfaceId", interfaceOnline.getInterfaceId()); // 在线接口id
			updateInterfaceVersionMap.put("onlineVersion", version);
			updateInterfaceVersionMap.put("realOnlineTime", new Date());
			interfaceDaoImpl.updateInterface(updateInterfaceVersionMap);
			
			//检查如果是项目的最后一个接口上线，将[项目表]中的status字段置为已上线
			if(isProjectOnline(projectId)){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("projectId", projectId);
				map.put("status", 2); // 2-已上线
				projectDaoImpl.updateProject(map);
			}
		}else{  // 如果该接口在[在线接口表]中存在，更新在线接口表中的数据和接口表中一样，并保存历史版本。
			InterfaceOnline oldInter = onlineInterface;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("interfaceId", onlineInterface.getInterfaceId());
			map.put("interfaceName", interf.getInterfaceName());
			map.put("creatorId", interf.getCreatorId());
			map.put("interfaceType", interf.getInterfaceType());
			map.put("requestType", interf.getRequestType());
			map.put("url", interf.getUrl());
			map.put("desc", interf.getDesc());
			map.put("isNeedInterfaceTest", interf.getIsNeedInterfaceTest());
			map.put("isNeedPressureTest", interf.getIsNeedPressureTest());
			map.put("expectOnlineTime", interf.getExpectOnlineTime());
			map.put("realOnlineTime", interf.getRealOnlineTime());
			map.put("expectTestTime", interf.getExpectTestTime());
			map.put("realTestTime", interf.getRealTestTime());
			map.put("reqPeopleIds", interf.getReqPeopleIds());
			map.put("frontPeopleIds", interf.getFrontPeopleIds());
			map.put("behindPeopleIds", interf.getBehindPeopleIds());
			map.put("clientPeopleIds", interf.getClientPeopleIds());
			map.put("testPeopleIds", interf.getTestPeopleIds());
			map.put("interfaceStatus", CommonConstant.INTERFACE_STATUS_ONLINE);
			version = onlineInterface.getOnlineVersion();
			map.put("onlineVersion", version + 1);
			map.put("iterVersion", interf.getIterVersion());
			map.put("updateTime", interf.getUpdateTime()); //更新时间会根据当前时间进行更新
			map.put("status", interf.getStatus());
			map.put("ftlTemplate", interf.getFtlTemplate());
			interfaceOnlineDaoImpl.updateOnlineInterface(map);
			
			//生成新的版本表插入在线版本库
			OnlineVersion onlineVersion = new OnlineVersion();
			onlineVersion.setInterfaceId(onlineInterface.getInterfaceId());
			onlineVersion.setVersionNum(version);
			UserModel user = TokenManager.getToken();
			if(user == null){
				LogConstant.runLog.info("执行上线时，生成在线版本对应的提交者不存在!");
				return 0;
			}
			Long userId = user.getUserId();
			onlineVersion.setCommitId(userId);
			// 设置用户相关信息
			oldInter.setFrontPeoples(loginDaoImpl.selectUserModelListByIds(oldInter.getFrontPeopleIds()));
			oldInter.setCreator(loginDaoImpl.queryUserModelByUserId(oldInter.getCreatorId()));
			oldInter.setReqPeoples(loginDaoImpl.selectUserModelListByIds(oldInter.getReqPeopleIds()));
			oldInter.setTestPeoples(loginDaoImpl.selectUserModelListByIds(oldInter.getTestPeopleIds()));
			oldInter.setClientPeoples(loginDaoImpl.selectUserModelListByIds(oldInter.getClientPeopleIds()));
			oldInter.setBehindPeoples(loginDaoImpl.selectUserModelListByIds(oldInter.getBehindPeopleIds()));
			// 设置请求参数信息
			List<Param> reqList = paramDaoImpl.selectRequestParamByInterfaceId(interf.getInterfaceId());
			if(reqList != null && !reqList.isEmpty()){
				for(Param pm: reqList){
					if(pm.getDictId() != null){
						pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
					}
				}
			}
			oldInter.setReqParams(reqList);
			// 设置返回参数信息
			List<Param> retList = paramDaoImpl.selectReturnParamByInterfaceId(interf.getInterfaceId());
			if(retList != null && !retList.isEmpty()){
				for(Param pm: retList){
					if(pm.getDictId() != null){
						pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
					}
				}
			}
			oldInter.setRetParams(retList);
			String snapshot = JSONObject.fromObject(oldInter).toString();
			onlineVersion.setSnapshot(snapshot);
			if(interf.getIterVersion() > oldInter.getIterVersion()){ // 非在线版本的版本号大于在线版本的
				onlineVersion.setVersionDesc("迭代版本-" + oldInter.getIterVersion());
			}else{
				onlineVersion.setVersionDesc("强制回收上线");
			}
			onlineVersion.setStatus(1);
			onlineVersionDaoImpl.insertOnlineVersion(onlineVersion);
			
			//修改[接口表]中的online_version字段
			Map<String, Object> updateInterfaceVersionMap = new HashMap<String, Object>();
			updateInterfaceVersionMap.put("interfaceId", interfaceId);
			updateInterfaceVersionMap.put("onlineVersion", version);
			updateInterfaceVersionMap.put("realOnlineTime", new Date());
			interfaceDaoImpl.updateInterface(updateInterfaceVersionMap);
			
		}
		//参数表插入相关信息
		// reqParams
		List<Param> reqParams = null;
		if(form.getReqParams().length > 0){
			reqParams = interServiceUtilImpl.saveReqParams(onlineInterface.getInterfaceId(), form.getReqParams(), true, false);
			onlineInterface.setReqParams(reqParams);
		}
		// retParams
		List<Param> retParams = null;
		if(form.getRetParams().length > 0){
			retParams = interServiceUtilImpl.saveRetParams(onlineInterface.getInterfaceId(), form.getRetParams(), true, false);
			onlineInterface.setRetParams(retParams);
		}
		//更新在线参数关系表
		updateOnlineParams(onlineInterface, reqParams, retParams);
		String creatorMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_ONLINE_POPO_MESSAGE_TO_CREATOR);
		if(StringUtil.isEmpty(creatorMsg)){
			creatorMsg = "{creator}：您好，接口[{interfaceName}]已经上线，接口链接：{interfaceLink}，提交人：{tester}";
		}else{
			creatorMsg = creatorMsg.replace("{tab}", "\r\n\t");
		}
		//修改接口状态为interfaceStatus
		paramMap.put("interfaceStatus", CommonConstant.INTERFACE_STATUS_ONLINE);
		intefaceFlowDaoImpl.updateInterfaceStatus(paramMap);
		//给接口创建者发送popo消息
		long creatorId = interf.getCreatorId();
		UserModel creator = loginDaoImpl.queryUserModelByUserId(creatorId);
		if(creator == null){
			LogConstant.runLog.info("执行上线时，接口创建者[" + creatorId + "]不存在!");
			return 0;
		}
		if(StringUtil.isEmpty(creator.getCorpMail())){
			LogConstant.runLog.info("执行上线时，接口创建者邮箱为空!");
			return 0;
		}
		
		creatorMsg = creatorMsg.replace("{creator}", creator.getUserName());
		creatorMsg = creatorMsg.replace("{interfaceName}", interf.getInterfaceName());
		String url = interfaceUrl + "?projectId=" + projectId + "&interfaceId=" + interfaceId;
		creatorMsg = creatorMsg.replace("{interfaceLink}", url);
		creatorMsg = creatorMsg.replace("{tester}", LoginUserInfoUtil.getUserName());
//		sendPopoMessage.send(creator.getCorpMail(), creatorMsg, SendPopoMessageMethod.POST);
		
		return 1;
	}
	//在线参数表
	private void updateOnlineParams(InterfaceOnline interfaceOnline, List<Param> reqParams, List<Param> retParams) {
		Map<String, Object> deleteMap = new HashMap<String, Object>();
		deleteMap.put("interfaceId", interfaceOnline.getInterfaceId());
		//先删除掉之前的参数关系数据
		paramDaoImpl.deleteOnlineRequestParam(deleteMap);
		paramDaoImpl.deleteOnlineReturnParam(deleteMap);
		//执行相关的插入操作
		Long onlineInterfaceId = interfaceOnline.getInterfaceId();
		if(reqParams != null){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("interfaceId", onlineInterfaceId);
			for(Param req : reqParams){
				map.put("paramId", req.getParamId());
				paramDaoImpl.insertOnlineRequestParam(map);
			}
		}
		if(retParams != null){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("interfaceId", onlineInterfaceId);
			for(Param ret : retParams){
				map.put("paramId", ret.getParamId());
				paramDaoImpl.insertOnlineReturnParam(map);
			}
		}
	}
	
	/* 
	 * 迭代版本
	 */
	@Override
	@Transactional
	public int iterationInter(long projectId, long interfaceId){
//		String englishName = LoginUserInfoUtil.getUserEnglishName();
//		String userId = loginDaoImpl.queryUserIdByUserName(englishName);
		Long userId = TokenManager.getUserId();
		if(userId == null){
			ErrorMessageUtil.put("没有找到用户信息，请登录后再操作！");
			LogConstant.runLog.error("userId="+userId + " 没有找到用户信息，请登录后再操作！");
			throw new RuntimeException("userId="+userId + " 没有找到用户信息，请登录后再操作！");
		}
		
		int status = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("interfaceId", interfaceId);
		Interface interf = interfaceDaoImpl.selectInterfaceById(paramMap);
		if(interf == null){
			LogConstant.runLog.info("执行时，接口为空!");
			return status;
		}
		
		int iterVersion = (int) (interf.getIterVersion() == null ? 1 : interf.getIterVersion()) + 1;
		int editVersion = (int) ((interf.getEditVersion() == null ? 1 : interf.getEditVersion()) + 1);
		// 保存版本相关信息 
		Interface oldInter = interfaceServiceImpl.getInterfaceInfo(interfaceId);
		Long oldVersion = oldInter.getEditVersion();
		if(oldVersion != 0){
			String interJson = JSONObject.fromObject(oldInter).toString();
			InterVersion interVersion = new InterVersion();
			interVersion.setCommitId(Long.valueOf(userId));
			interVersion.setInterfaceId(interfaceId);
			interVersion.setSnapshot(interJson);
			interVersion.setStatus(1);
			interVersion.setVersionDesc("迭代版本-" + iterVersion);
			interVersion.setVersionNum(oldInter.getEditVersion());
			status = interVersionDaoImpl.insertInterVersion(interVersion);
		}
		if(status == 0){
			return status;
		}
				
		//将接口状态改为‘编辑中’，迭代版本递增
		paramMap.put("interfaceStatus", CommonConstant.INTERFACE_STATUS_EDITING);
		paramMap.put("iterVersion", iterVersion);
		paramMap.put("editVersion", editVersion);
		paramMap.put("realTestTime", null);
		paramMap.put("realOnlineTime", null);
		status = intefaceFlowDaoImpl.updateInterfaceStatus(paramMap);

		return status;
	}

	/* 
	 * 强制回收
	 */
	@Override
	@Transactional
	public int forceBackToEdit(long projectId, long interfaceId, int reason) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("interfaceId", interfaceId);
		Interface interf = interfaceDaoImpl.selectInterfaceById(paramMap);
		if(interf == null){
			LogConstant.runLog.info("执行强制回收时，接口为空!");
			return 0;
		}
		//将接口状态改为‘编辑中’
		paramMap.put("interfaceStatus", CommonConstant.INTERFACE_STATUS_EDITING);
		intefaceFlowDaoImpl.updateInterfaceStatus(paramMap);
		UserModel creator = loginDaoImpl.queryUserModelByUserId(interf.getCreatorId());
		if(creator == null){
			LogConstant.runLog.info("执行强制回收时，接口创建者为空!");
			return 0;
		}
		
		//如果[项目表]中的status已上线，将status置为‘进行中’
		//long projectId = interfaceDaoImpl.selectProjectIdByInterfaceId(interfaceId);
		ProjectModel project = projectDaoImpl.selectProjectModelByProjectId(projectId);
		if(project != null && project.getStatus() == 2){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("projectId", projectId);
			map.put("status", 1); // 1-进行中，2-已上线
			projectDaoImpl.updateProject(map);
		}
		
		//通知前端/后台/客户端开发负责人分配其他相关负责人
		String url = interfaceUrl + "?projectId=" + projectId + "&interfaceId=" + interfaceId;
		String frontPeopleIds = interf.getFrontPeopleIds();
		if(StringUtil.isEmpty(frontPeopleIds)){
			LogConstant.runLog.info("执行强制回收时，前端开发负责人为空!");
		}else {
			String[] frontPeople = frontPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
			String frontMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_BACK_POPO_MESSAGE_TO_FRONT);
			if(StringUtil.isEmpty(frontMsg)){
				frontMsg = "{front}：\r\n\t您好，{creator}创建的接口[{interfaceName}]已经被强制回收重新进行修改，回收的原因是：{reason}，请随时关注接口修改动态，接口地址：{interfaceLink}，回收人：{creator}";
			}else{
				frontMsg = frontMsg.replace("{tab}", "\r\n\t");
			}
			for(String front : frontPeople){
				long frontId = Long.parseLong(front);
				UserModel frontModel = loginDaoImpl.queryUserModelByUserId(frontId);
				if(frontModel == null){
					LogConstant.runLog.info("执行强制回收时，接口前端负责人[" + frontId + "]不存在!");
					continue;
				}
				String sendMsg = frontMsg;
				sendMsg = sendMsg.replace("{front}", frontModel.getUserName());
				sendMsg = sendMsg.replace("{creator}", creator.getUserName());
				sendMsg = sendMsg.replace("{interfaceName}", interf.getInterfaceName());
				sendMsg = sendMsg.replace("{interfaceLink}", url);
				sendMsg = sendMsg.replace("{reason}", ForceBackReason.getReason(reason));
//				sendPopoMessage.send(frontModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
			}
		}
		
		String behindPeopleIds = interf.getFrontPeopleIds();
		if(StringUtil.isEmpty(behindPeopleIds)){
			LogConstant.runLog.info("执行强制回收时，后台开发负责人为空!");
		}else {
			String[] frontPeople = behindPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
			String frontMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_BACK_POPO_MESSAGE_TO_BEHIND);
			if(StringUtil.isEmpty(frontMsg)){
				frontMsg = "{front}：\r\n\t您好，{creator}创建的接口[{interfaceName}]已经被强制回收重新进行修改，回收的原因是：{reason}，请随时关注接口修改动态，接口地址：{interfaceLink}，回收人：{creator}";
			}else{
				frontMsg = frontMsg.replace("{tab}", "\r\n\t");
			}
			for(String front : frontPeople){
				long frontId = Long.parseLong(front);
				UserModel frontModel = loginDaoImpl.queryUserModelByUserId(frontId);
				if(frontModel == null){
					LogConstant.runLog.info("执行强制回收时，接口前端负责人[" + frontId + "]不存在!");
					continue;
				}
				String sendMsg = frontMsg;
				sendMsg = sendMsg.replace("{front}", frontModel.getUserName());
				sendMsg = sendMsg.replace("{creator}", creator.getUserName());
				sendMsg = sendMsg.replace("{interfaceName}", interf.getInterfaceName());
				sendMsg = sendMsg.replace("{interfaceLink}", url);
				sendMsg = sendMsg.replace("{reason}", ForceBackReason.getReason(reason));
//				sendPopoMessage.send(frontModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
			}
		}
		
		String clientPeopleIds = interf.getClientPeopleIds();
		if(StringUtil.isEmpty(clientPeopleIds)){
			LogConstant.runLog.info("执行强制回收时，客户端开发负责人为空!");
		}else {
			String[] clientPeople = clientPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
			String clientMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_BACK_POPO_MESSAGE_TO_CLIENT);
			if(StringUtil.isEmpty(clientMsg)){
				clientMsg = "{client}：\r\n\t您好，{creator}创建的接口[{interfaceName}]已经被强制回收重新进行修改，回收的原因是：{reason}，请随时关注接口修改动态，接口地址：{interfaceLink}，回收人：{creator}";
			}else{
				clientMsg = clientMsg.replace("{tab}", "\r\n\t");
			}
			for(String client : clientPeople){
				long clientId = Long.parseLong(client);
				UserModel clientModel = loginDaoImpl.queryUserModelByUserId(clientId);
				if(clientModel == null){
					LogConstant.runLog.info("执行强制回收时，接口客户端负责人[" + clientId + "]不存在!");
					continue;
				}
				String sendMsg = clientMsg;
				sendMsg = sendMsg.replace("{client}", clientModel.getUserName());
				sendMsg = sendMsg.replace("{creator}", creator.getUserName());
				sendMsg = sendMsg.replace("{interfaceName}", interf.getInterfaceName());
				sendMsg = sendMsg.replace("{interfaceLink}", url);
				sendMsg = sendMsg.replace("{reason}", ForceBackReason.getReason(reason));
//				sendPopoMessage.send(clientModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
			}
		}
		
		//如果需要测试，通知测试负责人分配其他相关负责人
		if(interf.getIsNeedInterfaceTest() == 1){
			String testPeopleIds = interf.getTestPeopleIds();
			if(StringUtil.isEmpty(testPeopleIds)){
				LogConstant.runLog.info("执行强制回收时，测试开发负责人为空!");
			}else {
				String[] testPeople = testPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
				String testMsg = (String) memcacheServer.getCacheData(CommonConstant.INI_BACK_POPO_MESSAGE_TO_TESTER);
				if(StringUtil.isEmpty(testMsg)){
					testMsg = "{tester}：\r\n\t您好，{creator}创建的接口[{interfaceName}]已经被强制回收重新进行修改，回收的原因是：{reason}，请随时关注接口修改动态，接口地址：{interfaceLink}，回收人：{creator}";
				}else{
					testMsg = testMsg.replace("{tab}", "\r\n\t");
				}
				for(String tester : testPeople){
					long testId = Long.parseLong(tester);
					UserModel testModel = loginDaoImpl.queryUserModelByUserId(testId);
					if(testModel == null){
						LogConstant.runLog.info("执行强制回收时，接口测试负责人[" + testId + "]不存在!");
						continue;
					}
					String sendMsg = testMsg;
					sendMsg = sendMsg.replace("{tester}", testModel.getUserName());
					sendMsg = sendMsg.replace("{creator}", creator.getUserName());
					sendMsg = sendMsg.replace("{interfaceName}", interf.getInterfaceName());
					sendMsg = sendMsg.replace("{interfaceLink}", url);
					sendMsg = sendMsg.replace("{reason}", ForceBackReason.getReason(reason));
//					sendPopoMessage.send(testModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
				}
			}
		}
		return 1;
	}
	
	/**
	 * 测试人员发送popo消息给接口创建者
	 * @param interfaceId  接口id
	 * @param logMsg   日志中关于调用的信息
	 * @param interfaceStatus 需要改变的接口状态
	 * @param creatorMsg  发送给创接着的消息内容
	 */
	private int sendPopoMessageToCreator(long projectId, long interfaceId, String logMsg, int interfaceStatus, String creatorMsg){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("interfaceId", interfaceId);
		Interface interf = interfaceDaoImpl.selectInterfaceById(paramMap);
		if(interf == null){
			LogConstant.runLog.info("执行" + logMsg + "时，接口[" + interfaceId + "]不存在!");
			return 0;
		}
		//修改接口状态为interfaceStatus
		paramMap.put("interfaceStatus", interfaceStatus);
		intefaceFlowDaoImpl.updateInterfaceStatus(paramMap);
		//给接口创建者发送popo消息
		long creatorId = interf.getCreatorId();
		UserModel creator = loginDaoImpl.queryUserModelByUserId(creatorId);
		if(creator == null){
			LogConstant.runLog.info("执行" + logMsg + "时，接口创建者[" + creatorId + "]不存在!");
			return 0;
		}
		if(StringUtil.isEmpty(creator.getCorpMail())){
			LogConstant.runLog.info("执行" + logMsg + "时，接口创建者邮箱为空!");
			return 0;
		}
		
		creatorMsg = creatorMsg.replace("{creator}", creator.getUserName());
		creatorMsg = creatorMsg.replace("{interfaceName}", interf.getInterfaceName());
		String url = interfaceUrl + "?projectId=" + projectId + "&interfaceId=" + interfaceId;
		creatorMsg = creatorMsg.replace("{interfaceLink}", url);
		creatorMsg = creatorMsg.replace("{tester}", LoginUserInfoUtil.getUserName());
//		sendPopoMessage.send(creator.getCorpMail(), creatorMsg, SendPopoMessageMethod.POST);
		return 1;
	}
	
	/**
	 * 判断项目下的所有接口是否上线
	 * @return
	 */
	private boolean isProjectOnline(long projectId){
		List<Interface> allInterfaces = projectDaoImpl.selectInterfaceByProjectId(projectId);
		for(Interface interf : allInterfaces){
			if(interf.getOnlineInterfaceId() == null){
				// 如果对应的在线接口id为空，再根据url和产品id查找在线接口表是否存在该接口
				Map<String,Object> condMap = new HashMap<String,Object>();
				Long productId = pageDaoImpl.selectProductIdByPageId(interf.getPageId());
				condMap.put("url", interf.getUrl());
				condMap.put("productId", productId);
				
				List<InterfaceOnline> onlineInterList = interfaceOnlineDaoImpl.selectOnlineInterfaceListByCond(condMap);
				if(onlineInterList == null || onlineInterList.isEmpty()){
					return false;
				}
			}
		}
		return true;
	}
}