package com.idoc.model;

import java.io.Serializable;

public class LoginUserInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String userName;
	private String englishName;
	private String email;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}