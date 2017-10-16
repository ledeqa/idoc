package com.idoc.model.rap;

import java.util.List;

/**
 * @author tangjun
 *
 */
public class RapParameter {
	
	private Long id;
	private String dataType;
	private String remark;
	private String validator;
	private List<RapParameter> parameterList;
	private String name;
	private String identifier;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getValidator() {
		return validator;
	}
	public void setValidator(String validator) {
		this.validator = validator;
	}
	public List<RapParameter> getParameterList() {
		return parameterList;
	}
	public void setParameterList(List<RapParameter> parameterList) {
		this.parameterList = parameterList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}
