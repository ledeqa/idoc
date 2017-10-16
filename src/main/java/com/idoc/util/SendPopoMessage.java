package com.idoc.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.constant.CommonConstant;
import com.idoc.constant.LogConstant;
import com.idoc.constant.SendPopoMessageMethod;
import com.idoc.memcache.MemcacheServer;
import com.netease.common.util.StringUtil;

/**
 * @author li_zhe
 * 调用app.qa上的接口发送popo消息，支持get，post方式，可以发送给个人或群组
 */
@Service("sendPopoMessage")
public class SendPopoMessage {
	
	@Autowired
	private MemcacheServer memcacheServer;
	private static final String sendPopoUrl = "http://app.qa.ms.netease.com/popo/sendPopoMsg.html";
	private static final String sendGroupPopoUrl = "http://app.qa.ms.netease.com/popo/sendGroupPopoMsg.html";
	
	public void send(String to, String msg){
		String sendPopoMessageSwitch = (String) memcacheServer.getCacheData(CommonConstant.INI_SEND_POPO_MESSAGE_SWITCH);
		if(StringUtil.isEmpty(sendPopoMessageSwitch)){
			sendPopoMessageSwitch = "1";   //默认打开
		}
		if(sendPopoMessageSwitch.equals("0")){
			LogConstant.debugLog.info("发送popo消息开关关闭！");
			return;
		}
		if(StringUtil.isEmpty(to) || StringUtil.isEmpty(msg)){
			LogConstant.debugLog.info("发送popo消息时，发送人或消息为空！");
		}
		String url = sendPopoUrl + "?to=" + to + "&content=" + msg;
		String res = HttpClient.get(url);
		LogConstant.debugLog.info("发送popo消息返回结果：" + res);
	}
	
	public void send(String to, String msg, String method){
		if(StringUtil.isEmpty(to) || StringUtil.isEmpty(msg)){
			LogConstant.debugLog.info("发送popo消息时，发送人或消息为空！");
		}
		if(StringUtil.isEmpty(method)){
			method = "get";
		}
		
		String res = null;
		if(method.equals(SendPopoMessageMethod.GET)){
			String sendPopoMessageSwitch = (String) memcacheServer.getCacheData(CommonConstant.INI_SEND_POPO_MESSAGE_SWITCH);
			if(StringUtil.isEmpty(sendPopoMessageSwitch)){
				sendPopoMessageSwitch = "1";   //默认打开
			}
			if(sendPopoMessageSwitch.equals("0")){
				LogConstant.debugLog.info("发送popo消息开关关闭！");
				return;
			}
			String url = sendPopoUrl + "?to=" + to + "&content=" + msg;
			res = HttpClient.get(url);
		}else if(method.equals(SendPopoMessageMethod.POST)){
			Map<String, String> params = new HashMap<String, String>();
			params.put("to", to);
			params.put("content", msg);
			res = HttpClient.post(sendPopoUrl, params);
		}
		
		LogConstant.debugLog.info("发送popo消息返回结果：" + res);
	}
	
	public void sendGroupMessage(String groupId, String msg){
		if(StringUtil.isEmpty(groupId) || StringUtil.isEmpty(msg)){
			LogConstant.debugLog.info("发送群组popo消息时，群组或消息为空！");
		}
		String url = sendGroupPopoUrl + "?groupId=" + groupId + "&content=" + msg;
		String res = HttpClient.get(url);
		LogConstant.debugLog.info("发送群组popo消息返回结果：" + res);
	}
	
	public void sendGroupMessage(String groupId, String msg, String method){
		if(StringUtil.isEmpty(groupId) || StringUtil.isEmpty(msg)){
			LogConstant.debugLog.info("发送群组popo消息时，群组或消息为空！");
		}
		if(StringUtil.isEmpty(method)){
			method = "get";
		}
		
		String res = null;
		if(method.equals(SendPopoMessageMethod.GET)){
			String url = sendGroupPopoUrl + "?groupId=" + groupId + "&content=" + msg;
			res = HttpClient.get(url);
		}else if(method.equals(SendPopoMessageMethod.POST)){
			Map<String, String> params = new HashMap<String, String>();
			params.put("groupId", groupId);
			params.put("content", msg);
			res = HttpClient.post(sendGroupPopoUrl, params);
		}
		
		LogConstant.debugLog.info("发送群组popo消息返回结果：" + res);
	}
}