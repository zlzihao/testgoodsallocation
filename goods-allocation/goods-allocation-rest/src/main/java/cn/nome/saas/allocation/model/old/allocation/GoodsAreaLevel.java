package cn.nome.saas.allocation.model.old.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * 
 * @author bare
 *
 */
public class GoodsAreaLevel extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;

	private String matCode;
	private String area;
	private String level;
	private String categoryName;
	
	private String areas;
	private String levels;
	
	
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getMatCode() {
		return matCode;
	}
	public void setMatCode(String matCode) {
		this.matCode = matCode;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getAreas() {
		return areas;
	}
	public void setAreas(String areas) {
		this.areas = areas;
	}
	public String getLevels() {
		return levels;
	}
	public void setLevels(String levels) {
		this.levels = levels;
	}
	
	

}