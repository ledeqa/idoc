package com.idoc.service.cron.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.constant.CommonConstant;
import com.idoc.constant.LogConstant;
import com.idoc.constant.SendPopoMessageMethod;
import com.idoc.dao.docManage.InterfaceDaoImpl;
import com.idoc.dao.productAndProject.ProductUserDaoImpl;
import com.idoc.dao.productAndProject.ProjectDaoImpl;
import com.idoc.memcache.MemcacheServer;
import com.idoc.model.Interface;
import com.idoc.model.ProjectModel;
import com.idoc.model.UserModel;
import com.idoc.util.SendPopoMessage;
import com.netease.common.util.StringUtil;

@Service("cronScanProjectScheduleServiceImpl")
public class CronScanProjectScheduleServiceImpl {
	@Autowired
	private ProjectDaoImpl projectDaoImpl;
	
	@Autowired
	private InterfaceDaoImpl interfaceDaoImpl;
	
	@Autowired
	private ProductUserDaoImpl productUserDaoImpl;
	
	@Autowired
	private MemcacheServer memcacheServer;
	
	@Autowired
	private SendPopoMessage sendPopoMessage;
	
	// 首先查询所有的项目，如果整个项目延期>=50% 发送popo通知延期接口的各个角色
	private final String interfaceUrl = "http://idoc.qa.lede.com/idoc/inter/index.html";
	private static final int before = 3; // 查询当前时间前3个月的记录
	public void cronScanProjectSchedule(){
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String endTime = sdf.format(now);
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.MONTH, -before);
		String startTime = sdf.format(cal.getTime());
		List<ProjectModel> projects = projectDaoImpl.selectProjectInTimespan(startTime, endTime);
		if(projects == null || projects.size() <= 0){
			return ;
		}
		
		// 查询出所有的用户信息方便发送popo消息使用
		Map<String, UserModel> userMap = new HashMap<String, UserModel>();
		List<UserModel> userList = productUserDaoImpl.selectAllUserFromTB_IDOC_USER();
		if(userList != null && userList.size() > 0){
			for(UserModel user : userList){
				userMap.put(String.valueOf(user.getUserId()), user);
			}
		}
		
