package com.idoc.util;

public class ForceBackReason {
	public static String getReason(int reason){
		switch(reason){
		case 1:
			return "系统操作正常状态变更";
		case 2:
			return "产品原因";
		case 3:
			return "后台开发原因";
		case 4:
			return "客户端开发原因";
		case 5:
			return "前端开发原因";
		case 6:
			return "测试原因 ";
		case 7:
			return "其他 ";
		default:
			return "";
		}
	}
}