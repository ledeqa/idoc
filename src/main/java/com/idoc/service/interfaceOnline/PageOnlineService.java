package com.idoc.service.interfaceOnline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.dao.docManage.OnlineModuleDaoImpl;
import com.idoc.dao.docManage.OnlinePageDaoImpl;
import com.idoc.model.OnlineModule;
import com.idoc.model.OnlinePage;
import com.idoc.util.ErrorMessageUtil;

@Service("pageOnlineService")
public class PageOnlineService {
	
	@Autowired
	private OnlinePageDaoImpl onlinePageDaoImpl;
	
	@Autowired
	private OnlineModuleDaoImpl onlineModuleDaoImpl;
	
	public OnlinePage addOnlinePage(Long onlineModuleId, String pageName){
		
		// 查询module是否存在，如果不存在报错
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("onlineModuleId", onlineModuleId);
		List<OnlineModule> mList = onlineModuleDaoImpl.selectOnlineModuleListByCond(map);
		if(CollectionUtils.isEmpty(mList)){
			ErrorMessageUtil.put("添加在线页面失败,不存在相关的模块");
			return null;
		}
		OnlinePage page = new OnlinePage();
		page.setOnlineModuleId(onlineModuleId);
		page.setPageName(pageName);
		page.setStatus(1);
		int num = onlinePageDaoImpl.insertOnlinePage(page);
		if(num == 0){
			ErrorMessageUtil.put("添加在线页面失败");
		}
		return page;
	}
	
	public List<OnlinePage> getPageListByModules(List<OnlineModule> modules) {
	
		if (CollectionUtils.isNotEmpty(modules)) {
			List<Long> moduleIds = new ArrayList<Long>();
			for (OnlineModule module : modules) {
				moduleIds.add(module.getOnlineModuleId());
			}
			
			List<OnlinePage> pageList = onlinePageDaoImpl.getPageListByModules(moduleIds);
			
			return pageList;
		}
		
		return null;
	}
	public int updatePage(Long onlinePageId, String pageName){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("onlinePageId", onlinePageId);
		map.put("pageName", pageName);
		int num = onlinePageDaoImpl.updateOnlinePage(map);
		if(num == 0){
			ErrorMessageUtil.put("更新在线页面失败");
		}
		return num;
	}
	public int deleteOnlinePage(Long onlinePageId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("onlinePageId", onlinePageId);	
		map.put("status", 0);
		int num =onlinePageDaoImpl.deleteOnlinePage(onlinePageId);
		if(num == 0){
			ErrorMessageUtil.put("删除在线页面失败");
		}
		return num;
	}
	
}
