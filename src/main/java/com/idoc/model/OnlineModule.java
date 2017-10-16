package com.idoc.model;

import java.sql.Timestamp;
import java.util.List;

public class OnlineModule {
	private Long onlineModuleId;
	private Long productId;
	private String moduleName;
	private Timestamp createTime;
	private Timestamp updateTime;
	private int status;
	List<OnlinePage> pageList;
	
	public Long getOnlineModuleId() {
		return onlineModuleId;
	}
	public void setOnlineModuleId(Long onlineModuleId) {
		this.onlineModuleId = onlineModuleId;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
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
	public List<OnlinePage> getPageList() {
		return pageList;
	}
	public void setPageList(List<OnlinePage> pageList) {
		this.pageList = pageList;
	}
	@Override
	public String toString() {
		return "OnlineModule [onlineModuleId=" + onlineModuleId + ", projectId=" + productId + ", moduleName="
				+ moduleName + ", createTime=" + createTime + ", updateTime=" + updateTime + ", status=" + status
				+ ", pageList=" + pageList + "]";
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
}