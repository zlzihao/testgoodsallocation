package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TaskStore
 *
 * @author Bruce01.fan
 * @date 2019/7/10
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskStore extends ToString {

    @JsonProperty("shop_id")
    private String shopId;

    @JsonProperty("shop_name")
    private String shopName;

    @JsonProperty("commodity_num")
    private int commodityNum;

    @JsonProperty("commodity_price")
    private int commodityPrice;

    @JsonProperty("shopqty")
    private int shopQty;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

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

    public int getShopQty() {
        return shopQty;
    }

    public void setShopQty(int shopQty) {
        this.shopQty = shopQty;
    }

}
