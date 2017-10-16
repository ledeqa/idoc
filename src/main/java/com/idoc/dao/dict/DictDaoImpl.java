package com.idoc.dao.dict;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.dao.pagination.PaginationInfo;
import com.idoc.dao.pagination.PaginationList;
import com.idoc.model.Dict;
import com.idoc.model.DictVersion;
import com.idoc.model.Param;

@Repository
public class DictDaoImpl extends BaseMysqlDBDaoImpl {

	@SuppressWarnings("unchecked")
	public PaginationList<Dict> queryDicts(Map<String, String> map, 
			PaginationInfo paginationInfo) {

		return this.selectPaginationList("Dict.queryDictsByPage", map, paginationInfo);
	}
	
	@SuppressWarnings("unchecked")
	public Dict queryDict(Map<String, String> map){
		
		return (Dict) this.getSqlSession().selectOne("Dict.queryDict", map);
	}

	@SuppressWarnings("unchecked")
	public Dict queryDictByName(Map<String, String> map){
		
		return (Dict) this.getSqlSession().selectOne("Dict.selectDictByName", map);
	}
	
	public int removeDict(Long dictId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("dictId", dictId);
		map.put("delete", "1");
		return this.getSqlSession().update("Dict.deleteDict", map);
	}
	public List<Dict> selectDictsByProductId(Dict dict){
		return this.getSqlSession().selectList("Dict.selectDictsByProductId", dict);
	}
	
	public Dict selectDictById(Long dictId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("dictId", dictId);
		return (Dict) this.getSqlSession().selectOne("Dict.selectDictById", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Param> selectDictParamById(Long dictId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("dictId", dictId);
		return this.getSqlSession().selectList("Dict.selectDictParamById", map);
	}

	public Dict insertDict(Dict dict) {
		SqlSession sqlSession = this.getSqlSession();
		sqlSession.insert("Dict.insert", dict);
		return dict;
	}
	public int updateDict(Dict dict){
		return this.getSqlSession().update("Dict.updateDict", dict);
	}
	
	public int insertDictVersion(DictVersion dictVersion){
		return this.getSqlSession().insert("Dict.insertDictVersion", dictVersion);
	}
	
	public Long selectCurrentDictVersionById(Long id){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("dictId", id);
		return (Long) this.getSqlSession().selectOne("Dict.selectCurrentDictVersionById",map);
	}

	public PaginationList<Dict> queryDictHistory(Map<String, String> map, PaginationInfo paginationInfo) {

		return this.selectPaginationList("Dict.selectDictVersionByPage", map, paginationInfo);
	}

	public DictVersion queryDictVersion(Map<String, String> map) {

		return (DictVersion) this.getSqlSession().selectOne("Dict.queryDictVersion", map);
	}
}
