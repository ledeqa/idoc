package com.idoc.web.docManage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.dao.docManage.InterVersionDaoImpl;
import com.idoc.dao.login.LoginDaoImpl;
import com.idoc.model.InterVersion;
import com.idoc.model.Interface;
import com.idoc.model.UserModel;
import com.idoc.service.docManage.InterfaceServiceImpl;
import com.idoc.util.DataTypeCheckUtil;
import com.idoc.web.BaseController;

@Controller("interfaceHistoryController")
public class InterfaceHistoryController extends BaseController {
	
	@Autowired
	private InterVersionDaoImpl interVersionDaoImpl;
	
	@Autowired
	private LoginDaoImpl loginDaoImpl;
	
	@Autowired
	private InterfaceServiceImpl interfaceServiceImpl;
	
	@RequestMapping("idoc/inter/interfaceHistoryVersion.html")
	public String interfaceHistoryVersion(String interfaceName, String interfaceId, String productId, String projectId, Model model){
		try {
			model.addAttribute("interfaceName", URLDecoder.decode(interfaceName, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(!DataTypeCheckUtil.isNumber(interfaceId)){
			model.addAttribute("exceptionMsg", "参数格式有误，请检查参数。");
			return "/datatypeError";
		}
		model.addAttribute("interfaceId", interfaceId);
		model.addAttribute("productId", productId);
		model.addAttribute("projectId", projectId);
		long id = Long.parseLong(interfaceId);
		List<InterVersion> interfaceVersions = interVersionDaoImpl.selectInterVersionListById(id);
		model.addAttribute("interfaceVersions", interfaceVersions);
		
		return "/interface/history/index";
	}
	
	@RequestMapping("idoc/inter/searchInterface.html")
	@ResponseBody
	public Map<String,Object> searchInterface(String interfaceId) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		Long id = Long.valueOf(interfaceId);
		Interface inter = interfaceServiceImpl.getInterfaceInfo(id);
		if(inter != null){
			retMap.put("retCode", 200);
			retMap.put("retDesc", "操作成功！");
			retMap.put("inter", inter);
		}else{
			retMap.put("retCode", 400);
			retMap.put("retDesc", "操作失败！");
		}
		return retMap;
	}
	
	@RequestMapping("idoc/inter/searchInterfaceVersion.html")
	@ResponseBody
	public Map<String,Object> searchInterfaceVersion(String versionId, String projectId, String interfaceId) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		InterVersion version = interVersionDaoImpl.searchInterfaceVersion(Long.parseLong(versionId));
		if(version != null){
			UserModel usermodel = loginDaoImpl.queryUserModelByUserId(version.getCommitId());
			String operator  = usermodel.getUserName();
			retMap.put("retCode", 200);
			retMap.put("retDesc", "操作成功！");
			retMap.put("version", version);
			retMap.put("operator", operator);
		}else{
			retMap.put("retCode", 400);
			retMap.put("retDesc", "操作失败！");
		}
		return retMap;
	}
	
	@RequestMapping("idoc/inter/searchLastInterfaceVersion.html")
	@ResponseBody
	public Map<String,Object> searchLastInterfaceVersion(String versionNum, String interfaceId) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("interfaceId", Long.parseLong(interfaceId));
		map.put("versionNum", Long.parseLong(versionNum));
		InterVersion version = interVersionDaoImpl.selectLastInterVersion(map);
		if(version != null){
			retMap.put("retCode", 200);
			retMap.put("retDesc", "操作成功！");
			retMap.put("version", version);
		}else{
			retMap.put("retCode", 400);
			retMap.put("retDesc", "操作失败！");
		}
		return retMap;
	}
}