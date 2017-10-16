package com.idoc.web.productReleated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idoc.util.QRCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.idoc.constant.CommonConstant;
import com.idoc.constant.LogConstant;
import com.idoc.dao.pagination.PaginationInfo;
import com.idoc.dao.pagination.PaginationList;
import com.idoc.memcache.MemcacheServer;
import com.idoc.model.LoginUserInfo;
import com.idoc.model.ProductModel;
import com.idoc.model.ProductUserModel;
import com.idoc.model.Role;
import com.idoc.model.UserModel;
import com.idoc.service.login.LoginIndexServiceImpl;
import com.idoc.service.productAndProject.ProductAndProjectServiceImpl;
import com.idoc.service.productAndProject.ProductUserServiceImpl;
import com.idoc.service.role.RoleConfigManagementService;
import com.netease.common.util.StringUtil;
import com.netease.common.util.MD5Util;
@Controller
public class ProductController {
	@Autowired
	private RoleConfigManagementService roleConfigManagementServiceImpl;
	@Resource(name = "loginIndexServiceImpl")
	private LoginIndexServiceImpl loginIndexServiceImpl;
	@Resource(name = "productUserServiceImpl")
	private ProductUserServiceImpl productUserServiceImpl;
	@Resource(name = "productAndProjectServiceImpl")
	private ProductAndProjectServiceImpl productAndProjectServiceImpl;
	@Autowired
	private MemcacheServer memcacheServer;

	@RequestMapping("/idoc/createProduct.html")
	public String createProduct(){
		return "/idoc/createProduct";
	}
	
	@RequestMapping("/idoc/updateProduct.html")
	public String updateProduct(@RequestParam("productId") String productId, HttpServletRequest request, ModelMap modelMap) {
	
		if (StringUtils.isBlank(productId)) {
			return null;
		}
		LoginUserInfo userInfo = (LoginUserInfo) memcacheServer.getCacheData(request.getSession().getId());
		String englishName = null;
		if(userInfo != null){
			englishName = userInfo.getEnglishName();
		}
		
		Role role = productUserServiceImpl.selectFromProductUserRole(englishName, Long.valueOf(productId.trim()));
		ProductModel productModel = productAndProjectServiceImpl.queryProductModelByProductId(Long.valueOf(productId));
		if (productModel != null) {
			modelMap.put("productModel", productModel);
		}
		if(loginIndexServiceImpl.confirmAdmin(englishName) == 1){
			// 如果是系统管理员
			LogConstant.debugLog.info("you  are admins ");
			return "/idoc/updateProduct";
		}
		
		if (role != null) {
			LogConstant.debugLog.info("roleName is  " + role.getRoleName());
			if (!role.getRoleName().contains(CommonConstant.ROLE_ADMIN) && !role.getRoleName().contains(CommonConstant.ROLE_TESTOR_MANAGER)) {
				return "/idoc/updateProductDetial";
			} else {
				LogConstant.debugLog.info("you  are admins ");
				return "/idoc/updateProduct";
			}
		} else {
			return "/idoc/updateProductDetial";
		}
		
	}
	
	@RequestMapping("/idoc/selectAllUser.html")
	@ResponseBody
	public Map<String,Object> selectAllUser(){
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<UserModel> userModels=	productUserServiceImpl.selectAllUserFromTB_IDOC_USER();
		if (userModels!=null&&userModels.size()>0) {
		retMap.put("userModels", userModels);
		retMap.put("retCode", 200);
		}else {
			retMap.put("retCode", -1);	
		}
		return retMap;
	}
	
