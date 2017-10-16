package com.idoc.model;

import java.sql.Timestamp;

public class MockRule {

	private String englishName;
	private Long interfaceId;
	private String mockRule;
	private Timestamp createTime;
	private Timestamp updateTime;
	private int status;
	
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	public Long getInterfaceId() {
		return interfaceId;
	}
	public void setInterfaceId(Long interfaceId) {
		this.interfaceId = interfaceId;
	}
	public String getMockRule() {
		return mockRule;
	}
	public void setMockRule(String mockRule) {
		this.mockRule = mockRule;
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
}
