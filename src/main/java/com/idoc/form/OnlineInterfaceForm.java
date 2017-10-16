package com.idoc.form;

import java.sql.Timestamp;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 
* @ClassName: OnlineInterfaceForm 
* @Description: 在线接口Form
* @author xlgeng 
* @date 2016年1月20日 下午1:47:07 
*
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnlineInterfaceForm {
	
	private Long interfaceId; // 接口文档id
	private Long onlinePageId;
	private String interfaceName;
	private int interfaceType; // 接口类型 1 ajax 2 ftl
	private int requestType; // 请求类型 1 get 2 post 3 put 4 delete
	private String url;
	private String desc;
	private int isNeedInterfaceTest; //是否需要接口测试,0 不需要，1 需要
	private int isNeedPressureTest; // 是否需要压力测试,0 不需要，1 需要
	private Timestamp expectOnlineTime;
	private Timestamp expectTestTime;
	private String reqPeopleIds; // 需求人员id，多个用户用英文逗号隔开
	private String frontPeopleIds;
	private String behindPeopleIds;
	private String clientPeopleIds;
	private String testPeopleIds;
	private String versionDesc; // 版本描述
	private Long iterVersion;
	
	// 请求参数列表
	private ParamForm[] reqParams;
	// 返回参数列表
	private ParamForm[] retParams;
	private String ftlTemplate;
	
	public Long getInterfaceId() {
	
		return interfaceId;
	}
	
	public void setInterfaceId(Long interfaceId) {
	
		this.interfaceId = interfaceId;
	}
	
	public Long getIterVersion() {
		return iterVersion;
	}

	public void setIterVersion(Long iterVersion) {
		this.iterVersion = iterVersion;
	}

	public Long getOnlinePageId() {
	
		return onlinePageId;
	}
	
	public void setOnlinePageId(Long onlinePageId) {
	
		this.onlinePageId = onlinePageId;
	}
	
	public String getInterfaceName() {
	
		return interfaceName;
	}
	
	public void setInterfaceName(String interfaceName) {
	
		this.interfaceName = interfaceName;
	}
	
	public int getInterfaceType() {
	
		return interfaceType;
	}
	
	public void setInterfaceType(int interfaceType) {
	
		this.interfaceType = interfaceType;
	}
	
	public int getRequestType() {
	
		return requestType;
	}
	
	public void setRequestType(int requestType) {
	
		this.requestType = requestType;
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
	
	public int getIsNeedInterfaceTest() {
	
		return isNeedInterfaceTest;
	}
	
	public void setIsNeedInterfaceTest(int isNeedInterfaceTest) {
	
		this.isNeedInterfaceTest = isNeedInterfaceTest;
	}
	
	public int getIsNeedPressureTest() {
	
		return isNeedPressureTest;
	}
	
	public void setIsNeedPressureTest(int isNeedPressureTest) {
	
		this.isNeedPressureTest = isNeedPressureTest;
	}
	
	public Timestamp getExpectOnlineTime() {
	
		return expectOnlineTime;
	}
	
	public void setExpectOnlineTime(Timestamp expectOnlineTime) {
	
		this.expectOnlineTime = expectOnlineTime;
	}
	
	public Timestamp getExpectTestTime() {
	
		return expectTestTime;
	}
	
	public void setExpectTestTime(Timestamp expectTestTime) {
	
		this.expectTestTime = expectTestTime;
	}
	
	public String getReqPeopleIds() {
	
		return reqPeopleIds;
	}
	
	public void setReqPeopleIds(String reqPeopleIds) {
	
		this.reqPeopleIds = reqPeopleIds;
	}
	
	public String getFrontPeopleIds() {
	
		return frontPeopleIds;
	}
	
	public void setFrontPeopleIds(String frontPeopleIds) {
	
		this.frontPeopleIds = frontPeopleIds;
	}
	
	public String getBehindPeopleIds() {
		return behindPeopleIds;
	}

	public void setBehindPeopleIds(String behindPeopleIds) {
		this.behindPeopleIds = behindPeopleIds;
	}

	public String getClientPeopleIds() {
	
		return clientPeopleIds;
	}
	
	public void setClientPeopleIds(String clientPeopleIds) {
	
		this.clientPeopleIds = clientPeopleIds;
	}
	
	public String getTestPeopleIds() {
	
		return testPeopleIds;
	}
	
	public void setTestPeopleIds(String testPeopleIds) {
	
		this.testPeopleIds = testPeopleIds;
	}
	
	public String getVersionDesc() {
	
		return versionDesc;
	}
	
	public void setVersionDesc(String versionDesc) {
	
		this.versionDesc = versionDesc;
	}
	
	public ParamForm[] getReqParams() {
	
		return reqParams;
	}
	
	public void setReqParams(ParamForm[] reqParams) {
	
		this.reqParams = reqParams;
	}
	
	public ParamForm[] getRetParams() {
	
		return retParams;
	}
	
	public void setRetParams(ParamForm[] retParams) {
	
		this.retParams = retParams;
	}

	public String getFtlTemplate() {
		return ftlTemplate;
	}

	public void setFtlTemplate(String ftlTemplate) {
		this.ftlTemplate = ftlTemplate;
	}
}
