package cn.nome.saas.allocation.repository.old.vertica.entity;

import cn.nome.platform.common.utils.ToString;

/**
 * 用于查询尺码、剩余尺码、库存数等信息（最小陈列需求）
 *
 * @author Bruce01.fan
 * @date 2019/6/2
 */
public class ShopSizeIdInfoDO extends ToString {

    private String shopId;

    private String matCode;

    private int totalSizeIdCount; // 总尺码数

    private int remainderSizeIdCount; // 剩余尺码数

    private int totalQty; // 库存数

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

    public int getTotalSizeIdCount() {
        return totalSizeIdCount;
    }

    public void setTotalSizeIdCount(int totalSizeIdCount) {
        this.totalSizeIdCount = totalSizeIdCount;
    }

    public int getRemainderSizeIdCount() {
        return remainderSizeIdCount;
    }

    public void setRemainderSizeIdCount(int remainderSizeIdCount) {
        this.remainderSizeIdCount = remainderSizeIdCount;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }
}
