package cn.nome.saas.allocation.model.old.issue;

import java.util.Date;
import java.util.List;

/**
 * @author bare
 */
public class OrderListParam {

    private int taskId;
    private String shopName;
    private List<String> issueTime;
    private String regioneName;
    private String subRegoneName;
    private String cityName;
    private Date createdBegin;
    private Date createdEnd;

    private Integer offset;
    private Integer pageSize;

    private List<String> shopIds;
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

    public List<String> getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(List<String> issueTime) {
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

    public Date getCreatedBegin() {
        return createdBegin;
    }

    public void setCreatedBegin(Date createdBegin) {
        this.createdBegin = createdBegin;
    }

    public Date getCreatedEnd() {
        return createdEnd;
    }

    public void setCreatedEnd(Date createdEnd) {
        this.createdEnd = createdEnd;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getShopIds() {
        return shopIds;
    }

    public void setShopIds(List<String> shopIds) {
        this.shopIds = shopIds;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}