package com.idoc.util;

public class DataTypeCheckUtil {
	public static boolean isNumber(Object... objs){
		if(objs == null)
			return false;
		try {
			for(Object obj : objs){
				if(obj == null)
					return false;
				Long.parseLong(obj.toString().trim());
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
