package com.idoc.model;

import java.sql.Timestamp;


public class ProjectModel {
	
	private Long projectId;
	private Long productId;
	private String projectName;	
	private Timestamp createTime;
	private Timestamp updateTime;
	private int interfaceNum;
	private int submitTestNum;
	private int onlineNum;
	private int status;
	
	public Long getProjectId() {
	
		return projectId;
	}
	
	public void setProjectId(Long projectId) {
	
		this.projectId = projectId;
	}
	
	public Long getProductId() {
	
		return productId;
	}
	
	public void setProductId(Long productId) {
	
		this.productId = productId;
	}
	
	public String getProjectName() {
	
		return projectName;
	}
	
	public void setProjectName(String projectName) {
	
		this.projectName = projectName;
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

	@Override
	public String toString() {
	
		return "ProjectModel [projectId=" + projectId + ", productId=" + productId + ", projectName=" + projectName + ", createTime=" + createTime + ", updateTime=" + updateTime + ", status=" + status + "]";
	}

	
	public int getInterfaceNum() {
	
		return interfaceNum;
	}

	
	public void setInterfaceNum(int interfaceNum) {
	
		this.interfaceNum = interfaceNum;
	}

	
	public int getSubmitTestNum() {
	
		return submitTestNum;
	}

	
	public void setSubmitTestNum(int submitTestNum) {
	
		this.submitTestNum = submitTestNum;
	}

	
	public int getOnlineNum() {
	
		return onlineNum;
	}

	
	public void setOnlineNum(int onlineNum) {
	
		this.onlineNum = onlineNum;
	}
	
	
}

