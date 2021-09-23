package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * ForbiddenRule
 *
 * @author Bruce01.fan
 * @date 2019/5/26
 */
public class NewGoodsIssueRangeDetailDO extends ToString {

    /*
    铺货计划
     */
    public static int PLAN_FLAG_PLAN = 1;
    /*
    铺货范围(白名单)
     */
    public static int PLAN_FLAG_WHITELIST = 0;

    private Integer id;
    private Integer rangeId;
    private Integer num;

    /*
    是否计划内, 1-计划, 0-白名单
     */
    private Integer planFlag;
    private String  shopCode;
    private String  shopId;
    private Integer  issueFin;
    private Date saleTime;

    //--------ext----------
    private String  matCode;
    private String  sizeId;
    private String  sizeName;

    /**
     * 运输天数
     */
    private Integer roadDay;
    /**
     * 配发时间  一四六
     */
    private String IssueTime;

    private String shopName;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public Date getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(Date saleTime) {
        this.saleTime = saleTime;
    }


    public Integer getRoadDay() {
        return roadDay;
    }

    public void setRoadDay(Integer roadDay) {
        this.roadDay = roadDay;
    }

    public String getIssueTime() {
        return IssueTime;
    }

    public void setIssueTime(String issueTime) {
        IssueTime = issueTime;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public Integer getIssueFin() {
        return issueFin;
    }

    public void setIssueFin(Integer issueFin) {
        this.issueFin = issueFin;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRangeId() {
        return rangeId;
    }

    public void setRangeId(Integer rangeId) {
        this.rangeId = rangeId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getPlanFlag() {
        return planFlag;
    }

    public void setPlanFlag(Integer planFlag) {
        this.planFlag = planFlag;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopIdMatCodeSizeNameKey() {
        return this.getShopId() + "_" + this.getMatCode() + "_" + this.getSizeName();
    }
    public String getMatCodeSizeNameKey() {
        return this.getMatCode() + "_" + this.getSizeName();
    }
}
