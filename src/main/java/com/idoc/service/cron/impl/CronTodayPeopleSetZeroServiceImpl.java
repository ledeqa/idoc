package com.idoc.service.cron.impl;

import org.springframework.stereotype.Service;

import com.idoc.util.CountSession;

@Service("cronTodayPeopleSetZeroServiceImpl")
public class CronTodayPeopleSetZeroServiceImpl{
	public void scanSetSave() {
//		CountSession.todayPeople.compareAndSet(CountSession.todayPeople.get(), 0);
	}

}
