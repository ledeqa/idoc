package com.idoc.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.service.cron.crontask.CronScanInterStatusTask;
import com.idoc.util.ErrorMessageUtil;

@Controller
@RequestMapping("test")
public class TestController {

	@Autowired
	private CronScanInterStatusTask cronScanInterStatusTask;
	
	@RequestMapping("cronScan")
	@ResponseBody
	public Map<String,Object> testCron(){
		Map<String,Object> map = new HashMap<>();
		try{
			cronScanInterStatusTask.runTask();
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isNotBlank(error)){
				map.put("retCode", 402);
				map.put("retDesc", error);
			}else{
				map.put("retCode", 200);
				map.put("retDesc", "操作成功");
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	
}
