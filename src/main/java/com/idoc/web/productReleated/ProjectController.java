package com.idoc.web.productReleated;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.constant.CommonConstant;
import com.idoc.constant.LogConstant;
import com.idoc.dao.pagination.PaginationInfo;
import com.idoc.dao.pagination.PaginationList;
import com.idoc.model.Interface;
import com.idoc.model.ProductModel;
import com.idoc.model.ProjectModel;
import com.idoc.model.Role;
import com.idoc.model.rap.RapProject;
import com.idoc.service.login.LoginIndexServiceImpl;
import com.idoc.service.productAndProject.ProductAndProjectServiceImpl;
import com.idoc.service.role.RoleConfigManagementService;
import com.idoc.util.DataTypeCheckUtil;
import com.idoc.util.ErrorMessageUtil;
import com.idoc.util.LoginUserInfoUtil;
import com.netease.common.util.JsonUtil;


@Controller
public class ProjectController {
	@Autowired
	
	@Resource(name = "productAndProjectServiceImpl")
	private ProductAndProjectServiceImpl productAndProjectServiceImpl;
	
	@Autowired
	private RoleConfigManagementService roleConfigManagementService;
	@Autowired
	private LoginIndexServiceImpl loginIndexServiceImpl;
	
	@RequestMapping("/idoc/createProject.html")
	public String createProject(){
		
		return "/idoc/createProject";
	}
	
	@RequestMapping("/idoc/projectList.html")
	public String SelectProjectListWithProduct(@RequestParam(value = "productId", required = false) String productId,ModelMap modelMap){
//		List<ProjectModel> projectModels=projectDaoImpl.selectProjectListWithProduct(productId);
//		if (projectModels!=null&&projectModels.size()>0) {
//			modelMap.put("projectModels", projectModels);
//		}
		if(!DataTypeCheckUtil.isNumber(productId)){
			modelMap.put("exceptionMsg", "参数格式有误，请检查参数。");
			return "/datatypeError";
		}
		String englishName = LoginUserInfoUtil.getUserEnglishName();
		Role role = null;
		role = roleConfigManagementService.selectRoleByUserEnglishName(englishName, Long.parseLong(productId));
		String roleName = null;
		if(role!=null || loginIndexServiceImpl.confirmAdmin(englishName) == 1){
			if(loginIndexServiceImpl.confirmAdmin(englishName) == 1){
				//判断如果当前用户时超级管理员，直接将role的值设置为“管理员”
				modelMap.addAttribute("role", CommonConstant.ROLE_ADMIN);
			}else{
				roleName = role.getRoleName();
				modelMap.addAttribute("role", roleName);
			}
		}
		if (productId!=null) {
			ProductModel productModel=productAndProjectServiceImpl.queryProductModelByProductId(Long.valueOf(productId));
			if (productModel!=null) {
				modelMap.put("productName", productModel.getProductName());
				modelMap.put("productId", productId);	
			}
		}
		
		return "/idoc/projectList";
	}
	
	@RequestMapping("/demo/productDemo.html")
	public String SelectProjectListWithDemoProduct(@RequestParam("productName") String productName,ModelMap modelMap){
		if (productName!=null) {
			ProductModel productModel=productAndProjectServiceImpl.selectFromProductByProductName(productName.trim());
			if (productModel!=null) {
				modelMap.put("productName", productModel.getProductName());
				modelMap.put("productId", productModel.getProductId());	
			}
			
		}
		
		return "/idoc/projectList";
	}
	
	/**
	 * 
	 * @param projectName
	 * @param productId
	 * 插入前判断是否已存在
	 * @return
	 */
	@RequestMapping("/idoc/addProject.html")
	@ResponseBody
	public  Map<String,Object> createProject(@RequestParam("projectName") String projectName,
		@RequestParam("productId") String  productId 
		){
		Map<String,Object> retMap = new HashMap<String,Object>();
		if(StringUtils.isBlank(projectName) || StringUtils.isBlank(productId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
       List<ProjectModel> projectModels=productAndProjectServiceImpl.selectProjectModelByProjectName(Long.valueOf(productId.trim()), projectName);
		if (projectModels!=null&&projectModels.size()>0) {
			retMap.put("retCode", 201);
			retMap.put("retDesc", "该项目已存在");
			return retMap;
		}
		else {
			try {
				
				ProjectModel projectModel = productAndProjectServiceImpl.addProject(Long.valueOf(productId), projectName.trim());
				String error = ErrorMessageUtil.getErrorMessages();
				if(StringUtils.isBlank(error)){
					retMap.put("retCode", 200);
					retMap.put("retDesc", "操作成功");
					retMap.put("projectModel", projectModel);
				}else{
					retMap.put("retCode", 402);
					retMap.put("retDesc", error);
					// 返回插入失败原因
				}
			} catch (Exception e) {
				LogConstant.runLog.error("异常错误",e);
				retMap.put("retCode", 500);
				retMap.put("retDesc", "内部错误");
				return retMap;
			}	
		}
       
		return retMap;
		
	}
	@RequestMapping("/idoc/updateProject.html")
	@ResponseBody
	public Map<String,Object> updateProject(@RequestParam("projectId")String projectId, @RequestParam("projectName")String projectName) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(projectId) || StringUtils.isBlank(projectName)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		try {
			Long id = Long.valueOf(projectId);
			productAndProjectServiceImpl.updateProject(id, projectName);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
			}else{
				retMap.put("retCode", 402);
				retMap.put("retDesc", error);
			}
		} catch (Exception e) {
			LogConstant.runLog.error("异常错误",e);
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
			return retMap;
		}
		return retMap;

	}
	
