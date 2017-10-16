package com.idoc.model;

import java.sql.Timestamp;
import java.util.List;

public class OnlinePage {
	private Long onlinePageId;
	private Long onlineModuleId;
	private String pageName;
	private Timestamp createTime;
	private Timestamp updateTime;
	private int status;
	
	private List<InterfaceOnline> interfaceList;
	
	public Long getOnlinePageId() {
		return onlinePageId;
	}
	public void setOnlinePageId(Long onlinePageId) {
		this.onlinePageId = onlinePageId;
	}
	public Long getOnlineModuleId() {
		return onlineModuleId;
	}
	public void setOnlineModuleId(Long onlineModuleId) {
		this.onlineModuleId = onlineModuleId;
	}
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
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
	public List<InterfaceOnline> getInterfaceList() {
		return interfaceList;
	}
	public void setInterfaceList(List<InterfaceOnline> interfaceList) {
		this.interfaceList = interfaceList;
	}
	@Override
	public String toString() {
		return "OnlinePage [onlinePageId=" + onlinePageId + ", onlineModuleId=" + onlineModuleId + ", pageName="
				+ pageName + ", createTime=" + createTime + ", updateTime=" + updateTime + ", status=" + status
				+ ", interfaceList=" + interfaceList + "]";
	}
}