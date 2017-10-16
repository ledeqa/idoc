package com.idoc.web.onlineInterface;

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

import com.idoc.dao.docManage.OnlineVersionDaoImpl;
import com.idoc.model.InterfaceOnline;
import com.idoc.model.OnlineVersion;
import com.idoc.service.interfaceOnline.InterfaceOnlineService;
import com.idoc.web.BaseController;

@Controller("onlineInterfaceHistoryController")
public class OnlineInterfaceHistoryController extends BaseController {
	
	@Autowired
	private OnlineVersionDaoImpl onlineVersionDaoImpl;
	
	@Autowired
	private InterfaceOnlineService interfaceOnlineService;
	
	@RequestMapping("idoc/onlineInter/interfaceHistoryVersion.html")
	public String interfaceHistoryVersion(String interfaceName, String interfaceId, String productId, Model model){
		try {
			model.addAttribute("interfaceName", URLDecoder.decode(interfaceName, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		model.addAttribute("interfaceId", interfaceId);
		model.addAttribute("productId", productId);
		long id = Long.parseLong(interfaceId);
		List<OnlineVersion> onlineVersions = onlineVersionDaoImpl.selectOnlineVersionListById(id);
		model.addAttribute("onlineVersions", onlineVersions);
		
		return "/idoc/onlineInter/history/index";
	}
	
	@RequestMapping("idoc/onlineInter/searchInterface.html")
	@ResponseBody
	public Map<String,Object> searchInterface(String interfaceId) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		Long id = Long.valueOf(interfaceId);
		InterfaceOnline inter = interfaceOnlineService.getOnlineInterfaceInfo(id);
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
	
	@RequestMapping("idoc/onlineInter/searchInterfaceVersion.html")
	@ResponseBody
	public Map<String,Object> searchInterfaceVersion(String versionId) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		OnlineVersion version = onlineVersionDaoImpl.searchOnlineVersion(Long.parseLong(versionId));
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