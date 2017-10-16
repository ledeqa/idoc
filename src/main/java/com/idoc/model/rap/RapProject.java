package com.idoc.model.rap;

import java.util.List;

/**
 * @author tangjun
 *
 */
public class RapProject {
	
	private List<RapModule> moduleList;
	private Long id;
	private String name;
	private RapUser user;
	private String introduction;
	private String version;
	private String createDateStr;
	
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
	public RapUser getUser() {
		return user;
	}
	public void setUser(RapUser user) {
		this.user = user;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCreateDateStr() {
		return createDateStr;
	}
	public void setCreateDateStr(String createDateStr) {
		this.createDateStr = createDateStr;
	}
	public List<RapModule> getModuleList() {
		return moduleList;
	}
	public void setModuleList(List<RapModule> moduleList) {
		this.moduleList = moduleList;
	}


}
