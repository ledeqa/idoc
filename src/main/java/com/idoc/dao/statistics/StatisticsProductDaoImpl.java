package com.idoc.dao.statistics;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.model.StatisticsProductModel;

@Repository("statisticsProductDaoImpl")
public class StatisticsProductDaoImpl extends BaseMysqlDBDaoImpl{
	public int insertStatisticsProduct(StatisticsProductModel statisticsProductModel){
		return this.getSqlSession().insert("Statistics.insertStatisticsProduct", statisticsProductModel);
	}
	
	public int updateStatisticsProduct(StatisticsProductModel statisticsProductModel){
		return this.getSqlSession().update("Statistics.updateStatisticsProduct", statisticsProductModel);
	}
	
	@SuppressWarnings("unchecked")
	public List<StatisticsProductModel> selectAllStatisticsProduct(){
		return this.getSqlSession().selectList("Statistics.selectAllStatisticsProduct");
	}
	
	public StatisticsProductModel selectStatisticsProductByProjectId(Long projectId){
		return (StatisticsProductModel) this.getSqlSession().selectOne("Statistics.selectStatisticsProductByProjectId", projectId);
	}
	
	@SuppressWarnings("unchecked")
	public List<StatisticsProductModel> getStatisticsProductByProductId(Map<String, Object> map){
		return this.getSqlSession().selectList("Statistics.getStatisticsProductByProductId", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<StatisticsProductModel> selectStatisticsProductByProductIds(Map<String, Object> map){
		return this.getSqlSession().selectList("Statistics.selectStatisticsProductByProductIds", map);
	}
}