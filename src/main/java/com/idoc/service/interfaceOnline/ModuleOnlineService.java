package com.idoc.service.interfaceOnline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.idoc.dao.docManage.OnlineModuleDaoImpl;
import com.idoc.dao.docManage.OnlinePageDaoImpl;
import com.idoc.model.InterfaceOnline;
import com.idoc.model.OnlineModule;
import com.idoc.model.OnlinePage;
import com.idoc.util.ErrorMessageUtil;

@Repository("moduleOnlineService")
public class ModuleOnlineService {
	
	@Autowired
	private OnlineModuleDaoImpl onlineModuleDaoImpl;
	@Autowired
	private OnlinePageDaoImpl onlinePageDaoImpl;
	@Autowired
	private PageOnlineService pageOnlineService;
	@Autowired
	private InterfaceOnlineService interfaceOnlineService;
	
	public OnlineModule addOnlineModule(Long productId, String moduleName){
		
		// 查询项目是否存在，如果不存在报错 TODO
		OnlineModule module = new OnlineModule();
		module.setModuleName(moduleName);
		module.setProductId(productId);
		module.setStatus(1);
		int num = onlineModuleDaoImpl.insertOnlineModule(module);
		if(num == 0){
			ErrorMessageUtil.put("添加在线模块失败");
		}
		return module;
	}

	public int updateOnlineModule(Long onlineModuleId, String moduleName) {
	
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("onlineModuleId", onlineModuleId);
		map.put("moduleName", moduleName);
		int num = onlineModuleDaoImpl.updateOnlineModule(map);
		if (num == 0) {
			ErrorMessageUtil.put("更新在线模块失败");
		}
		return num;
	}
	
	/**
	 * 删除模块
	 */
	public int deleteOnlineModule(Long onlineModuleId) {
	
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("onlineModuleId", onlineModuleId);
		// 查询模块下是否还有页面存在，如果存在不能删除
		List<OnlinePage> pageList = onlinePageDaoImpl.selectOnlinePageListByCond(map);
		if (pageList != null && !pageList.isEmpty()) {
			ErrorMessageUtil.put("模块下还有页面存在不能删除，请删除页面后，再删除模块！");
			return -1;
		}
		map.put("status", 0);// 逻辑删除
		int num = onlineModuleDaoImpl.updateOnlineModule(map);
		if (num == 0) {
			ErrorMessageUtil.put("删除模块失败");
		}
		return num;
	}
	
	/**
	 * 根据项目id获取项目下所有的模块
	 * 
	 * @param projectId
	 */
	public List<OnlineModule> getModuleListByProductId(String projectId) {
	
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("productId", projectId);
		List<OnlineModule> moduleList = onlineModuleDaoImpl.selectOnlineModuleListByCond(map);
		return moduleList;
	}
	
	public List<OnlineModule> getOnlineModuleListNoInterByProductId(String productId) {
	
		List<OnlineModule> moduleList = this.getModuleListByProductId(productId);
		if (CollectionUtils.isEmpty(moduleList)) {
			return moduleList;
		}
		
		List<OnlinePage> pageList = pageOnlineService.getPageListByModules(moduleList);
		if (CollectionUtils.isEmpty(pageList)) {
			return moduleList;
		}
		
		// 把Page放入对应的OnlineModule对象中
		for (OnlinePage page : pageList) {
			for (OnlineModule module : moduleList) {
				if (page.getOnlineModuleId().equals(module.getOnlineModuleId())) {
					if (module.getPageList() == null) {
						module.setPageList(new ArrayList<OnlinePage>());
					}
					
					module.getPageList().add(page);
				}
			}
		}
		
		return moduleList;
	}
	
	public List<OnlineModule> getFullOnlineModules(String productId) {
		/* 先简单修改，等待确认
		List<ProjectModel> projectModels = projectDaoImpl.selectOnlineProjectListWithProduct(productId);
		List<OnlineModule> onlineModules = new ArrayList<OnlineModule>();
		if (projectModels != null && projectModels.size() > 0) {
			for (ProjectModel projectModel : projectModels) {
				String projectName = projectModel.getProjectName();
				List<OnlineModule> onlineModulesTemp = getFullModuleInfoListByProjectId(String.valueOf(projectModel.getProductId()));				
				if (onlineModulesTemp != null && onlineModulesTemp.size() > 0) {
					for (OnlineModule onlineModule :onlineModulesTemp ) {
						onlineModules.add(onlineModule)	;
					}
					
				}
			}
		}
		return onlineModules;
		*/
		List<OnlineModule> onlineModules = new ArrayList<OnlineModule>();
		List<OnlineModule> onlineModulesTemp = getFullModuleInfoListByProjectId(productId);				
		if (onlineModulesTemp != null && onlineModulesTemp.size() > 0) {
			for (OnlineModule onlineModule :onlineModulesTemp ) {
				onlineModules.add(onlineModule)	;
			}
		}
		return onlineModules;
	}
	
