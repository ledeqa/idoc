package com.idoc.model.data;

import java.util.List;

public class DictParamInfo {
	private String dictName; // 该字典的名称
	private int hasList;
	private List<ParamInfo> paramList;
	public String getDictName() {
		return dictName;
	}
	public void setDictName(String dictName) {
		this.dictName = dictName;
	}
	public List<ParamInfo> getParamList() {
		return paramList;
	}
	public void setParamList(List<ParamInfo> paramList) {
		this.paramList = paramList;
	}
	public int getHasList() {
		return hasList;
	}
	public void setHasList(int hasList) {
		this.hasList = hasList;
	}
}