package cn.nome.saas.search.model.vo;

import java.util.ArrayList;
import java.util.List;

public class SearchListWrap {
    private List<SearchWordConfListVO> listVOS;
    private int totalCount;
    private int totalPage;
    private int curPage;
    private int pageSize;

    public List<SearchWordConfListVO> getListVOS() {
        return listVOS;
    }

    public void setListVOS(List<SearchWordConfListVO> listVOS) {
        this.listVOS = listVOS;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
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
}
