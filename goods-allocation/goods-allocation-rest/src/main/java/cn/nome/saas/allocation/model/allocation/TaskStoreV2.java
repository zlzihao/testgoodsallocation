package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TaskStoreDO
 *
 * @author Bruce01.fan
 * @date 2019/7/1
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskStoreV2 extends ToString {

    @JsonProperty("inshop_id")
    private String inShopId;

    @JsonProperty("inshop_name")
    private String inShopName;

    @JsonProperty("outshop_id")
    private String outShopId;

    @JsonProperty("outshop_name")
    private String outShopName;

    @JsonProperty("commodity_num")
    private int commodityNum;

    @JsonProperty("commodity_price")
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
