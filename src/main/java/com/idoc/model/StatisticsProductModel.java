package com.idoc.model;

import java.sql.Timestamp;

public class StatisticsProductModel {
	private Long projectId;
	private Long productId;
	private Timestamp projectCreateTime;
	private String projectName;
	private int interfaceNum;
	private int onlineNum; // 上线接口个数
	private int userNum;
	private String userIds;
	private int forceBackNum;
	private String forceBackDetail;
	private int delayInterfaceNum;
	private int averageTestTime; // 单位：天
	
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
	public Timestamp getProjectCreateTime() {
		return projectCreateTime;
	}
	public void setProjectCreateTime(Timestamp projectCreateTime) {
		this.projectCreateTime = projectCreateTime;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public int getInterfaceNum() {
		return interfaceNum;
	}
	public void setInterfaceNum(int interfaceNum) {
		this.interfaceNum = interfaceNum;
	}
	public int getOnlineNum() {
		return onlineNum;
	}
	public void setOnlineNum(int onlineNum) {
		this.onlineNum = onlineNum;
	}
	public int getUserNum() {
		return userNum;
	}
	public void setUserNum(int userNum) {
		this.userNum = userNum;
	}
	public String getUserIds() {
		return userIds;
	}
	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}
	public int getForceBackNum() {
		return forceBackNum;
	}
	public void setForceBackNum(int forceBackNum) {
		this.forceBackNum = forceBackNum;
	}
	public String getForceBackDetail() {
		return forceBackDetail;
	}
	public void setForceBackDetail(String forceBackDetail) {
		this.forceBackDetail = forceBackDetail;
	}
	public int getDelayInterfaceNum() {
		return delayInterfaceNum;
	}
	public void setDelayInterfaceNum(int delayInterfaceNum) {
		this.delayInterfaceNum = delayInterfaceNum;
	}
	public int getAverageTestTime() {
		return averageTestTime;
	}
	public void setAverageTestTime(int averageTestTime) {
		this.averageTestTime = averageTestTime;
	}
}