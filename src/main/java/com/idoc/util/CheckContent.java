package com.idoc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckContent {
	
	public static void isNull(String str) throws Exception  {
		if(str == null) {
			throw new Exception("string can't be null");
		}
	}
	
	// 判断是否是有效的URL(仅限http的链接)
	public static boolean isValidUrl(String str) throws Exception {
		isNull(str);
		String regex = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;${}]*[-a-zA-Z0-9+&@#/%=~_|${}]";
		Pattern patt = Pattern. compile(regex);
		Matcher matcher = patt.matcher(str);
		boolean isMatch = matcher.matches();
		if (isMatch) {
		    return true;
		} else {
			return false;
		}
	}
	
	// 判断是否是有效的ftl模板路径
	public static boolean isValidFtlTemplateStr(String str) throws Exception {
		isNull(str);
		String regex = "^(/?[-a-zA-Z0-9_]+)(/[-a-zA-Z0-9_]+)*\\.ftl$";
		Pattern patt = Pattern. compile(regex);
		Matcher matcher = patt.matcher(str);
		boolean isMatch = matcher.matches();
		if (isMatch) {
		    return true;
		} else {
			return false;
		}
	}
	
	// 根据Unicode编码判断中文汉字和符号
	private static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}
	 
	// 完整的判断中文汉字和符号
	public static boolean isChinese(String str) throws Exception {
		isNull(str);
		char[] ch = str.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}
	
	// 只能判断是否含有汉字
	public static boolean isContainChineseCharacter(String str) throws Exception {
		isNull(str);
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
	
}
