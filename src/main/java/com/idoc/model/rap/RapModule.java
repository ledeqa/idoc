package com.idoc.model.rap;

import java.util.List;

/**
 * @author tangjun
 *
 */
public class RapModule {
	
	private Long id;
	private List<RapPage> pageList;
	private String name;
	private String introduction;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<RapPage> getPageList() {
		return pageList;
	}
	public void setPageList(List<RapPage> pageList) {
		this.pageList = pageList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

}
