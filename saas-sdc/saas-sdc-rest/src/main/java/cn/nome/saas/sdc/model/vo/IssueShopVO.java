package cn.nome.saas.sdc.model.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zengdewu@nome.com
 */
public class IssueShopVO {

    private String province;//店铺省份
    private String shopCode;//店铺编码
    private String shopName;//店铺名称
    private String shopState;//店铺状态
    private String shopLevel;//店铺等级
    private Integer logisticsDays;//物流天数
    private List<String> issueDays;//发货日
    private BigDecimal coefficient;//转季系数
    private String stockCode;//仓库编号

    public static List<String> getAttributeNames() {
        return Arrays.asList(
                "省",
                "门店等级",
                "发货日",
                "物流天数"
        );
    }

    public static IssueShopVO initIssueShopVO() {
        IssueShopVO shop = new IssueShopVO();
        shop.setProvince("");
        shop.setShopCode("");
        shop.setShopName("");
        shop.setShopState("");
        shop.setShopLevel("");
        shop.setLogisticsDays(0);
        shop.setIssueDays(new ArrayList<>());
        shop.setCoefficient(new BigDecimal(0));
        shop.setStockCode("");
        return shop;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopState() {
        return shopState;
    }

    public void setShopState(String shopState) {
        this.shopState = shopState;
    }

    public String getShopLevel() {
        return shopLevel;
    }

    public void setShopLevel(String shopLevel) {
        this.shopLevel = shopLevel;
    }

    public Integer getLogisticsDays() {
        return logisticsDays;
    }

    public void setLogisticsDays(Integer logisticsDays) {
        this.logisticsDays = logisticsDays;
    }

    public List<String> getIssueDays() {
        return issueDays;
    }

    public void setIssueDays(List<String> issueDays) {
        this.issueDays = issueDays;
    }

    public BigDecimal getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }
}
