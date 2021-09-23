package cn.nome.saas.allocation.repository.entity.allocation;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * IssueNeedStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/25
 */
public class IssueNeedStockDO implements Comparator<IssueNeedStockDO> {

    private Integer id;

    private int taskId;

    private String shopId;

    private String matCode;

    private String sizeId;

    private String sizeName;

    private String categoryName;

    private String midCategoryName;

    private String smallCategoryName;

    private Double totalStockQty = 0d;

    private Double totalSaleQty = 0d;

    private Double needQty = 0d;

    private BigDecimal percentCategory;

    private Double needStockQty1 = 0d;

    private Double needStockQty2 = 0d;

    private Double needStockQty3 = 0d;

    private Double needStockQty = 0d;

    private Double minQty;

    private Double remainNeedQty = 0d;

    //===============================非表字段=========
    /**
     * 是否完成首配 1-已完成, 0-未完成
     */
    private Integer issueFin = 1;
    /**
     * 店铺等级
     */
    private String shopLevel;
    /**
     * 有效日均销
     */
    private Double avgSaleQty;
    /**
     * 小类有效日均销
     */
    private Double smallSumAvg;
    /**
     * 中类有效日均销
     */
    private Double midSumAvg;
    /**
     * 大类有效日均销
     */
    private Double bigSumAvg;
    /**
     * 全店有效日均销
     */
    private Double allSumAvg;
    /**
     * 店铺代码
     */
    private String shopCode;

    public Double getAllSumAvg() {
        return allSumAvg;
    }

    public void setAllSumAvg(Double allSumAvg) {
        this.allSumAvg = allSumAvg;
    }

    public String getSmallCategoryName() {
        return smallCategoryName;
    }

    public void setSmallCategoryName(String smallCategoryName) {
        this.smallCategoryName = smallCategoryName;
    }

    public Double getSmallSumAvg() {
        return smallSumAvg;
    }

    public void setSmallSumAvg(Double smallSumAvg) {
        this.smallSumAvg = smallSumAvg;
    }

    public Double getMidSumAvg() {
        return midSumAvg;
    }

    public void setMidSumAvg(Double midSumAvg) {
        this.midSumAvg = midSumAvg;
    }

    public Double getBigSumAvg() {
        return bigSumAvg;
    }

