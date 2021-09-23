package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;
import cn.nome.saas.allocation.model.issue.IssueReserveDetailDo;
import cn.nome.saas.allocation.model.issue.NewIssueDetailDo;
import cn.nome.saas.allocation.model.issue.NewIssueInStockDo;
import cn.nome.saas.allocation.model.issue.NewIssueOutStockDo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * IssueSandBoxTask
 *
 * @author Bruce01.fan
 * @date 2019/11/29
 */
public class IssueSandBoxTask extends ToString {

    public static int READY_STATUS = 1;

    public static int RUNNING_STATUS = 2;

    public static int DONE_STATUS = 3;

    public static int FAIL_STATUS = 4;

    private int taskId;

    private String taskName;

    private Date startDate;

    private Date endDate;

    // 1-等待中 2-执行中 3-已完成 4-失败
    private int status;

    /**
     * 是否使用销售预测  0-否, 1-是
     */
    private Integer useSalePredict;

    private String creator;

    private Date createdAt;

    private Date updatedAt;

    //======ext====================
    /**
     * 备注, 保存跑批的店铺
     */
    private String remark;

    /**
     * 计算类型 1-沙盘 2-预留存
     */
    private Integer calcType;

    public static Integer CALC_TYPE_SANDBOX = 1;

    public static Integer CALC_TYPE_RESERVE = 2;

    /**
     * outStockMap<warehouseCode, <matCode_sizeId, NewIssueOutStockDo>>
     */
    private Map<String, Map<String, NewIssueOutStockDo>> issueOutMap;

    /**
     * inStock记录表 多线程操作用ConcurrentHashMap Map<shopId, <matCode_sizeId, IssueNeedStockDO>>
     */
    private Map<String, Map<String, NewIssueInStockDo>> issueInStockShopIdMap;

    /**
     * needStock记录表 多线程操作用ConcurrentHashMap Map<shopId, <matCode_sizeId, IssueNeedStockDO>>
     */
    private Map<String, Map<String, IssueNeedStockDO>> issueNeedShopIdMap;

    /**
     * detail记录表 发货日期的detailMap<issueDate, <shopId, list>>
     */
    private Map<Date, Map<String, List<NewIssueDetailDo>>> issueDetailDateMap;

    /**
     * detail预留存记录表 发货日期的detailReserveMap<shopId_matCode_sizeName, IssueReserveDetailDo>
     */
    private Map<String, IssueReserveDetailDo> issueDetailReserveDateMap;

    /**
     * 销售预算Map<String, avg>
     */
    private Map<String, BigDecimal> issueSalePredictMap;

    /**
     * 店铺的发货日期到货日期对应Map<issueDate, <shopId, arriveDate>>
     */
    private Map<Date, Map<String, Date>> issueArriveMappingMap;

    /**
     * 跑批的店铺
     */
    private Set<String> shopIds;



    public Map<Date, Map<String, Date>> getIssueArriveMappingMap() {
        return issueArriveMappingMap;
    }

    public void setIssueArriveMappingMap(Map<Date, Map<String, Date>> issueArriveMappingMap) {
        this.issueArriveMappingMap = issueArriveMappingMap;
    }

    public Set<String> getShopIds() {
        return shopIds;
    }

    public void setShopIds(Set<String> shopIds) {
        this.shopIds = shopIds;
    }

    public Map<String, BigDecimal> getIssueSalePredictMap() {
        return issueSalePredictMap;
    }

    public void setIssueSalePredictMap(Map<String, BigDecimal> issueSalePredictMap) {
        this.issueSalePredictMap = issueSalePredictMap;
    }

    public Map<String, Map<String, NewIssueOutStockDo>> getIssueOutMap() {
        return issueOutMap;
    }

    public void setIssueOutMap(Map<String, Map<String, NewIssueOutStockDo>> issueOutMap) {
        this.issueOutMap = issueOutMap;
    }

    public Map<String, Map<String, NewIssueInStockDo>> getIssueInStockShopIdMap() {
        return issueInStockShopIdMap;
    }

    public void setIssueInStockShopIdMap(Map<String, Map<String, NewIssueInStockDo>> issueInStockShopIdMap) {
        this.issueInStockShopIdMap = issueInStockShopIdMap;
    }

    public Map<String, Map<String, IssueNeedStockDO>> getIssueNeedShopIdMap() {
        return issueNeedShopIdMap;
    }

    public void setIssueNeedShopIdMap(Map<String, Map<String, IssueNeedStockDO>> issueNeedShopIdMap) {
        this.issueNeedShopIdMap = issueNeedShopIdMap;
    }

    public Map<Date, Map<String, List<NewIssueDetailDo>>> getIssueDetailDateMap() {
        return issueDetailDateMap;
    }

    public void setIssueDetailDateMap(Map<Date, Map<String, List<NewIssueDetailDo>>> issueDetailDateMap) {
        this.issueDetailDateMap = issueDetailDateMap;
    }

    public Map<String, IssueReserveDetailDo> getIssueDetailReserveDateMap() {
        return issueDetailReserveDateMap;
    }

    public void setIssueDetailReserveDateMap(Map<String, IssueReserveDetailDo> issueDetailReserveDateMap) {
        this.issueDetailReserveDateMap = issueDetailReserveDateMap;
    }

    public Integer getUseSalePredict() {
        return useSalePredict;
    }

    public void setUseSalePredict(Integer useSalePredict) {
        this.useSalePredict = useSalePredict;
    }

    public Integer getCalcType() {
        return calcType;
    }

    public void setCalcType(Integer calcType) {
        this.calcType = calcType;
    }

    public IssueSandBoxTask() {
    }

    public IssueSandBoxTask(int taskId, Date startDate, Date endDate, String remark) {
        this.taskId = taskId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
