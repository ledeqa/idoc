package com.idoc.model;

import java.sql.Timestamp;


public class ProductModel {
	
	private Long productId;
	private String productName;
	private String productDesc;
	private String productDomainUrl;
	private Integer productFlow;
	public Integer getProductFlow() {
		return productFlow;
	}

	public void setProductFlow(Integer productFlow) {
		this.productFlow = productFlow;
	}

	private Timestamp createTime;
	private Timestamp updateTime;
	private int status;
	
	public Long getProductId() {
	
		return productId;
	}
	
	public void setProductId(Long productId) {
	
		this.productId = productId;
	}
	
	public String getProductName() {
	
		return productName;
	}
	
	public void setProductName(String productName) {
	
		this.productName = productName;
	}
	
	public String getProductDesc() {
	
		return productDesc;
	}
	
	public void setProductDesc(String productDesc) {
	
		this.productDesc = productDesc;
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

	public String getProductDomainUrl() {
		return productDomainUrl;
	}

	public void setProductDomainUrl(String productDomainUrl) {
		this.productDomainUrl = productDomainUrl;
	}
}

