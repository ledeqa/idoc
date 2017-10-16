package com.idoc.dao.login;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.model.ProductModel;
import com.idoc.model.ProductUserModel;
import com.idoc.model.ProjectModel;
import com.idoc.model.UserModel;
import com.idoc.model.login.SearchInterfaceInProduct;

@Repository("loginDaoImpl")
public class LoginDaoImpl extends BaseMysqlDBDaoImpl {
	
	public UserModel queryUserModelByUserName(String userName){
		return (UserModel) this.getSqlSession().selectOne("userModel.queryUserModelByUserName", userName);
	}
	
	public UserModel queryUserModelByUserId(long userId){
		return (UserModel) this.getSqlSession().selectOne("userModel.queryUserModelByUserId", userId);
	}
	
	public String queryUserIdByUserName(String userName){
		return (String) this.getSqlSession().selectOne("userModel.queryUserIdByUserName", userName);
	}
	
	public String queryUserIdByNickName(String nickName){
		return (String) this.getSqlSession().selectOne("userModel.queryUserIdByNickName", nickName);
	}
	
	public UserModel login(String userName, String pswd) {
		Map<String, Object> map = new HashMap<>();
		map.put("userName", userName);
		map.put("pswd", pswd);
		return (UserModel) this.getSqlSession().selectOne("userModel.login", map);
	}
	
	public int register(UserModel userModel) {
		return this.getSqlSession().insert("userModel.register", userModel);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductModel> queryProductModelByProductId(Long productId){
		return this.getSqlSession().selectList("productModel.queryProductModelByProductId", productId);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProjectModel> queryProjectModelByProductId(Long productId){
		return this.getSqlSession().selectList("projectModel.queryProjectModelByProductId", productId);
	}

	public  int deleteProduct(String productId){
		return this.getSqlSession().update("productModel.deleteProduct", productId);
	}
	
	@SuppressWarnings("unchecked")
	public List<SearchInterfaceInProduct> searchInterfaceId(Map<String, Object> paramMap){
		return this.getSqlSession().selectList("productModel.searchInterfaceId", paramMap);
	}
	
	public String queryIni(String key){
		return (String) this.getSqlSession().selectOne("iniModel.queryIni", key);
	}

	public int insertUser(UserModel userModel) {
		return this.getSqlSession().insert("userModel.insertUser",userModel);	
	}
	
	public int updateUserSelective(UserModel userModel) {
		return this.getSqlSession().insert("userModel.updateUserSelective", userModel);	
	}
	
	public ProjectModel selectProjectModelByPageId(Long pageId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("pageId", pageId);
		return (ProjectModel) this.getSqlSession().selectOne("projectModel.selectProjectModelByPageId", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductUserModel> selectProductUserModelListByProductId(Long productId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("productId", productId);
		return this.getSqlSession().selectList("productUserModel.selectProductUserModelListByProductId", map);
	}

	@SuppressWarnings("unchecked")
	public List<UserModel> selectUserModelListByIds(String ids){
		if(StringUtils.isBlank(ids)){
			return null;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		String[] idArrays = ids.split(",");
		if(idArrays == null || idArrays.length <=0){
			return null;
		}
		map.put("idList", Arrays.asList(idArrays));
		return this.getSqlSession().selectList("productUserModel.selectUserModelListByIds", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<UserModel> selectProductUserModelList(Long productId, String userName, String englishName){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("productId", productId);
		map.put("nickName", userName);
		map.put("userName", englishName);
		return this.getSqlSession().selectList("productUserModel.selectProductUserModelList", map);
	}

	@SuppressWarnings("unchecked")
	public List<UserModel> queryUserByCorpMail(String corpMail) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("corpMail", corpMail);
		return this.getSqlSession().selectList("userModel.queryUserByCorpMail",map);
	}


	public int updateUserInfo(UserModel userModel) {
		if(userModel != null){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("corpMail", userModel.getCorpMail());
			map.put("userName", userModel.getUserName());
			map.put("jobNumber", userModel.getJobNumber());
			map.put("telePhone", userModel.getTelePhone());
			return this.getSqlSession().update("userModel.updateUserModel", map);
		}
		return 0;
	}
}
