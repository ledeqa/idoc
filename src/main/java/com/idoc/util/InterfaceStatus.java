package com.idoc.util;

import com.idoc.constant.CommonConstant;

public class InterfaceStatus {
	public static String getInterfaceStatus(int status){
		switch(status){
		case CommonConstant.INTERFACE_STATUS_EDITING:
			return "编辑中";
		case CommonConstant.INTERFACE_STATUS_AUDITING:
			return "审核中";
		case CommonConstant.INTERFACE_STATUS_FRONT_AUDITED:
			return "前端审核通过";
		case CommonConstant.INTERFACE_STATUS_CLIENT_AUDITED:
			return "客户端审核通过";
		case CommonConstant.INTERFACE_STATUS_AUDITED:
			return "审核通过";
		case CommonConstant.INTERFACE_STATUS_TOTEST:
			return "已提测";
		case CommonConstant.INTERFACE_STATUS_TESTING:
			return "测试中";
		case CommonConstant.INTERFACE_STATUS_TESTED:
			return "测试完成";
		case CommonConstant.INTERFACE_STATUS_PRESSURE:
			return "压测中";
		case CommonConstant.INTERFACE_STATUS_PRESSURED:
			return "压测完成";
		case CommonConstant.INTERFACE_STATUS_ONLINE:
			return "已上线";
		default:
			return "";
		}
	}
}