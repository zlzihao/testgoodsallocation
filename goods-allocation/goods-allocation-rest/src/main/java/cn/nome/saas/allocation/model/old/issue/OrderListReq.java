package cn.nome.saas.allocation.model.old.issue;

import cn.nome.platform.common.utils.ToString;

/**
 * @author bare
 */
public class OrderListReq extends ToString {
    private static final long serialVersionUID = -3171660219195919830L;

    private int taskId;
    private String shopName;
    private String issueTime;
    private String regioneName;
    private String subRegoneName;
    private String cityName;
    private String createdBegin;
    private String createdEnd;

    private Integer curPage;
    private Integer pageSize;

    private String userName;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public String getRegioneName() {
        return regioneName;
    }

    public void setRegioneName(String regioneName) {
        this.regioneName = regioneName;
    }

    public String getSubRegoneName() {
        return subRegoneName;
    }

    public void setSubRegoneName(String subRegoneName) {
        this.subRegoneName = subRegoneName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCreatedBegin() {
        return createdBegin;
    }

    public void setCreatedBegin(String createdBegin) {
        this.createdBegin = createdBegin;
    }

    public String getCreatedEnd() {
        return createdEnd;
    }

    public void setCreatedEnd(String createdEnd) {
        this.createdEnd = createdEnd;
    }

    public Integer getCurPage() {
        return curPage;
    }

    public void setCurPage(Integer curPage) {
        this.curPage = curPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}