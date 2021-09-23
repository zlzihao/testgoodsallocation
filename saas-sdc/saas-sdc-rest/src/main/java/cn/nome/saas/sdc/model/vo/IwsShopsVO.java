package cn.nome.saas.sdc.model.vo;

import cn.nome.platform.common.utils.ToString;

import java.util.Arrays;
import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/11/19 10:37
 */
public class IwsShopsVO extends ToString {

    private Integer shopId;
    private String shopCode;
    private String shopName;
    private Integer cashRegisterNumber;
    private Boolean hasIceCreamMachine;
    private String openingTime;
    private String closingTime;
    private String startBusinessTime;
    private String endBusinessTime;
    private String openingDate;
    private String closingDate;
    private Integer fittingRoomFloorNumber;

    public static List<String> getAttributeNames() {
        return Arrays.asList(
                "收银机数量",
                "雪糕机",
                "开门时间",
                "关门时间",
                "营业开始时间",
                "营业结束时间",
                "开业时间",
                "结业时间",
                "试衣间数量（一层）",
                "试衣间数量（二层）"
        );
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
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

    public Integer getCashRegisterNumber() {
        return cashRegisterNumber;
    }

    public void setCashRegisterNumber(Integer cashRegisterNumber) {
        this.cashRegisterNumber = cashRegisterNumber;
    }

    public Boolean getHasIceCreamMachine() {
        return hasIceCreamMachine;
    }

    public void setHasIceCreamMachine(Boolean hasIceCreamMachine) {
        this.hasIceCreamMachine = hasIceCreamMachine;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getStartBusinessTime() {
        return startBusinessTime;
    }

    public void setStartBusinessTime(String startBusinessTime) {
        this.startBusinessTime = startBusinessTime;
    }

    public String getEndBusinessTime() {
        return endBusinessTime;
    }

    public void setEndBusinessTime(String endBusinessTime) {
        this.endBusinessTime = endBusinessTime;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public String getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }

    public Integer getFittingRoomFloorNumber() {
        return fittingRoomFloorNumber;
    }

    public void setFittingRoomFloorNumber(Integer fittingRoomFloorNumber) {
        this.fittingRoomFloorNumber = fittingRoomFloorNumber;
    }
}
