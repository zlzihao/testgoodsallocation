package cn.nome.saas.allocation.model.old.allocation;
import cn.nome.platform.common.utils.ToString;

/**
 * 
 * @author bare
 *
 */
public class DBArea extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;
	
	private String province_code;
	private String province_name;
	private String city_code;
	private String city_name;
	
	public String getProvince_code() {
		return province_code;
	}
	public void setProvince_code(String province_code) {
		this.province_code = province_code;
	}
	public String getProvince_name() {
		return province_name;
	}
	public void setProvince_name(String province_name) {
		this.province_name = province_name;
	}
	public String getCity_code() {
		return city_code;
	}
	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}
	public String getCity_name() {
		return city_name;
	}
	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

}