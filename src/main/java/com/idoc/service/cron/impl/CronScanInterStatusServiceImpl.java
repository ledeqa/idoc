package com.idoc.service.cron.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.idoc.constant.LogConstant;
import com.idoc.constant.SendPopoMessageMethod;
import com.idoc.dao.docManage.InterfaceDaoImpl;
import com.idoc.dao.login.LoginDaoImpl;
import com.idoc.model.Interface;
import com.idoc.model.UserModel;
import com.idoc.util.SendPopoMessage;

@Service("cronScanInterStatusServiceImpl")
public class CronScanInterStatusServiceImpl {
		
	@Autowired
	private InterfaceDaoImpl interfaceDaoImpl;
	
	@Autowired
	private LoginDaoImpl loginDaoImpl;
	
	@Autowired
	private SendPopoMessage sendPopoMessage;
	
	private final String interfaceUrl = "http://idoc.qa.lede.com/idoc/inter/index.html";
	public void cronScanInterStatus(){
		Date now = new Date();
		//前后两天未完成提测或上线
		long start = now.getTime()/1000 - 2*24*60*60;//秒
		long end = now.getTime()/1000 + 2*24*60*60;//秒
		Date startTime = new Date(start*1000);
		Date endTime = new Date(end*1000);
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		List<Interface> interList = interfaceDaoImpl.selectInterfaceByTime(map);
		if(interList == null || interList.size()<=0){
			return;
		}
		for(Interface inter : interList){
			int interfaceStatus = inter.getInterfaceStatus();
			long creatorId = inter.getCreatorId();
			UserModel creator = loginDaoImpl.queryUserModelByUserId(creatorId);
			String interfaceName = inter.getInterfaceName();
			Timestamp expectTestTime = inter.getExpectTestTime();
			Timestamp expectOnlineTime = inter.getExpectOnlineTime();
			//查出接口所有的测试人员
			String testPeopleIds = inter.getTestPeopleIds();
			List<UserModel> testPeopleList = new ArrayList<>();
			if(testPeopleIds != null && !"".equals(testPeopleIds)){
				String[] testIds = testPeopleIds.split(",");
				List<Long> testList = new ArrayList<>();
				for(String testId : testIds){
					testList.add(Long.parseLong(testId));
				}
				testPeopleList = interfaceDaoImpl.selectUserListByInterface(testList);
			}else{
				testPeopleList = null;
			}
			
			Long projectId = interfaceDaoImpl.selectProjectIdByInterfaceId(inter.getInterfaceId());
			String interfaceLink = interfaceUrl + "?projectId=" + projectId + "&interfaceId=" + inter.getInterfaceId();
			String message = "{who}：\r\n\t您好，{creator}创建的接口[{interfaceName}]预计于{time}完成{type}，请尽快完成。接口链接：{interfaceLink}";
			message = message.replace("{creator}", creator.getUserName());
			message = message.replace("{interfaceName}", interfaceName);
			message = message.replace("{interfaceLink}", interfaceLink);
			if(interfaceStatus<4 || interfaceStatus == 301 || interfaceStatus == 310){
				//未完成提测的
				if(testPeopleList ==null || testPeopleList.size()<=0){
					LogConstant.runLog.info("执行定时扫描接口状态发送popo消息时，测试人员为空!");
				}else{
					for(int i=0;i<testPeopleList.size();i++){
						UserModel user = testPeopleList.get(i);
						String userName = user.getUserName();
						message = message.replace("{who}", userName);
						if(expectTestTime != null){
							message = message.replace("{time}", expectTestTime.toString());
						}else{
							message = message.replace("{time}", "未指定期望测试时间");
						}
						message = message.replace("{type}", "提测");
						message = message.replace(" ", ",");
//						sendPopoMessage.send(user.getCorpMail(), message, SendPopoMessageMethod.POST);
					}
				}
			}else{
				//未上线的
				
				//查出接口所有需求人员
				String reqPeopleIds = inter.getReqPeopleIds();
				List<UserModel> reqPeopleList = new ArrayList<>();
				if(reqPeopleIds != null && !"".equals(reqPeopleIds)){
					String[] reqIds = reqPeopleIds.split(",");
					List<Long> reqList = new ArrayList<>();
					for(String reqId : reqIds){
						reqList.add(Long.parseLong(reqId));
					}
					reqPeopleList = interfaceDaoImpl.selectUserListByInterface(reqList);
				}else{
					reqPeopleList = null;
				}
				
				
				//查出接口所有前端开发人员
				String frontPeopleIds = inter.getReqPeopleIds();
				List<UserModel> frontPeopleList = new ArrayList<>();
				if(frontPeopleIds != null && !"".equals(frontPeopleIds)){
					String[] frontIds = frontPeopleIds.split(",");
					List<Long> frontList = new ArrayList<>();
					for(String frontId : frontIds){
						frontList.add(Long.parseLong(frontId));
					}
					frontPeopleList = interfaceDaoImpl.selectUserListByInterface(frontList);
				}else{
					frontPeopleList = null;
				}
				
				//查出接口所有客户端开发人员
				String clientPeopleIds = inter.getClientPeopleIds();
				List<UserModel> clientPeopleList = new ArrayList<>();
				if(clientPeopleIds != null && !"".equals(clientPeopleIds)){
					String[] clientIds = clientPeopleIds.split(",");
					List<Long> clientList = new ArrayList<>();
					for(String clientId : clientIds){
						clientList.add(Long.parseLong(clientId));
					}
					clientPeopleList = interfaceDaoImpl.selectUserListByInterface(clientList);
				}else{
					clientPeopleList = null;
				}
				
				Map<String,String> sendPeoples = new HashMap<String,String>();
				if(reqPeopleList == null || reqPeopleList.size()<=0){
					LogConstant.runLog.info("执行定时扫描接口状态发送popo消息时，需求人员为空!");
				}else{
					for(UserModel user : reqPeopleList){
						sendPeoples.put(user.getUserName(), user.getCorpMail());
					}
				}
				if(frontPeopleList == null || frontPeopleList.size()<=0){
					LogConstant.runLog.info("执行定时扫描接口状态发送popo消息时，前端开发人员为空!");
				}else{
					for(UserModel user : frontPeopleList){
						sendPeoples.put(user.getUserName(), user.getCorpMail());
					}
				}
				if(clientPeopleList == null || clientPeopleList.size()<=0){
					LogConstant.runLog.info("执行定时扫描接口状态发送popo消息时，客户端开发人员为空!");
				}else{
					for(UserModel user : clientPeopleList){
						sendPeoples.put(user.getUserName(), user.getCorpMail());
					}
				}
				if(testPeopleList == null || testPeopleList.size()<=0){
					LogConstant.runLog.info("执行定时扫描接口状态发送popo消息时，测试人员为空!");
				}else{
					for(UserModel user : testPeopleList){
						sendPeoples.put(user.getUserName(), user.getCorpMail());
					}
				}
				
				if(sendPeoples == null || sendPeoples.size()<=0){
					continue;
				}else{
					for(String userName : sendPeoples.keySet()){
						message = message.replace("{who}", userName);
						if(expectOnlineTime != null){
							message = message.replace("{time}", expectOnlineTime.toString());
						}else{
							message = message.replace("{time}", "未指定期望上线时间");
						}
						message = message.replace("{type}", "上线");
						message = message.replace(" ", ",");
//						sendPopoMessage.send(sendPeoples.get(userName), message, SendPopoMessageMethod.POST);
					}
				}
			}
		}
	}
}