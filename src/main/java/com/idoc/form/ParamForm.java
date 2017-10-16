package com.idoc.form;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.idoc.model.Param;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParamForm {
	
	private Long paramId;
	private String paramName;
	private String paramDesc;
	private String paramType;
	private int isNecessary;
	private Long dictId;
	private String remark;
	private String mock;
	private DictForm dict;
	
	public ParamForm(){
		
	}
	
	public ParamForm(Param param){
		this.paramId = param.getParamId();
		this.paramName = param.getParamName();
		this.paramType = param.getParamType();
		this.isNecessary = param.getIsNecessary();
		this.remark = param.getRemark();
		this.mock = param.getMock();
		
		if(param.getDict() != null){
			this.dictId = param.getDictId();
			this.dict = new DictForm(param.getDict());
		}
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

	public DictForm getDict() {
		return dict;
	}

	public void setDict(DictForm dict) {
		this.dict = dict;
	}
	
	public boolean hasMockJSData(){
		if(StringUtils.isNotBlank(this.mock)){
			return true;
		}
		
		return false;
	}
	
	public String getMockJSIdentifier(){
		if(StringUtils.isBlank(this.paramName)){
			return "\"emptyIdentifier\"";
		}
		
		return "\"" + this.paramName + "\"";
	}

	@Override
	public String toString() {
		return "ParamForm [paramId=" + paramId + ", paramName=" + paramName
				+ ", paramDesc=" + paramDesc + ", paramType=" + paramType
				+ ", isNecessary=" + isNecessary + ", dictId=" + dictId
				+ ", remark=" + remark + ", mock=" + mock + ", dict=" + dict
				+ "]";
	}
	
}
