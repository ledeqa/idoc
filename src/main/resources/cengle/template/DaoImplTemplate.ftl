package ${bussPackage}#if($!entityPackage).${entityPackage}#end.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import com.netease.duobao.dao.impl.BaseDao;
import ${bussPackage}#if($!entityPackage).${entityPackage}#end.dao.${className}Dao;
import ${bussPackage}#if($!entityPackage).${entityPackage}#end.entity.${className};

@Repository("$!{lowerName}Dao")
public class ${className}DaoImpl extends BaseDao implements ${className}Dao {

	@Override
	public List<${className}> queryList(){
		return sqlSessionTemplate.selectList("${bussPackage}#if($!entityPackage).${entityPackage}#end.dao.${className}Dao.select");
	}

	@Override
	public void insert(${className} ${lowerName}){
		sqlSessionTemplate.insert("${bussPackage}#if($!entityPackage).${entityPackage}#end.dao.${className}Dao.insert",  ${lowerName});
	}

	@Override
	public void modify(${className} ${lowerName}){
        sqlSessionTemplate.update("${bussPackage}#if($!entityPackage).${entityPackage}#end.dao.${className}Dao.update", ${lowerName});
	}

	@Override
	public void delete(String id){
		sqlSessionTemplate.delete("${bussPackage}#if($!entityPackage).${entityPackage}#end.dao.${className}Dao.delete", id);
	}

}