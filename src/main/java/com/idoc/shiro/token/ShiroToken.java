package com.idoc.shiro.token;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * Shiro token
 * 
 * @author bjhuwei1
 * @version 创建时间：2017年7月19日 下午4:28:15
 */
public class ShiroToken extends UsernamePasswordToken implements java.io.Serializable {
	private static final long serialVersionUID = -6451794657814516274L;

	public ShiroToken(String username, String pswd) {
		super(username, pswd);
		this.pswd = pswd;
	}

	/** 登录密码[字符串类型] 因为父类是char[] ] **/
	private String pswd;

	public String getPswd() {
		return pswd;
	}

	public void setPswd(String pswd) {
		this.pswd = pswd;
	}
}
