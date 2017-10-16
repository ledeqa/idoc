package com.idoc.service.productAndProject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idoc.dao.docManage.InterfaceDaoImpl;
import com.idoc.dao.docManage.ModuleDaoImpl;
import com.idoc.dao.docManage.PageDaoImpl;
import com.idoc.dao.docManage.ParamDaoImpl;
import com.idoc.dao.pagination.PaginationInfo;
import com.idoc.dao.pagination.PaginationList;
import com.idoc.dao.productAndProject.ProjectDaoImpl;
import com.idoc.model.Interface;
import com.idoc.model.Module;
import com.idoc.model.Page;
import com.idoc.model.Param;
import com.idoc.model.ProductModel;
import com.idoc.model.ProjectModel;
import com.idoc.model.rap.RapAction;
import com.idoc.model.rap.RapModule;
import com.idoc.model.rap.RapPage;
import com.idoc.model.rap.RapParameter;
import com.idoc.model.rap.RapProject;
import com.idoc.service.dict.DictServiceImpl;
import com.idoc.util.ErrorMessageUtil;

@Service("productAndProjectServiceImpl")
public class ProductAndProjectServiceImpl {
	
	@Autowired
	private ProjectDaoImpl projectDaoImpl;
	
	@Autowired
	private ModuleDaoImpl moduleDaoImpl;
	
	@Autowired
	private PageDaoImpl pageDaoImpl;
	
	@Autowired
	private InterfaceDaoImpl interfaceDaoImpl;
	
	@Autowired
	private ParamDaoImpl paramDaoImpl;
	
	@Autowired
	private DictServiceImpl dictServiceImpl;
	
	@Transactional
	public ProjectModel addProject(Long productId, String projectName) {
	
		// 查询项目是否存在，如果不存在报错 TODO
		ProjectModel projectModel = new ProjectModel();
		projectModel.setProductId(productId);
		projectModel.setProjectName(projectName);
		projectModel.setStatus(1);
		int num = projectDaoImpl.insertTB_IDOC_PROJECT(projectModel);
		
		//插入默认模块
		Module module = new Module();
		module.setModuleName("新建模块");
		module.setProjectId(projectModel.getProjectId());
		module.setStatus(1);
		
		moduleDaoImpl.insertModule(module);
		
		//插入默认页面
		Page page = new Page();
		page.setPageName("新建页面");
		page.setModuleId(module.getModuleId());
		page.setStatus(1);

		pageDaoImpl.insertPage(page);
		
		if (num == 0) {
			ErrorMessageUtil.put("添加项目失败");
		}
		return projectModel;
	}
	
	public int updateProject(Long projectId, String projectName) {
	
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("projectId", projectId);
		map.put("projectName", projectName);
		int num = projectDaoImpl.updateProject(map);
		if (num == 0) {
			ErrorMessageUtil.put("更新项目失败");
		}
		return num;
	}
	
	public int deleteProject(Long projectId) {
	
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("projectId", projectId);
		map.put("status", 0);// 逻辑删除
		int num = projectDaoImpl.updateProject(map);
		if (num == 0) {
			ErrorMessageUtil.put("删除项目失败");
		}
		return num;
	}
	
