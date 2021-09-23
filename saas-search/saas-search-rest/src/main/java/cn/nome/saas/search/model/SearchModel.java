package cn.nome.saas.search.model;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.domain.Pageable;

/**
 * @author chentaikuang
 */
public class SearchModel {

    private QueryBuilder filterBuilder;
    private QueryBuilder queryBuilder;
    private Pageable pageable;

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    public QueryBuilder getFilterBuilder() {
        return filterBuilder;
    }

    public void setFilterBuilder(QueryBuilder filterBuilder) {
        this.filterBuilder = filterBuilder;
    }
}
