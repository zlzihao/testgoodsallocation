package cn.nome.saas.cart.feign;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

@ApiModel("满N减1列表对象")
public class CampaignModel implements Serializable {
    private static final long serialVersionUID = -6079867704519827122L;
    @ApiModelProperty("活动id")
    private Integer id;

    @ApiModelProperty(value = "活动名称")
    private String name;

    private String tag;

    @ApiModelProperty(value = "活动类型：1，买n减1")
    private Integer campaignType;

    @ApiModelProperty(value = "活动优惠数量")
    private Integer subType;

    @ApiModelProperty("活动状态")
    private Integer campaignStatus;

    @ApiModelProperty(value = "活动对象, 1:全部;2：新用户")
    private String target;

    @ApiModelProperty(value = "活动开始时间")
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间")
    private Date endTime;

    @ApiModelProperty("扩展字段，为json字符串")
    private String extension;

    @ApiModelProperty(value = "选品集id")
    private Integer productSetId;

    public Integer getProductSetId() {
        return productSetId;
    }

    public void setProductSetId(Integer productSetId) {
        this.productSetId = productSetId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(Integer campaignType) {
        this.campaignType = campaignType;
    }

    public Integer getSubType() {
        return subType;
    }

    public void setSubType(Integer subType) {
        this.subType = subType;
    }

    public Integer getCampaignStatus() {
        return campaignStatus;
    }

    public void setCampaignStatus(Integer campaignStatus) {
        this.campaignStatus = campaignStatus;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
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

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "CampaignModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tag='" + tag + '\'' +
                ", campaignType=" + campaignType +
                ", subType=" + subType +
                ", campaignStatus=" + campaignStatus +
                ", target='" + target + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", extension='" + extension + '\'' +
                ", productSetId=" + productSetId +
                '}';
    }
}