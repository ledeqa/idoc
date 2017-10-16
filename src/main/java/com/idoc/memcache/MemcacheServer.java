package com.idoc.memcache;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.constant.CommonConstant;
import com.idoc.constant.LogConstant;

@Service("memcacheServer")
public class MemcacheServer {
	
	@Autowired
	private InitBean initBean;
	
	@Autowired
	private MemcachedService memcachedService;
	/** ----------------------Cache Memcached------------------------ * */
	
	/**
	 * 初始化ini表，将信息存入memcache
	 * @return
	 */
	@PostConstruct
	public boolean initMemcache(){
		Map<String,String> iniValueMap = initBean.getIniValueMap();
		if(iniValueMap == null || iniValueMap.size() <= 0){
			LogConstant.debugLog.info("初始化iniValueMap时为空！");
			return false;
		}
		for (Map.Entry<String, String> entry : iniValueMap.entrySet()) {
			setCacheData(entry.getKey(), entry.getValue());
		    LogConstant.debugLog.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());		  
		}
		return true;
	}
	
	public boolean setCacheData(String key, Object value) {

		if (initBean.getIniIntValue(CommonConstant.MEMCACHED_SWITCH_KEY, CommonConstant.MEMCACHED_SWITCH_OPEN) == CommonConstant.MEMCACHED_SWITCH_CLOSE) {
			return false;
		}
		if (value != null) {
			return memcachedService.set(key, value);
		} else {
			LogConstant.debugLog.warn("[setCacheData] set null value. [key = " + key + "]");
			return false;
		}
	}
	
	public Object getCacheData(String key) {

		if (initBean.getIniIntValue(CommonConstant.MEMCACHED_SWITCH_KEY, CommonConstant.MEMCACHED_SWITCH_OPEN) == CommonConstant.MEMCACHED_SWITCH_CLOSE) {
			return null;
		}
		return memcachedService.get(key);
	}
	
	public boolean setCacheData(String key, Object value, long overDate) {

		if (initBean.getIniIntValue(CommonConstant.MEMCACHED_SWITCH_KEY, CommonConstant.MEMCACHED_SWITCH_OPEN) == CommonConstant.MEMCACHED_SWITCH_CLOSE) {
			return false;
		}
		
		if (value != null) {
			return memcachedService.set(key, value, overDate);
		} else {
			LogConstant.debugLog.warn("[setCacheData-overDate] set null value. [key = " + key + "]");
			return false;
		}
	}
	
	/**
	 * 多key查询，Cache Memcached
	 * 
	 * @param keys
	 * @return
	 */
	public Object[] getMultiCacheData(String[] keys) {

		if (initBean.getIniIntValue(CommonConstant.MEMCACHED_SWITCH_KEY, CommonConstant.MEMCACHED_SWITCH_OPEN) == CommonConstant.MEMCACHED_SWITCH_CLOSE) {
			return null;
		}
		return memcachedService.getMemCachedClient().getMultiArray(keys);
	}
	
	/**
	 * 删除cache中的缓存根据key
	 * 
	 * @param key
	 * @return
	 */
	public boolean deleteCacheData(String key) {

		if (initBean.getIniIntValue(CommonConstant.MEMCACHED_SWITCH_KEY, CommonConstant.MEMCACHED_SWITCH_OPEN) == CommonConstant.MEMCACHED_SWITCH_CLOSE) {
			return false;
		}
		return memcachedService.getMemCachedClient().delete(key);
	}
}