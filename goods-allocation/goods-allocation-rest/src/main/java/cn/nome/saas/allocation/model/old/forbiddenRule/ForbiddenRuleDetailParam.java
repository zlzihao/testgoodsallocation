package cn.nome.saas.allocation.model.old.forbiddenRule;

import cn.nome.platform.common.utils.ToString;

/**
 * ForbiddenRuleDetailParam
 *
 * @author Bruce01.fan
 * @date 2019/6/10
 */
public class ForbiddenRuleDetailParam extends ToString {

    private Integer ruleId;

    private String regionCode;

    private int regionLevel = 0;

    private String categoryCode;

    private int categoryLevel = 0;

    private String modifiedBy; // 修改人

    private String queryName;

    private int page = 1;

    private int pageSize = 500;

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public int getRegionLevel() {
        return regionLevel;
    }

    public void setRegionLevel(int regionLevel) {
        this.regionLevel = regionLevel;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public int getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(int categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }
}
