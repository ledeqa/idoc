package com.idoc.model.rap;

import java.util.List;

/**
 * @author tangjun
 *
 */
public class RapPage {
	
	private Long id;
	private String name;
	private List<RapAction> actionList;
	private String introduction;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<RapAction> getActionList() {
		return actionList;
	}
	public void setActionList(List<RapAction> actionList) {
		this.actionList = actionList;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}


}
