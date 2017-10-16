package com.idoc.dao.productAndProject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.dao.pagination.PaginationInfo;
import com.idoc.dao.pagination.PaginationList;
import com.idoc.model.ProductUserModel;
import com.idoc.model.Role;
import com.idoc.model.UserModel;

@Repository("productUserDaoImpl")
public class ProductUserDaoImpl extends BaseMysqlDBDaoImpl {
	
	public List<ProductUserModel> selectProductUserWithProduct(Long productId) {
	
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("productId", productId);
		return this.getSqlSession().selectList("productUserModel.selectProductUserModelListByProductId", map);
	}
	@SuppressWarnings("unchecked")
	public PaginationList<ProductUserModel> selectProductUserByPage(Map<String,Object> map,PaginationInfo paginationInfo){
		return this.selectPaginationList("productUserModel.selectProductUserModelListByPage", map, paginationInfo);
	}
	public int insertTB_IDOC_PRODUCT_USER(ProductUserModel productUserModel){
		return this.getSqlSession().insert("productUserModel.insertProductUser", productUserModel);
	}
	public int updateProductUser(Map<String,Object> map){
		return this.getSqlSession().update("productUserModel.updateproductUserModel", map);
	}
	
	public List<UserModel> selectAllUserFromTB_IDOC_USER(){
		return this.getSqlSession().selectList("userModel.queryAllUserModel");
	}
	public UserModel queryUserByUserId(Long userId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userId", userId);
		return (UserModel) this.getSqlSession().selectOne("userModel.queryUserByUserId", map);
	}
	 public List<ProductUserModel> selectFromTB_IDOC_PRODUCT_USERCheck(Long productId,Long userId){
		 Map<String,Object> map = new HashMap<String,Object>();
			map.put("productId", productId);
			map.put("userId", userId);
			return this.getSqlSession().selectList("productUserModel.selectProductUserModelCheck", map);
			
			
	 }
	 
	 public Role selectFromProductUserRole(String englishName,Long productId){
		 Map<String,Object> map = new HashMap<String,Object>();
			map.put("englishName", englishName);
			map.put("productId", productId);
		 return (Role) this.getSqlSession().selectOne("roleModel.selectProductUserRole", map);
	 }
}
