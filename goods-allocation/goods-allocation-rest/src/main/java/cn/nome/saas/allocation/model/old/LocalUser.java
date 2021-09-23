package cn.nome.saas.allocation.model.old;

import cn.nome.platform.common.utils.ToString;

/**
 * 
 * @author bare
 *
 */
public class LocalUser extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;
	
	private String user_id;
	private String user_name;
    private int role;
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
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
		
}