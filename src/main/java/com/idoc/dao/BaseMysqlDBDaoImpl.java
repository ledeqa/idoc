package com.idoc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.idoc.dao.pagination.PaginationInfo;
import com.idoc.dao.pagination.PaginationList;

@Repository("baseLotteryMysqlDBDao")
public class BaseMysqlDBDaoImpl extends SqlSessionDaoSupport{
	
	@Autowired
	public void setMySqlSessionFactory(@Qualifier("mysqlSqlSessionFactory") SqlSessionFactory sqlSessionFactory){
		this.setSqlSessionFactory(sqlSessionFactory);
	}
	
	public int delete(String sqlId,Object obj){
		return getSqlSession().delete(sqlId,obj);
	}
	
	public int insertOrUpdate(String sqlId, Object obj){
		return getSqlSession().update(sqlId,obj);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PaginationList selectPaginationList(String statement, Object parameter, PaginationInfo paginationInfo) {

		PaginationList paginationList = new PaginationList();
		if (parameter == null) {
			throw new RuntimeException("parameter can not be null");
		}
		if (parameter instanceof Map<?,?>) {
			((Map) parameter).put("paginationInfo", paginationInfo);
		}
		List result = this.getSqlSession().selectList(statement, parameter);
		paginationList.addAll(result);
		if (paginationInfo == null) {
			paginationInfo = new PaginationInfo();
			paginationInfo.setCurrentPage(1);
			paginationInfo.setRecordPerPage(result.size());
			paginationInfo.setTotalPage(1);
			paginationInfo.setTotalRecord(result.size());
		}
		paginationList.setPaginationInfo(paginationInfo);
		
		return paginationList;
	}
}