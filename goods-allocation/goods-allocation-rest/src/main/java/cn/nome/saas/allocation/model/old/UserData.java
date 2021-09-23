package cn.nome.saas.allocation.model.old;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

/**
 * 
 * @author bare
 *
 */
public class UserData extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;
	
	private String user_id;
	private String user_name;
    private List<Integer> appid_list;
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public List<Integer> getAppid_list() {
		return appid_list;
	}
	public void setAppid_list(List<Integer> appid_list) {
		this.appid_list = appid_list;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}	

}