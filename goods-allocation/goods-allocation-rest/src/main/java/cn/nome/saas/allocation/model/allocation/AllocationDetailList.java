package cn.nome.saas.allocation.model.allocation;

import java.util.List;

/**
 * AllocationDetailList
 *
 * @author Bruce01.fan
 * @date 2019/8/29
 */
public class AllocationDetailList {

    private long total;

    private long totalPage;

    private List<AllocationDetail> list;

    private List<AllocationDetail> failList;

    private String failMsg;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public List<AllocationDetail> getList() {
        return list;
    }

    public void setList(List<AllocationDetail> list) {
        this.list = list;
    }

    public List<AllocationDetail> getFailList() {
        return failList;
    }

    public void setFailList(List<AllocationDetail> failList) {
        this.failList = failList;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }
}
