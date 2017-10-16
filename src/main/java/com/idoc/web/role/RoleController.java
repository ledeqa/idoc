package com.idoc.web.role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.model.Role;
import com.idoc.service.login.LoginIndexServiceImpl;
import com.idoc.service.role.RoleConfigManagementService;
import com.idoc.util.LoginUserInfoUtil;
import com.idoc.web.BaseController;

@Controller
@RequestMapping("/idoc")
public class RoleController extends BaseController {

	@Autowired
	private RoleConfigManagementService roleConfigManagementServiceImpl;
	
	@Resource(name = "loginIndexServiceImpl")
	private LoginIndexServiceImpl loginIndexServiceImpl;

	@RequestMapping("/roleConfig")
	public String index(Model map) {
		List<Role> roleConfigList = roleConfigManagementServiceImpl
				.selectRoleConfigModel();
		map.addAttribute("roleConfigList", roleConfigList);
		
		String englishName = LoginUserInfoUtil.getUserEnglishName();
		if(englishName != null ){
			int adminFlag = loginIndexServiceImpl.confirmAdmin(englishName);
			if(adminFlag ==1 ){
				return "/role/roleConfigManagement";
			}else{
				return "redirect:/index.html";
			}
		}
		return "redirect:/index.html";
	}
	
	/**
	 * 添加ini配置信息
	 * 
	 * @param iniKey
	 * @param iniValue
	 * @param iniDesc
	 * @return
	 */
	@RequestMapping("/addRoleConfigModel")
	@ResponseBody
	public Map<String, Object> addRoleConfigModel(String roleValue) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		if (roleValue == null) {
			retMap.put("retCode", 400);
			retMap.put("retDesc", "添加角色信息时，参数为空!");
			return retMap;
		}

		int ret = roleConfigManagementServiceImpl.addRoleConfig(roleValue);
		
		if(ret == 0){
			retMap.put("retCode", 200);
			retMap.put("retDesc", "添加角色信息成功！");
		}else if(ret == 1){
			retMap.put("retCode", 300);
			retMap.put("retDesc", "该角色已经存在！");
		}else{
			retMap.put("retCode", 400);
			retMap.put("retDesc", "添加角色失败！");
		}
		
		return retMap;
	}
	
	
	@RequestMapping("/deleteRole")
	@ResponseBody
	public Map<String, Object> deleteRole(String roleId) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		int ret = roleConfigManagementServiceImpl.deleteRole(roleId);
		if(ret == 1){
			retMap.put("retCode", 200);
			retMap.put("retDesc", "删除成功！");
		}
		return retMap;
	}
	
	
	@RequestMapping("/updateRoleConfigModel")
	@ResponseBody
	public Map<String, Object> updateRoleConfigModel(String roleName, String roleId) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		if (roleName == null) {
			retMap.put("retCode", 400);
			retMap.put("retDesc", "修改角色信息时，参数为空!");
			return retMap;
		}

		int ret = roleConfigManagementServiceImpl.updateRoleConfig(roleName,roleId);
		
		if(ret == 1){
			retMap.put("retCode", 200);
			retMap.put("retDesc", "修改角色信息成功！");
		}else{
			retMap.put("retCode", 400);
			retMap.put("retDesc", "修改角色失败！");
		}
		
		return retMap;
	}
	
	
}