	/**
	 * 获取模块的完整信息，即首先获取项目下的模块，然后获取模块下的页面，再获取页面下的接口
	 * 
	 * @param projectId
	 * @return
	 */
	public List<OnlineModule> getFullModuleInfoListByProjectId(String projectId) {
	
		List<OnlineModule> moduleList = this.getOnlineModuleListNoInterByProductId(projectId);
		if (CollectionUtils.isEmpty(moduleList)) {
			return moduleList;
		}
		
		List<OnlinePage> pageList = pageOnlineService.getPageListByModules(moduleList);
		if (CollectionUtils.isEmpty(pageList)) {
			return moduleList;
		}
		
		List<InterfaceOnline> interfaceList = interfaceOnlineService.getInterfaceListByPages(pageList);
		// 把接口放入对应的Page对象中
		if (CollectionUtils.isNotEmpty(interfaceList)) {
			for(OnlinePage page : pageList){
				for(InterfaceOnline inter : interfaceList){
					if(page.getOnlinePageId().longValue() == inter.getOnlinePageId().longValue()){
						if(page.getInterfaceList() == null){
							page.setInterfaceList(new ArrayList<InterfaceOnline>());
							page.getInterfaceList().add(inter);
						}else{
							int len = page.getInterfaceList().size();
							boolean contains = false;
							for(int i=0; i<len; i++){
								InterfaceOnline ifo = page.getInterfaceList().get(i);
								if(ifo.getInterfaceId().longValue() == inter.getInterfaceId().longValue()){
									page.getInterfaceList().set(i, inter);
									contains = true;
									break;
								}
							}
							if(!contains){
								page.getInterfaceList().add(inter);
							}
						}
					}
				}
			}
			/*for (InterfaceOnline inter : interfaceList) {
				for (OnlinePage page : pageList) {
					if (inter.getOnlinePageId().longValue() == page.getOnlinePageId().longValue()) {
						if (page.getInterfaceList() == null) {
							page.setInterfaceList(new ArrayList<InterfaceOnline>());
							page.getInterfaceList().add(inter);
						}else{
							int len = page.getInterfaceList().size();
							for(int i=0; i<len; i++){
								InterfaceOnline ifo = page.getInterfaceList().get(i);
								if(ifo.getInterfaceId().longValue() == inter.getInterfaceId().longValue()){
									page.getInterfaceList().set(i, inter);
								}else{
									page.getInterfaceList().add(inter);
								}
							}
						}
						
						break;
					}
				}
			}*/
		}
		// 把Page放入对应的Module对象中
		for(OnlineModule module : moduleList){
			for(OnlinePage page : pageList){
				if(page.getOnlineModuleId().longValue() == module.getOnlineModuleId().longValue()){
					if(module.getPageList() == null){
						module.setPageList(new ArrayList<OnlinePage>());
						module.getPageList().add(page);
					}else{
						int len = module.getPageList().size();
						boolean contains = false;
						for(int i=0; i<len; i++){
							OnlinePage p = module.getPageList().get(i);
							if(p.getOnlinePageId().longValue() == page.getOnlinePageId().longValue()){
								module.getPageList().set(i, page);
								contains = true;
								break;
							}
						}
						if(!contains){
							module.getPageList().add(page);
						}
					}
				}
			}
		}
		/*for (OnlineModule module : moduleList) {
			for (OnlinePage page : pageList) {
				if (page.getOnlineModuleId().longValue() == module.getOnlineModuleId().longValue()) {
					if (module.getPageList() == null) {
						module.setPageList(new ArrayList<OnlinePage>());
						module.getPageList().add(page);
					}else{
						int len = module.getPageList().size();
						for(int i=0; i<len; i++){
							OnlinePage p = module.getPageList().get(i);
							if(p.getOnlinePageId().longValue() == page.getOnlinePageId().longValue()){
								module.getPageList().set(i, page);
							}else{
								module.getPageList().add(page);
							}
						}
					}
				}
			}
		}*/
		
		return moduleList;
	}
}