package com.idoc.model;

import java.sql.Timestamp;
import java.util.List;


/**
 * 接口类
 * @author wangxiaoming
 *
 */
/**
 * @author li_zhe
 *
 */
public class Interface {
	private Long interfaceId; // 接口文档id
	private Long onlineInterfaceId; // 在线接口文档id
	private Long pageId;
	private String interfaceName;   //接口名称
	private Long creatorId;
	private Integer interfaceType; // 接口类型 1 ajax 2 ftl
	private String ftlTemplate;
	private Integer requestType; // 请求类型 1 get 2 post 3 put 4 delete
	private String url;
	private String desc;
	private String retParamExample;
	private Integer isNeedInterfaceTest; //是否需要接口测试,0 不需要，1 需要
	private Integer isNeedPressureTest; // 是否需要压力测试,0 不需要，1 需要
	private Timestamp expectOnlineTime;
	private Timestamp realOnlineTime;
	private Timestamp expectTestTime;
	private Timestamp realTestTime;
	private String reqPeopleIds; // 需求人员id，多个用户用英文逗号隔开
	private String frontPeopleIds;
	private String behindPeopleIds;
	private String clientPeopleIds;
	private String testPeopleIds;
	private Integer interfaceStatus; // 接口状态 1 编辑中 2 审核中 3 审核通过 4 已提测 5 测试中 6 测试完成 7 压测中 8 压测完成 9 已上线
	private Long onlineVersion;
	private Long editVersion;
	private Timestamp createTime;
	private Timestamp updateTime;
	private int status;
	private Long iterVersion;
	
	private List<UserModel> reqPeoples;
	private List<UserModel> frontPeoples;
	private List<UserModel> behindPeoples;
	private List<UserModel> clientPeoples;
	private List<UserModel> testPeoples;
	
	private List<Param> reqParams;
	private List<Param> retParams;
	private UserModel creator;
	
	public Long getInterfaceId() {
	    
		return interfaceId==null?null:interfaceId;
	}
	
	public Timestamp getExpectTestTime() {
		return expectTestTime;
	}

	public void setExpectTestTime(Timestamp expectTestTime) {
		this.expectTestTime = expectTestTime;
	}

	public Timestamp getRealTestTime() {
		return realTestTime;
	}

	public void setRealTestTime(Timestamp realTestTime) {
		this.realTestTime = realTestTime;
	}

	public void setInterfaceId(Long interfaceId) {
	
		this.interfaceId = interfaceId;
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
	
	public void setPageId(Long pageId) {
	
		this.pageId = pageId;
	}
	
	public String getInterfaceName() {
	
		return interfaceName;
	}
	
	public void setInterfaceName(String interfaceName) {
	
		this.interfaceName = interfaceName;
	}
	
	public Long getCreatorId() {
	
		return creatorId;
	}
	
	public void setCreatorId(Long creatorId) {
	
		this.creatorId = creatorId;
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
	
	
	public Timestamp getRealOnlineTime() {
	
		return realOnlineTime;
	}

	public Long getIterVersion() {
		return iterVersion;
	}

	public void setIterVersion(Long iterVersion) {
		this.iterVersion = iterVersion;
	}
	
	public void setRealOnlineTime(Timestamp realOnlineTime) {
	
		this.realOnlineTime = realOnlineTime;
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
	
	public String getReqPeopleIds() {
	
		return reqPeopleIds;
	}
	
	
	public List<Param> getReqParams() {
	
		return reqParams;
	}

	
	public void setReqParams(List<Param> reqParams) {
	
		this.reqParams = reqParams;
	}

	
	public List<Param> getRetParams() {
	
		return retParams;
	}

	
	public void setRetParams(List<Param> retParams) {
	
		this.retParams = retParams;
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

	public Integer getInterfaceStatus() {
	
		return interfaceStatus;
	}
	
	public void setInterfaceStatus(Integer interfaceStatus) {
	
		this.interfaceStatus = interfaceStatus;
	}
	
	public Long getOnlineVersion() {
	
		return onlineVersion;
	}
	
	public void setOnlineVersion(Long onlineVersion) {
	
		this.onlineVersion = onlineVersion;
	}
	
	public Long getEditVersion() {
	
		return editVersion;
	}
	
	public void setEditVersion(Long editVersion) {
	
		this.editVersion = editVersion;
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

	
	public List<UserModel> getReqPeoples() {
	
		return reqPeoples;
	}

	
	public void setReqPeoples(List<UserModel> reqPeoples) {
	
		this.reqPeoples = reqPeoples;
	}

	
	public List<UserModel> getFrontPeoples() {
	
		return frontPeoples;
	}

	
	public void setFrontPeoples(List<UserModel> frontPeoples) {
	
		this.frontPeoples = frontPeoples;
	}

	
	public List<UserModel> getBehindPeoples() {
		return behindPeoples;
	}

	public void setBehindPeoples(List<UserModel> behindPeoples) {
		this.behindPeoples = behindPeoples;
	}

	public List<UserModel> getClientPeoples() {
	
		return clientPeoples;
	}

	
	public void setClientPeoples(List<UserModel> clientPeoples) {
	
		this.clientPeoples = clientPeoples;
	}

	
	public List<UserModel> getTestPeoples() {
	
		return testPeoples;
	}

	
	public void setTestPeoples(List<UserModel> testPeoples) {
	
		this.testPeoples = testPeoples;
	}

	
	public UserModel getCreator() {
	
		return creator;
	}

	
	public void setCreator(UserModel creator) {
	
		this.creator = creator;
	}

	@Override
	public String toString() {
		return "Interface [interfaceId=" + interfaceId + ", onlineInterfaceId=" + onlineInterfaceId + ", pageId="
				+ pageId + ", interfaceName=" + interfaceName + ", creatorId=" + creatorId + ", interfaceType="
				+ interfaceType + ", ftlTemplate=" + ftlTemplate + ", requestType=" + requestType + ", url=" + url
				+ ", desc=" + desc + ", retParamExample=" + retParamExample + ", isNeedInterfaceTest="
				+ isNeedInterfaceTest + ", isNeedPressureTest=" + isNeedPressureTest + ", expectOnlineTime="
				+ expectOnlineTime + ", realOnlineTime=" + realOnlineTime + ", expectTestTime=" + expectTestTime
				+ ", realTestTime=" + realTestTime + ", reqPeopleIds=" + reqPeopleIds + ", frontPeopleIds="
				+ frontPeopleIds + ", behindPeopleIds=" + behindPeopleIds + ", clientPeopleIds=" + clientPeopleIds + ", testPeopleIds=" + testPeopleIds
				+ ", interfaceStatus=" + interfaceStatus + ", onlineVersion=" + onlineVersion + ", editVersion="
				+ editVersion + ", createTime=" + createTime + ", updateTime=" + updateTime + ", status=" + status
				+ ", reqPeoples=" + reqPeoples + ", frontPeoples=" + frontPeoples + ", clientPeoples=" + clientPeoples
				+ ", testPeoples=" + testPeoples + ", reqParams=" + reqParams + ", retParams=" + retParams
				+ ", creator=" + creator + "]";
	}

	public String getFtlTemplate() {
		return ftlTemplate;
	}

	public void setFtlTemplate(String ftlTemplate) {
		this.ftlTemplate = ftlTemplate;
	}
}
