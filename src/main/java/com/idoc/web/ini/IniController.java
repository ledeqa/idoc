package com.idoc.web.ini;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.model.IniConfigModel;
import com.idoc.service.ini.IniConfigManagementService;
import com.idoc.service.login.LoginIndexServiceImpl;
import com.idoc.util.LoginUserInfoUtil;
import com.idoc.web.BaseController;

@Controller
@RequestMapping("ini")
public class IniController extends BaseController {

	@Autowired
	private IniConfigManagementService iniConfigManagementServiceImpl;
	
	@Resource(name = "loginIndexServiceImpl")
	private LoginIndexServiceImpl loginIndexServiceImpl;

	@RequestMapping("index")
	public String index(Model map) {
		List<IniConfigModel> iniConfigList = iniConfigManagementServiceImpl
				.selectIniConfigModel();
		
		map.addAttribute("iniConfigList", iniConfigList);
		
		String englishName = LoginUserInfoUtil.getUserEnglishName();
		if(englishName != null ){
			int adminFlag = loginIndexServiceImpl.confirmAdmin(englishName);
			if(adminFlag ==1 ){
				return "/ini/iniConfigManagement";
			}else{
				return "redirect:/index.html";
			}
		}
		
		return "redirect:/index.html";
	}

	/**
	 * 刷新当前页面
	 * 
	 * @return
	 */
	@RequestMapping("refreshCurrentPage")
	@ResponseBody
	public Map<String, Object> refreshCurrentPage() {
		Map<String, Object> retMap = new HashMap<String, Object>();

		List<IniConfigModel> iniConfigList = iniConfigManagementServiceImpl
				.selectIniConfigModel();
		if (iniConfigList != null && iniConfigList.size() > 0) {
			retMap.put("retCode", 200);
			retMap.put("iniConfigList", iniConfigList);
			retMap.put("retDesc", "刷新页面成功！");
		} else {
			retMap.put("retCode", 400);
			retMap.put("retDesc", "刷新页面失败！");
		}
		return retMap;
	}

	/**
	 * 根据iniKey查询记录
	 * iniKey
	 * @return
	 */
	@RequestMapping("selectIniConfigByIniKey")
	@ResponseBody
	public Map<String, Object> selectIniConfigByIniKey(String iniKey) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		if (iniKey == null) {
			retMap.put("retCode", 400);
			retMap.put("retDesc", "查询ini配置信息时，参数为空!");
			return retMap;
		}
		paramMap.put("iniKey", iniKey);
		IniConfigModel iniConfig = iniConfigManagementServiceImpl
				.selectIniConfigByIniKey(paramMap);
		if (iniConfig != null) {
			retMap.put("retCode", 200);
			retMap.put("iniConfig", iniConfig);
			retMap.put("retDesc", "查询ini配置信息成功！");
		} else {
			retMap.put("retCode", 400);
			retMap.put("retDesc", "查询ini配置信息失败！");
		}
		return retMap;
	}
	
	/**
	 * 添加ini配置信息
	 * 
	 * @param iniKey
	 * @param iniValue
	 * @param iniDesc
	 * @return
	 */
	@RequestMapping("addIniConfigModel")
	@ResponseBody
	public Map<String, Object> addIniConfigModel(String iniKey,
			String iniValue, String iniDesc) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		if (iniKey == null || iniValue == null || iniDesc == null) {
			retMap.put("retCode", 400);
			retMap.put("retDesc", "添加ini配置信息时，参数为空!");
			return retMap;
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("iniKey", iniKey);
		IniConfigModel iniConfigModel = iniConfigManagementServiceImpl.selectIniConfigByIniKey(paramMap);
		if(iniConfigModel != null){
			retMap.put("retCode", 300);
			retMap.put("retDesc", "插入的key已经存在！");
			return retMap;
		}
		iniConfigModel = new IniConfigModel();
		iniConfigModel.setIniKey(iniKey);
		iniConfigModel.setIniValue(iniValue);
		iniConfigModel.setIniDesc(iniDesc);
		int res = iniConfigManagementServiceImpl
				.insertIniConfigModel(iniConfigModel);
		if (res > 0) {
			retMap.put("retCode", 200);
			retMap.put("retDesc", "添加ini配置信息成功！");
		} else {
			retMap.put("retCode", 400);
			retMap.put("retDesc", "添加ini配置信息失败！");
		}
		return retMap;
	}

	/**
	 * 修改ini配置信息
	 * 
	 * @param iniKey
	 * @param iniValue
	 * @param iniDesc
	 * @return
	 */
	@RequestMapping("updateIniConfigModel")
	@ResponseBody
	public Map<String, Object> updateIniConfigModel(String iniKey,
			String iniValue, String iniDesc) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		if (iniKey == null || iniValue == null || iniDesc == null) {
			retMap.put("retCode", 400);
			retMap.put("retDesc", "修改ini配置信息时，参数为空!");
			return retMap;
		}
		IniConfigModel iniConfigModel = new IniConfigModel();
		iniConfigModel.setIniKey(iniKey);
		iniConfigModel.setIniValue(iniValue);
		iniConfigModel.setIniDesc(iniDesc);
		int res = iniConfigManagementServiceImpl
				.updateIniConfigModel(iniConfigModel);
		if (res > 0) {
			retMap.put("retCode", 200);
			retMap.put("retDesc", "修改ini配置信息成功！");
		} else {
			retMap.put("retCode", 400);
			retMap.put("retDesc", "修改ini配置信息失败！");
		}
		return retMap;
	}

	/**
	 * 删除ini配置信息
	 * 
	 * @param iniKey
	 * @return
	 */
	@RequestMapping("deleteIniConfigModel")
	@ResponseBody
	public Map<String, Object> deleteIniConfigModel(String iniKey) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		if (iniKey == null) {
			retMap.put("retCode", 400);
			retMap.put("retDesc", "删除ini配置信息时，参数为空!");
			return retMap;
		}
		IniConfigModel iniConfigModel = new IniConfigModel();
		iniConfigModel.setIniKey(iniKey);
		int res = iniConfigManagementServiceImpl
				.deleteIniConfigModel(iniConfigModel);
		if (res > 0) {
			retMap.put("retCode", 200);
			retMap.put("retDesc", "删除ini配置信息成功！");
		} else {
			retMap.put("retCode", 400);
			retMap.put("retDesc", "删除ini配置信息失败！");
		}
		return retMap;
	}

	/**
	 * 刷新ini配置表
	 * 
	 * @return
	 */
	@RequestMapping("refreshIniConfig")
	@ResponseBody
	public Map<String, Object> refreshIniConfig() {
		Map<String, Object> retMap = new HashMap<String, Object>();
		boolean res = iniConfigManagementServiceImpl.refresh();
		if (res) {
			retMap.put("retCode", 200);
			retMap.put("retDesc", "刷新ini配置表成功！");
		} else {
			retMap.put("retCode", 400);
			retMap.put("retDesc", "刷新ini配置表失败！");
		}
		return retMap;
	}

}