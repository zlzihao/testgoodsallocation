package cn.nome.saas.allocation.repository.entity.allocation;

/**
 * QdIssueSizeScaleDO
 *
 * @author Bruce01.fan
 * @date 2019/8/5
 */
public class QdIssueSizeScaleDO {

    private Integer id;

    private String areaName; // 大区名称

    private String categoryName;

    private String midCategoryName;

    private String modelType;

    private String sizeSegment; // 码段

    private String sizeName;

    private Double percentage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public String getSizeSegment() {
        return sizeSegment;
    }

    public void setSizeSegment(String sizeSegment) {
        this.sizeSegment = sizeSegment;
    }
}