    public void setBigSumAvg(Double bigSumAvg) {
        this.bigSumAvg = bigSumAvg;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public Integer getIssueFin() {
        return issueFin;
    }

    public void setIssueFin(Integer issueFin) {
        this.issueFin = issueFin;
    }

    public String getShopLevel() {
        return shopLevel;
    }

    public void setShopLevel(String shopLevel) {
        this.shopLevel = shopLevel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

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

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMidCategoryName() {
        return midCategoryName;
    }

    public void setMidCategoryName(String midCategoryName) {
        this.midCategoryName = midCategoryName;
    }

    public Double getTotalStockQty() {
        return totalStockQty;
    }

    public void setTotalStockQty(Double totalStockQty) {
        this.totalStockQty = totalStockQty;
    }

    public Double getTotalSaleQty() {
        return totalSaleQty;
    }

    public void setTotalSaleQty(Double totalSaleQty) {
        this.totalSaleQty = totalSaleQty;
    }

    public Double getNeedQty() {
        return needQty;
    }

    public void setNeedQty(Double needQty) {
        this.needQty = needQty;
    }

    public BigDecimal getPercentCategory() {
        return percentCategory;
    }

    public void setPercentCategory(BigDecimal percentCategory) {
        this.percentCategory = percentCategory;
    }

    public Double getNeedStockQty1() {
        return needStockQty1;
    }

    public void setNeedStockQty1(Double needStockQty1) {
        this.needStockQty1 = needStockQty1;
    }

    public Double getNeedStockQty2() {
        return needStockQty2;
    }

    public void setNeedStockQty2(Double needStockQty2) {
        this.needStockQty2 = needStockQty2;
    }

    public Double getNeedStockQty3() {
        return needStockQty3;
    }

    public void setNeedStockQty3(Double needStockQty3) {
        this.needStockQty3 = needStockQty3;
    }

    public Double getNeedStockQty() {
        return needStockQty;
    }

    public void setNeedStockQty(Double needStockQty) {
        this.needStockQty = needStockQty;
    }

    public Double getMinQty() {
        return minQty;
    }

    public void setMinQty(Double minQty) {
        this.minQty = minQty;
    }

    public Double getAvgSaleQty() {
        return avgSaleQty;
    }

    public void setAvgSaleQty(Double avgSaleQty) {
        this.avgSaleQty = avgSaleQty;
    }

//    public String getKey() {
//        return this.getShopId() +":"+this.getCategoryName()+":"+this.getMidCategoryName();
//    }

    public String getShopIdCategoryMidSmallCategoryKey() {
        return this.getShopId() +"_"+this.getCategoryName()+"_"+this.getMidCategoryName()+"_"+this.getSmallCategoryName();
    }
    public String getShopIdCategoryMidCategoryKey() {
        return this.getShopId() +"_"+this.getCategoryName()+"_"+this.getMidCategoryName();
    }
    public String getShopIdCategory() {
        return this.getShopId() +"_"+this.getCategoryName();
    }

    public String getShopIdMatCodeSizeNameKey() {
        return this.getShopId() +"_"+this.getMatCode() +"_"+this.getSizeName();
    }
    public String getMatCodeSizeNameKey() {
        return this.getMatCode() +"_"+this.getSizeName();
    }

    public Double getRemainNeedQty() {
        return remainNeedQty;
    }

    public void setRemainNeedQty(Double remainNeedQty) {
        this.remainNeedQty = remainNeedQty;
    }

    @Override
    public int compare(IssueNeedStockDO o1, IssueNeedStockDO o2) {
        int issueFinComp;
        int shopLevelComp;
        int avgSaleQtyComp;
        int smallAvgSaleQtyComp;
        int midAvgSaleQtyComp;
        int bigAvgSaleQtyComp;
        int allAvgSaleQtyComp;
        return (issueFinComp = o1.getIssueFin().compareTo(o2.getIssueFin())) != 0 ? issueFinComp :
                (shopLevelComp = o1.getShopLevel().compareTo(o2.getShopLevel())) != 0 ? shopLevelComp :
                //倒序, 替换02比O1
                (avgSaleQtyComp = o2.getAvgSaleQty().compareTo(o1.getAvgSaleQty())) != 0 ? avgSaleQtyComp :
                (smallAvgSaleQtyComp = o2.getSmallSumAvg().compareTo(o1.getSmallSumAvg())) != 0 ? smallAvgSaleQtyComp :
                (midAvgSaleQtyComp = o2.getMidSumAvg().compareTo(o1.getMidSumAvg())) != 0 ? midAvgSaleQtyComp :
                (bigAvgSaleQtyComp = o2.getBigSumAvg().compareTo(o1.getBigSumAvg())) != 0 ? bigAvgSaleQtyComp :
                (allAvgSaleQtyComp = o2.getAllSumAvg().compareTo(o1.getAllSumAvg())) != 0 ? allAvgSaleQtyComp : o1.shopCode.compareTo(o2.shopCode);
    }

    /**
     * getLoadString, 用于保存文本, 批量插入sql
     * @return
     */
    public String getLoadString() {
        return (shopId == null ? "" : shopId) + "||" +
                (matCode == null ? "" : matCode) + "||" +
                (sizeId == null ? "" : sizeId) + "||" +
                (sizeName == null ? "" : sizeName) + "||" +
                (categoryName == null ? "" : categoryName) + "||" +
                (midCategoryName == null ? "" : midCategoryName) + "||" +
                (smallCategoryName == null ? "" : smallCategoryName) + "||" +
                (totalSaleQty == null ? 0 : totalSaleQty) + "||" +
                (needQty == null ? 0 : needQty) + "||" +
                (percentCategory == null ? 0 : percentCategory) + "||" +
                (needStockQty1 == null ? 0 : needStockQty1) + "||" +
                (needStockQty2 == null ? 0 : needStockQty2) + "||" +
                (needStockQty3 == null ? 0 : needStockQty3) + "||" +
                (totalStockQty == null ? 0 : totalStockQty) + "||" +
                (needStockQty == null ? 0 : needStockQty) + "||" +
                (remainNeedQty == null ? 0 : remainNeedQty);
    }

    @Override
    public String toString() {
        return "IssueNeedStockDO{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", shopId='" + shopId + '\'' +
                ", matCode='" + matCode + '\'' +
                ", sizeId='" + sizeId + '\'' +
                ", sizeName='" + sizeName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", midCategoryName='" + midCategoryName + '\'' +
                ", smallCategoryName='" + smallCategoryName + '\'' +
                ", totalStockQty=" + totalStockQty +
                ", totalSaleQty=" + totalSaleQty +
                ", needQty=" + needQty +
                ", percentCategory=" + percentCategory +
                ", needStockQty1=" + needStockQty1 +
                ", needStockQty2=" + needStockQty2 +
                ", needStockQty3=" + needStockQty3 +
                ", needStockQty=" + needStockQty +
                ", minQty=" + minQty +
                ", remainNeedQty=" + remainNeedQty +
                ", shopLevel='" + shopLevel + '\'' +
                ", avgSaleQty=" + avgSaleQty +
                ", smallSumAvg=" + smallSumAvg +
                ", midSumAvg=" + midSumAvg +
                ", bigSumAvg=" + bigSumAvg +
                ", allSumAvg=" + allSumAvg +
                ", shopCode='" + shopCode + '\'' +
                '}';
    }
}
