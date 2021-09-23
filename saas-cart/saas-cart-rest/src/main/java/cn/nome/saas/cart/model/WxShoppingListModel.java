package cn.nome.saas.cart.model;

/**
 * @author chentaikuang
 */
public class WxShoppingListModel extends BaseModel {

    private int uid;
    private String skuCode;
    private String type;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
