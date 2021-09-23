package cn.nome.saas.search.model;

import java.util.List;

/**
 * 前缀搜索
 * @author chentaikuang
 */
public class ProductPrefixModel extends BaseModel {

    private List<SortModel> sortModels;
    private PageModel pageModel;

    public List<SortModel> getSortModels() {
        return sortModels;
    }

    public void setSortModels(List<SortModel> sortModels) {
        this.sortModels = sortModels;
    }

    public PageModel getPageModel() {
        return pageModel;
    }

    public void setPageModel(PageModel pageModel) {
        this.pageModel = pageModel;
    }

}
