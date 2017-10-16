package ${bussPackage}#if($!entityPackage).${entityPackage}#end.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ${bussPackage}#if($!entityPackage).${entityPackage}#end.entity.${className};


/**
 * <br>
 * <b>功能：</b>${className}Service<br>
 */
public interface ${className}Service{

	public List<${className}> queryList();

	public void insert(${className} ${lowerName});

	public void modify(${className} ${lowerName});

	public void delete(String id);
}