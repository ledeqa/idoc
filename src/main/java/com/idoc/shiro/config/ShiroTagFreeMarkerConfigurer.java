package com.idoc.shiro.config;

import java.io.IOException;

import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.jagregory.shiro.freemarker.ShiroTags;

import freemarker.template.TemplateException;

/**
 * 
 * @author bjhuwei1
 * @version 创建时间：2017年7月21日 下午3:09:31 
*/
public class ShiroTagFreeMarkerConfigurer extends FreeMarkerConfigurer{
	@Override  
    public void afterPropertiesSet() throws IOException, TemplateException {  
        super.afterPropertiesSet();  
        this.getConfiguration().setSharedVariable("shiro", new ShiroTags());  
    }
}
