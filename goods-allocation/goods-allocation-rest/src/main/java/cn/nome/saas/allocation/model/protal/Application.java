package cn.nome.saas.allocation.model.protal;

import cn.nome.platform.common.utils.ToString;

/**
 * 
 * @author bare
 *
 */
public class Application extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;
	
	
	private int id;
	private String code;
	private String name;
	private String iconPath;
	private String url;
	private String applyLink;
    private String introduction;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getApplyLink() {
		return applyLink;
	}

	public void setApplyLink(String applyLink) {
		this.applyLink = applyLink;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
}