package com.idoc.service.ini.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.constant.LogConstant;
import com.idoc.dao.ini.IniConfigManagementDao;
import com.idoc.memcache.MemcacheServer;
import com.idoc.model.IniConfigModel;
import com.idoc.service.ini.IniConfigManagementService;

@Service("iniConfigManagementServiceImpl")
public class IniConfigManagementServiceImpl implements
		IniConfigManagementService {

	@Autowired
	private IniConfigManagementDao iniConfigManagementDaoImpl;
	
	@Autowired
	private MemcacheServer memcacheServer;
	
	@Override
	public List<IniConfigModel> selectIniConfigModel() {
		return iniConfigManagementDaoImpl.selectIniConfigModel();
	}

	@Override
	public int insertIniConfigModel(IniConfigModel iniConfigModel) {
		return iniConfigManagementDaoImpl.insertIniConfigModel(iniConfigModel);
	}

	@Override
	public int updateIniConfigModel(IniConfigModel iniConfigModel) {
		return iniConfigManagementDaoImpl.updateIniConfigModel(iniConfigModel);
	}

	@Override
	public int deleteIniConfigModel(IniConfigModel iniConfigModel) {
		return iniConfigManagementDaoImpl.deleteIniConfigModel(iniConfigModel);
	}

	@Override
	public boolean refresh() {
		List<IniConfigModel> iniList = iniConfigManagementDaoImpl
				.selectIniConfigModel();
		if (iniList == null || iniList.size() == 0) {
			LogConstant.debugLog.info("初始化失败，从数据库读取的配置信息为空。");
			return false;
		}
		for (IniConfigModel item : iniList) {
			if (item != null) {
				memcacheServer.setCacheData(item.getIniKey(), item.getIniValue());
				LogConstant.debugLog.info("初始化配置：" + item);
			}
		}
		LogConstant.debugLog.info("刷新初始化配置成功。");
		return true;
	}

	@Override
	public IniConfigModel selectIniConfigByIniKey(Map<String, Object> paramMap) {
		return iniConfigManagementDaoImpl.selectIniConfigByIniKey(paramMap);
	}

}