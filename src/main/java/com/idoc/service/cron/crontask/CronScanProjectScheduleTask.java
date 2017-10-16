package com.idoc.service.cron.crontask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.constant.LogConstant;
import com.idoc.service.cron.CronTask;
import com.idoc.service.cron.impl.CronScanProjectScheduleServiceImpl;

@Service("cronScanProjectScheduleTask")
public class CronScanProjectScheduleTask implements CronTask{
	
	@Autowired
	private CronScanProjectScheduleServiceImpl cronScanProjectScheduleServiceImpl;
	
	@Override
	public void runTask(){
		LogConstant.debugLog.info("执行定时扫描项目进度发送popo消息开始！");
		cronScanProjectScheduleServiceImpl.cronScanProjectSchedule();
		LogConstant.debugLog.info("执行定时扫描项目进度发送popo消息成功！");
	}
}