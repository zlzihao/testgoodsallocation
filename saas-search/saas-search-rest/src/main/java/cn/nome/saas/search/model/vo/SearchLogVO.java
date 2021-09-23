package cn.nome.saas.search.model.vo;


import java.util.Date;

import cn.nome.platform.common.utils.ToString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "搜索日志")
public class SearchLogVO extends ToString {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "企业id")
    private Integer corpId;

    @ApiModelProperty(value = "应用id")
    private Integer appId;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "关键字")
    private String inputWord;

    @ApiModelProperty(value = "分割单词")
    private String splitWord;

    @ApiModelProperty(value = "来源")
    private String source;

    @ApiModelProperty(value = "请求ip")
    private String ip;

    @ApiModelProperty(value = "总数量")
    private Integer totalCount;

    @ApiModelProperty(value = "当前页")
    private Integer curPage;

    @ApiModelProperty(value = "当前页")
    private Integer pageSize;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "修改时间")
    private Date updatedAt;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setInputWord(String inputWord) {
        this.inputWord = inputWord;
    }

    public String getInputWord() {
        return inputWord;
    }

    public void setSplitWord(String splitWord) {
        this.splitWord = splitWord;
    }

    public String getSplitWord() {
        return splitWord;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setCurPage(Integer curPage) {
        this.curPage = curPage;
    }

    public Integer getCurPage() {
        return curPage;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

}
