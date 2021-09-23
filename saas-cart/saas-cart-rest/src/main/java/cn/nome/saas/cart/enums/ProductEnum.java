package cn.nome.saas.cart.enums;

/**
 * 商品状态枚举(表中状态)
 * @author chentaikuang
 */
public class ProductEnum {

	public enum Status {
		DOWN(0, "下架"), UP(1, "上架");

		private int status;
		private String msg;

		Status(int status, String msg) {
			this.status = status;
			this.msg = msg;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
}
