package com.idoc.dao.docManage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.model.InterfaceOnline;

@Repository("interfaceOnlineDaoImpl")
public class InterfaceOnlineDaoImpl extends BaseMysqlDBDaoImpl{
	
	@SuppressWarnings("unchecked")
	public List<InterfaceOnline> selectOnlineInterfaceListByCond(Map<String,Object> map){
		return this.getSqlSession().selectList("InterfaceOnline.selectOnlineInterfaceListProduct", map);
	}
	
	public InterfaceOnline selectOnlineInterfaceById(Long interfaceId){
		return (InterfaceOnline) this.getSqlSession().selectOne("InterfaceOnline.selectOnlineInterfaceById", interfaceId);
	}
	
	public InterfaceOnline insertOnlineInterface(InterfaceOnline interf){
		this.getSqlSession().insert("InterfaceOnline.insertOnlineInterface", interf);
		return interf;
	}
	
	public int updateOnlineInterface(Map<String, Object> map){
		return this.getSqlSession().update("InterfaceOnline.updateOnlineInterface", map);
	}
	
	public int updateOnlineInter(InterfaceOnline interfaceOnline){
		return this.getSqlSession().update("InterfaceOnline.updateOnlineInter", interfaceOnline);
	}
	public int deleteOnlineInterface(Long interfaceId){
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("interfaceId", interfaceId);
		map.put("delete", "1");
		return this.getSqlSession().delete("InterfaceOnline.updateOnlineInterface",map);
	}
	
	@SuppressWarnings("unchecked")
	public List<InterfaceOnline> selectOnlineInterfaceByConditionsByPage(Map<String, Object> map){
			
		return this.getSqlSession().selectList("InterfaceOnline.selectOnlineInterfaceByConditions",map);
  }
	@SuppressWarnings("unchecked")
	public List<InterfaceOnline> selectOnlineInterfaceListByProductByPage(String url,
		Long interfaceId,
		Long ProductId
		){
		Map<String, Object> map=new HashMap<String,Object>();
		map.put("url", url);
		map.put("interfaceId", interfaceId);
		map.put("ProductId", ProductId);
		return this.getSqlSession().selectList("InterfaceOnline.selectOnlineInterfaceListProduct",map);
  }
	@SuppressWarnings("unchecked")
	public List<InterfaceOnline> selectInterfaceListByPages(List<Long> pageIds){
		return this.getSqlSession().selectList("InterfaceOnline.selectInterfaceOnlineList", pageIds);
	}

	public InterfaceOnline selectOnlineInterfaceByConditions(Map<String, Object> map) {

		return (InterfaceOnline) this.getSqlSession().selectOne("InterfaceOnline.selectOnlineInterfaceByConditions", map);
	}

	@SuppressWarnings("unchecked")
	public List<InterfaceOnline> selectInterfaceListByCond(
			Map<String, Object> condMap) {
		return this.getSqlSession().selectList("InterfaceOnline.selectInterfaceListByCond", condMap);
	}
}