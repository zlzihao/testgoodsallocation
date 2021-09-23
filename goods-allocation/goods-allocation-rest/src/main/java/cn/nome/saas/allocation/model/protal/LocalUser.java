package cn.nome.saas.allocation.model.protal;

import cn.nome.platform.common.utils.ToString;

/**
 * 
 * @author bare
 *
 */
public class LocalUser extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;
	
	private String userId;
	private String userName;
    private int role;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}
}