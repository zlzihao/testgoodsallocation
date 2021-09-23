package cn.nome.saas.search.model.vo;

/**
 * 封装分页返回数据
 */
public class PageVo {

    /**
     * 当前页
     */
    private int curPage = 0;
    /**
     * 页码
     */
    private int pageSize = 0;
    /**
     * 总页数
     */
    private int totalPage = 0;
    /**
     * 总条数
     */
    private long totalCount = 0;

    public PageVo(int curPage, int pageSize) {
        this.curPage = curPage;
        this.pageSize = pageSize;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
