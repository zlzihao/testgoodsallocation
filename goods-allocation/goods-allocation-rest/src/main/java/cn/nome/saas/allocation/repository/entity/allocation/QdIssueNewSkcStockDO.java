package cn.nome.saas.allocation.repository.entity.allocation;

import java.util.Date;

/**
 * QdIssueNewSkcStockDO
 *
 * @author Bruce01.fan
 * @date 2019/10/28
 */
public class QdIssueNewSkcStockDO {

    private Integer id;

    private Integer taskId;

    private String shopId;

    private long newSuggestSkc = 0; //  新品建议SKC数

    private long shopSalesSkc = 0;

    private long shopApplySkc = 0;

    private long shopPathSkc = 0;

    private long newIssueSkc = 0;

    private long newHadIssueSkc = 0;

    private Date createdAt;

    // 不入库的字段
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

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public long getNewSuggestSkc() {
        return newSuggestSkc;
    }

    public void setNewSuggestSkc(long newSuggestSkc) {
        this.newSuggestSkc = newSuggestSkc;
    }

    public long getShopSalesSkc() {
        return shopSalesSkc;
    }

    public void setShopSalesSkc(long shopSalesSkc) {
        this.shopSalesSkc = shopSalesSkc;
    }

    public long getShopApplySkc() {
        return shopApplySkc;
    }

    public void setShopApplySkc(long shopApplySkc) {
        this.shopApplySkc = shopApplySkc;
    }

    public long getShopPathSkc() {
        return shopPathSkc;
    }

    public void setShopPathSkc(long shopPathSkc) {
        this.shopPathSkc = shopPathSkc;
    }

    /**
     * 新品初始可分配skc数=新品建议skc数- 门店总在售skc数 - 门店总在途skc数 - 门店总在配skc数
     * @return
     */
    public long getNewIssueSkc() {
        if (newIssueSkc == 0) {
            this.newIssueSkc = this.newSuggestSkc - this.shopSalesSkc - this.shopPathSkc - this.shopApplySkc;
        }
        return this.newIssueSkc;
    }

    public void setNewIssueSkc(long newIssueSkc) {
        this.newIssueSkc = newIssueSkc;
    }

    public long getNewHadIssueSkc() {
        return newHadIssueSkc;
    }

    public void setNewHadIssueSkc(long newHadIssueSkc) {
        this.newHadIssueSkc = newHadIssueSkc;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }
}
