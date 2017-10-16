package com.idoc.dao.docManage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.model.DictParamDisplay;
import com.idoc.model.Interface;
import com.idoc.model.Redis;
import com.idoc.model.UserModel;

@Repository("interfaceDaoImpl")
public class InterfaceDaoImpl extends BaseMysqlDBDaoImpl{
	
	@SuppressWarnings("unchecked")
	public List<Interface> selectInterfaceListByCond(Map<String,Object> map){
		
		return this.getSqlSession().selectList("docManage.selectInterfaceListByCond", map);
	}
	
	/**
	 * 根据接口id查该接口的产品id
	 * @param interfaceId
	 * @return
	 */
	public long selectProductIdByInterfaceId(Long interfaceId){
		return (long) this.getSqlSession().selectOne("docManage.selectProductIdByInterfaceId", interfaceId);
	}
	
	/**
	 * 根据接口id查该接口的项目id
	 * @param interfaceId
	 * @return
	 */
	public long selectProjectIdByInterfaceId(Long interfaceId){
		return (long) this.getSqlSession().selectOne("docManage.selectProjectIdByInterfaceId", interfaceId);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> selectInterfaceIdByRequestUrl(String requestUrl){
		return this.getSqlSession().selectList("docManage.selectInterfaceIdByRequestUrl", requestUrl);
	}

	@SuppressWarnings("unchecked")
	public List<Interface> selectInterfaceListByPages(List<Long> pageIds){
		return this.getSqlSession().selectList("docManage.selectInterfaceListByPages", pageIds);
	}
	
	@SuppressWarnings("unchecked")
	public List<Interface> getAllInterfaceListByProjectId(Long projectId){
		return this.getSqlSession().selectList("docManage.getAllInterfaceListByProjectId", projectId);
	}
	
	@SuppressWarnings("unchecked")
	public List<UserModel> selectUserListByInterface(List<Long> userIds){
		return this.getSqlSession().selectList("docManage.selectUserListByInterface", userIds);
	}
	
	public int insertInterface(Interface inter){
		return this.getSqlSession().insert("docManage.insertInterface", inter);
	}
	
	public Interface selectInterfaceById(Map<String, Object> map){
		return (Interface) this.getSqlSession().selectOne("docManage.selectInterfaceById", map);
	}
	
	public int deleteInterface(Long interId){
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("interfaceId", interId);
		map.put("delete", "1");
		return this.getSqlSession().update("docManage.updateInterface", map);
	}
	
	public int updateInterface(Map<String, Object> map){
		return this.getSqlSession().update("docManage.updateInterface", map);
	}
	
	public int updateInterface(Interface inter){
		return this.getSqlSession().update("docManage.updateInterfaceModel", inter);
	}

	/**
	 * 彻底更新接口信息，如果inter中的值为null，也会更新到数据库
	 * @param inter
	 * @return
	 */
	public int updateInterfaceTotally(Interface inter){
		return this.getSqlSession().update("docManage.updateInterfaceTotally", inter);
	}
	
	public int operateDictParamDisplay(DictParamDisplay dictParamDisplay) {
		return this.getSqlSession().insert("docManage.operateDictParamDisplay", dictParamDisplay);
	}

	@SuppressWarnings("unchecked")
	public List<DictParamDisplay> queryDictParamList(DictParamDisplay dictParamDisplay) {
		return this.getSqlSession().selectList("docManage.queryDictParamList", dictParamDisplay);
	}

	public DictParamDisplay queryDictParam(DictParamDisplay display) {
		return (DictParamDisplay) this.getSqlSession().selectOne("docManage.queryDictParam", display);
	}
	
	@SuppressWarnings("unchecked")
	public List<Interface> selectInterfaceByTime(Map<String, Object> map){
		return this.getSqlSession().selectList("docManage.selectInterfaceListByTime", map);
	}
	
	public Long getInterStatus(Long interId){
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("interfaceId", interId);
		return (Long) this.getSqlSession().selectOne("docManage.selectInterfaceStatusById",map);
	}

	@SuppressWarnings("unchecked")
	public List<Redis> selectInterfaceRedis(Long interId) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("interfaceId", interId);
		return this.getSqlSession().selectList("docManage.selectInterfaceRedis", map);
	}

	public int updateInterfaceRedis(Long interfaceId, Redis redis) {
		if(interfaceId!=null){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("interfaceId", interfaceId);
			return this.getSqlSession().update("docManage.updateInterfaceRedisByInterId", map);
		}
		if(redis!=null){
			return this.getSqlSession().update("docManage.updateInterfaceRedisByRedisId", redis);
		}
		return 0;
	}

	public int insertInterfaceRedis(Redis redis) {
		return this.getSqlSession().insert("docManage.insertInterfaceRedis", redis);
	}

	public int deleteInterfaceRedis(Long redisId) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("redisId", redisId);
		return this.getSqlSession().delete("docManage.updateInterfaceRedisByInterId", map);
	}

	@SuppressWarnings("unchecked")
	public List<Interface> getAllInterfaceListByProjectIdAndModules(
			Map<String, Object> map) {
		return this.getSqlSession().selectList("docManage.selectAllInterfaceListByProjectIdAndModuleIds", map);
	}

	@SuppressWarnings("unchecked")
	public List<Interface> getInterSimpleInfo(List<String> interfaceIdList) {
		return this.getSqlSession().selectList("docManage.selectInterfaceSimpleInfoByIds", interfaceIdList);
	}

	public Interface selectInterfaceByInterName(String interName) {
		return (Interface) this.getSqlSession().selectOne("docManage.selectInterfaceByInterName",interName);
	}
	
}