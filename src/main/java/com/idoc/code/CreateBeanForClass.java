package com.idoc.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.idoc.code.def.CommUtil;

/**
 * use for class generate code
 * */
public class CreateBeanForClass {
	
	private String tableName;
	private List<ColumnData> columnDatas;
	
	public CreateBeanForClass(String tableName, String params) {
		this.tableName = tableName;
		columnDatas = new ArrayList<ColumnData>();
		if(params != null && !params.equals("")) {
			String[] columns = params.split(",");
			for(String column : columns) {
				String[] param = column.split(":");
				String name = param[0];
				String type = param[1];
				String comment = param[2];
				String isPri = param[3];
				ColumnData data = new ColumnData();
				data.setColumnName(name);
				data.setDataType(type);
				data.setColumnComment(comment);
				columnDatas.add(data);
			}
		}
	}
	
	public List<ColumnData> getColumnDatas() {
		return columnDatas;
	}

	public String getBeanFeilds() {
		StringBuffer str = new StringBuffer();
		StringBuffer getset = new StringBuffer();
		for (ColumnData d : columnDatas)
		{
			String name = d.getColumnName();
			String type = d.getDataType();
			String comment = d.getColumnComment();
			
			String maxChar = name.substring(0, 1).toUpperCase();
			if(comment != null && !comment.equals("")) {
				str.append("\r\t").append("/**");
				str.append("\r\t").append(" * ").append(comment);
				str.append("\r\t").append(" */");
			}
			str.append("\r\t").append("private ").append(type + " ").append(name).append(";");
			String method = maxChar + name.substring(1, name.length());
			getset.append("\r\t").append("public ").append(type + " ").append("get" + method + "() {\r\t");
			getset.append("    return this.").append(name).append(";\r\t}");
			getset.append("\r\t").append("public void ").append("set" + method + "(" + type + " " + name + ") {\r\t");
			getset.append("    this." + name + "=").append(name).append(";\r\t}");
		}
		return str.toString() + getset.toString();
	}
	
	public Map<String, Object> getAutoCreateSql() {
		Map<String, Object> sqlMap = new HashMap<String, Object>();
		String columns = getColumnSplit(columnDatas);
		String formatColumns = getFormatColumnSplit(columnDatas);
		String[] columnList = getColumnList(columns);
		String columnFields = getColumnFields(columns);
		String insert = "insert into " + tableName + "(" + columns.replaceAll("\\|", ",") + ")\n values(#{"
				+ formatColumns.replaceAll("\\|", "},#{") + "})";
		String update = getUpdateSql(tableName, columnList);
		String updateSelective = getUpdateSelectiveSql(tableName, columnDatas);
		String selectById = getSelectSql(tableName, columnList);
		String delete = getDeleteSql(tableName, columnList);
		sqlMap.put("columnList", columnList);
		sqlMap.put("columnFields", columnFields);
		sqlMap.put("insert", insert.replace("#{createTime}", "now()").replace("#{updateTime}", "now()"));
		sqlMap.put("update", update.replace("#{createTime}", "now()").replace("#{updateTime}", "now()"));
		sqlMap.put("delete", delete);
		sqlMap.put("updateSelective", updateSelective);
		sqlMap.put("select", selectById);
		return sqlMap;
	}

	private String getDeleteSql(String tableName, String[] columnsList) {
		StringBuffer sb = new StringBuffer();
		sb.append("delete ");
		sb.append("\t from ").append(tableName).append(" where ");
		sb.append(columnsList[0]).append(" = #{").append(CommUtil.formatName(columnsList[0])).append("}");
		return sb.toString();
	}

	private String getSelectSql(String tableName, String[] columnList) {
		StringBuffer sb = new StringBuffer();
		sb.append("select <include refid=\"Base_Column_List\" /> \n");
		sb.append("\t from ").append(tableName);
		return sb.toString();
	}

	private String getUpdateSelectiveSql(String tableName, List<ColumnData> columnList) {
		StringBuffer sb = new StringBuffer();
		ColumnData cd = (ColumnData) columnList.get(0);
		sb.append("\t<trim  suffixOverrides=\",\" >\n");
		for (int i = 1; i < columnList.size(); i++)
		{
			ColumnData data = (ColumnData) columnList.get(i);
			String columnName = data.getColumnName();
			sb.append("\t<if test=\"").append(CommUtil.formatName(columnName)).append(" != null ");

			if ("String" == data.getDataType())
			{
				sb.append(" and ").append(CommUtil.formatName(columnName)).append(" != ''");
			}
			sb.append(" \">\n\t\t");
			sb.append(columnName + "=#{" + CommUtil.formatName(columnName) + "},\n");
			sb.append("\t</if>\n");
		}
		sb.append("\t</trim>");
		String update = "update " + tableName + " set \n" + sb.toString() + " where " + cd.getColumnName() + "=#{"
				+ CommUtil.formatName(cd.getColumnName()) + "}";
		return update;
	}

	private String getUpdateSql(String tableName, String[] columnsList) {
		StringBuffer sb = new StringBuffer();

		for (int i = 1; i < columnsList.length; i++)
		{
			String column = columnsList[i];
			if (!"CREATETIME".equals(column.toUpperCase()))
			{
				if ("UPDATETIME".equals(column.toUpperCase()))
					sb.append(column + "=now()");
				else
				{
					sb.append(column + "=#{" + CommUtil.formatName(column) + "}");
				}
				if (i + 1 < columnsList.length)
					sb.append(",");
			}
		}
		String update = "update " + tableName + " set " + sb.toString() + " where " + columnsList[0] + "=#{"
				+ CommUtil.formatName(columnsList[0]) + "}";
		return update;
	}

	private String getColumnFields(String columns) {
		String fields = columns;
		if ((fields != null) && (!"".equals(fields)))
		{
			fields = fields.replaceAll("[|]", ",");
		}
		return fields;
	}

	private String[] getColumnList(String columns) {
		String[] columnList = columns.split("[|]");
		return columnList;
	}

	private String getFormatColumnSplit(List<ColumnData> columnDatas) {
		StringBuffer commonColumns = new StringBuffer();
		for (ColumnData data : columnDatas)
		{
			commonColumns.append(data.getFormatColumnName() + "|");
		}
		return commonColumns.delete(commonColumns.length() - 1, commonColumns.length()).toString();
	}

	private String getColumnSplit(List<ColumnData> columnDatas) {
		StringBuffer commonColumns = new StringBuffer();
		for (ColumnData data : columnDatas)
		{
			commonColumns.append(data.getColumnName() + "|");
		}
		return commonColumns.delete(commonColumns.length() - 1, commonColumns.length()).toString();
	}
	
}
