package cn.nome.saas.search.repository.entity;


import java.util.Date;

import cn.nome.platform.common.utils.ToString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 搜索单词配置表
 **/
public class SearchWordConfDO {

    /**
     * 主键
     **/
    private Integer id;


    /**
     * 单词
     **/
    private String word;

    /**
     * 单词类型,0其他,1引导词,2热词
     **/
    private Integer wordType;

    /**
     * 跳转路径ID
     **/
    private Integer jumpId;

    /**
     * 跳转路径名称
     **/
    private String jumpName;

    /**
     * 跳转类型,0无操作,1跳活动页,2跳搜索
     **/
    private Integer jumpType;

    /**
     * 跳转路径
     **/
    private String jumpUrl;

    /**
     * 状态，0初始化,1发布,2停止
     **/
    private Integer status;

    /**
     * 排序值
     **/
    private Integer sortNum;

    /**
     * 开始时间
     **/
    private Date startTime;

    /**
     * 结束时间
     **/
    private Date endTime;

    /**
     * 发布时间
     **/
    private Date releaseTime;

    /**
     * 创建时间
     **/
    private Date createdAt;

    /**
     * 备注
     **/
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getWordType() {
        return wordType;
    }

    public void setWordType(Integer wordType) {
        this.wordType = wordType;
    }

    public Integer getJumpId() {
        return jumpId;
    }

    public void setJumpId(Integer jumpId) {
        this.jumpId = jumpId;
    }

    public String getJumpName() {
        return jumpName;
    }

    public void setJumpName(String jumpName) {
        this.jumpName = jumpName;
    }

    public Integer getJumpType() {
        return jumpType;
    }

    public void setJumpType(Integer jumpType) {
        this.jumpType = jumpType;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
