package com.idoc.model.rap;

import java.util.List;

/**
 * @author tangjun
 *
 */
public class RapAction {
	
	private Long id;
	private String responseTemplate;
	private List<RapParameter> responseParameterList;
	private String description;
	private String name;
	private String requestType; //0:get 1:post
	private List<RapParameter> requestParameterList;
	private String requestUrl;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getResponseTemplate() {
		return responseTemplate;
	}
	public void setResponseTemplate(String responseTemplate) {
		this.responseTemplate = responseTemplate;
	}
	public List<RapParameter> getResponseParameterList() {
		return responseParameterList;
	}
	public void setResponseParameterList(List<RapParameter> responseParameterList) {
		this.responseParameterList = responseParameterList;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public List<RapParameter> getRequestParameterList() {
		return requestParameterList;
	}
	public void setRequestParameterList(List<RapParameter> requestParameterList) {
		this.requestParameterList = requestParameterList;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}


}
