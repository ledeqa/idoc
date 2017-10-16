package ${bussPackage}#if($!controllerEntityPackage).${controllerEntityPackage}#end.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Lists;
import ${bussPackage}#if($!entityPackage).${entityPackage}#end.entity.${className};
import ${bussPackage}#if($!entityPackage).${entityPackage}#end.service.${className}Service;

/**
 * 
 * ${codeName}
 *<br>
 */ 
@Controller
public class ${className}Controller {
	
	private final static Log LOG = LogFactory.getLog(${className}Controller.class);
	
	@Autowired(required=false) 
	private ${className}Service ${lowerName}Service; 
	
	/**
	 * 查询
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/${className}/query") 
	public List<${className}>  query(HttpServletRequest request, HttpServletResponse response){
		List<${className}> dataList = Lists.newArrayList();
		try{
			dataList = ${lowerName}Service.queryList();
		} catch (Exception e){
			LOG.error("query error", e);
		}
		return dataList; 
	}
	
	/**
	 * 插入
	 * @param request
	 * @param response
	 * @param ${lowerName}
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/${className}/insert") 
	public void  insert(HttpServletRequest request, HttpServletResponse response, ${className} ${lowerName}) {
		try{
			${lowerName}Service.insert(${lowerName});
		} catch (Exception e){
			LOG.error("insert error", e);
		}
	}
	
	/**
	 * 修改
	 * @param request
	 * @param response
	 * @param ${lowerName}
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/${className}/modify") 
	public void  modify(HttpServletRequest request, HttpServletResponse response,${className} ${lowerName}) {
		try{
			${lowerName}Service.modify(${lowerName});
		} catch (Exception e){
			LOG.error("query error", e);
		}
	}
	
	/**
	 * 删除
	 * @param request
	 * @param response
	 * @param primary
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/${className}/delete") 
	public void  delete(HttpServletRequest request, HttpServletResponse response, String primary) {
		try{
			${lowerName}Service.delete(primary);
		} catch (Exception e){
			LOG.error("query error", e);
		}
	}
	
}