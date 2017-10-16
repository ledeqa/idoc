package com.idoc.dao.pagination.dialect;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * mysql dialect接口实现，提供生成分页和总和的sql
 * 
 */
@Component("mysqlDialect")
public class MysqlDialect implements Dialect {
	
	protected static final String SQL_END_DELIMITER = ";";
	
	/**
	 * 是否支持物理分页
	 * 
	 * @return
	 */
	public boolean supportsLimit() {

		return true;
	}
	
	/**
	 * 是否支持物理分页偏移量
	 * 
	 * @return
	 */
	public boolean supportsLimitOffset() {

		return true;
	}
	
	/**
	 * 分页查询
	 * 
	 * @param sql
	 * @param hasOffset
	 * @return
	 */
	public String getLimitString(String sql, boolean hasOffset) {

		return null;
	}
	
	/**
	 * 分页查询
	 * 
	 * @param sql
	 * @param offset
	 * @param limit
	 * @return
	 */
	public String getLimitString(String sql, int offset, int limit) {

		Assert.isTrue(offset >= 0, "offset < 0 is not permited.");
		
		StringBuffer pageStr = new StringBuffer();
		pageStr.append(this.trim(sql));
		pageStr.append(" limit " + offset + ", " + limit);
		return pageStr.toString();
	}
	
	/**
	 * 生成查询总和的sql
	 * 
	 * @param sql
	 * @return
	 */
	public String getCountString(String sql) {

		String resultSql = "select count(1) " + sql.substring(StringUtils.indexOfIgnoreCase(sql, "from"), sql.length());
		if (resultSql.toLowerCase().lastIndexOf("order by") != -1) {
			resultSql = resultSql.substring(0, resultSql.toLowerCase().lastIndexOf("order by"));
		}
		return resultSql;
	}
	
	private String trim(String sql) {

		sql = sql.trim();
		if (sql.endsWith(SQL_END_DELIMITER)) {
			sql = sql.substring(0, sql.length() - SQL_END_DELIMITER.length());
		}
		return sql;
	}
	
	public static void main(String[] args) {

		MysqlDialect od = new MysqlDialect();
		System.out.println(od.getLimitString("select * from tb_ini", 0, 10));
		System.out.println(od.getCountString("select * from tb_ini where a > 1"));
	}
}
