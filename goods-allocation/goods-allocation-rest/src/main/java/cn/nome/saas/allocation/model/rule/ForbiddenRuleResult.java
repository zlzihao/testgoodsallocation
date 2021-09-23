package cn.nome.saas.allocation.model.rule;

import cn.nome.platform.common.utils.ToString;
import cn.nome.saas.allocation.repository.entity.allocation.ForbiddenRuleDO;

import java.util.List;

/**
 * ForbiddenRuleResult
 *
 * @author Bruce01.fan
 * @date 2019/6/11
 */
public class ForbiddenRuleResult extends ToString {

    List<ForbiddenRuleDO> list;

    int total;

    int totalPage;


    public List<ForbiddenRuleDO> getList() {
        return list;
    }

    public void setList(List<ForbiddenRuleDO> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
