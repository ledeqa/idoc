package com.idoc.util.mail;

import java.util.Locale;
import java.util.ResourceBundle;

public class ConfigResource {
	
	public ConfigResource() {

		resource = ResourceBundle.getBundle("configuration", Locale.getDefault());
	}
	
	public ConfigResource(String baseName) {

		resource = ResourceBundle.getBundle(baseName, Locale.getDefault());
	}
	
	public String getConfigValueFromKey(String key) {

		return resource.getString(key);
	}
	
	private ResourceBundle resource;
}
