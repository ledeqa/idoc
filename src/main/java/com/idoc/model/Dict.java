/**
 * 
 */
package com.idoc.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author kanghuaisong
 *
 */
public class Dict {

	private Long dictId;
	private Long productId;
	private String dictName;
	private String dictDesc;
	private Long version;
	private Timestamp createTime;
	private Timestamp updateTime;
	private int status;
	private List<Param> params;
	public Long getDictId() {
		return dictId;
	}
	public void setDictId(Long dictId) {
		this.dictId = dictId;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getDictName() {
		return dictName;
	}
	public void setDictName(String dictName) {
		this.dictName = dictName;
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
	public List<Param> getParams() {
		return params;
	}
	public void setParams(List<Param> params) {
		this.params = params;
	}
	
	public String getDictDesc() {
	
		return dictDesc;
	}
	
	public void setDictDesc(String dictDesc) {
	
		this.dictDesc = dictDesc;
	}
	
	public Long getVersion() {
	
		return version;
	}
	
	public void setVersion(Long version) {
	
		this.version = version;
	}
	@Override
	public String toString() {
	
		return "Dict [dictId=" + dictId + ", productId=" + productId + ", dictName=" + dictName + ", dictDesc=" + dictDesc + ", version=" + version + ", createTime=" + createTime + ", updateTime=" + updateTime + ", status=" + status + ", params=" + params + "]";
	}
	
}
