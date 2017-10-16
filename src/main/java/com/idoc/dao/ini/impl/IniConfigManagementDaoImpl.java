package com.idoc.dao.ini.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.dao.BaseMysqlDBDaoImpl;
import com.idoc.dao.ini.IniConfigManagementDao;
import com.idoc.model.IniConfigModel;

@Repository
public class IniConfigManagementDaoImpl extends BaseMysqlDBDaoImpl implements
		IniConfigManagementDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<IniConfigModel> selectIniConfigModel() {
		return this.getSqlSession()
				.selectList("iniConfig.selectIniConfigModel");
	}

	@Override
	public int insertIniConfigModel(IniConfigModel iniConfigModel) {
		return this.getSqlSession().insert("iniConfig.insertIniConfigModel",
				iniConfigModel);
	}

	@Override
	public int updateIniConfigModel(IniConfigModel iniConfigModel) {
		return this.getSqlSession().update("iniConfig.updateIniConfigModel",
				iniConfigModel);
	}

	@Override
	public int deleteIniConfigModel(IniConfigModel iniConfigModel) {
		return this.getSqlSession().delete("iniConfig.deleteIniConfigModel",
				iniConfigModel);
	}

	@Override
	public IniConfigModel selectIniConfigByIniKey(Map<String, Object> paramMap) {
		return (IniConfigModel) this.getSqlSession().selectOne(
				"iniConfig.selectIniConfigByIniKey", paramMap);
	}

}