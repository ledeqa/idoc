package com.idoc.protocol.plugin.filter;

import com.idoc.protocol.plugin.IPlugin;

public interface IFilterPlugin extends IPlugin {
	public String handleFilterPlugin(String request, String bodyContent);
}