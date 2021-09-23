package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.logger.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * QdIssueSkcStockDO
 *
 * @author Bruce01.fan
 * @date 2019/8/5
 */
public class QdIssueSkcStockDO {

    private static Logger logger = LoggerFactory.getLogger(QdIssueSkcStockDO.class);

    private Integer id;

    private Integer taskId;

    private String shopId;

    private String categoryName;

    private String midCategoryName;

    private int maleStandardSkc; // 男装陈列数

    private int femaleStandardSkc; // 女装陈列数

    private double oldSkcPercentageSuggest = 0D; // 秋冬老品

    private double midCategorySuggestSkcPercent = 0D; //  中类建议占比

    private long midCategorySkc; //  中类SKC数

    private long midCategorySalesSkc = 0;

    private long midCategoryApplySkc = 0;

    private long midCategoryPathSkc = 0;

    private long midCategoryIssueSkc; // 中类可分配SKC数

    private long midCategoryHadIssueSkc = 0;

    private long newGoodsHadIssueSkc = 0; // 新品中类已分配skc数

    private Date createdAt;

    /**
     *  辅助字段，不落库
     */
    private String regionName; // 区域名称

    private String shopCode; // 门店编码

    private double displayRatio = 1D; // 陈列系数

    private String matCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
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

    public int getMaleStandardSkc() {
        return maleStandardSkc;
    }

    public void setMaleStandardSkc(int maleStandardSkc) {
        this.maleStandardSkc = maleStandardSkc;
    }

    public int getFemaleStandardSkc() {
        return femaleStandardSkc;
    }

    public void setFemaleStandardSkc(int femaleStandardSkc) {
        this.femaleStandardSkc = femaleStandardSkc;
    }

    public Double getOldSkcPercentageSuggest() {
        return oldSkcPercentageSuggest;
    }

    public void setOldSkcPercentageSuggest(Double oldSkcPercentageSuggest) {
        this.oldSkcPercentageSuggest = oldSkcPercentageSuggest;
    }

    public Double getMidCategorySuggestSkcPercent() {
        return midCategorySuggestSkcPercent;
    }

    public void setMidCategorySuggestSkcPercent(Double midCategorySuggestSkcPercent) {
        this.midCategorySuggestSkcPercent = midCategorySuggestSkcPercent;
    }

    public void setMidCategorySkc(int midCategorySkc) {
        this.midCategorySkc = midCategorySkc;
    }

    public void setMidCategorySkc(long midCategorySkc) {
        this.midCategorySkc = midCategorySkc;
    }

    public long getMidCategorySalesSkc() {
        return midCategorySalesSkc;
    }

    public void setMidCategorySalesSkc(long midCategorySalesSkc) {
        this.midCategorySalesSkc = midCategorySalesSkc;
    }

    public long getMidCategoryApplySkc() {
        return midCategoryApplySkc;
    }

    public void setMidCategoryApplySkc(long midCategoryApplySkc) {
        this.midCategoryApplySkc = midCategoryApplySkc;
    }

    public long getMidCategoryPathSkc() {
        return midCategoryPathSkc;
    }

    public void setMidCategoryPathSkc(long midCategoryPathSkc) {
        this.midCategoryPathSkc = midCategoryPathSkc;
    }


    /**
     * 中类建议skc数公式
     * 建议陈列量 * 中类skc数占比 * 建议陈列系数
     * @return
     */
    public long getMidCategorySkc() {

        int displayQty = "男装".equals(this.getCategoryName()) ? this.getMaleStandardSkc() : this.getFemaleStandardSkc();

        return Math.round(displayQty * this.midCategorySuggestSkcPercent * this.displayRatio);
    }

    /**
     * 可分配skc数
     * 计算公式：
     * (建议中类skc数 - 在售skc数 - 在途skc数 - 在配skc数) * 秋冬老品skc占比 - 新品已配发skc数
     *
     * @return
     */
    public long getMidCategoryIssueSkc() {
        if (this.midCategoryIssueSkc == 0) {
            long midSkc = this.getMidCategorySkc();
            long skc = Math.round((midSkc - this.midCategorySalesSkc - this.midCategoryPathSkc - this.midCategoryApplySkc) * this.oldSkcPercentageSuggest) - this.newGoodsHadIssueSkc;

            if (this.newGoodsHadIssueSkc > 0) {
                LoggerUtil.debug(logger,"[NEWS_SKC_CALC] msg=suggest:{0},sales:{1},path:{2},apply:{3},percent:{4},new:{5},result:{6}",
                        midSkc,this.midCategorySalesSkc,this.midCategoryPathSkc,this.midCategoryApplySkc,this.oldSkcPercentageSuggest,this.newGoodsHadIssueSkc);
            }

            return skc <=0 ? 0 : skc;
        }
        return this.midCategoryIssueSkc;
    }


    public void setMidCategoryIssueSkc(long midCategoryIssueSkc) {
        this.midCategoryIssueSkc = midCategoryIssueSkc;
    }

    public long getMidCategoryHadIssueSkc() {
        return midCategoryHadIssueSkc;
    }

    public void setMidCategoryHadIssueSkc(long midCategoryHadIssueSkc) {
        this.midCategoryHadIssueSkc = midCategoryHadIssueSkc;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public double getDisplayRatio() {
        return displayRatio;
    }

    public void setDisplayRatio(double displayRatio) {
        this.displayRatio = displayRatio;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public long getNewGoodsHadIssueSkc() {
        return newGoodsHadIssueSkc;
    }

    public void setNewGoodsHadIssueSkc(long newGoodsHadIssueSkc) {
        this.newGoodsHadIssueSkc = newGoodsHadIssueSkc;
    }
}
