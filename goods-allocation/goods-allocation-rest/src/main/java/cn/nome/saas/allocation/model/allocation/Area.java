package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 
 * @author bare
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Area extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;

	@JsonProperty("area_code")
	private String areaCode;
	@JsonProperty("area_name")
	private String areaName;
	@JsonProperty("sub_area_list")
	private List<Area> subAreaList;

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public List<Area> getSubAreaList() {
		return subAreaList;
	}

	public void setSubAreaList(List<Area> subAreaList) {
		this.subAreaList = subAreaList;
	}

	@Override
	public boolean equals(Object obj) {

		Area other = (Area) obj;
		if (this.areaCode.equals(other.getAreaCode()) && this.areaName.equals(other.getAreaName())) {
			return true;
		}
		return false;
	}
}