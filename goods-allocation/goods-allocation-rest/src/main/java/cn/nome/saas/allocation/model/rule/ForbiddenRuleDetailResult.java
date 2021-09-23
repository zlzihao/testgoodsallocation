package cn.nome.saas.allocation.model.rule;

import cn.nome.platform.common.utils.ToString;
import cn.nome.saas.allocation.repository.entity.allocation.ForbiddenSingleItemDO;

import java.util.List;

/**
 * ForbiddenRuleDetailResult
 *
 * @author Bruce01.fan
 * @date 2019/6/10
 */
public class ForbiddenRuleDetailResult extends ToString {

    private Integer ruleId;

    private String ruleName;

    private Integer total;

    private Integer totalPage;

    private Integer currentPage;

    private List<ForbiddenSingleItemDO> list;

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public List<ForbiddenSingleItemDO> getList() {
        return list;
    }

    public void setList(List<ForbiddenSingleItemDO> list) {
        this.list = list;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return ruleName;
    }
}
