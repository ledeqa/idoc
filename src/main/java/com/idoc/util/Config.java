package com.idoc.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.idoc.constant.LogConstant;

/**
 * 配置文件中的数据
 * 
 * @author Administrator
 * 
 */

public class Config {

	static Properties config = null;
	static Properties promoter_config = null;

	static Map<String, Properties> mapProperties = new HashMap<String, Properties>();

	@PostConstruct
	public void autoLoadConfig() {
		if (null == mapProperties) {
			LogConstant.runLog.info("没有任何*.propeties文件");
			return;
		}
		Set<String> keySet = mapProperties.keySet();
		Iterator<String> itr = keySet.iterator();
		while (itr.hasNext()) {
			String fileName = itr.next();
			try {
				getConfig(fileName, "test");
			} catch (IOException e) {
				System.out.println("error loading config file");
				e.printStackTrace();
				LogConstant.runLog.info("error loading config file", e);
			}
		}
	}

	public static String getConfig(String propertyName) throws IOException {
		return getConfig("data.properties", propertyName);
	}
	
	public static String getConfig(String fileName, String propertyName)
			throws IOException {
		Properties config = mapProperties.get(fileName);
		if (null == config || config.isEmpty()) {
			InputStream in = null;

			in = Config.class.getResourceAsStream("/"+fileName);
			config = new Properties();

			config.load(in);
			if (null != in) {
				in.close();
			}
			mapProperties.put(fileName, config);
			LogConstant.runLog.info("config path:" + Config.class.getResource("/"+fileName) + " load success!");
		}

		String property = config.getProperty(propertyName);
		if (null == property && !"test".equals(propertyName)) {
			LogConstant.runLog.info("config path:" + Config.class.getResource("/"+fileName)+ "|"+propertyName + " load failed!");
			return null;
		}

		return property;
	}

	public Map<String, Properties> getMapProperties() {
		return mapProperties;
	}

	@SuppressWarnings("static-access")
	public void setMapProperties(Map<String, Properties> mapProperties) {
		this.mapProperties = mapProperties;
	}
}