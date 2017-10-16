package com.idoc.service.login;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.constant.CommonConstant;
import com.idoc.constant.LogConstant;
import com.idoc.dao.login.LoginDaoImpl;
import com.idoc.dao.pagination.PaginationInfo;
import com.idoc.dao.pagination.PaginationList;
import com.idoc.dao.productAndProject.ProductUserDaoImpl;
import com.idoc.dao.productAndProject.ProjectDaoImpl;
import com.idoc.dao.role.RoleDaoImpl;
import com.idoc.memcache.MemcacheServer;
import com.idoc.model.ProductModel;
import com.idoc.model.ProductUserModel;
import com.idoc.model.ProjectModel;
import com.idoc.model.Role;
import com.idoc.model.UserModel;
import com.idoc.model.login.ProductListModel;
import com.idoc.model.login.SearchInterfaceInProduct;
import com.netease.common.util.StringUtil;


@Service("loginIndexServiceImpl")
public class LoginIndexServiceImpl {
	
	@Resource(name="loginDaoImpl")
	private LoginDaoImpl loginDaoImpl;
	
	@Autowired
	private ProjectDaoImpl projectDaoImpl;
	
	@Autowired
	private RoleDaoImpl roleDaoImpl;
	
	@Autowired
	private ProductUserDaoImpl productUserDaoImpl;
	
	@Autowired
	private MemcacheServer memcacheServer;
	
	
	public UserModel login(String userName, String pswd) {
		return loginDaoImpl.login(userName, pswd);
	}
	
	public int register(UserModel userModel) {
		return loginDaoImpl.register(userModel);
	}
	
	public List<ProductListModel> queryProductModelByPage(int adminFlag, String userName, PaginationInfo paginationInfo){
		List<ProductListModel> productList = new ArrayList<ProductListModel>();
		
		if(adminFlag == 1){
			PaginationList<ProductModel> productModels = projectDaoImpl.queryProductModelByPage(paginationInfo);
			int id = 0;
			if(productModels != null && !productModels.isEmpty()){
				for(ProductModel productModel : productModels){
					
					Long productId = productModel.getProductId();
					List<ProjectModel> projectModelList = loginDaoImpl.queryProjectModelByProductId(productId);			
					String projectNum = String.valueOf(projectModelList.size());
					
					List<ProductModel> productModelList = loginDaoImpl.queryProductModelByProductId(productId);
					if(productModelList.size() == 0){
						continue;
					}
					String productName = productModelList.get(0).getProductName();
					
					id += 1;
					ProductListModel productListModel = new ProductListModel();
					productListModel.setId(String.valueOf(id));
					productListModel.setProductId(productId);
					productListModel.setIsAdmin(1);
					productListModel.setProductName(productName);
					productListModel.setProjectNum(projectNum);
					productListModel.setInterfaceOnlineUrl("http://idoc.qa.lede.com/idoc/onlineInter/index.html?productId=" + productId);
					productListModel.setDataDictUrl("http://idoc.qa.lede.com/dict/index.html?productId=" + productId);
					productList.add(productListModel);
				}
			}
		}else{
			String userId = loginDaoImpl.queryUserIdByUserName(userName);
			PaginationList<ProductUserModel> productUserModels = projectDaoImpl.queryProductModelByPageAndUser(userId, paginationInfo);
			int id = 0;
			if(productUserModels != null && productUserModels.size() != 0){
				for(ProductUserModel productUserModel : productUserModels){
					
					Long productId = productUserModel.getProductId();
					List<ProjectModel> projectModelList = loginDaoImpl.queryProjectModelByProductId(productId);			
					String projectNum = String.valueOf(projectModelList.size());
					
					List<ProductModel> productModelList = loginDaoImpl.queryProductModelByProductId(productId);
					if(productModelList.size() == 0){
						if(paginationInfo != null){
							paginationInfo.setTotalRecord(paginationInfo.getTotalRecord()-1);
							if(paginationInfo.getTotalPage()>Math.ceil((double)paginationInfo.getTotalRecord()/paginationInfo.getRecordPerPage()))
								paginationInfo.setTotalPage(paginationInfo.getTotalPage()-1);
						}
						continue;
					}
					String productName = productModelList.get(0).getProductName();
					
					id += 1;
					ProductListModel productListModel = new ProductListModel();
					productListModel.setId(String.valueOf(id));
					productListModel.setProductId(productId);
					if(productUserModel.getRole().getRoleName().equalsIgnoreCase(CommonConstant.ROLE_ADMIN)){
						productListModel.setIsAdmin(1);
					}else{
						productListModel.setIsAdmin(0);
					}
					productListModel.setProductName(productName);
					productListModel.setProjectNum(projectNum);
					productListModel.setInterfaceOnlineUrl("http://idoc.qa.lede.com/idoc/onlineInter/index.html?productId=" + productId);
					productListModel.setDataDictUrl("http://idoc.qa.lede.com/dict/index.html?productId=" + productId);
					productList.add(productListModel);
				}
			}
		}
		return productList;
	}

