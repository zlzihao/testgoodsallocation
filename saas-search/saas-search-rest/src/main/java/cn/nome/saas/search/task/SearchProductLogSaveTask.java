package cn.nome.saas.search.task;

import cn.nome.saas.search.constant.Constant;
import cn.nome.saas.search.model.LogBaseModel;
import cn.nome.saas.search.model.vo.PageVo;
import cn.nome.saas.search.model.vo.ProductSearchVoWrap;
import cn.nome.saas.search.repository.entity.SearchLogDO;
import cn.nome.saas.search.service.NmSearchService;
import cn.nome.saas.search.service.SearchLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class SearchProductLogSaveTask implements Callable<Integer> {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private LogBaseModel logBase;
    private ProductSearchVoWrap wrap;
    private String keyword;
    private SearchLogService searchLogService;
    private NmSearchService nmSearchService;

    public SearchProductLogSaveTask(ProductSearchVoWrap wrap, String keyword, LogBaseModel logBase, SearchLogService searchLogService, NmSearchService nmSearchService) {
        this.wrap = wrap;
        this.keyword = keyword;
        this.logBase = logBase;
        this.searchLogService = searchLogService;
        this.nmSearchService = nmSearchService;
    }

    @Override
    public Integer call() throws Exception {

        SearchLogDO record = new SearchLogDO();

        PageVo pageVo = wrap.getPageVo();
        if (pageVo != null) {
            record.setPageSize(pageVo.getPageSize());
            record.setCurPage(pageVo.getCurPage());
            record.setTotalCount(Integer.valueOf("" + pageVo.getTotalCount()));
        }

        record.setUserId(logBase.getUid());
        record.setCorpId(logBase.getCorpId());
        record.setAppId(logBase.getAppId());
        record.setIp(logBase.getIp());
        record.setSource(logBase.getSource());

        String sw = nmSearchService.splitWords(keyword, Constant.PRODUCT_INDEX_ALIAS);
        record.setSplitWord(sw);
        record.setInputWord(keyword.length() > 100 ? keyword.substring(0, 90) + "..." : keyword);
        int rtn = searchLogService.insertSelective(record);
        LOGGER.info("[saveSearchLog] rtn:{}", rtn);
        return rtn;
    }

}

