package com.idoc.util;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * properties帮助类
 * 默认加载ftpConfig.properties
 */
public class PropertyUtils {

	private static Logger logger = Logger.getLogger(PropertyUtils.class);

	public static String config = "ftpConfig";

	private ResourceBundle bundle = null;

	public PropertyUtils(String baseName, Locale local) {
		if (StringUtils.isBlank(baseName))
			baseName = config;
		if (local == null)
			local = new Locale("zh", "CN");
		try {
			bundle = ResourceBundle.getBundle(baseName, local);
		} catch (Exception e) {
			logger.fatal(e.getMessage());
		}
	}

	public String getProperty(String key) {
		String value = "";
		try {
			value = bundle.getString(key);
		} catch (Exception e) {
			logger.fatal(e.getMessage());
		}
		return value;
	}
}