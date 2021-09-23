package cn.nome.saas.allocation.model.old;

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
	private String icon_path;
	private String url;
	private String apply_link;
    private String introduction;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the icon_path
	 */
	public String getIcon_path() {
		return icon_path;
	}
	/**
	 * @param icon_path the icon_path to set
	 */
	public void setIcon_path(String icon_path) {
		this.icon_path = icon_path;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the apply_link
	 */
	public String getApply_link() {
		return apply_link;
	}
	/**
	 * @param apply_link the apply_link to set
	 */
	public void setApply_link(String apply_link) {
		this.apply_link = apply_link;
	}
	/**
	 * @return the introduction
	 */
	public String getIntroduction() {
		return introduction;
	}
	/**
	 * @param introduction the introduction to set
	 */
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}