package cn.nome.saas.allocation.model.protal;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

public class ApplicationMangled extends ToString {
	
	private String name;
	private boolean isOpen;
	private String icon;
	private String applyLink;
	private String projectLink;
    private List<String> intros;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean open) {
		isOpen = open;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getApplyLink() {
		return applyLink;
	}

	public void setApplyLink(String applyLink) {
		this.applyLink = applyLink;
	}

	public String getProjectLink() {
		return projectLink;
	}

	public void setProjectLink(String projectLink) {
		this.projectLink = projectLink;
	}

	public List<String> getIntros() {
		return intros;
	}

	public void setIntros(List<String> intros) {
		this.intros = intros;
	}
}