package com.idoc.code.def;

public class TableColumn {
	private String field;
	private String type;
	private String nullable;
	private String key;
	private String defaultContent;
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNullable() {
		return nullable;
	}
	public void setNullable(String nullable) {
		this.nullable = nullable;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getDefaultContent() {
		return defaultContent;
	}
	public void setDefaultContent(String defaultContent) {
		this.defaultContent = defaultContent;
	}
	@Override
	public String toString() {
		return "TableColumn [field=" + field + ", type=" + type + ", nullable=" + nullable + ", key=" + key
				+ ", defaultContent=" + defaultContent + "]";
	}
}