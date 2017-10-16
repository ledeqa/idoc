package com.idoc.model;

import java.sql.Timestamp;


public class Role {
	
	private Long roleId;
	private String roleName;
	private Timestamp createTime;
	private int status;
	
	public Long getRoleId() {
	
		return roleId;
	}
	
	public void setRoleId(Long roleId) {
	
		this.roleId = roleId;
	}
	
	public String getRoleName() {
	
		return roleName;
	}
	
	public void setRoleName(String roleName) {
	
		this.roleName = roleName;
	}
	
	public Timestamp getCreateTime() {
	
		return createTime;
	}
	
	public void setCreateTime(Timestamp createTime) {
	
		this.createTime = createTime;
	}
	
	public int getStatus() {
	
		return status;
	}
	
	public void setStatus(int status) {
	
		this.status = status;
	}
}
