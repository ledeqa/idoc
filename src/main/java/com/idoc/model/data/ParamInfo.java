package com.idoc.model.data;

public class ParamInfo {
	private String paramName;
	private String paramType;
	private String paramDesc;
	private String paramRemark;
	private String paramIsNecessary;
	
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamType() {
		return paramType;
	}
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	public String getParamDesc() {
		return paramDesc;
	}
	public void setParamDesc(String paramDesc) {
		this.paramDesc = paramDesc;
	}
	public String getParamRemark() {
		return paramRemark;
	}
	public void setParamRemark(String paramRemark) {
		this.paramRemark = paramRemark;
	}
	public String getParamIsNecessary() {
		return paramIsNecessary;
	}
	public void setParamIsNecessary(String paramIsNecessary) {
		this.paramIsNecessary = paramIsNecessary;
	}
}