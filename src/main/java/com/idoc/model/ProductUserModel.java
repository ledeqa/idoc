package com.idoc.model;

import java.sql.Timestamp;


public class ProductUserModel {
	
	private Long productUserId;
	private Long productId;
	private Long userId;	
	private Long roleId;
	private Timestamp createTime;
	private Timestamp updateTime;
	private int status;
	
	private UserModel user;
	private Role role;
	
	public Long getProductUserId() {
	
		return productUserId;
	}
	
	public void setProductUserId(Long productUserId) {
	
		this.productUserId = productUserId;
	}
	
	public Long getProductId() {
	
		return productId;
	}
	
	public void setProductId(Long productId) {
	
		this.productId = productId;
	}
	
	public Long getUserId() {
	
		return userId;
	}
	
	public void setUserId(Long userId) {
	
		this.userId = userId;
	}
	
	public Long getRoleId() {
	
		return roleId;
	}
	
	public void setRoleId(Long roleId) {
	
		this.roleId = roleId;
	}
	
	public Timestamp getCreateTime() {
	
		return createTime;
	}
	
	public void setCreateTime(Timestamp createTime) {
	
		this.createTime = createTime;
	}
	
	public Timestamp getUpdateTime() {
	
		return updateTime;
	}
	
	public void setUpdateTime(Timestamp updateTime) {
	
		this.updateTime = updateTime;
	}
	
	public int getStatus() {
	
		return status;
	}
	
	public void setStatus(int status) {
	
		this.status = status;
	}
	
	public UserModel getUser() {
	
		return user;
	}
	
	public void setUser(UserModel user) {
	
		this.user = user;
	}
	
	public Role getRole() {
	
		return role;
	}
	
	public void setRole(Role role) {
	
		this.role = role;
	}

	@Override
	public String toString() {
	
		return "ProductUserModel [productUserId=" + productUserId + ", productId=" + productId + ", userId=" + userId + ", roleId=" + roleId + ", createTime=" + createTime + ", updateTime=" + updateTime + ", status=" + status + ", user=" + user + ", role=" + role + "]";
	}
	
	
	
	
}

