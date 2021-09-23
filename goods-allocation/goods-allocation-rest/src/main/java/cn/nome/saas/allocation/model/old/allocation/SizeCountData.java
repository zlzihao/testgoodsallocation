package cn.nome.saas.allocation.model.old.allocation;

/**
 * @author chentaikuang
 */
public class SizeCountData {

    private String matcode;
    private String shopId;
    private Integer count;

    public String getMatcode() {
        return matcode;
    }

    public void setMatcode(String matcode) {
        this.matcode = matcode;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
