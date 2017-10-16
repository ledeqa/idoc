package com.idoc.dao.code;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.idoc.code.CreateBean;
import com.idoc.code.def.TableColumn;
import com.idoc.constant.LogConstant;
import com.idoc.dao.BaseMysqlDBDaoImpl;

@Repository("codeDaoImpl")
public class CodeDaoImpl extends BaseMysqlDBDaoImpl {

	public List<String> getDatabaseTables(Map<String, Object> map){
		if(map == null || map.size() <= 0){
			return null;
		}
		String driver = (String) map.get("databaseDriver");
		String url = (String) map.get("databaseUrl");
		String username = (String) map.get("userName");
		String password = (String) map.get("password");
		//String database = (String) map.get("databaseName");
		CreateBean bean = new CreateBean();
		bean.setMysqlInfo(driver, url, username, password);
		List<String> list = null;
		try {
			list = bean.getTables();
		} catch (SQLException e) {
			LogConstant.runLog.info("查询数据库表出错！");
			e.printStackTrace();
		}
		return list;
	}
	
	public List<TableColumn> getTableColumns(Map<String, Object> map){
		if(map == null || map.size() <= 0){
			return null;
		}
		String driver = (String) map.get("databaseDriver");
		String url = (String) map.get("databaseUrl");
		String username = (String) map.get("userName");
		String password = (String) map.get("password");
		String tableName = (String) map.get("tableName");
		CreateBean bean = new CreateBean();
		bean.setMysqlInfo(driver, url, username, password);
		List<TableColumn> list = null;
		try {
			list = bean.getTableColumns(tableName);
		} catch (SQLException e) {
			LogConstant.runLog.info("查询数据库表出错！");
			e.printStackTrace();
		}
		return list;
	}
}