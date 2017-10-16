package com.idoc.service.docManage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.dao.docManage.ModuleDaoImpl;
import com.idoc.dao.docManage.PageDaoImpl;
import com.idoc.model.Interface;
import com.idoc.model.Module;
import com.idoc.model.Page;
import com.idoc.model.ProjectModel;
import com.idoc.model.ProjectModuleModel;
import com.idoc.service.productAndProject.ProductAndProjectServiceImpl;
import com.idoc.util.ErrorMessageUtil;

@Service("moduleServiceImpl")
public class ModuleServiceImpl {
	
	@Autowired
	private ModuleDaoImpl moduleDaoImpl;
	
	@Autowired
	private PageDaoImpl pageDaoImpl;
	
	@Autowired
	private PageServiceImpl pageServiceImpl;
	
	@Autowired
	private InterfaceServiceImpl interfaceServiceImpl;
	
	@Autowired
	private ProductAndProjectServiceImpl productAndProjectServiceImpl;
	
	public Module addModule(Long projectId, String moduleName){
		
		List<Module> moduleList = getModuleListByProjectIdAndModuleName(projectId, moduleName);
		if(moduleList.size()>0){
			ErrorMessageUtil.put("此模块名称已经存在！");
			return null;
		}
		// 查询项目是否存在，如果不存在报错 TODO
		Module module = new Module();
		module.setModuleName(moduleName);
		module.setProjectId(projectId);
		module.setStatus(1);
		int num = moduleDaoImpl.insertModule(module);
		if(num == 0){
			ErrorMessageUtil.put("添加模块失败");
		}
		return module;
	}
	
	public int updateModule(Long moduleId, String moduleName){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("moduleId", moduleId);
		map.put("moduleName", moduleName);
		int num = moduleDaoImpl.updateModule(map);
		if(num == 0){
			ErrorMessageUtil.put("更新模块失败");
		}
		return num;
	}
	
	public int deleteModule(Long moduleId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("moduleId", moduleId);
		// 查询模块下是否还有页面存在，如果存在不能删除
		List<Page> pageList = pageDaoImpl.selectPageListByModuleId(map);
		if(pageList != null && !pageList.isEmpty()){
			ErrorMessageUtil.put("模块下还有页面存在不能删除，请删除页面后，在删除模块！");
			return -1;
		}
		map.put("status", 0);// 逻辑删除
		int num = moduleDaoImpl.updateModule(map);
		if(num == 0){
			ErrorMessageUtil.put("删除模块失败");
		}
		return num;
	}
	
	/**
	 * 根据项目id和模块名称获取模块
	 */
	public List<Module> getModuleListByProjectIdAndModuleName(Long projectId,String moduleName){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("projectId", projectId);
		map.put("moduleName", moduleName);
		List<Module> moduleList = moduleDaoImpl.selectModuleListByProjectIdAndModuleName(map);
		return moduleList;
	}
	
	/**
	 * 根据项目id获取项目下所有的模块
	 * @param projectId
	 * @return
	 */
	public List<Module> getModuleListByProjectId(String projectId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("projectId", projectId);
		
		List<Module> moduleList = moduleDaoImpl.selectModuleListByCond(map);
		
		return moduleList;
	}
	
	/**
	 * 获取模块的完整信息，即首先获取项目下的模块，然后获取模块下的页面，再获取页面下的接口
	 * @param projectId
	 * @return
	 */
	public List<Module> getFullModuleInfoListByProjectId(String projectId){
		List<Module> moduleList = this.getModuleListByProjectId(projectId);
		if(CollectionUtils.isEmpty(moduleList)){
			return moduleList;
		}
		
		List<Page> pageList = pageServiceImpl.getPageListByModules(moduleList);
		if(CollectionUtils.isEmpty(pageList)){
			return moduleList;
		}
		
		
		List<Interface> interfaceList = interfaceServiceImpl.getInterfaceListByPages(pageList);
		//把接口放入对应的Page对象中
		if(CollectionUtils.isNotEmpty(interfaceList)){
			for(Interface inter: interfaceList){
				for(Page page: pageList){
					if(inter.getPageId().equals(page.getPageId())){
						if(page.getInterfaceList() == null){
							page.setInterfaceList(new ArrayList<Interface>());
						}
						
						page.getInterfaceList().add(inter);
						break;
					}
				}
			}
		}
		//把Page放入对应的Module对象中
		for(Page page: pageList){
			for(Module module: moduleList){
				if(page.getModuleId().equals(module.getModuleId())){
					if(module.getPageList() == null){
						module.setPageList(new ArrayList<Page>());
					}
					
					module.getPageList().add(page);
				}
			}
		}
		
		return moduleList;
	}
	
	public List<Module> getModuleListNoInterByProjectId(String projectId){
		List<Module> moduleList = this.getModuleListByProjectId(projectId);
		if(CollectionUtils.isEmpty(moduleList)){
			return moduleList;
		}
		
		List<Page> pageList = pageServiceImpl.getPageListByModules(moduleList);
		if(CollectionUtils.isEmpty(pageList)){
			return moduleList;
		}
		
		//把Page放入对应的Module对象中
		for(Page page: pageList){
			for(Module module: moduleList){
				if(page.getModuleId().equals(module.getModuleId())){
					if(module.getPageList() == null){
						module.setPageList(new ArrayList<Page>());
					}
					
					module.getPageList().add(page);
				}
			}
		}
		
		return moduleList;
	}
	
	public List<ProjectModuleModel> getProjectModuleListByProductId(String productId){
		
		List<ProjectModel> projectList = productAndProjectServiceImpl.selectProjectListWithProduct(productId);
		List<ProjectModuleModel> projectModuleList = new ArrayList<ProjectModuleModel>();

		if(CollectionUtils.isEmpty(projectList)){
			return projectModuleList;
		}
		
		for(ProjectModel projectModel : projectList){
			ProjectModuleModel projectModuleModel = new ProjectModuleModel();
			projectModuleModel.setProjectId(projectModel.getProjectId());
			projectModuleModel.setProductId(projectModel.getProductId());
			projectModuleModel.setProjectName(projectModel.getProjectName());
			projectModuleModel.setCreateTime(projectModel.getCreateTime());
			projectModuleModel.setUpdateTime(projectModel.getUpdateTime());
			projectModuleModel.setInterfaceNum(projectModel.getInterfaceNum());
			projectModuleModel.setSubmitTestNum(projectModel.getSubmitTestNum());
			projectModuleModel.setOnlineNum(projectModel.getOnlineNum());
			projectModuleModel.setStatus(projectModel.getStatus());

			List<Module> moduleList = getModuleListNoInterByProjectId(projectModel.getProjectId().toString());
			projectModuleModel.setModuleList(moduleList);
			
			projectModuleList.add(projectModuleModel);
		}
		
		return projectModuleList;
	}
}
