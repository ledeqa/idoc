package com.idoc.form;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.idoc.model.Redis;

/**
 * @author li_zhe
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InterfaceForm {
	private Long interfaceId; // 接口文档id
	private Long creatorId;
	private Long onlineInterfaceId; // 在线接口文档id
	private Long pageId;
	private String interfaceName;
	private Integer interfaceType; // 接口类型 1 ajax 2 ftl
	private Integer requestType; // 请求类型 1 get 2 post 3 put 4 delete
	private Integer interfaceStatus;
	private String url;
	private List<Redis> redisList;
	private String desc;
	private String retParamExample;
	private Integer isNeedInterfaceTest; //是否需要接口测试,0 不需要，1 需要
	private Integer isNeedPressureTest; // 是否需要压力测试,0 不需要，1 需要
	private Timestamp expectOnlineTime;
	private Timestamp expectTestTime;
	private String reqPeopleIds; // 需求人员id，多个用户用英文逗号隔开
	private String frontPeopleIds;
	private String behindPeopleIds;
	private int flag;
	private Integer iterVersion;
	public Integer getIterVersion() {
		return iterVersion;
	}

	public void setIterVersion(Integer iterVersion) {
		this.iterVersion = iterVersion;
	}

	public String getBehindPeopleIds() {
		return behindPeopleIds;
	}

	public void setBehindPeopleIds(String behindPeopleIds) {
		this.behindPeopleIds = behindPeopleIds;
	}

	private String clientPeopleIds;
	private String testPeopleIds;
	private String versionDesc; // 版本描述
	
	// 请求参数列表
	private ParamForm[] reqParams;
	// 返回参数列表
	private ParamForm[] retParams;
	private Long projectId;
	private String ftlTemplate;
	
	public Long getInterfaceId() {
	
		return interfaceId;
	}
	
	public void setInterfaceId(Long interfaceId) {
	
		this.interfaceId = interfaceId;
	}
	
	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public Long getOnlineInterfaceId() {
	
		return onlineInterfaceId;
	}
	
	public void setOnlineInterfaceId(Long onlineInterfaceId) {
	
		this.onlineInterfaceId = onlineInterfaceId;
	}
	
	public Long getPageId() {
	
		return pageId;
	}
	
	public Integer getInterfaceStatus() {
		return interfaceStatus;
	}

	public void setInterfaceStatus(Integer interfaceStatus) {
		this.interfaceStatus = interfaceStatus;
	}

	public void setPageId(Long pageId) {
	
		this.pageId = pageId;
	}
	
	public String getInterfaceName() {
	
		return interfaceName;
	}
	
	public void setInterfaceName(String interfaceName) {
	
		this.interfaceName = interfaceName;
	}
	
	public Integer getInterfaceType() {
	
		return interfaceType;
	}
	
	public void setInterfaceType(Integer interfaceType) {
	
		this.interfaceType = interfaceType;
	}
	
	public Integer getRequestType() {
	
		return requestType;
	}
	
	public void setRequestType(Integer requestType) {
	
		this.requestType = requestType;
	}
	
	public String getUrl() {
	
		return url;
	}
	
	public void setUrl(String url) {
	
		this.url = url;
	}
	
	public List<Redis> getRedisList() {
		return redisList;
	}

	public void setRedisList(List<Redis> redisList) {
		this.redisList = redisList;
	}

	public String getDesc() {
	
		return desc;
	}
	
	public void setDesc(String desc) {
	
		this.desc = desc;
	}
	
	public String getRetParamExample() {
		return retParamExample;
	}

	public void setRetParamExample(String retParamExample) {
		this.retParamExample = retParamExample;
	}

	public Integer getIsNeedInterfaceTest() {
	
		return isNeedInterfaceTest;
	}
	
	public void setIsNeedInterfaceTest(Integer isNeedInterfaceTest) {
	
		this.isNeedInterfaceTest = isNeedInterfaceTest;
	}
	
	public Integer getIsNeedPressureTest() {
	
		return isNeedPressureTest;
	}
	
	public void setIsNeedPressureTest(Integer isNeedPressureTest) {
	
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
	
	@Override
	public String toString() {
	
		return "InterfaceForm [interfaceId=" + interfaceId + ", onlineInterfaceId=" + onlineInterfaceId + ", pageId=" + pageId + ", interfaceName=" + interfaceName + ", interfaceType=" + interfaceType + ", requestType=" + requestType + ", url=" + url + ", desc=" + desc + ", isNeedInterfaceTest=" + isNeedInterfaceTest + ", isNeedPressureTest=" + isNeedPressureTest + ", expectOnlineTime=" + expectOnlineTime + ", expectTestTime=" + expectTestTime + ", reqPeopleIds=" + reqPeopleIds + ", frontPeopleIds=" + frontPeopleIds + ", clientPeopleIds=" + clientPeopleIds + ", testPeopleIds=" + testPeopleIds + ", versionDesc=" + versionDesc + ", reqParams=" + Arrays.toString(reqParams) + ", retParams=" + Arrays.toString(retParams) + "]";
	}

	public void setFrontPeopleIds(String frontPeopleIds) {
	
		this.frontPeopleIds = frontPeopleIds;
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

	
	public String getVersionDesc() {
	
		return versionDesc;
	}

	
	public void setVersionDesc(String versionDesc) {
	
		this.versionDesc = versionDesc;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getFtlTemplate() {
		return ftlTemplate;
	}

	public void setFtlTemplate(String ftlTemplate) {
		this.ftlTemplate = ftlTemplate;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
}
