package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * AllocationClothingSKC
 *
 * @author Bruce01.fan
 * @date 2019/12/4
 */
public class AllocationClothingSKC extends ToString {

    private String shopId;

    private String matCode;

    /**
     * 总库存
     */
    private int totalStock;

    /**
     * 尺码数
     */
    private int sizeCount;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public int getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }

    public int getSizeCount() {
        return sizeCount;
    }

    public void setSizeCount(int sizeCount) {
        this.sizeCount = sizeCount;
    }
}
