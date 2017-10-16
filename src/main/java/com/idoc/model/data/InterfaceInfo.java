package com.idoc.model.data;

import java.util.List;

public class InterfaceInfo {
	private String interfaceName;
	private String creator;
	private String url;
	private String desc;
	private String requestType;
	private String interfaceType;
	private List<ParamInfo> reqParamList;
	private List<ParamInfo> retParamList;
	private List<DictParamInfo> dictParamList;
	
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getInterfaceType() {
		return interfaceType;
	}
	public void setInterfaceType(String interfaceType) {
		this.interfaceType = interfaceType;
	}
	public List<ParamInfo> getReqParamList() {
		return reqParamList;
	}
	public void setReqParamList(List<ParamInfo> reqParamList) {
		this.reqParamList = reqParamList;
	}
	public List<ParamInfo> getRetParamList() {
		return retParamList;
	}
	public void setRetParamList(List<ParamInfo> retParamList) {
		this.retParamList = retParamList;
	}
	public List<DictParamInfo> getDictParamList() {
		return dictParamList;
	}
	public void setDictParamList(List<DictParamInfo> dictParamList) {
		this.dictParamList = dictParamList;
	}
}