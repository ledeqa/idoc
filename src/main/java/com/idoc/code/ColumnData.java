package com.idoc.code;

import com.idoc.code.def.CommUtil;

public class ColumnData
{
  public static final String OPTION_REQUIRED = "required:true";
  public static final String OPTION_NUMBER_INSEX = "precision:2,groupSeparator:','";
  private String columnName;
  private String dataType;
  private String columnComment;
  private String columnType;
  private String charmaxLength = "";
  private String nullable;
  private String scale;
  private String precision;
  private String classType = "";

  private String optionType = "";

  public String getFormatColumnName() {
	return columnName!=null?CommUtil.formatName(columnName):"";
}
  public String getColumnName()
  {
    return columnName;
  }
  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }
  public String getDataType() {
    return dataType;
  }
  public void setDataType(String dataType) {
    this.dataType = dataType;
  }
  public String getColumnComment() {
    return columnComment;
  }
  public void setColumnComment(String columnComment) {
    this.columnComment = columnComment;
  }
  public String getScale() {
    return scale;
  }
  public String getPrecision() {
    return precision;
  }
  public void setScale(String scale) {
    this.scale = scale;
  }
  public void setPrecision(String precision) {
    this.precision = precision;
  }
  public String getClassType() {
    return classType;
  }
  public String getOptionType() {
    return optionType;
  }
  public String getCharmaxLength() {
    return charmaxLength;
  }
  public String getNullable() {
    return nullable;
  }
  public void setClassType(String classType) {
    this.classType = classType;
  }
  public void setOptionType(String optionType) {
    this.optionType = optionType;
  }
  public void setCharmaxLength(String charmaxLength) {
    this.charmaxLength = charmaxLength;
  }
  public void setNullable(String nullable) {
    this.nullable = nullable;
  }
  public String getColumnType() {
    return columnType;
  }
  public void setColumnType(String columnType) {
    this.columnType = columnType;
  }
}