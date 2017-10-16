package com.idoc.service.cron.crontask;

import org.springframework.beans.factory.annotation.Autowired;

import com.idoc.service.cron.CronTask;
import com.idoc.service.cron.impl.CronScanInterStatusServiceImpl;
import com.idoc.service.cron.impl.CronTodayPeopleSetZeroServiceImpl;

public class CronTodayPeopleSetZero implements CronTask{
	@Autowired
	private CronTodayPeopleSetZeroServiceImpl cronTodayPeopleSetZeroServiceImpl; 
	@Override
	public void runTask() {
		cronTodayPeopleSetZeroServiceImpl.scanSetSave();	
	}

}
