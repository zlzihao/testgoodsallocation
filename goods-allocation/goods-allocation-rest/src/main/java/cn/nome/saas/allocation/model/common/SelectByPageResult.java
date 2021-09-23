package cn.nome.saas.allocation.model.common;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

/**
 * ForbiddenRuleResult
 *
 * @author Bruce01.fan
 * @date 2019/6/11
 */
public class SelectByPageResult<T> extends ToString {

    private List<T> list;

    private int total;

    private int totalPage;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
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