	public ProductModel addProduct( String productName, String productDesc, String productDomainUrl) {
	
		// 查询产品是否存在，如果不存在报错 TODO
		ProductModel productModel = new ProductModel();
		productModel.setProductName(productName);
		productModel.setProductDesc(productDesc);
		productModel.setProductDomainUrl(productDomainUrl);
		productModel.setStatus(1);
		int num = projectDaoImpl.insertTB_IDOC_PRODUCT(productModel);
		if (num == 0) {
			ErrorMessageUtil.put("添加产品失败");
		}
		return productModel;
	}
	
public int updateProduct(Long productId, String productName,String productDesc, String productDomainUrl,Integer productFlow) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("productId", productId);
		map.put("productName", productName);
		map.put("productDesc", productDesc);
		map.put("productDomainUrl", productDomainUrl);
		map.put("productFlow", productFlow);
		int num = projectDaoImpl.updateProduct(map);
		if (num == 0) {
			ErrorMessageUtil.put("更新产品失败");
		}
		return num;
	}
	
	public int deleteProduct(Long productId) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("productId", productId);
		map.put("status", 0);// 逻辑删除
		int num = projectDaoImpl.updateProduct(map);
		if (num == 0) {
			ErrorMessageUtil.put("删除产品失败");
		}
		return num;
	}
	
	public List<ProjectModel> selectProjectListWithProduct(String productId) {
		
		return projectDaoImpl.selectProjectListWithProduct(productId);
	}
	public ProductModel selectFromProductByProductName(String productName){
		
		return  projectDaoImpl.selectFromProductByProductName(productName);
		
	}
	public ProductModel queryProductModelByProductId(Long productId){
		return projectDaoImpl.queryProductModelByProductId(productId);
	}
	public PaginationList<ProjectModel> queryProjectModelByPage(Long productId,int status, String projectName, PaginationInfo paginationInfo){
		return projectDaoImpl.queryProjectModelByPage(productId,status, projectName, paginationInfo);
	}
	
	public List<ProjectModel> selectProjectModelByProjectName(Long productId,String projectName){
		return projectDaoImpl.selectProjectModelByProjectName(productId, projectName);
	}
	
	public ProjectModel selectProjectModelByProjectId(Long projectId){
		return projectDaoImpl.selectProjectModelByProjectId(projectId);
	}
	
	public List<Interface> selectInterfaceByProjectId(Long projectId){
		return projectDaoImpl.selectInterfaceByProjectId(projectId);
	}

	public RapProject getRapProjectById(Long projectId) {
		RapProject rapProject = new RapProject();
		ProjectModel projectModel = selectProjectModelByProjectId(projectId);
		rapProject.setId(projectModel.getProductId());
		rapProject.setName(projectModel.getProjectName());
		rapProject.setCreateDateStr(projectModel.getCreateTime().toString().substring(0, 10));
		
		List<RapModule> rapModuleList = new ArrayList<RapModule>();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("projectId", projectId);
		List<Module> moduleList = moduleDaoImpl.selectModuleListByCond(map);
		if(moduleList != null && !moduleList.isEmpty()){
			for(Module module : moduleList){
				RapModule rapModule = new RapModule();
				rapModule.setId(module.getModuleId());
				rapModule.setName(module.getModuleName());
				
				List<RapPage> rapPageList = new ArrayList<RapPage>();
				Map<String,Object> map1 = new HashMap<String,Object>();
				map1.put("moduleId", module.getModuleId());
				List<Page> pageList = pageDaoImpl.selectPageListByModuleId(map1);
				if(pageList != null && !pageList.isEmpty()){
					for(Page page : pageList){
						RapPage rapPage = new RapPage();
						rapPage.setId(page.getPageId());
						rapPage.setName(page.getPageName());
						
						List<RapAction> rapActionList = new ArrayList<RapAction>();
						Map<String,Object> map2 = new HashMap<String,Object>();
						map2.put("pageId", page.getPageId());
						List<Interface> interfaceList = interfaceDaoImpl.selectInterfaceListByCond(map2);
						if(interfaceList != null && !interfaceList.isEmpty()){
							for(Interface interf : interfaceList){
								RapAction rapAction = new RapAction();
								rapAction.setId(interf.getInterfaceId());
								rapAction.setName(interf.getInterfaceName());
								rapAction.setDescription(interf.getDesc());
								rapAction.setRequestType(interf.getRequestType()==1 ? "0":"1");
								rapAction.setRequestUrl(interf.getUrl());
								
								List<RapParameter> requestParameterList = new ArrayList<RapParameter>();
								// 设置请求参数信息
								List<Param> reqList = paramDaoImpl.selectRequestParamByInterfaceId(interf.getInterfaceId());
								if(reqList != null && !reqList.isEmpty()){
									for(Param pm: reqList){
										RapParameter rapParameter = new RapParameter();
										if(pm.getDictId() != null){
											pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
										}
										
										rapParameter = setRapParameterInfo(rapParameter, pm);
										requestParameterList.add(rapParameter);
									}
								}
								
								rapAction.setRequestParameterList(requestParameterList);
								
							
								List<RapParameter> responseParameterList = new ArrayList<RapParameter>();
								// 设置返回参数信息
								List<Param> resList = paramDaoImpl.selectReturnParamByInterfaceId(interf.getInterfaceId());
								if(resList != null && !resList.isEmpty()){
									for(Param pm: resList){
										RapParameter rapParameter = new RapParameter();
										if(pm.getDictId() != null){
											pm.setDict(dictServiceImpl.getDict(pm.getDictId()));
										}
										
										rapParameter = setRapParameterInfo(rapParameter, pm);
										responseParameterList.add(rapParameter);
									}
								}
								
								rapAction.setResponseParameterList(responseParameterList);
								
								rapActionList.add(rapAction);
							}
							
						}
						rapPage.setActionList(rapActionList);
						rapPageList.add(rapPage);
					}
				}
				rapModule.setPageList(rapPageList);
				rapModuleList.add(rapModule);
			}
		}
		rapProject.setModuleList(rapModuleList);
		
		return rapProject;
	}
	
	public RapParameter setRapParameterInfo(RapParameter rapParameter,Param pm){
		
		if(pm.getDictId() != null){
			rapParameter.setId(pm.getParamId());
			if(pm.getParamType().contains("array<"))
				rapParameter.setDataType("array<object>");
			else
				rapParameter.setDataType("object");
			rapParameter.setRemark(pm.getRemark());
			rapParameter.setName(pm.getParamDesc());
			rapParameter.setIdentifier(pm.getParamName());
			
			List<RapParameter> parameterList = new ArrayList<RapParameter>();
			if(pm.getDict() != null){
				List<Param> params = pm.getDict().getParams();
				for(Param pm1: params){
					RapParameter rapParameter1 = new RapParameter();
					rapParameter1 = setRapParameterInfo(rapParameter1, pm1);
					parameterList.add(rapParameter1);
				}
			}
			
			rapParameter.setParameterList(parameterList);
			
		}else{
			rapParameter.setId(pm.getParamId());
			rapParameter.setDataType(pm.getParamType());
			rapParameter.setRemark(pm.getRemark());
			rapParameter.setName(pm.getParamDesc());
			rapParameter.setIdentifier(pm.getParamName());
		}
		
		return rapParameter;
	}
	public Integer getProductFlowById(Long productId) {
		return projectDaoImpl.getProductFlowById(productId);
	}
}
