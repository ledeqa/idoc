package com.idoc.model;

import java.sql.Timestamp;


public class Param {
	
	private Long paramId;
	private String paramName;
	private String paramDesc;
	private String paramType;
	private int isNecessary;
	private Long dictId;
	private String remark;
	private String mock;
	private Timestamp createTime;
	private Timestamp updateTime;
	private int status;
	private Dict dict;
	private String uniqueId;
	private int isShow;
	
	
	public int getIsShow() {
		return isShow;
	}

	public void setIsShow(int isShow) {
		this.isShow = isShow;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public Long getParamId() {
	
		return paramId;
	}
	
	public void setParamId(Long paramId) {
	
		this.paramId = paramId;
	}
	
	public String getParamName() {
	
		return paramName;
	}
	
	public void setParamName(String paramName) {
	
		this.paramName = paramName;
	}
	
	public String getParamDesc() {
	
		return paramDesc;
	}
	
	public void setParamDesc(String paramDesc) {
	
		this.paramDesc = paramDesc;
	}
	
	public String getParamType() {
	
		return paramType;
	}
	
	public void setParamType(String paramType) {
	
		this.paramType = paramType;
	}
	
	public int getIsNecessary() {
	
		return isNecessary;
	}
	
	public void setIsNecessary(int isNecessary) {
	
		this.isNecessary = isNecessary;
	}
	
	public Long getDictId() {
	
		return dictId;
	}
	
	public void setDictId(Long dictId) {
	
		this.dictId = dictId;
	}
	
	public String getRemark() {
	
		return remark;
	}
	
	public void setRemark(String remark) {
	
		this.remark = remark;
	}
	
	public String getMock() {
	
		return mock;
	}
	
	public void setMock(String mock) {
	
		this.mock = mock;
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

	
	public Dict getDict() {
	
		return dict;
	}

	
	public void setDict(Dict dict) {
	
		this.dict = dict;
	}

	@Override
	public String toString() {
	
		return "Param [paramId=" + paramId + ", paramName=" + paramName + ", paramDesc=" + paramDesc + ", paramType=" + paramType + ", isNecessary=" + isNecessary + ", dictId=" + dictId + ", remark=" + remark + ", mock=" + mock + ", createTime=" + createTime + ", updateTime=" + updateTime + ", status=" + status + ", dict=" + dict + "]";
	}

	
}
