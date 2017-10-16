package com.idoc.model;

import java.sql.Timestamp;
import java.util.List;


/**
 * 接口类
 * @author wangxiaoming
 *
 */
public class InterfaceOnline {
	private Long interfaceId; // 接口文档id
	private Long productId;
	private Long onlinePageId;
	private String interfaceName;
	private Long creatorId;
	private int interfaceType; // 接口类型 1 ajax 2 ftl
	private int requestType; // 请求类型 1 get 2 post 3 put 4 delete
	private String url;
	private String desc;
	private int isNeedInterfaceTest; //是否需要接口测试,0 不需要，1 需要
	private int isNeedPressureTest; // 是否需要压力测试,0 不需要，1 需要
	private Timestamp expectOnlineTime;
	private Timestamp realOnlineTime;
	private Timestamp expectTestTime;
	private Timestamp realTestTime;
	private String reqPeopleIds; // 需求人员id，多个用户用英文逗号隔开
	private String frontPeopleIds;
	private String behindPeopleIds;
	private String clientPeopleIds;
	private String testPeopleIds;
	private int interfaceStatus; // 接口状态 1 编辑中 2 审核中 3 审核通过 4 已提测 5 测试中 6 测试完成 7 压测中 8 压测完成 9 已上线
	private Long onlineVersion;
	private Timestamp createTime;
	private Timestamp updateTime;
	private int status;
	private List<UserModel> reqPeoples;
	private List<UserModel> frontPeoples;
	private List<UserModel> behindPeoples;
	private List<UserModel> clientPeoples;
	private List<UserModel> testPeoples;
	
	private List<Param> reqParams;
	private List<Param> retParams;
	private UserModel creator;
	private String ftlTemplate;
	private Long iterVersion;
	
	public Long getIterVersion() {
		return iterVersion;
	}

	public void setIterVersion(Long iterVersion) {
		this.iterVersion = iterVersion;
	}

	public Long getInterfaceId() {
	
		return interfaceId;
	}
	
	public void setInterfaceId(Long interfaceId) {
	
		this.interfaceId = interfaceId;
	}
	
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
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
	
	public Long getCreatorId() {
	
		return creatorId;
	}
	
	public void setCreatorId(Long creatorId) {
	
		this.creatorId = creatorId;
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
	
	public Timestamp getRealOnlineTime() {
	
		return realOnlineTime;
	}
	
	public void setRealOnlineTime(Timestamp realOnlineTime) {
	
		this.realOnlineTime = realOnlineTime;
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

	public List<UserModel> getBehindPeoples() {
		return behindPeoples;
	}

	public void setBehindPeoples(List<UserModel> behindPeoples) {
		this.behindPeoples = behindPeoples;
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
	
	public int getInterfaceStatus() {
	
		return interfaceStatus;
	}
	
	public void setInterfaceStatus(int interfaceStatus) {
	
		this.interfaceStatus = interfaceStatus;
	}
	
	public Long getOnlineVersion() {
	
		return onlineVersion;
	}
	
	public void setOnlineVersion(Long onlineVersion) {
	
		this.onlineVersion = onlineVersion;
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

	@Override
	public String toString() {
		return "InterfaceOnline [interfaceId=" + interfaceId + ", productId=" + productId + ", onlinePageId="
				+ onlinePageId + ", interfaceName=" + interfaceName + ", creatorId=" + creatorId + ", interfaceType="
				+ interfaceType + ", requestType=" + requestType + ", url=" + url + ", desc=" + desc
				+ ", isNeedInterfaceTest=" + isNeedInterfaceTest + ", isNeedPressureTest=" + isNeedPressureTest
				+ ", expectOnlineTime=" + expectOnlineTime + ", realOnlineTime=" + realOnlineTime + ", expectTestTime="
				+ expectTestTime + ", realTestTime=" + realTestTime + ", reqPeopleIds=" + reqPeopleIds
				+ ", frontPeopleIds=" + frontPeopleIds + ", clientPeopleIds=" + clientPeopleIds + ", testPeopleIds="
				+ testPeopleIds + ", interfaceStatus=" + interfaceStatus + ", onlineVersion=" + onlineVersion
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + ", status=" + status + ", reqPeoples="
				+ reqPeoples + ", frontPeoples=" + frontPeoples + ", clientPeoples=" + clientPeoples + ", testPeoples="
				+ testPeoples + ", reqParams=" + reqParams + ", retParams=" + retParams + ", creator=" + creator
				+ ", ftlTemplate=" + ftlTemplate + "]";
	}

	public UserModel getCreator() {
		return creator;
	}

	public void setCreator(UserModel creator) {
		this.creator = creator;
	}

	public String getFtlTemplate() {
		return ftlTemplate;
	}

	public void setFtlTemplate(String ftlTemplate) {
		this.ftlTemplate = ftlTemplate;
	}
	
	
}
