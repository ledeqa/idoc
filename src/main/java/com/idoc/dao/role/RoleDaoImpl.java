package com.idoc.dao.role;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.model.Role;

@Repository("roleDaoImpl")
public class RoleDaoImpl extends BaseMysqlDBDaoImpl {
	
	public List<Role> queryRoleModelByRoleName(String roleName){
		return this.getSqlSession().selectList("roleModel.queryRoleModelByRoleName", roleName);
	}
	
	public List<Role> queryRoleModel(){
		return this.getSqlSession().selectList("roleModel.queryRoleModel");
	}
	
	public int insertRoleModelByRoleName(Role roleModel){
		return this.getSqlSession().insert("roleModel.insertRole", roleModel);
	}

	public int deleteRole(String roleId) {
		return this.getSqlSession().update("roleModel.deleteRole", roleId);
		
	}

	public int updateRole(Map<String,Object> map) {
		return this.getSqlSession().update("roleModel.updateRole", map);
	}
	
}