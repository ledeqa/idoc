package com.idoc.model;

import java.sql.Timestamp;

public class InterfaceStatusChangement {
	private Long changementId;
	private long interfaceId;
	private int interfaceStatus;
	private String changementDesc;
	private long operatorId;
	private Timestamp createTime;
	private int changementReason;
	private String remark;

	private UserModel operator;

	public Long getChangementId() {
		return changementId;
	}
	public void setChangementId(Long changementId) {
		this.changementId = changementId;
	}
	public long getInterfaceId() {
		return interfaceId;
	}
	public void setInterfaceId(long interfaceId) {
		this.interfaceId = interfaceId;
	}
	public int getInterfaceStatus() {
		return interfaceStatus;
	}
	public void setInterfaceStatus(int interfaceStatus) {
		this.interfaceStatus = interfaceStatus;
	}
	public String getChangementDesc() {
		return changementDesc;
	}
	public void setChangementDesc(String changementDesc) {
		this.changementDesc = changementDesc;
	}
	public long getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(long operatorId) {
		this.operatorId = operatorId;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public int getChangementReason() {
		return changementReason;
	}
	public void setChangementReason(int changementReason) {
		this.changementReason = changementReason;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public UserModel getOperator() {
		return operator;
	}
	public void setOperator(UserModel operator) {
		this.operator = operator;
	}
}