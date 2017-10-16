package com.idoc.form;

import java.util.Arrays;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.idoc.model.Dict;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DictForm {
	
	private Long dictId;
	private Long productId;
	private String dictName;
	private String dictDesc;
	private Long version;
	private ParamForm[] params;
	
	public DictForm(){
		
	}
	
	public DictForm(Dict dict){
		this.dictId = dict.getDictId();
		this.productId = dict.getProductId();
		this.dictName = dict.getDictName();
		this.dictDesc = dict.getDictDesc();
		this.version = dict.getVersion();
		
		if(CollectionUtils.isNotEmpty(dict.getParams())){
			int size = dict.getParams().size();
			this.params = new ParamForm[size];
			
			for(int i = 0; i < size; i++){
				this.params[i] = new ParamForm(dict.getParams().get(i));
			}
		}
	}
	
	public Long getDictId() {
	
		return dictId;
	}
	
	public void setDictId(Long dictId) {
	
		this.dictId = dictId;
	}
	
	public Long getProductId() {
	
		return productId;
	}
	
	public void setProductId(Long productId) {
	
		this.productId = productId;
	}
	
	public String getDictName() {
	
		return dictName;
	}
	
	public void setDictName(String dictName) {
	
		this.dictName = dictName;
	}
	
	public ParamForm[] getParams() {
	
		return params;
	}
	
	public void setParams(ParamForm[] params) {
	
		this.params = params;
	}

	
	public String getDictDesc() {
	
		return dictDesc;
	}

	
	public void setDictDesc(String dictDesc) {
	
		this.dictDesc = dictDesc;
	}

	
	public Long getVersion() {
	
		return version;
	}

	
	public void setVersion(Long version) {
	
		this.version = version;
	}

	@Override
	public String toString() {
	
		return "DictForm [dictId=" + dictId + ", productId=" + productId + ", dictName=" + dictName + ", dictDesc=" + dictDesc + ", version=" + version + ", params=" + Arrays.toString(params) + "]";
	}

}
