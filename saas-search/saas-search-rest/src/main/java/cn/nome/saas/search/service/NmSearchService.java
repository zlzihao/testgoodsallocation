package cn.nome.saas.search.service;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.saas.search.constant.Constant;
import cn.nome.saas.search.enums.StatusCode;
import cn.nome.saas.search.model.SearchModel;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 搜索服务
 *
 * @author chentaikuang
 */
@Service
public class NmSearchService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @ApolloConfig
    private Config config;

    private String SWITCH_IK_TOKENIZER = "SWITCH_IK_TOKENIZER";

    /**
     * 分页搜索
     *
     * @param searchModel
     * @param clz
     * @return
     */
    private Page<?> execQuery(SearchModel searchModel, Class<?> clz) {
        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        QueryBuilder queryBuilders = searchModel.getQueryBuilder();
        searchQuery.withQuery(queryBuilders);
        if (searchModel.getFilterBuilder() != null){
            searchQuery.withFilter(searchModel.getFilterBuilder());
        }
        if (searchModel.getPageable() != null) {
            searchQuery.withPageable(searchModel.getPageable());
        }
        Page<?> resp = esTemplate.queryForPage(searchQuery.build(), clz);
        return resp;
    }

    /**
     * 搜索执行方法
     *
     * @param searchModel
     * @param clz
     * @return
     */
    public Page<?> doSearch(SearchModel searchModel, Class<?> clz) {
        try {
            return execQuery(searchModel, clz);
        } catch (Exception e) {
            log.error("[doSearch] err:{}", e.getMessage());
            throw new BusinessException(StatusCode.NO_NODE_AVAILABLE_ERR.getCode(), StatusCode.NO_NODE_AVAILABLE_ERR.getMsg());
        }
    }

    /**
     * 分词方法,返回CN_WORD类型单词（去重），没有则返回原词
     *
     * @param keyword
     * @param indexName
     * @return
     */
    public String splitWords(String keyword, String indexName) {
        List<AnalyzeResponse.AnalyzeToken> ikTokenList = getAnalyzeTokens(keyword, indexName);
        Set<String> splitWords = getTokensByType(ikTokenList);
        if (splitWords.size() > 0) {
            return StringUtils.join(splitWords, "|");
        }
        return keyword;
    }

    private Set<String> getTokensByType(List<AnalyzeResponse.AnalyzeToken> ikTokenList) {
        Set<String> splitWords = new HashSet<>();
        for (AnalyzeResponse.AnalyzeToken ik : ikTokenList) {
            if (ik.getType().equalsIgnoreCase(Constant.IK_TOKEN_CN_WORD) || ik.getType().equalsIgnoreCase(Constant.IK_TOKEN_SYNONYM)) {
                splitWords.add(ik.getTerm());
            }
        }
        return splitWords;
    }

    /**
     * 根据索引名、关键字进行分词
     *
     * @param keyword
     * @param indexName
     * @return
     */
    public List<AnalyzeResponse.AnalyzeToken> getAnalyzeTokens(String keyword, String indexName) {
        AnalyzeRequestBuilder ikRequest = new AnalyzeRequestBuilder(esTemplate.getClient(), AnalyzeAction.INSTANCE, indexName, keyword);
        ikRequest.setTokenizer(config.getProperty(SWITCH_IK_TOKENIZER, Constant.TOKENIZER_IK_MAX_WORD));
        return ikRequest.execute().actionGet().getTokens();
    }

    public Set<String> getTokens(String keyword, String indexName) {
        List<AnalyzeResponse.AnalyzeToken> tokens = getAnalyzeTokens(keyword, indexName);
        return getTokensByType(tokens);
    }
}
