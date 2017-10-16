package com.idoc.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.constant.CommonConstant;
import com.idoc.constant.SendPopoMessageMethod;
import com.idoc.memcache.MemcacheServer;
import com.idoc.service.login.LoginIndexServiceImpl;
import com.idoc.shiro.token.TokenManager;
import com.idoc.util.CountSession;
import com.idoc.util.SendPopoMessage;
import com.netease.common.util.StringUtil;

@Controller
public class IndexController {

	@Resource(name = "loginIndexServiceImpl")
	private LoginIndexServiceImpl loginIndexServiceImpl;

	@Autowired
	private SendPopoMessage sendPopoMessage;

	@Autowired
	private MemcacheServer memcacheServer;

	@RequestMapping("index")
	public String index() {
		return "login/index";
	}

	@RequestMapping("admin")
	public String admin(HttpServletRequest request, HttpServletResponse response) {
		String englishName = (String)request.getSession().getAttribute("englishName");
		String cdnBaseUrl = (String)request.getAttribute("cdnBaseUrl");
		if(englishName != null ){
			int adminFlag = loginIndexServiceImpl.confirmAdmin(englishName);
			if(adminFlag ==1 ){
				return "admin";
			}
		}

		try {
			response.sendRedirect(cdnBaseUrl + "/index.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "index";
	}

	@RequestMapping("idoc/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		memcacheServer.deleteCacheData(request.getSession().getId());
		request.getSession().removeAttribute("admin");
		CountSession.onlinePeople.getAndDecrement();
		request.getSession().removeAttribute("firstRequest");
		TokenManager.logout();
		return "redirect:/index.html";
	}

	@RequestMapping("fail/contactAdmin.html")
	@ResponseBody
	public Map<String,Object> contactAdmin(@RequestParam("exceptionMsg")String exceptionMsg){
		Map<String,Object> retMap = new HashMap<String,Object>();
		if(StringUtil.isEmpty(exceptionMsg)){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "exceptionMsg为空！");
			return retMap;
		}
		String msgToAdmin = (String) memcacheServer.getCacheData(CommonConstant.EXCEPTION_POPO_MESSAGE_TO_ADMIN);
		if(!StringUtil.isEmpty(msgToAdmin)){
			String[] admins = msgToAdmin.split(";");
			for(String admin : admins){
//				sendPopoMessage.send(admin, "admin：\r\n\t您好！，接口管理平台系统出错，错误日志如下：\r\n" + exceptionMsg.replaceAll("\\{enter\\}", "\r\n"), SendPopoMessageMethod.POST);
			}
			retMap.put("retCode", 200);
			retMap.put("retDesc", "成功发送popo消息给管理员！");
 		}else{
			retMap.put("retCode", 203);
			retMap.put("retDesc", "没有找到管理员！");
		}
		return retMap;
	}
}