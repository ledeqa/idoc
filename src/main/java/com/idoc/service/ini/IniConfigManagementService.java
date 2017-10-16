package com.idoc.service.ini;

import java.util.List;
import java.util.Map;

import com.idoc.model.IniConfigModel;

public interface IniConfigManagementService {
	
	List<IniConfigModel> selectIniConfigModel();
	
	IniConfigModel selectIniConfigByIniKey(Map<String, Object> paramMap);

	int insertIniConfigModel(IniConfigModel iniConfigModel);

	int updateIniConfigModel(IniConfigModel iniConfigModel);

	int deleteIniConfigModel(IniConfigModel iniConfigModel);

	boolean refresh();
	
}