	@RequestMapping("/idoc/selectOtherUser.html")
	@ResponseBody
	public Map<String,Object> selectOtherUser(@RequestParam("productId") String productId){
		Map<String, Object> retMap = new HashMap<String, Object>();
		if("".equals(productId) || productId == null){
			retMap.put("retCode", -1);
			return retMap;
		}
		
		List<UserModel> userModels=	productUserServiceImpl.selectAllUserFromTB_IDOC_USER();
		List<ProductUserModel> exituserModels = productUserServiceImpl.selectProductUserWithProduct(Long.parseLong(productId));
		if (userModels!=null&&userModels.size()>0) {
			if(exituserModels!=null&&exituserModels.size()>0){
				for(ProductUserModel productUserModel : exituserModels){
					long eid = productUserModel.getUserId();
					for(UserModel userModel: userModels){
						long uid = userModel.getUserId();
						if(uid == eid){
							userModels.remove(userModel);
							break;
						}
					}
				}
			}
			retMap.put("userModels", userModels);
			retMap.put("retCode", 200);
		}else {
			retMap.put("retCode", -1);	
		}
		return retMap;
	}
	
	@RequestMapping("/idoc/selectAllRole.html")
	@ResponseBody
    public Map<String, Object> selectAllRole(){
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<Role> roleConfigList = roleConfigManagementServiceImpl
				.selectRoleConfigModel();
		if (roleConfigList!=null&&roleConfigList.size()>0) {
			retMap.put("retCode", 200);
			retMap.put("roleConfigList", roleConfigList);
		}else {
			retMap.put("retCode", -1);
		}
		return retMap;
	}
	
	@RequestMapping("/idoc/selectUserByIds.html")
	@ResponseBody
	  public Map<String, Object> selectUserById(@RequestParam("userIds[]") List<String> userIds){
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<UserModel> userModels=new ArrayList<UserModel>();
//		String []userIdTmp=userIds.split(",");
		if (userIds!=null&&userIds.size()>0) {
			for (int i = 0; i <userIds.size(); i++) {
				if (!"".equals(userIds.get(i).trim())) {
					UserModel userModel=new UserModel();
					userModel=productUserServiceImpl.selctUserByUserId(userIds.get(i).trim());
					userModels.add(userModel);
				}
			}
			if (userModels!=null&&userModels.size()>0) {
			retMap.put("retCode", 200);
			retMap.put("userModels", userModels);
			}
			else {
				retMap.put("retCode", -1);
			}	
		}
		
		return retMap;
	}
	
