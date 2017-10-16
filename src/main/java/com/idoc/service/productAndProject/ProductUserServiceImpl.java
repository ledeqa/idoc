package com.idoc.service.productAndProject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.dao.pagination.PaginationInfo;
import com.idoc.dao.pagination.PaginationList;
import com.idoc.dao.productAndProject.ProductUserDaoImpl;
import com.idoc.model.ProductUserModel;
import com.idoc.model.Role;
import com.idoc.model.UserModel;
import com.idoc.util.ErrorMessageUtil;

@Service("productUserServiceImpl")
public class ProductUserServiceImpl {
	@Autowired
	private ProductUserDaoImpl productUserDaoImpl;
	public ProductUserModel addProductUser(Long productId, Long userId,
			Long roleId
			) {
		// 查询产品成员是否存在，如果不存在报错 TODO
		ProductUserModel productUserModel = new ProductUserModel();
		productUserModel.setProductId(productId);
		productUserModel.setRoleId(roleId);
		productUserModel.setUserId(userId);
		productUserModel.setStatus(1);
		int num = productUserDaoImpl.insertTB_IDOC_PRODUCT_USER(productUserModel);
		if (num == 0) {
			ErrorMessageUtil.put("添加产品成员失败");
		}
		return productUserModel;
	}
	public int updateProductUser(Long productUserId, Long roleId) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("productUserId", productUserId);
		map.put("roleId", roleId);
		int num = productUserDaoImpl.updateProductUser(map);
		if (num == 0) {
			ErrorMessageUtil.put("更新产品成员失败");
		}
		return num;
	}
	public int deleteProductUser(Long productUserId) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("productUserId", productUserId);
		map.put("status", 0);// 逻辑删除
		int num = productUserDaoImpl.updateProductUser(map);
		if (num == 0) {
			ErrorMessageUtil.put("删除产品成员失败");
		}
		return num;
	}
	public List<ProductUserModel> selectProductUserWithProduct(Long productId) {
		return productUserDaoImpl.selectProductUserWithProduct(productId);
	}
	public PaginationList<ProductUserModel> selectProductUserByPage(Map<String,Object> map,PaginationInfo paginationInfo){
		return productUserDaoImpl.selectProductUserByPage(map,paginationInfo);
	}
    public List<UserModel> selectAllUserFromTB_IDOC_USER(){
		
		return productUserDaoImpl.selectAllUserFromTB_IDOC_USER();
	}
     
	 public UserModel selctUserByUserId(String userId){
		 Long id = Long.valueOf(userId);
		return productUserDaoImpl.queryUserByUserId(id);
	}
	 
	 public List<ProductUserModel> selectFromTB_IDOC_PRODUCT_USERCheck(Long productId,Long userId){
		 return productUserDaoImpl.selectFromTB_IDOC_PRODUCT_USERCheck(productId, userId);
	 } 
	 
	 public Role selectFromProductUserRole(String englishName,Long productId){
		 return productUserDaoImpl.selectFromProductUserRole(englishName, productId);
	 }
}
