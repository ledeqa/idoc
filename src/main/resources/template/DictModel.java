package default.model;

<#if dictParamInfo?? && dictParamInfo.hasList??>
	<#if dictParamInfo.hasList == 1>
import java.util.List;
	</#if>
</#if>

public class ${dictParamInfo.dictName!''}Model {

	<#if dictParamInfo?? && dictParamInfo.paramList?size gt 0>
	<#list dictParamInfo.paramList as dict>
	private ${dict.paramType!''} ${dict.paramName!''};  <#if dict.paramDesc??>// ${dict.paramDesc!''} </#if>
	</#list>
	
	<#list dictParamInfo.paramList as dict>
	<#if dict.paramType == 'boolean'>
	<#if dict.paramName?substring(0, 2) == 'is' && dict.paramName?substring(2) == dict.paramName?substring(2)?cap_first>
	public void set${dict.paramName?substring(2)}(${dict.paramType!''} ${dict.paramName!''}){
		this.${dict.paramName!''} = ${dict.paramName!''};
	}
	
	public ${dict.paramType!''} ${dict.paramName!''}(){
		return ${dict.paramName!''};
	}
	<#else>
	public void set${dict.paramName?cap_first}(${dict.paramType!''} ${dict.paramName!''}){
		this.${dict.paramName!''} = ${dict.paramName!''};
	}
	
	public ${dict.paramType!''} is${dict.paramName?cap_first}(){
		return ${dict.paramName!''};
	}
	</#if>
	<#else>
	public void set${dict.paramName?cap_first}(${dict.paramType!''} ${dict.paramName!''}){
		this.${dict.paramName!''} = ${dict.paramName!''};
	}
	
	public ${dict.paramType!''} get${dict.paramName?cap_first}(){
		return ${dict.paramName!''};
	}
	</#if>
	
	</#list>
	</#if>
}