	@RequestMapping("/idoc/addProduct.html")
	@ResponseBody
	 public Map<String, Object> selectUserById(@RequestParam("productName") String productName,
		 @RequestParam("producDescription") String producDescription,
		 @RequestParam("allIds") String allIds, String productDomainUrl){
		Map<String, Object> retMap = new HashMap<String, Object>();
		Long  productId=(long) 0;
		if(StringUtils.isBlank(productName) ){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		 ProductModel productModelTmp=productAndProjectServiceImpl.selectFromProductByProductName(productName.trim());
	    if (productModelTmp!=null) {
	    	retMap.put("retCode", 400);
	    	return retMap;
		}
	    if(StringUtils.isNotEmpty(producDescription)){
	    	producDescription = producDescription.trim();
	    }
	    if(StringUtils.isNotEmpty(productDomainUrl)){
	    	productDomainUrl = productDomainUrl.trim();
	    }
		ProductModel productModel=productAndProjectServiceImpl.addProduct(productName.trim(), producDescription, productDomainUrl);
		if (productModel!=null) {
			retMap.put("retCode", 200);
			productId=productModel.getProductId();
		}
		LogConstant.debugLog.info("productId is "+productId);
	    String [] tmpIdsStrings=allIds.trim().split("##");
		if (tmpIdsStrings!=null&&tmpIdsStrings.length>0) {
			
			for (int i = 0; i < tmpIdsStrings.length; i++) {
				if (!"".equals(tmpIdsStrings[i])) {
					Long userId=(long) 0,roleId=(long) 0;
					String [] tmpIdsRole=tmpIdsStrings[i].trim().split(",");	
					ProductUserModel productUserModel=new ProductUserModel();
					productUserModel.setProductId(productId);
					if (tmpIdsRole[0]!=null&&!"".equals(tmpIdsRole[0])) {
						productUserModel.setUserId(Long.valueOf(tmpIdsRole[0]));
						userId=Long.valueOf(tmpIdsRole[0]);
					}
					if (tmpIdsRole[1]!=null&&!"".equals(tmpIdsRole[1])) {
						productUserModel.setRoleId(Long.valueOf(tmpIdsRole[1]));
						roleId=Long.valueOf(tmpIdsRole[1]);
					}
					
					productUserServiceImpl.addProductUser(productId, userId, roleId);
				}
			}
		}
		
		return retMap;
	}
	
	@RequestMapping("/idoc/getProductUserById")
	@ResponseBody
	public Map<String, Object> getProductUserById(@RequestParam("productId") String productId, 
			@RequestParam("perPage") Integer perPage, 
			@RequestParam("pageNum") Integer pageNum,
			@RequestParam("userName") String userName,
			@RequestParam("userEmail") String userEmail,
			@RequestParam("userRole") String userRole){
		Map<String, Object> retMap = new HashMap<String, Object>();
		if(StringUtils.isBlank(productId) ){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("productId", Long.valueOf(productId));
		if(!StringUtil.isEmpty(userName))
			map.put("userName", userName);
		if(!StringUtil.isEmpty(userEmail))
			map.put("userEmail", userEmail);
		if(!StringUtil.isEmpty(userRole))
			map.put("roleId", Long.valueOf(userRole));
		PaginationInfo paginationInfo = new PaginationInfo(pageNum, perPage);
		PaginationList<ProductUserModel> productUserModels = productUserServiceImpl.selectProductUserByPage(map, paginationInfo);
		if (productUserModels!=null&&productUserModels.size()>0) {
			retMap.put("retCode", 200);
			retMap.put("productUserModels", productUserModels);
			retMap.put("page", productUserModels.getPaginationInfo());
		}else {
			retMap.put("retCode", -1);
			retMap.put("page", paginationInfo);
		}
		return retMap;
		
	}
	@RequestMapping("/idoc/addProductUser")
	@ResponseBody
	public Map<String, Object> addProductUser(@RequestParam("productId") String productId, @RequestParam("userIds[]") List<String> userIds,	
			@RequestParam("productName") String productName, @RequestParam("roleId") String roleId){
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		if(StringUtils.isBlank(productId) || StringUtils.isBlank(productName) || StringUtils.isBlank(roleId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		List<UserModel> userModels=new ArrayList<UserModel>();
		if (userIds != null && userIds.size() > 0) {
			for (int i = 0; i <userIds.size(); i++) {
				Long userId=Long.valueOf(userIds.get(i)),roleIdTmp=Long.valueOf(roleId);
				
				if (!"".equals(userIds.get(i).trim())) {
					UserModel userModel=new UserModel();
					List<ProductUserModel> productUserModels=productUserServiceImpl.selectFromTB_IDOC_PRODUCT_USERCheck(Long.valueOf(productId.trim()), userId);
					if (productUserModels==null||productUserModels.size()==0) {
						productUserServiceImpl.addProductUser(Long.valueOf(productId.trim()), userId, roleIdTmp);
						userModel=productUserServiceImpl.selctUserByUserId(userIds.get(i).trim());
						userModels.add(userModel);	
					}
					
				}
			}
			if (userModels!=null&&userModels.size()>0) {
			retMap.put("retCode", 200);
			retMap.put("userModels", userModels);
			}
			else {
				retMap.put("retCode", -1);
			}	
		}
		
		return retMap;
	}
	
	@RequestMapping("/idoc/updateProductUser.html")
	@ResponseBody
	public Map<String, Object> updateProductUser(
			@RequestParam("userId") String userId,
			@RequestParam("userName") String userName,
			@RequestParam("productUserId") String productUserId,
			@RequestParam("roleId") String roleId,
			@RequestParam("isUpdateUserName") boolean isUpdateUserName){
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		if(StringUtils.isBlank(productUserId)||StringUtils.isBlank(roleId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		if(isUpdateUserName){
			if(StringUtils.isBlank(userId)||StringUtils.isBlank(userName)){
				retMap.put("retCode", 401);
				retMap.put("retDesc", "参数错误");
				return retMap;
			}
			UserModel userModel = new UserModel();
			userModel.setUserId(Long.parseLong(userId));
			userModel.setNickName(userName);
			int res = loginIndexServiceImpl.updateUserSelective(userModel);
			if (res <= 0) {
				retMap.put("retCode", 500);
				retMap.put("retDesc", "修改用户名称失败");
				return retMap;
			}
		}
		
		int ret=productUserServiceImpl.updateProductUser(Long.valueOf(productUserId.trim()), Long.valueOf(roleId.trim()));
		if (ret>0) {
			retMap.put("retCode", 200);
			retMap.put("retDesc", "修改成功");
		}else {
			retMap.put("retCode", 500);	
		}
		return retMap;
	}
	
	@RequestMapping("/idoc/deleteProductUser.html")
	@ResponseBody
	public Map<String, Object> deleteProductUser(@RequestParam("productUserId") String productUserId){
		Map<String, Object> retMap = new HashMap<String, Object>();
		if(StringUtils.isBlank(productUserId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		int ret=productUserServiceImpl.deleteProductUser(Long.valueOf(productUserId.trim()));
		if (ret>0) {
			retMap.put("retCode", 200);
			retMap.put("retDesc", "删除成功");
		}else {
			retMap.put("retCode", 500);	
		}
		return retMap;
	}
	
	@RequestMapping("/idoc/updateProductName.html")
	@ResponseBody
	public Map<String, Object> updateProductName(@RequestParam("productId") String productId,
		@RequestParam("productName") String productName,@RequestParam("productDesc") String productDesc,
		@RequestParam("productDomainUrl")String productDomainUrl,@RequestParam("productFlow")Integer productFlow){
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		if(StringUtils.isBlank(productId)||StringUtils.isBlank(productName)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		if(StringUtils.isNotEmpty(productDesc)){
			productDesc = productDesc.trim();
		}
		if(StringUtils.isNotEmpty(productDomainUrl)){
			productDomainUrl = productDomainUrl.trim();
		}
		int ret=productAndProjectServiceImpl.updateProduct(Long.valueOf(productId.trim()), productName.trim(), productDesc, productDomainUrl,productFlow);
		if (ret>0) {
			retMap.put("retCode", 200);
			retMap.put("retDesc", "修改成功");
		}else {
			retMap.put("retCode", 500);	
		}
		return retMap;
	}


	@RequestMapping("/idoc/addUserInfoPage")
	public String addUserInfoPage(HttpServletRequest request, HttpServletResponse response, ModelMap map) {
//		String userCorpMail = (String) request.getSession().getAttribute("userCorpMail");
		String userCorpMail = (String) request.getAttribute("userCorpMail");
		List<UserModel> user = loginIndexServiceImpl.selectUserByCorpMail(userCorpMail);
		if(userCorpMail != null && user != null && user.size() > 0){
			map.put("doesUserExist", true);
			map.put("userName", user.get(0).getNickName());
			map.put("jobNumber", user.get(0).getJobNumber());
			map.put("telephone", user.get(0).getTelePhone());
		}
		else {
			map.put("doesUserExist", false);
		}

		return "userinfo/addUserInfo";
	}


	@RequestMapping("/idoc/updateUserInfo")
	@ResponseBody
	public Map<String, Object> updateUserInfo(UserModel userModel) {
		int status = loginIndexServiceImpl.updateUserInfo(userModel);
		Map<String, Object> retMap = new HashMap<String, Object>();
		if (status > 0) {
			retMap.put("retCode", 200);
			retMap.put("retDesc", "操作成功");
		} else {
			retMap.put("retCode", -1);
			retMap.put("retDesc", "插入数据库失败");
		}
		return retMap;
	}

	@RequestMapping("/idoc/qrcode")
	public void orderIdBarcodeGenerate(String email, HttpServletResponse response) {

		try {
			if (org.apache.commons.lang.StringUtils.isBlank(email)) {
				email = "";
				return ;
			}
			String emailMd5 = MD5Util.get(email+ "&" + CommonConstant.MD5_PRIVATE_KEY, "utf-8");
			QRCodeUtil.writeQRCodeToStream(emailMd5+"&"+email, response.getOutputStream());
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

}
