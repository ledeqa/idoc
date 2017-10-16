package ${bussPackage}#if($!entityPackage).${entityPackage}#end.dao;
import ${bussPackage}#if($!entityPackage).${entityPackage}#end.entity.${className};
import java.util.List;

/**
 * <br>
 * <b>功能：</b>${className}Dao<br>
 */
public interface ${className}Dao {

	public List<${className}> queryList();

	public void insert(${className} ${lowerName});

	public void modify(${className} ${lowerName});

	public void delete(String id);
}