package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * TaskStoreDO
 *
 * @author Bruce01.fan
 * @date 2019/7/1
 */
public class TaskStoreDOV2 extends ToString {

    private String inShopId;

    private String inShopName;

    private String outShopId;

    private String outShopName;

    private int commodityNum;

    private int commodityPrice;

    public int getCommodityNum() {
        return commodityNum;
    }

    public void setCommodityNum(int commodityNum) {
        this.commodityNum = commodityNum;
    }

    public int getCommodityPrice() {
        return commodityPrice;
    }

    public void setCommodityPrice(int commodityPrice) {
        this.commodityPrice = commodityPrice;
    }

    public String getInShopId() {
        return inShopId;
    }

    public void setInShopId(String inShopId) {
        this.inShopId = inShopId;
    }

    public String getInShopName() {
        return inShopName;
    }

    public void setInShopName(String inShopName) {
        this.inShopName = inShopName;
    }

    public String getOutShopId() {
        return outShopId;
    }

    public void setOutShopId(String outShopId) {
        this.outShopId = outShopId;
    }

    public String getOutShopName() {
        return outShopName;
    }

    public void setOutShopName(String outShopName) {
        this.outShopName = outShopName;
    }
}
