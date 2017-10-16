package com.idoc.model;

import java.sql.Timestamp;


public class InterVersion {
	
	private Long versionId;
	private Long interfaceId;
	private Long versionNum;
	private String versionDesc;
	private Long commitId;
	private String snapshot;
	private Timestamp createTime;
	private Timestamp updateTime;
	private int status;
	
	public Long getVersionId() {
	
		return versionId;
	}
	
	public void setVersionId(Long versionId) {
	
		this.versionId = versionId;
	}
	
	public Long getInterfaceId() {
	
		return interfaceId;
	}
	
	public void setInterfaceId(Long interfaceId) {
	
		this.interfaceId = interfaceId;
	}
	
	public Long getVersionNum() {
	
		return versionNum;
	}
	
	public void setVersionNum(Long versionNum) {
	
		this.versionNum = versionNum;
	}
	
	public String getVersionDesc() {
	
		return versionDesc;
	}
	
	public void setVersionDesc(String versionDesc) {
	
		this.versionDesc = versionDesc;
	}
	
	public Long getCommitId() {
	
		return commitId;
	}
	
	public void setCommitId(Long commitId) {
	
		this.commitId = commitId;
	}
	
	public String getSnapshot() {
	
		return snapshot;
	}
	
	public void setSnapshot(String snapshot) {
	
		this.snapshot = snapshot;
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
	
		return "InterVersion [versionId=" + versionId + ", interfaceId=" + interfaceId + ", versionNum=" + versionNum + ", versionDesc=" + versionDesc + ", commitId=" + commitId + ", snapshot=" + snapshot + ", createTime=" + createTime + ", updateTime=" + updateTime + ", status=" + status + "]";
	}
	
	
}
