package cn.nome.saas.allocation.repository.entity.allocation;

import java.util.Date;

/**
 * 深度指引
 *
 * @author Bruce01.fan
 * @date 2019/8/5
 */
public class QdIssueDepthSuggestDO {

    private Integer id;

    private String matchType; // 内外搭(1-内搭 2-外搭 3-下装)

    private Integer level;

    private Integer depth;

    private Double avgDepth;

    private Date createdAt;

    /**
     * 是否处理，1-是，0-否
     */
    private Integer isDeal;

    /**
     * 处理结果
     */
    private String remark;

    /**
     * 类型： 内外搭、中类、货号
     */
    private String typeName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public Double getAvgDepth() {
        return avgDepth;
    }

    public void setAvgDepth(Double avgDepth) {
        this.avgDepth = avgDepth;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getIsDeal() {
        return isDeal;
    }

    public void setIsDeal(Integer isDeal) {
        this.isDeal = isDeal;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
