package com.idoc.model.login;

public class ProductListModel {
	
	private String id;
	private long productId;
	private String productName;
	private String projectNum;	
	private String dataDictUrl;
	private String interfaceOnlineUrl;
	private long isAdmin;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProjectNum() {
		return projectNum;
	}
	public void setProjectNum(String projectNum) {
		this.projectNum = projectNum;
	}
	public String getDataDictUrl() {
		return dataDictUrl;
	}
	public void setDataDictUrl(String dataDictUrl) {
		this.dataDictUrl = dataDictUrl;
	}
	public String getInterfaceOnlineUrl() {
		return interfaceOnlineUrl;
	}
	public void setInterfaceOnlineUrl(String interfaceOnlineUrl) {
		this.interfaceOnlineUrl = interfaceOnlineUrl;
	}
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
	}
	
	public long getIsAdmin() {
	
		return isAdmin;
	}
	
	public void setIsAdmin(long isAdmin) {
	
		this.isAdmin = isAdmin;
	}
	
	

	
}