	public int updateUserSelective(UserModel userModel){
		return loginDaoImpl.updateUserSelective(userModel);
	}
	
	public int deleteProduct(String productId){
		int ret = loginDaoImpl.deleteProduct(productId);
		return ret;
	}
	
	/**
	 * 根据接口名称或接口url查询茶品下接口信息
	 * @param productId
	 * @param interfaceName
	 * @param interfaceUrl
	 * @return
	 */
	public List<SearchInterfaceInProduct> searchInterfaceId(String productId, String interfaceName, String interfaceUrl){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("productId", Long.parseLong(productId));
		if(!StringUtil.isEmpty(interfaceName))
			paramMap.put("interfaceName", interfaceName);
		if(!StringUtil.isEmpty(interfaceUrl))
			paramMap.put("interfaceUrl", interfaceUrl);
		return loginDaoImpl.searchInterfaceId(paramMap);
	}
	
	public int confirmAdmin(String userName){
		
		String adminUsers = (String) memcacheServer.getCacheData(CommonConstant.INI_ADMIN);
		if(adminUsers == null || adminUsers.equals("") || userName == null){
			return 0;
		}
		if(adminUsers.contains(userName)){
			return 1;
		}
		return 0;
	}

	public void insertUser(String userName, String nickName, String email) {
		
		String UserId = loginDaoImpl.queryUserIdByUserName(userName);
		if(UserId == null || UserId.equals("")){
			UserModel userModel = new UserModel();
			userModel.setUserName(userName);
			userModel.setNickName(nickName);
			userModel.setCorpMail(email);
			userModel.setStatus(1);
			Date date = new Date();       
			Timestamp currentTime = new Timestamp(date.getTime());
			userModel.setCreateTime(currentTime);

			System.out.println(loginDaoImpl.insertUser(userModel));
			// 插入demo项目角色关系
			ProductUserModel pUser = new ProductUserModel();
			pUser.setUserId(userModel.getUserId());
			// productId
			ProductModel pm = projectDaoImpl.selectFromProductByProductName(CommonConstant.PROJECT_DEMO);
			// roleId
			List<Role> rList = roleDaoImpl.queryRoleModelByRoleName(CommonConstant.ROLE_ADMIN);
			if(rList != null && rList.size() >0 && pm != null){
				pUser.setProductId(pm.getProductId());
				pUser.setRoleId(rList.get(0).getRoleId());
				pUser.setStatus(1);
				productUserDaoImpl.insertTB_IDOC_PRODUCT_USER(pUser);
			}else{
				LogConstant.runLog.error("没有找到管理员角色的id");
			}
			
			
		}
		
	}

	public List<UserModel> selectUserByCorpMail(String corpMail) {
		List<UserModel> users = loginDaoImpl.queryUserByCorpMail(corpMail);
		return users;
	}


	public int updateUserInfo(UserModel userModel) {
		if (userModel != null) {

			int updateUserModelStatus = loginDaoImpl.updateUserInfo(userModel);
			if (updateUserModelStatus <= 0)
				LogConstant.runLog.debug("Update UserInfo to database TB_User_INFO error! return code: "
						+ updateUserModelStatus);
			return updateUserModelStatus;
		}
		return 0;
	}
}
