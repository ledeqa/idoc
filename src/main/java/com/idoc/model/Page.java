package com.idoc.model;

import java.sql.Timestamp;
import java.util.List;


/**
 * 页面类
 * @author wangxiaoming
 *
 */
public class Page {
	private Long pageId;
	private Long moduleId;
	private String pageName;
	private Timestamp createTime;
	private Timestamp updateTime;
	private int status;
	private List<Interface> interfaceList;
	
	public Long getPageId() {
	
		return pageId;
	}
	
	public void setPageId(Long pageId) {
	
		this.pageId = pageId;
	}
	
	public Long getModuleId() {
	
		return moduleId;
	}
	
	public void setModuleId(Long moduleId) {
	
		this.moduleId = moduleId;
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

	
	public List<Interface> getInterfaceList() {
	
		return interfaceList;
	}

	
	public void setInterfaceList(List<Interface> interfaceList) {
	
		this.interfaceList = interfaceList;
	}

	@Override
	public String toString() {
	
		return "Page [pageId=" + pageId + ", moduleId=" + moduleId + ", pageName=" + pageName + ", createTime=" + createTime + ", updateTime=" + updateTime + ", status=" + status + ", interfaceList=" + interfaceList + "]";
	}
	
}
