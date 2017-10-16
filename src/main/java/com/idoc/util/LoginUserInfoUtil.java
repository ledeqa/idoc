package com.idoc.util;

import com.idoc.constant.ThreadLocalConstant;


public class LoginUserInfoUtil {
	
	public static String getUserName(){
		return (String)ThreadLocalConstant.sessionTL.get().getUserName();
	}
	
	public static String getUserEnglishName(){
		return (String)ThreadLocalConstant.sessionTL.get().getEnglishName();
	}
	
	public static String getUserEmail(){
		return (String)ThreadLocalConstant.sessionTL.get().getEmail();
	}
}