package com.idoc.service.cron.crontask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.constant.LogConstant;
import com.idoc.service.cron.CronTask;
import com.idoc.service.cron.impl.CronScanInterStatusServiceImpl;

@Service("cronScanInterStatusTask")
public class CronScanInterStatusTask implements CronTask{
	
	@Autowired
	private CronScanInterStatusServiceImpl cronScanInterStatusServiceImpl;
	
	@Override
	public void runTask(){
		LogConstant.debugLog.info("执行定时扫描接口状态发送popo消息开始！");
		cronScanInterStatusServiceImpl.cronScanInterStatus();
		LogConstant.debugLog.info("执行定时扫描接口状态发送popo消息成功！");
	}
}
