package ${bussPackage}#if($!entityPackage).${entityPackage}#end.service.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ${bussPackage}#if($!entityPackage).${entityPackage}#end.service.${className}Service;
import ${bussPackage}#if($!entityPackage).${entityPackage}#end.dao.${className}Dao;
import ${bussPackage}#if($!entityPackage).${entityPackage}#end.entity.${className};


/**
 * <br>
 * <b>功能：</b>${className}Service<br>
 */
@Service("$!{lowerName}Service")
public class  ${className}ServiceImpl  implements ${className}Service{
  	private final static Log LOG = LogFactory.getLog(${className}ServiceImpl.class);

	@Autowired
	private ${className}Dao ${lowerName}dao;

	@Override
	public List<${className}> queryList(){
		return ${lowerName}dao.queryList();
	}

	@Override
	public void insert(${className} ${lowerName}){
		${lowerName}dao.insert(${lowerName});
	}

	@Override
	public void modify(${className} ${lowerName}){
		${lowerName}dao.modify(${lowerName});
	}

	@Override
	public void delete(String id){
		${lowerName}dao.delete(id);
	}

}