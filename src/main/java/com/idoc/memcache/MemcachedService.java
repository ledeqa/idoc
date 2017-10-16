package com.idoc.memcache;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.idoc.constant.CommonConstant;
/**
 * 服务器
 */
@Scope("singleton")
@Service("memcachedService")
public class MemcachedService {
	
	@Autowired
	private InitBean initBean;
	
	private SockIOPool pool = null;
	
	private MemCachedClient mc = new MemCachedClient();
	
	private final String poolName = new String("CACHE_POOL");
	
	public Logger memcachedLog = Logger.getLogger("memcached.log");
	
	
	public Object get(String key) {
		return this.mc.get(key);
	}
	
	public boolean set(String key, Object value) {
		return this.mc.set(key, value);
	}
	
	/**
	 * 将Memcached Client的key的值设为value,过期时间为overDate(单位：毫秒)
	 * 
	 * @param key
	 * @param value
	 * @return 是否成功
	 */
	public boolean set(String key, Object value, long overDate) {
//		return this.mc.set(key, value, new Date(System.currentTimeMillis() + overDate));
		return this.mc.set(key, value, new Date(overDate));
	}
	
	public MemCachedClient getMemCachedClient() {
		return this.mc;
	}
	
	@PostConstruct
	public void init() {
		String iniName = initBean.getCacheMemcachedDomain();
		
		memcachedLog.info("MemcachedService.init()");
		// 设置每个参数的默认值
		String serverList = new String();
		serverList = iniName.trim();
		
		String failOver = initBean.getIniStringValue(CommonConstant.MEMCACHED_FAIL_OVER).trim();
		int initConn = Integer.parseInt(initBean.getIniStringValue(CommonConstant.MEMCACHED_INI_CONN).trim());
		int minConn = Integer.parseInt(initBean.getIniStringValue(CommonConstant.MEMCACHED_MIN_CONN).trim());
		int maxConn = Integer.parseInt(initBean.getIniStringValue(CommonConstant.MEMCACHED_MAX_CONN).trim());
		int maxIdle = Integer.parseInt(initBean.getIniStringValue(CommonConstant.MEMCACHED_MAX_IDLE).trim());
		// Tcp的规则就是在发送一个包之前，本地机器会等待远程主机
		// 对上一次发送的包的确认信息到来；这个方法就可以关闭套接字的缓存，
		// 以至这个包准备好了就发；
		String nagle = initBean.getIniStringValue(CommonConstant.MEMCACHED_NAGLE).trim();
		// 连接建立后对超时的控制
		int socketTO = Integer.parseInt(initBean.getIniStringValue(CommonConstant.MEMCACHED_SOCKET_TO).trim());
		// 连接建立时对超时的控制
		int socketConnectTO = Integer.parseInt(initBean.getIniStringValue(CommonConstant.MEMCACHED_SOCKET_CONNECT_TO).trim());
		
		String primitiveAsString = initBean.getIniStringValue(CommonConstant.MEMCACHED_PRIMITIVE_AS_STRING).trim();
		// 将参数值set到pool中
		String[] servers = serverList.split(",");
		boolean ifFailOver = "true".equalsIgnoreCase(failOver) ? true : false;
		boolean ifNagle = "true".equalsIgnoreCase(nagle) ? true : false;
		
		boolean ifPrimitiveAsString = "true".equalsIgnoreCase(primitiveAsString) ? true : false;
		
		// 处理字符串前后的空格
		for (int j = 0; j < servers.length; j++) {
			servers[j] = servers[j].trim();
		}
		this.pool = SockIOPool.getInstance(poolName);
		this.pool.setServers(servers);
		this.pool.setInitConn(initConn);
		this.pool.setMinConn(minConn);
		this.pool.setMaxConn(maxConn);
		this.pool.setMaxIdle(maxIdle);
		this.pool.setNagle(ifNagle);
		this.pool.setSocketTO(socketTO);
		this.pool.setSocketConnectTO(socketConnectTO);
		this.pool.setFailover(ifFailOver);
		this.pool.initialize();
		this.mc = new MemCachedClient(poolName);
		this.mc.setPrimitiveAsString(ifPrimitiveAsString);
		memcachedLog.info("MemcachedService.init() Successfully");
	}
	
	/**
	 * 销毁过期连接池
	 */
	protected void destroy() {
		SockIOPool.getInstance().shutDown();
	}
	
	public static void main(String[] args) {
		
//		String s = (String) instance.get(CommonConstant.MEMCACHED_PRIMITIVE_AS_STRING);
//		System.out.println(s);
		// MemcachedService ms = MemcachedService.getInstance();
		// MemCachedClient mcc =
		// ms.getMemCachedClient(ICommonConstant.USER_MEMCACHED);
		// mcc.set("foo", "This is a test String");
		// String bar = mcc.get("foo").toString();
		// System.out.println(bar);
		// mcc.flushAll();
	}
	
}