	@RequestMapping("/idoc/deleteProject.html")
	@ResponseBody
	public Map<String,Object> deleteProject(@RequestParam("projectId")String projectId) {
		
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(projectId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		try {
			Long id = Long.valueOf(projectId);
			productAndProjectServiceImpl.deleteProject(id);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
			}else{
				retMap.put("retCode", 402);
				retMap.put("retDesc", error);
			}
		} catch (Exception e) {
			LogConstant.runLog.error("异常错误",e);
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
			return retMap;
		}
		return retMap;

	}
	
	@RequestMapping("/idoc/getProductAllProject.html")
	@ResponseBody
	public  Map<String, Object> getProductAllProject(@RequestParam("projectName")String projectName,
		@RequestParam("status") int status,
		@RequestParam("pageNum") Integer pageNum,
		@RequestParam("perPage") Integer perPage,
		@RequestParam("productId") String productId
		
			){
		
		LogConstant.debugLog.info("status is "+status+"\n"+"perPage is "+perPage+"\n"+"projectName is "+projectName);
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(productId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		
		PaginationInfo paginationInfo = new PaginationInfo(pageNum, perPage);
		PaginationList<ProjectModel> projectModels=productAndProjectServiceImpl.queryProjectModelByPage(Long.valueOf(productId.trim()),status, projectName, paginationInfo);
		if (projectModels!= null&&projectModels.size()>0) {
			
			for (ProjectModel projectModel :projectModels) {
				List<Interface> interfaces=productAndProjectServiceImpl.selectInterfaceByProjectId(projectModel.getProjectId());
				
				int tempSubmitTest=0;
				int tempOnline=0;
				if (interfaces!=null) {
					for (Interface interface1 :interfaces) {
						if (interface1.getInterfaceStatus()==9) {
							tempOnline+=1;	
							
						}
						if (interface1.getInterfaceStatus()>=4) {
							tempSubmitTest+=1;
						 }
							
					}
				}
					projectModel.setOnlineNum(tempOnline);
					projectModel.setSubmitTestNum(tempSubmitTest);
					projectModel.setInterfaceNum(interfaces.size());
				}
				
			retMap.put("retCode", 200);
			retMap.put("retContent", projectModels);
			retMap.put("page", projectModels.getPaginationInfo());
		}
			
		 else {
			retMap.put("retCode", -1);
			retMap.put("retContent", "检索失败");
		}
		
		return retMap;
	}
	
    @RequestMapping(value = "/idoc/getProjectJsonTxt")  
    public ResponseEntity<byte[]> getProjectJsonTxt(HttpServletRequest request, HttpServletResponse response, @RequestParam("projectId") String projectId) throws UnsupportedEncodingException{  

		RapProject rapProject = productAndProjectServiceImpl.getRapProjectById(Long.parseLong(projectId));
		String projectJson = JsonUtil.BeanToJson(rapProject);
		byte[] flieByteStream = null;
		flieByteStream = projectJson.getBytes("UTF-8");
        HttpHeaders headers = new HttpHeaders();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String name = rapProject.getName() + "项目Json信息文档-" + sdf.format(new Date()) + ".txt";
        String fileName = new String(name.getBytes("UTF-8"),"iso-8859-1"); //为了解决中文名称乱码问题  
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        
        return new ResponseEntity<byte[]>(flieByteStream, headers, HttpStatus.CREATED);    
 
    }
    
    @RequestMapping(value = "/getProjectJson")  
    public void getProjectJson(HttpServletRequest request, HttpServletResponse response, @RequestParam("projectId") String projectId){  

		RapProject rapProject = productAndProjectServiceImpl.getRapProjectById(Long.parseLong(projectId));
		String projectJson = JsonUtil.BeanToJson(rapProject);
        response.setCharacterEncoding("UTF-8"); //设置编码格式
        response.setContentType("text/json");   //设置数据格式
        PrintWriter out = null;
		try {
			out = response.getWriter();
	        out.print(projectJson); //将json数据写入流中
	        out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if (out != null) {
                out.close();
            }
        }

    }
	
	@RequestMapping("/test.html")
	public String test(){
		
		return "/test";
	}
	
}
