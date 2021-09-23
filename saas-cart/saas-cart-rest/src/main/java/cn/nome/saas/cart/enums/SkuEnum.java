package cn.nome.saas.cart.enums;

/**
 * sku状态枚举（前两个状态是表中状态，后三个是前端购物车需要显示的状态）
 * @author chentaikuang
 */
public class SkuEnum {

    public enum Status {
    	NO_DEL(0, "未删除"), DEL(1, "已删除"),INVALID(0, "无效"), VALID(1, "有效"), LESS_STORE(2, "库存不足");

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
