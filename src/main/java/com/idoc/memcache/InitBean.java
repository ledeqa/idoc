package com.idoc.memcache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.dao.ini.IniConfigManagementDao;
import com.idoc.model.IniConfigModel;
import com.idoc.util.PropertyUtils;

@Service("initBean")
public class InitBean {
	
	@Autowired
	private IniConfigManagementDao iniConfigManagementDaoImpl;
	
	private Map<String,String> iniValueMap = new HashMap<String,String>();
	private String cacheMemcachedDomain;// cache
	
	public Logger debugLog = Logger.getLogger("debug.log");
	
	private InitBean() {
	}
	
	/**
	 * 初始化函数
	 */
	@PostConstruct
	public void init() {
		// 1.刷新文件配置
		this.refreshConfiguration();
		// 2.刷新数据库配置
		this.refresh();
		debugLog.info("init InitBean success...");
	}
	
	/**
	 * 刷新数据库配置
	 */
	public void refresh() {
		// 获取常规Ini配置项内容
		Map<String,String> tempIniValueMap = new HashMap<String,String>();
		List<IniConfigModel> iniList = iniConfigManagementDaoImpl
				.selectIniConfigModel();
		if (iniList != null) {
			for (IniConfigModel ini : iniList) {
				tempIniValueMap.put(ini.getIniKey(), ini.getIniValue());
			}
		}
		this.iniValueMap = tempIniValueMap;
	}
	
	/**
	 * 刷新文件配置
	 */
	public void refreshConfiguration() {
		PropertyUtils propertyUtils = new PropertyUtils("configuration", null);
		cacheMemcachedDomain = propertyUtils.getProperty("cache_memcached_domain");
	}
	
	public Map<String,String> getIniValueMap(){
		return this.iniValueMap;
	}
	
	/**
	 * 根据key获得整个ini的值
	 * 
	 * @param key
	 * @return
	 */
	public String getIniStringValue(String key) {
		if (!iniValueMap.containsKey(key)) {
			throw new RuntimeException("no ini found for :" + key);
		}
		return this.iniValueMap.get(key);
	}
	
	/**
	 * 根据key获得ini值，可以指定默认值
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getIniStringValue(String key, String defaultValue) {
		if (iniValueMap.containsKey(key)) {
			return iniValueMap.get(key);
		} else if (StringUtils.isNotBlank(defaultValue)) {
			return defaultValue;
		} else {
			throw new RuntimeException("no ini found for :" + key);
		}
	}
	
	/**
	 * 直接返回int类型的ini值，可以指定默认值
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getIniIntValue(String key) {
		String iniValue = getIniStringValue(key);
		int result;
		try {
			result = Integer.parseInt(iniValue);
		} catch (NumberFormatException e) {
			throw new RuntimeException("not found an int for ini :" + key);
		}
		return result;
	}
	
	/**
	 * 直接返回int类型的ini值，可以指定默认值
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getIniIntValue(String key, int defaultValue) {
		int result = defaultValue;
		if (iniValueMap.containsKey(key)) {
			String iniValue = getIniStringValue(key);
			try {
				result = Integer.parseInt(iniValue);
			} catch (NumberFormatException e) {
				throw new RuntimeException("no valid ini :" + key);
			}
		}
		return result;
	}
	
	/**
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public long getIniLongValue(String key, long defaultValue) {
		long result = defaultValue;
		if (iniValueMap.containsKey(key)) {
			String iniValue = getIniStringValue(key);
			try {
				result = Long.parseLong(iniValue);
			} catch (NumberFormatException e) {
				throw new RuntimeException("no valid ini :" + key);
			}
		}
		return result;
	}
	
	public String getCacheMemcachedDomain() {
		return cacheMemcachedDomain;
	}
	
	public void setCacheMemcachedDomain(String cacheMemcachedDomain) {
		this.cacheMemcachedDomain = cacheMemcachedDomain;
	}
	
}