package com.idoc.model;

import java.sql.Timestamp;

public class Redis {
	private Long redisId;
	private Long interfaceId;
	private String redisKey;
	private String redisInfo;
	private String redisTactics;
	private Timestamp createTime;
	private Timestamp updateTime;
	private Integer status;
	public Long getRedisId() {
		return redisId;
	}
	public void setRedisId(Long redisId) {
		this.redisId = redisId;
	}
	public Long getInterfaceId() {
		return interfaceId;
	}
	public void setInterfaceId(Long interfaceId) {
		this.interfaceId = interfaceId;
	}
	public String getRedisKey() {
		return redisKey;
	}
	public void setRedisKey(String redisKey) {
		this.redisKey = redisKey;
	}
	public String getRedisInfo() {
		return redisInfo;
	}
	public void setRedisInfo(String redisInfo) {
		this.redisInfo = redisInfo;
	}
	public String getRedisTactics() {
		return redisTactics;
	}
	public void setRedisTactics(String redisTactics) {
		this.redisTactics = redisTactics;
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
}
