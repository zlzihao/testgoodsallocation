package cn.nome.saas.allocation.feign.model;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

public class MatCodes extends ToString{

	private List<String> matCodes;

	public List<String> getMatCodes() {
		return matCodes;
	}

	public void setMatCodes(List<String> matCodes) {
		this.matCodes = matCodes;
	}
}

