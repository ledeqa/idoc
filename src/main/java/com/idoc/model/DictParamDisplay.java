/**
 * 
 */
package com.idoc.model;

/**
 * @author kanghuaisong
 *
 */
public class DictParamDisplay {

	private Long interfaceId;
	private Long dictId;
	private Long paramId;
	private int status;
	
	public Long getInterfaceId() {
		return interfaceId;
	}
	public void setInterfaceId(Long interfaceId) {
		this.interfaceId = interfaceId;
	}
	public Long getDictId() {
		return dictId;
	}
	public void setDictId(Long dictId) {
		this.dictId = dictId;
	}
	public Long getParamId() {
		return paramId;
	}
	public void setParamId(Long paramId) {
		this.paramId = paramId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "DictParamDisplay [interfaceId=" + interfaceId + ", dictId=" + dictId + ", paramId=" + paramId
				+ ", status=" + status + "]";
	}
}
