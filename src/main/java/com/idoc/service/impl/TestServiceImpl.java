package com.idoc.service.impl;

import org.springframework.stereotype.Service;

import com.idoc.constant.LogConstant;
import com.idoc.service.TestService;

@Service("testService")
public class TestServiceImpl implements TestService {
	
	@Override
	public void consoleOutput(String name) {
	
		System.out.println("Hello, " + name + "! This is a test method.");
		
	}
	
	@Override
	public void loggerOutput(String name) {
		LogConstant.debugLog.info("Hello, " + name + "! This is a test method.");
	}
	
}
