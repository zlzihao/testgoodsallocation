package cn.nome.saas.allocation.model.old.allocation;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

/**
 * 
 * @author bare
 *
 */
public class Area extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;
	
	private String area_code;
	private String area_name;
	private List<Area> sub_area_list;
	
	public List<Area> getSub_area_list() {
		return sub_area_list;
	}
	public void setSub_area_list(List<Area> sub_area_list) {
		this.sub_area_list = sub_area_list;
	}
	public String getArea_code() {
		return area_code;
	}
	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}
	public String getArea_name() {
		return area_name;
	}
	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

}