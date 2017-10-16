package com.idoc.dao.productAndProject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.dao.pagination.PaginationInfo;
import com.idoc.dao.pagination.PaginationList;
import com.idoc.model.Interface;
import com.idoc.model.ProductModel;
import com.idoc.model.ProductUserModel;
import com.idoc.model.ProjectModel;

@Repository("projectDaoImpl")
public class ProjectDaoImpl extends BaseMysqlDBDaoImpl {
	
	@SuppressWarnings("unchecked")
	public List<ProjectModel> selectProjectListWithProduct(String productId) {
	
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("productId", productId);
		return this.getSqlSession().selectList("projectModel.queryProjectModelByProductId", map);
	}
	@SuppressWarnings("unchecked")
	public List<ProjectModel> selectOnlineProjectListWithProduct(String productId) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("productId", productId);
		return this.getSqlSession().selectList("projectModel.queryOnlineProjectModelByProductId", map);
	}
	public int insertTB_IDOC_PROJECT(ProjectModel projectModel){
		return this.getSqlSession().insert("projectModel.insertIntoTB_IDOC_PROJECT", projectModel);
	}
	public int updateProject(Map<String,Object> map){
		return this.getSqlSession().update("projectModel.updateProject", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProjectModel> selectProjectInTimespan(String startTime, String endTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		return this.getSqlSession().selectList("projectModel.selectProjectInTimespan", map);
	}
	
	public int insertTB_IDOC_PRODUCT(ProductModel productModel){
		return this.getSqlSession().insert("productModel.insertIntoTB_IDOC_PRODUCT", productModel);
	}
	public int updateProduct(Map<String,Object> map){
		return this.getSqlSession().update("productModel.updateProduct", map);
	}
	
	public ProductModel selectFromProductByProductName(String productName){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("productName", productName);
		return (ProductModel) this.getSqlSession().selectOne("productModel.queryProductModelByProductName", map);
	}
	
	public ProductModel queryProductModelByProductId(Long productId){
		return (ProductModel) this.getSqlSession().selectOne("productModel.queryProductModelByProductId", productId);
	}
	
	@SuppressWarnings("unchecked")
	public PaginationList<ProjectModel> queryProjectModelByPage(Long productId,int status, String projectName, PaginationInfo paginationInfo){
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("projectName", projectName);
				if(status != 0){
					map.put("status", status);
				}
				map.put("productId", productId);
				return this.selectPaginationList("projectModel.queryProjectModelInforByPage",map,paginationInfo);
				
	}
	@SuppressWarnings("unchecked")
	public PaginationList<ProductModel> queryProductModelByPage(PaginationInfo paginationInfo){
		Map<String, Object> map = new HashMap<String, Object>();
		return this.selectPaginationList("productModel.queryProductModelInforByPage",map,paginationInfo);
	}
	@SuppressWarnings("unchecked")
	public PaginationList<ProductUserModel> queryProductModelByPageAndUser(String userId, PaginationInfo paginationInfo){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		return this.selectPaginationList("productUserModel.queryProductUserModelByUserIdAndByPage",map,paginationInfo);
				
	}
	@SuppressWarnings("unchecked")
	public List<ProjectModel> selectProjectModelByProjectName(Long productId,String projectName){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("projectName", projectName);
		map.put("productId", productId);
		return this.getSqlSession().selectList("projectModel.queryProjectModelByProjectName", map);
	}
	
	public ProjectModel selectProjectModelByProjectId(Long projectId){
		return (ProjectModel) this.getSqlSession().selectOne("projectModel.selectProjectModelByProjectId", projectId);
	}
	
	@SuppressWarnings("unchecked")
	public List<Interface> selectInterfaceByProjectId(Long projectId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("projectId", projectId);
		return this.getSqlSession().selectList("docManage.selectInterfaceModelListByProjectId", map);
	}
	public Integer getProductFlowById(Long productId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productId", productId);
		return (Integer) this.getSqlSession().selectOne("productModel.selectProductFlowById",map);
	}
}
