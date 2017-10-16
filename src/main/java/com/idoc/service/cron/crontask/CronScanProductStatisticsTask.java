package com.idoc.service.cron.crontask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.constant.LogConstant;
import com.idoc.service.cron.CronTask;
import com.idoc.service.cron.impl.CronScanProductStatisticsServiceImpl;

@Service("cronScanProductStatisticsTask")
public class CronScanProductStatisticsTask implements CronTask{

	@Autowired
	private CronScanProductStatisticsServiceImpl cronScanProductStatisticsServiceImpl;
	
	public void runTask() {
		LogConstant.debugLog.info("执行定时计算产品统计信息开始！");
		cronScanProductStatisticsServiceImpl.cronScanProductStatistics();
		LogConstant.debugLog.info("执行定时计算产品统计信息成功！");
	}
}