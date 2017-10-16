package com.idoc.model;

import java.sql.Timestamp;
import java.util.List;


/**
 * 模块类
 * @author wangxiaoming
 *
 */
public class Module {
	
	private Long moduleId;
	private Long projectId;
	private String moduleName;
	private Timestamp createTime;
	private Timestamp updateTime;
	private int status;
	List<Page> pageList;
	
	
	
	public Long getProjectId() {
	
		return projectId;
	}
	
	public void setProjectId(Long projectId) {
	
		this.projectId = projectId;
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

	
	public Long getModuleId() {
	
		return moduleId;
	}

	
	public void setModuleId(Long moduleId) {
	
		this.moduleId = moduleId;
	}

	
	public List<Page> getPageList() {
	
		return pageList;
	}

	
	public void setPageList(List<Page> pageList) {
	
		this.pageList = pageList;
	}

	@Override
	public String toString() {
	
		return "Module [moduleId=" + moduleId + ", projectId=" + projectId + ", moduleName=" + moduleName + ", createTime=" + createTime + ", updateTime=" + updateTime + ", status=" + status + ", pageList=" + pageList + "]";
	}
	
}
