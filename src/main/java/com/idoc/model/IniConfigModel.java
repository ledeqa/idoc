package com.idoc.model;

/**
 * 对应数据库中TB_IDOC_INI表，用于存放配置信息
 */
public class IniConfigModel {
	private String iniKey;
	private String iniValue;
	private String iniDesc;
	
	public String getIniKey() {
		return iniKey;
	}
	public void setIniKey(String iniKey) {
		this.iniKey = iniKey;
	}
	public String getIniValue() {
		return iniValue;
	}
	public void setIniValue(String iniValue) {
		this.iniValue = iniValue;
	}
	public String getIniDesc() {
		return iniDesc;
	}
	public void setIniDesc(String iniDesc) {
		this.iniDesc = iniDesc;
	}
	@Override
	public String toString() {
		return "IniConfigModel [iniKey=" + iniKey + ", iniValue=" + iniValue
				+ ", iniDesc=" + iniDesc + "]";
	}
}