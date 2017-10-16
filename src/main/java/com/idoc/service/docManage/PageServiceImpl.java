package com.idoc.service.docManage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.dao.docManage.InterfaceDaoImpl;
import com.idoc.dao.docManage.ModuleDaoImpl;
import com.idoc.dao.docManage.PageDaoImpl;
import com.idoc.model.Interface;
import com.idoc.model.Module;
import com.idoc.model.Page;
import com.idoc.util.ErrorMessageUtil;

@Service("pageServiceImpl")
public class PageServiceImpl {
	
	@Autowired
	private ModuleDaoImpl moduleDaoImpl;
	
	@Autowired
	private PageDaoImpl pageDaoImpl;
	
	@Autowired
	private InterfaceDaoImpl interfaceDaoImpl;
	
	public Page addPage(Long moduleId, String pageName){
		
		// 查询module是否存在，如果不存在报错
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("moduleId", moduleId);
		List<Module> mList = moduleDaoImpl.selectModuleListByCond(map);
		if(CollectionUtils.isEmpty(mList)){
			ErrorMessageUtil.put("添加页面失败,不存在相关的模块");
			return null;
		}
		Page page = new Page();
		page.setModuleId(moduleId);
		page.setPageName(pageName);
		page.setStatus(1);
		int num = pageDaoImpl.insertPage(page);
		if(num == 0){
			ErrorMessageUtil.put("添加页面失败");
		}
		return page;
	}
	
	public int updatePage(Long pageId, String pageName){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("pageId", pageId);
		map.put("pageName", pageName);
		int num = pageDaoImpl.updatePage(map);
		if(num == 0){
			ErrorMessageUtil.put("更新页面失败");
		}
		return num;
	}
	
	public int deletePage(Long pageId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("pageId", pageId);
		// 查询页面下是否还有接口存在，如果存在不能删除
		List<Interface> iList = interfaceDaoImpl.selectInterfaceListByCond(map);
		if(iList != null && !iList.isEmpty()){
			ErrorMessageUtil.put("页面下还有接口存在不能删除，请删除接口后，在删除页面！");
			return -1;
		}
		map.put("status", 0);// 逻辑删除
		int num = pageDaoImpl.updatePage(map);
		if(num == 0){
			ErrorMessageUtil.put("删除页面失败");
		}
		return num;
	}
	
	public List<Page> getPageListByModules(List<Module> modules){
		if(CollectionUtils.isNotEmpty(modules)){
			List<Long> moduleIds = new ArrayList<Long>();
			for(Module module: modules){
				moduleIds.add(module.getModuleId());
			}
			
			List<Page> pageList = pageDaoImpl.getPageListByModules(moduleIds);
			
			return pageList;
		}
		
		return null;
	}
}