		for(ProjectModel project : projects){
			List<Interface> interfaces = interfaceDaoImpl.getAllInterfaceListByProjectId(project.getProjectId());
			int interfaceNum = 0;
			int onlineNum = 0;
			int delayInterfaceNum = 0;
			List<Interface> delayInterfaceList = new ArrayList<Interface>();
			if(interfaces != null && interfaces.size() > 0){
				interfaceNum = interfaces.size();
				for(Interface inter : interfaces){
					// 统计接口延期个数
					boolean isDelay = false;
					Timestamp expectOnlineTime = inter.getExpectOnlineTime();
					Timestamp expectTestTime = inter.getExpectTestTime();
					Timestamp realOnlineTime = inter.getRealOnlineTime();
					Timestamp realTestTime = inter.getRealTestTime();
					if(expectOnlineTime != null){
						if(realOnlineTime != null){
							if(expectOnlineTime.before(realOnlineTime)){
								isDelay = true;
							}
						}else{
							Date today = new Date();
							if(expectOnlineTime.before(today)){
								isDelay = true;
							}
						}
					}
					if(expectTestTime != null){
						if(realTestTime != null){
							if(expectTestTime.before(realTestTime)){
								isDelay = true;
							}
						}else{
							Date today = new Date();
							if(expectTestTime.before(today)){
								isDelay = true;
							}
						}
					}
					if(isDelay){
						delayInterfaceNum++;
						if(inter.getInterfaceStatus() != 9){ // 已经上线不发送消息
							delayInterfaceList.add(inter);
						}
					}
				}
				int delayPercentage = delayInterfaceNum/interfaceNum;
				int schedule = onlineNum/interfaceNum;
				if(delayPercentage * 100 >= 50){ // 当整个项目的延期大于等于50%时，对处于延期的接口发送popo消息通知相关人员
					for(Interface interf : delayInterfaceList){
						sendDelayedInterfacePopoMessage(project.getProjectId(), project.getProjectName(), interfaceNum, onlineNum, schedule * 100, delayPercentage * 100, interf, userMap);
					}
				}
			}
		}
	}
	
	private void sendDelayedInterfacePopoMessage(Long projectId, String projectName, int interfaceNum, int onlineNum, int schedule, int delayPercetage, Interface interf, Map<String, UserModel> userMap){
		String url = interfaceUrl + "?projectId=" + projectId + "&interfaceId=" + interf.getInterfaceId();
		//通知前端/客户端开发负责人分配其他相关负责人
		String cronScanProjectSchedulePopoMessageSwitch = (String) memcacheServer.getCacheData(CommonConstant.INI_CRON_SCAN_PROJECT_SCHEDULE_POPO_MESSAGE_SWITCH);
		if(StringUtil.isEmpty(cronScanProjectSchedulePopoMessageSwitch) || cronScanProjectSchedulePopoMessageSwitch.equals("0")){
			LogConstant.runLog.info("执行定时扫描是否有上线风险发送popo消息的开关关闭!");
			return;
		}
		String message = (String) memcacheServer.getCacheData(CommonConstant.INI_CRON_SCAN_PROJECT_SCHEDULE_POPO_MESSAGE);
		if(StringUtil.isEmpty(message)){
			message = "{who}：\r\n\t您好，项目 {projectName} 下的接口 [{interfaceName}] 可能会延期上线，该项目下共有{interfaceNum}个接口，目前上线{onlineNum}个接口，开发进度估计为：{schedule}%，延期比例为：{delayPercetage}%，该接口的预计上线时间为：{expectOnlineTime}，请尽快处理。接口链接：{interfaceLink}";
		}else{
			message = message.replace("{tab}", "\r\n\t");
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		message = message.replace("{projectName}", projectName);
		message = message.replace("{interfaceName}", interf.getInterfaceName());
		message = message.replace("{interfaceNum}", String.valueOf(interfaceNum));
		message = message.replace("{onlineNum}", String.valueOf(onlineNum));
		message = message.replace("{schedule}", String.valueOf(schedule));
		message = message.replace("{delayPercetage}", String.valueOf(delayPercetage));
		message = message.replace("{expectOnlineTime}", sdf.format(interf.getExpectOnlineTime()));
		message = message.replace("{interfaceLink}", url);
		
		String frontPeopleIds = interf.getFrontPeopleIds();
		if(StringUtil.isEmpty(frontPeopleIds)){
			LogConstant.runLog.info("执行定时扫描是否有上线风险发送popo消息时，前端开发负责人为空!");
		}else {
			String[] frontPeople = frontPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
			
			for(String front : frontPeople){
				long frontId = Long.parseLong(front);
				UserModel frontModel = userMap.get(String.valueOf(frontId));
				if(frontModel == null){
					LogConstant.runLog.info("执行定时扫描是否有上线风险发送popo消息时，接口前端负责人[" + frontId + "]不存在!");
					continue;
				}
				String sendMsg = message;
				sendMsg = sendMsg.replace("{who}", frontModel.getUserName());
//				sendPopoMessage.send(frontModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
			}
		}
		
		String clientPeopleIds = interf.getClientPeopleIds();
		if(StringUtil.isEmpty(clientPeopleIds)){
			LogConstant.runLog.info("执行定时扫描是否有上线风险发送popo消息时，客户端开发负责人为空!");
		}else {
			String[] clientPeople = clientPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
			for(String client : clientPeople){
				long clientId = Long.parseLong(client);
				UserModel clientModel = userMap.get(String.valueOf(clientId));
				if(clientModel == null){
					LogConstant.runLog.info("执行定时扫描是否有上线风险发送popo消息时，接口客户端负责人[" + clientId + "]不存在!");
					continue;
				}
				String sendMsg = message;
				sendMsg = sendMsg.replace("{who}", clientModel.getUserName());
//				sendPopoMessage.send(clientModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
			}
		}
		
		//如果需要测试，通知测试负责人分配其他相关负责人
		if(interf.getIsNeedInterfaceTest() == 1){
			String testPeopleIds = interf.getTestPeopleIds();
			if(StringUtil.isEmpty(testPeopleIds)){
				LogConstant.runLog.info("执行定时扫描是否有上线风险发送popo消息时，测试开发负责人为空!");
			}else {
				String[] testPeople = testPeopleIds.split(CommonConstant.PEOPLE_ID_SPLIT);
				for(String tester : testPeople){
					long testId = Long.parseLong(tester);
					UserModel testModel = userMap.get(String.valueOf(testId));
					if(testModel == null){
						LogConstant.runLog.info("执行定时扫描是否有上线风险发送popo消息时，接口测试负责人[" + testId + "]不存在!");
						continue;
					}
					String sendMsg = message;
					sendMsg = sendMsg.replace("{who}", testModel.getUserName());
//					sendPopoMessage.send(testModel.getCorpMail(), sendMsg, SendPopoMessageMethod.POST);
				}
			}
		}
	}
}