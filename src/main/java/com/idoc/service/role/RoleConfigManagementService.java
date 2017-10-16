package com.idoc.service.role;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.idoc.dao.role.RoleDaoImpl;
import com.idoc.model.Role;
import com.idoc.service.productAndProject.ProductUserServiceImpl;


@Service("roleConfigManagementService")
public class RoleConfigManagementService {
	
	@Resource(name="roleDaoImpl")
	private RoleDaoImpl roleDaoImpl;
	
	@Resource(name="productUserServiceImpl")
	private ProductUserServiceImpl productUserServiceImpl;

	public int addRoleConfig(String roleName) {
		
		List<Role> roleModelList = roleDaoImpl.queryRoleModelByRoleName(roleName);
		if(roleModelList.size() > 0){
			return 1;
		}
		
		Role role = new Role();
		role.setRoleName(roleName);
		Date date = new Date();
		Timestamp currentTime = new Timestamp(date.getTime());
		role.setCreateTime(currentTime);
		role.setStatus(1);
		
		int num = roleDaoImpl.insertRoleModelByRoleName(role);
		
		if(num > 0){
			return 0;
		}
		return 2;
	}

	public List<Role> selectRoleConfigModel() {
		List<Role> roleModels = new ArrayList<Role>();
		List<Role> roleModelList = roleDaoImpl.queryRoleModel();
		for(Role roleModel : roleModelList){
			Role role = new Role();
			role.setRoleId(roleModel.getRoleId());
			role.setRoleName(roleModel.getRoleName());
			role.setCreateTime(roleModel.getCreateTime());
			role.setStatus(roleModel.getStatus());
			roleModels.add(role);
		}
		return roleModels;
	}

	public int deleteRole(String roleId) {
		int ret = roleDaoImpl.deleteRole(roleId);
		return ret;
	}

	public int updateRoleConfig(String roleValue, String roleId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleName", roleValue);
		map.put("roleId", roleId);
		int ret = roleDaoImpl.updateRole(map);
		return ret;
	}
	
	/**
	 * 通过用户邮箱和项目id查询用户的角色
	 * @param englishName
	 * @param productId
	 * @return
	 */
	public Role selectRoleByUserEnglishName(String englishName, long productId){
		return productUserServiceImpl.selectFromProductUserRole(englishName, productId);
	}
}