package cn.nome.saas.allocation.model.issue;

import java.math.BigDecimal;

public class ShopInfoData {
    private Integer ID;

    private String ShopID;
    private String ShopCode;
    private String UserId;
    private String GoodsArea;
    private String UserName;
    private String IssueTime;
    private String ShopLevel;
    private Integer MaxDays;
    private Integer HaveChild;
    private String WomenLevel;
    private String MenLevel;
    private String CommodityLevel;
    /**
     * 补货周期天数
     */
    private Integer IssueDay;
    private Integer RoadDay;

    /**
     * 安全天数
     */
    private Integer SafeDay;

    private String Operator;

    private Integer Status;

    private BigDecimal CommoditySpace;
    private BigDecimal ClothSpace;
    private BigDecimal SheetHeadSpaceNum;
    private Integer CosmeticsTable;
    private Integer StationeryTable;

    /**
     * 自定义属性与值 一到五
     */
    private String AttrKeys;
    private String AttrFirVal;
    private String AttrSecVal;
    private String AttrThiVal;
    private String AttrFourVal;
    private String AttrFifVal;

    private String shopName;

    private String DisplayLevel; // 陈列等级

    /**
     * 123
     */
    public enum ShopStatus {

        STATUS_1(1, "已开业"), STATUS_2(2, "未开业"),
        STATUS_3(3, "已撤店"), STATUS_4(4, "计划撤店"),
        STATUS_5(5, "配货中"), STATUS_6(6, "虚拟样板店"),
        STATUS_7(7, "待开业"), STATUS_8(8, "暂时营业");

        /**
         * 状态
         */
        private int status;
        /**
         * //状态名
         */
        private String statusName;

        public int getStatus() {
            return status;
        }

        public String getStatusName() {
            return statusName;
        }

        public static String getStatusName(int status) {
            for (ShopStatus ftype : ShopStatus.values()) {
                if (status == ftype.getStatus()) {
                    return ftype.statusName;
                }
            }
            return "";
        }
        public static int getStatus(String statusName) {
            for (ShopStatus ftype : ShopStatus.values()) {
                if (statusName.equals(ftype.getStatusName())) {
                    return ftype.status;
                }
            }
            return 0;
        }

        ShopStatus(int status, String statusName) {
            this.status = status;
            this.statusName = statusName;
        }

    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getShopID() {
        return ShopID;
    }

    public void setShopID(String shopID) {
        ShopID = shopID;
    }

    public String getShopCode() {
        return ShopCode;
    }

    public void setShopCode(String shopCode) {
        ShopCode = shopCode;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getGoodsArea() {
        return GoodsArea;
    }

    public void setGoodsArea(String goodsArea) {
        GoodsArea = goodsArea;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getIssueTime() {
        return IssueTime;
    }

    public void setIssueTime(String issueTime) {
        IssueTime = issueTime;
    }

    public String getShopLevel() {
        return ShopLevel;
    }

    public void setShopLevel(String shopLevel) {
        ShopLevel = shopLevel;
    }

    public Integer getMaxDays() {
        return MaxDays;
    }

    public void setMaxDays(Integer maxDays) {
        MaxDays = maxDays;
    }

    public Integer getHaveChild() {
        return HaveChild;
    }

    public void setHaveChild(Integer haveChild) {
        HaveChild = haveChild;
    }

    public String getWomenLevel() {
        return WomenLevel;
    }

    public void setWomenLevel(String womenLevel) {
        WomenLevel = womenLevel;
    }

    public String getMenLevel() {
        return MenLevel;
    }

    public void setMenLevel(String menLevel) {
        MenLevel = menLevel;
    }

    public String getCommodityLevel() {
        return CommodityLevel;
    }

    public void setCommodityLevel(String commodityLevel) {
        CommodityLevel = commodityLevel;
    }

    public Integer getRoadDay() {
        return RoadDay;
    }

    public void setRoadDay(Integer roadDay) {
        RoadDay = roadDay;
    }

    public String getOperator() {
        return Operator;
    }

    public void setOperator(String operator) {
        Operator = operator;
    }

    public BigDecimal getCommoditySpace() {
        return CommoditySpace;
    }

    public void setCommoditySpace(BigDecimal commoditySpace) {
        CommoditySpace = commoditySpace;
    }

    public BigDecimal getClothSpace() {
        return ClothSpace;
    }

    public void setClothSpace(BigDecimal clothSpace) {
        ClothSpace = clothSpace;
    }

    public BigDecimal getSheetHeadSpaceNum() {
        return SheetHeadSpaceNum;
    }

    public void setSheetHeadSpaceNum(BigDecimal sheetHeadSpaceNum) {
        SheetHeadSpaceNum = sheetHeadSpaceNum;
    }

    public Integer getCosmeticsTable() {
        return CosmeticsTable;
    }

    public void setCosmeticsTable(Integer cosmeticsTable) {
        CosmeticsTable = cosmeticsTable;
    }

    public Integer getStationeryTable() {
        return StationeryTable;
    }

    public void setStationeryTable(Integer stationeryTable) {
        StationeryTable = stationeryTable;
    }

    public String getAttrKeys() {
        return AttrKeys;
    }

    public void setAttrKeys(String attrKeys) {
        AttrKeys = attrKeys;
    }

    public String getAttrFirVal() {
        return AttrFirVal;
    }

    public void setAttrFirVal(String attrFirVal) {
        AttrFirVal = attrFirVal;
    }

    public String getAttrSecVal() {
        return AttrSecVal;
    }

    public void setAttrSecVal(String attrSecVal) {
        AttrSecVal = attrSecVal;
    }

    public String getAttrThiVal() {
        return AttrThiVal;
    }

    public void setAttrThiVal(String attrThiVal) {
        AttrThiVal = attrThiVal;
    }

    public String getAttrFourVal() {
        return AttrFourVal;
    }

    public void setAttrFourVal(String attrFourVal) {
        AttrFourVal = attrFourVal;
    }

    public String getAttrFifVal() {
        return AttrFifVal;
    }

    public void setAttrFifVal(String attrFifVal) {
        AttrFifVal = attrFifVal;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public Integer getIssueDay() {
        return IssueDay;
    }

    public void setIssueDay(Integer issueDay) {
        IssueDay = issueDay;
    }

    public Integer getSafeDay() {
        return SafeDay;
    }

    public void setSafeDay(Integer safeDay) {
        SafeDay = safeDay;
    }

    public String getDisplayLevel() {
        return DisplayLevel;
    }

    public void setDisplayLevel(String displayLevel) {
        DisplayLevel = displayLevel;
    }
}
