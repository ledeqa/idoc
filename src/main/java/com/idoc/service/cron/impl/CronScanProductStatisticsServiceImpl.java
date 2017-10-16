package com.idoc.service.cron.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.dao.docManage.InterfaceDaoImpl;
import com.idoc.dao.docManage.InterfaceStatusChangementDaoImpl;
import com.idoc.dao.productAndProject.ProjectDaoImpl;
import com.idoc.dao.statistics.StatisticsProductDaoImpl;
import com.idoc.model.Interface;
import com.idoc.model.InterfaceStatusChangement;
import com.idoc.model.ProjectModel;
import com.idoc.model.StatisticsProductModel;
import com.netease.common.util.StringUtil;

@Service("cronScanProductStatisticsServiceImpl")
public class CronScanProductStatisticsServiceImpl {
	@Autowired
	private ProjectDaoImpl projectDaoImpl;
	
	@Autowired
	private InterfaceDaoImpl interfaceDaoImpl;
	
	@Autowired
	private StatisticsProductDaoImpl statisticsProductDaoImpl;
	
	@Autowired
	private InterfaceStatusChangementDaoImpl interfaceStatusChangementDaoImpl;
	
	private static final int before = 6; // 每次只更新当前时间往前6个月的数据
	public void cronScanProductStatistics(){
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
		// 对于查出来的每个项目查询其接口对应的信息进行计算
		for(ProjectModel project : projects){
			List<Interface> interfaces = interfaceDaoImpl.getAllInterfaceListByProjectId(project.getProjectId());
			int interfaceNum = 0;
			int userNum = 0;
			String userIds = "";
			int onlineNum = 0; // 已上线接口个数
			int delayInterfaceNum = 0;
			int testNum = 0; // 记录测试接口的个数
			int testTime = 0;
			int forceBackNum = 0;
			String forceBackDetail = "";
			int averageTestTime = 0;
			if(interfaces != null && interfaces.size() > 0){
				interfaceNum = interfaces.size();
				Set<String> userSet = new HashSet<String>(); // 用于计算用户人数
				List<Long> interfaceIds = new ArrayList<Long>();
				for(Interface inter : interfaces){
					// 统计项目用户人数
					countUserNum(inter.getReqPeopleIds(), userSet);
					countUserNum(inter.getFrontPeopleIds(), userSet);
					countUserNum(inter.getClientPeopleIds(), userSet);
					countUserNum(inter.getTestPeopleIds(), userSet);
					userSet.add(String.valueOf(inter.getCreatorId()));
					
					if(inter.getInterfaceStatus() == 9){
						onlineNum++;
					}
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
					}
					// 统计平均测试时间
					if(realTestTime != null){
						if(realOnlineTime != null && realTestTime.before(realOnlineTime)){
							testTime += (realOnlineTime.getNanos() - realTestTime.getNanos())/(1000 * 60 * 60 * 24);
						}else{
							Date today = new Date();
							testTime += (today.getTime() - realTestTime.getTime())/(1000 * 60 * 60 * 24); //单位：天
						}
						testNum++;
					}
					
					interfaceIds.add(inter.getInterfaceId());
				}
				userNum = userSet.size();
				userIds = StringUtils.join(userSet, ",");
				if(testNum != 0){
					averageTestTime = testTime/testNum;
				}
				// 统计强制回收次数
				List<InterfaceStatusChangement> forceBackReason = interfaceStatusChangementDaoImpl.getInterfaceForceBackReason(interfaceIds);
				if(forceBackReason != null && forceBackReason.size() > 0){
					forceBackNum = forceBackReason.size();
					Map<String, Integer> forceBackMap = new HashMap<String, Integer>();
					for(InterfaceStatusChangement changement : forceBackReason){
						String reason = String.valueOf(changement.getChangementReason());
						int num = 1;
						if(forceBackMap.containsKey(reason)){ 
							num = forceBackMap.get(reason) + 1;
						}
						forceBackMap.put(reason, num);
					}
					List<String> detail = new ArrayList<String>();
					for(Entry<String, Integer> entry: forceBackMap.entrySet()){
						detail.add(entry.getKey() + ":" + entry.getValue());
					}
					forceBackDetail = StringUtils.join(detail, ",");
				}
			}
			// 更新数据库
			StatisticsProductModel statisticsProductModel = new StatisticsProductModel();
			statisticsProductModel.setProjectId(project.getProjectId());
			statisticsProductModel.setProductId(project.getProductId());
			statisticsProductModel.setProjectCreateTime(project.getCreateTime());
			statisticsProductModel.setProjectName(project.getProjectName());
			statisticsProductModel.setInterfaceNum(interfaceNum);
			statisticsProductModel.setOnlineNum(onlineNum);
			statisticsProductModel.setUserNum(userNum);
			statisticsProductModel.setUserIds(userIds);
			statisticsProductModel.setForceBackNum(forceBackNum);
			statisticsProductModel.setForceBackDetail(forceBackDetail);
			statisticsProductModel.setDelayInterfaceNum(delayInterfaceNum);
			statisticsProductModel.setAverageTestTime(averageTestTime);
			StatisticsProductModel statistics = statisticsProductDaoImpl.selectStatisticsProductByProjectId(project.getProjectId());
			if(statistics == null){
				statisticsProductDaoImpl.insertStatisticsProduct(statisticsProductModel);
			}else{
				statisticsProductDaoImpl.updateStatisticsProduct(statisticsProductModel);
			}
		}
	}
	
	private void countUserNum(String ids, Set<String> userSet){
		if(StringUtil.isEmpty(ids)){
			return;
		}
		String[] peopleIds = ids.split(",");
		for(String id : peopleIds){
			userSet.add(id);
		}
	}
}