package cn.nome.saas.search.manager;

import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.utils.JsonUtils;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.search.constant.Constant;
import cn.nome.saas.search.enums.StatusCode;
import cn.nome.saas.search.model.*;
import cn.nome.saas.search.model.vo.*;
import cn.nome.saas.search.repository.entity.ProductDO;
import cn.nome.saas.search.repository.entity.SearchWordConfDO;
import cn.nome.saas.search.service.NmSearchService;
import cn.nome.saas.search.service.SearchLogService;
import cn.nome.saas.search.service.SearchWordConfService;
import cn.nome.saas.search.task.SearchProductLogSaveTask;
import cn.nome.saas.search.util.ExecutorUtil;
import com.alibaba.fastjson.JSONObject;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 搜索服务聚合
 *
 * @author chentaikuang
 */
@Component
public class NmSearchManager {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final Long DEFAULT_SUCCESS = 1L;

    @Autowired
    private NmSearchService nmSearchService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private SearchWordConfService searchWordConfService;

    @Autowired
    private SearchLogService searchLogService;

    @Autowired
    private StoreServiceManager storeServiceManager;

    @Autowired
    private MgGoodsManager mgGoodsManager;

    @ApolloConfig
    private Config config;

    @Value("${IMG_DOMAIN_URL:http://storage2.nome.cn/}")
    private String IMG_DOMAIN_URL;

    /**
     * 模糊查找
     *
     * @param model
     * @return
     */
    public Result fuzzyProduct(ProductFuzzyModel model) {

        ProductSearchVoWrap wrap = new ProductSearchVoWrap();

        String keyword = model.getKeyword();
        QueryBuilder builder = QueryBuilders.fuzzyQuery(Constant.QRY_FIELD_NAME, keyword);

        Sort sort = convertSort(model.getSortModels());
        Pageable pageable = null;
        if (model.getPageModel() != null) {
            pageable = convertPageable(model.getPageModel(), sort);
        }
        SearchModel searchModel = convertSearchModel(pageable, builder);
        Page<ProductDO> resp = (Page<ProductDO>) nmSearchService.doSearch(searchModel, ProductDO.class);
//        LOGGER.info("[FUZZY_PRODUCT] resp:{}", JSONObject.toJSONString(resp));

        if (resp == null || resp.getContent() == null || resp.getContent().isEmpty()) {
            wrap.setProductVos(Collections.emptyList());
        } else {
            convertProductResult(wrap, resp, model.getPageModel());
        }

        return ResultUtil.handleSuccessReturn(wrap);
    }

    /**
     * 分页对象转换
     *
     * @param pageModel
     * @param sort
     * @return
     */
    private Pageable convertPageable(PageModel pageModel, Sort sort) {
        if (sort == null) {
            return PageRequest.of(convertCurPage(pageModel.getCurPage()), convertPageSize(pageModel.getPageSize()));
        }
        Pageable pageable = PageRequest.of(convertCurPage(pageModel.getCurPage()), convertPageSize(pageModel.getPageSize()), sort);
//        Pageable pageable = new PageRequest(convertCurPage(pageModel.getCurPage()), convertPageSize(pageModel.getPageSize()), sort);
        return pageable;
    }

    /**
     * 关键词字符串分词后查询
     *
     * @param model
     * @return
     */
    public Result matchProduct(ProductMatchModel model, LogBaseModel logBaseModel) {

        ProductSearchVoWrap wrap = new ProductSearchVoWrap();
        String keyword = model.getKeyword();

        BoolQueryBuilder queryBuilder = productNameBoolQuery(keyword);
        //QueryBuilder builder = QueryBuilders.matchQuery(Constant.QRY_FIELD_NAME, keyword);

        Sort sort = convertSort(model.getSortModels());
        Pageable pageable = null;
        if (model.getPageModel() != null) {
            pageable = convertPageable(model.getPageModel(), sort);
        }
        QueryBuilder filterBuilder = convertSexFilter(keyword);
        SearchModel searchModel = convertSearchModel(pageable, queryBuilder, filterBuilder);
        Page<ProductDO> resp = (Page<ProductDO>) nmSearchService.doSearch(searchModel, ProductDO.class);
        LOGGER.info("[MATCH_PRODUCT] resp:{}", JSONObject.toJSONString(resp));

        if (resp == null || resp.getContent() == null || resp.getContent().isEmpty()) {
            convertNullResult(wrap, model.getPageModel());
        } else {
            convertProductResult(wrap, resp, model.getPageModel());
            getDiscProducts(wrap.getProductVos(), logBaseModel);
            getProductTag(wrap.getProductVos(), logBaseModel);
        }

        insertLog(wrap, model, logBaseModel);

        return ResultUtil.handleSuccessReturn(wrap);
    }

    private void getProductTag(List<ProductVo> productVos, LogBaseModel baseModel) {
        List<Integer> ids = new ArrayList<>(productVos.size());
        productVos.stream().forEach(productVo -> ids.add(productVo.getId()));
        Map<Integer, Integer> rstMap = mgGoodsManager.productTagList(ids, baseModel.getCorpId(), baseModel.getAppId());
        if (rstMap == null || rstMap.isEmpty()) {
            return;
        }
        productVos.stream().forEach(productVo -> convertTag(productVo, rstMap));
    }

    /**
     * 商品赋值标签id
     *
     * @param productVo
     * @param rstMap
     */
    private void convertTag(ProductVo productVo, Map<Integer, Integer> rstMap) {
        if (rstMap.containsKey(productVo.getId())) {
            productVo.setTagId(rstMap.get(productVo.getId()));
        }
    }

    /**
     * 获取折扣商品信息
     *
     * @param productVos
     * @param baseModel
     */
    private void getDiscProducts(List<ProductVo> productVos, LogBaseModel baseModel) {
        List<Integer> ids = new ArrayList<>(productVos.size());
        productVos.stream().forEach(productVo -> ids.add(productVo.getId()));
        Map<Integer, DiscProductVo> rstMap = storeServiceManager.getDiscProductByIds(ids, baseModel.getCorpId(), baseModel.getAppId(), baseModel.getUid());
        if (rstMap == null || rstMap.isEmpty()) {
            return;
        }
        productVos.stream().forEach(productVo -> convertDiscInfo(productVo, rstMap));
    }

    /**
     * 转换折扣信息
     *
     * @param productVo
     * @param rstMap
     */
    private void convertDiscInfo(ProductVo productVo, Map<Integer, DiscProductVo> rstMap) {
        if (rstMap.containsKey(productVo.getId())) {
            DiscProductVo discProduct = rstMap.get(productVo.getId());
            productVo.setDiscPrice(discProduct.getDiscountPrice());
            productVo.setMinPrice(discProduct.getPrice());
            productVo.setImg(discProduct.getImage());
        }
    }

    private void convertNullResult(ProductSearchVoWrap wrap, PageModel pageModel) {
        PageVo pageVo = new PageVo(pageModel.getCurPage(), pageModel.getPageSize());
        pageVo.setTotalCount(0);
        pageVo.setTotalPage(0);
        wrap.setPageVo(pageVo);
        wrap.setProductVos(Collections.emptyList());
    }

    private SearchModel convertSearchModel(Pageable pageable, BoolQueryBuilder builder, QueryBuilder filterBuilder) {
        SearchModel model = convertSearchModel(pageable, builder);
        if (filterBuilder != null) {
            model.setFilterBuilder(filterBuilder);
        }
        return model;
    }

    private QueryBuilder convertSexFilter(String keyword) {
        QueryBuilder filterBuilder = null;
        if (openSexQryFilterSwitch()) {
            String filterWord = null;
            if (keyword.contains(Constant.SEX_MAN)) {
                filterWord = Constant.SEX_WOMEN;
            } else if (keyword.contains(Constant.SEX_WOMEN)) {
                filterWord = Constant.SEX_MAN;
            }
            if (StringUtils.isNotBlank(filterWord)) {
                filterBuilder = QueryBuilders.boolQuery().mustNot(QueryBuilders.prefixQuery(Constant.QRY_FIELD_CATE_PROD_NAME, filterWord));
            }
        }
        return filterBuilder;
    }

    /**
     * 插入搜索日志
     *
     * @param wrap
     * @param model
     * @param logBaseModel
     */
    private void insertLog(ProductSearchVoWrap wrap, ProductMatchModel model, LogBaseModel logBaseModel) {
        if (cacheManager.openSaveSearchLog()) {
            SearchProductLogSaveTask task = new SearchProductLogSaveTask(wrap, model.getKeyword(), logBaseModel, searchLogService, nmSearchService);
            Future<Integer> rtn = ExecutorUtil.execute(task);
            try {
                LOGGER.debug("[insertLog] rtn:{}", rtn.get());
            } catch (Exception e) {
                LOGGER.error("[insertLog] err:{}", e.getMessage());
            }
        }
    }

    /**
     * 商品名搜索Builder
     *
     * @param keyword
     * @return
     */
    private BoolQueryBuilder productNameBoolQuery(String keyword) {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(QueryBuilders.termQuery(Constant.QRY_FIELD_DISPLAY, true));
        builder.must(QueryBuilders.rangeQuery(Constant.QRY_FIELD_SPU_STOCK_COUNT).gt(0));
        if (cacheManager.searchCateProdNameSwitch()) {
            builder.must(QueryBuilders.matchQuery(Constant.QRY_FIELD_CATE_PROD_NAME, keyword).minimumShouldMatch(miniShouldMatch()));
        } else if (cacheManager.searchProdNameSwitch()) {
            builder.must(QueryBuilders.matchQuery(Constant.QRY_FIELD_NAME, keyword).minimumShouldMatch(miniShouldMatch()));
        } else {
            builder.must(QueryBuilders.multiMatchQuery(keyword, Constant.QRY_FIELD_PROD_NAME, Constant.QRY_FIELD_CATE_NAME).minimumShouldMatch(miniShouldMatch()));
        }
        return builder;
    }

    /**
     * 获取搜索最少匹配精度
     *
     * @return
     */
    private String miniShouldMatch() {
        return config.getProperty(Constant.MINI_SHOULD_MATCH, Constant.MINI_SHOULD_MATCH_DEFAULT_VAL);
    }

    /**
     * 搜索词截取
     *
     * @param keyword
     * @return
     */
    private QueryBuilder queryBuilderSplitKw(String keyword) {
        String[] kws = keyword.split("\\s+");//空格切割
        return QueryBuilders.termsQuery(Constant.QRY_FIELD_NAME, kws);
    }

    /**
     * 全文检索
     *
     * @param model
     * @return
     */
    public Result fullNameProduct(ProductFullNameModel model) {

        ProductSearchVoWrap wrap = new ProductSearchVoWrap();

        String keyword = model.getKeyword();
        /**
         * matchQuery 会将搜索词分词，再与目标查询字段进行匹配，若分词中的任意一个词与目标字段匹配上，则可查询到。
         * termQuery 不会对搜索词进行分词处理，而是作为一个整体与目标字段进行匹配，若完全匹配，则可查询到。
         */
        QueryBuilder builder = QueryBuilders.termQuery(Constant.QRY_FIELD_FULL_NAME, keyword);

        Pageable pageable = null;
        SearchModel searchModel = convertSearchModel(pageable, builder);
        Page<ProductDO> resp = (Page<ProductDO>) nmSearchService.doSearch(searchModel, ProductDO.class);
//        LOGGER.info("[FULL_NAME_PRODUCT] resp:{}", JSONObject.toJSONString(resp));

        if (resp == null || resp.getContent() == null || resp.getContent().isEmpty()) {
            wrap.setProductVos(Collections.emptyList());
        } else {
            convertProductResult(wrap, resp, null);
        }

        return ResultUtil.handleSuccessReturn(wrap);
    }

    /**
     * 前缀查找
     *
     * @param model
     * @return
     */
    public Result prefixProduct(ProductPrefixModel model) {
        ProductSearchVoWrap wrap = new ProductSearchVoWrap();

        String keyword = model.getKeyword();
        QueryBuilder builder = QueryBuilders.prefixQuery(Constant.QRY_FIELD_FULL_NAME, keyword);

        Sort sort = convertSort(model.getSortModels());
        Pageable pageable = null;
        if (model.getPageModel() != null) {
            pageable = convertPageable(model.getPageModel(), sort);
        }
        SearchModel searchModel = convertSearchModel(pageable, builder);
        Page<ProductDO> resp = (Page<ProductDO>) nmSearchService.doSearch(searchModel, ProductDO.class);
//        LOGGER.info("[PREFIX_PRODUCT] resp:{}", JSONObject.toJSONString(resp));

        if (resp == null || resp.getContent() == null || resp.getContent().isEmpty()) {
            wrap.setProductVos(Collections.emptyList());
        } else {
            convertProductResult(wrap, resp, model.getPageModel());
        }

        return ResultUtil.handleSuccessReturn(wrap);
    }

    /**
     * 转换搜索商品数据
     *
     * @param wrap
     * @param resp
     */
    private void convertProductResult(ProductSearchVoWrap wrap, Page<ProductDO> resp, PageModel pageModel) {
        List<ProductDO> content = resp.getContent();
        List<ProductVo> vos = new ArrayList<>(content.size());
        List<ProductVo> emptyStock = new ArrayList<>();

//        content.stream().forEach(cont -> vos.add(new ProductVo(cont.getId(), cont.getName(),
//                cont.getMaxPrice(), cont.getMinPrice(), convetImg(cont.getProdImg()), cont.getSpuStockCount())));

        Iterator<ProductDO> itr = content.iterator();
        while (itr.hasNext()) {
            ProductDO cont = itr.next();
            ProductVo vo = new ProductVo(cont.getId(), cont.getName(), cont.getMaxPrice(), cont.getMinPrice(), convetImg(cont.getProdImg()), cont.getSpuStockCount());
            //LOGGER.info("id:{}",vo.getId());
            //有库存
            if (cont.getSpuStockCount() >= 1) {
                vos.add(vo);
            } else {
                emptyStock.add(vo);
            }
        }
        //合并库存为空的商品到后面去
        if (emptyStock.size() > 0) {
            vos.addAll(emptyStock);
        }
        wrap.setProductVos(vos);

        if (pageModel != null) {
            PageVo pageVo = new PageVo(pageModel.getCurPage(), pageModel.getPageSize());
            pageVo.setTotalCount(resp.getTotalElements());
            pageVo.setTotalPage(convertTotalPage(pageVo.getTotalCount(), pageVo.getPageSize()));
            wrap.setPageVo(pageVo);
        }
    }

    /**
     * 商品图片拼接
     *
     * @param prodImg
     * @return
     */
    private String convetImg(String prodImg) {
        if (StringUtils.isNotBlank(prodImg)) {
//            LOGGER.info("IMG_DOMAIN_URL:{}",IMG_DOMAIN_URL.hashCode());
            return IMG_DOMAIN_URL + prodImg;
        }
        return "";
    }

    /**
     * 根据id降序
     *
     * @return
     */
    private Sort sortDescById() {
        return new Sort(Sort.Direction.DESC, Constant.SORT_BY_ID);
    }

    /**
     * 根据id升序
     *
     * @return
     */
    private Sort sortAscById() {
        return new Sort(Sort.Direction.ASC, Constant.SORT_BY_ID);
    }

    /**
     * 封装底层搜索参数
     *
     * @param pageable
     * @param builder
     * @return
     */
    private SearchModel convertSearchModel(Pageable pageable, QueryBuilder builder) {
        SearchModel searchModel = new SearchModel();
        searchModel.setQueryBuilder(builder);
        searchModel.setPageable(pageable);
        return searchModel;
    }

    /**
     * 转换总页数
     *
     * @param totalCount
     * @param pageSize
     * @return
     */
    private int convertTotalPage(long totalCount, int pageSize) {
        if (totalCount <= 0 || pageSize <= 0) {
            return 0;
        }
        return (int) ((totalCount - 1) / pageSize + 1);
    }

    /**
     * 转换当前页
     *
     * @param curPage
     * @return
     */
    private int convertCurPage(int curPage) {
        if (curPage < 1) {
            return 0;
        }
        return curPage - 1;
    }

    /**
     * 转换当前页条数
     *
     * @param pageSize
     * @return
     */
    private int convertPageSize(int pageSize) {
        if (pageSize < 1) {
            return 10;
        }
        return pageSize;
    }


    /**
     * 转换排序
     *
     * @param sortModelList
     * @return
     */
    private Sort convertSort(List<SortModel> sortModelList) {
        if (sortModelList == null || sortModelList.isEmpty()) {
            return null;
        }
        Iterator<SortModel> itr = sortModelList.iterator();
        List sortByList = new ArrayList();
        while (itr.hasNext()) {
            SortModel sortModel = itr.next();
            if (StringUtils.isBlank(sortModel.getDire()) || StringUtils.isBlank(sortModel.getFieldName())
                    || noContainSortField(sortModel.getFieldName())) {
                continue;
            }
            sortByList.add(new Sort.Order(convertSortDire(sortModel.getDire()), sortModel.getFieldName()));
        }
        return new Sort(sortByList);
    }

    /**
     * 是否匹配字段：排序字段和索引中不一致，会报错，需过滤or约束
     *
     * @param fieldName
     * @return
     */
    private boolean noContainSortField(String fieldName) {
        try {
            Field field = ProductDO.class.getDeclaredField(fieldName);
            if (field != null) {
                return false;
            }
        } catch (NoSuchFieldException e) {
            LOGGER.warn("[NO_CONTAIN_SORT_FIELD] err:{}", e.getMessage());
        }
        return true;
    }

    /**
     * 转换升、降序
     *
     * @param dire
     * @return
     */
    private Sort.Direction convertSortDire(String dire) {
        if (dire.equals(Constant.SORT_FIELD_DESC)) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    @Deprecated
    public String addRemoteDict(String keyword) {
        String rtnStr = "";
        if (StringUtils.isBlank(keyword)) {
            return rtnStr;
        }
        StringBuilder sbf = new StringBuilder();
        Long rtn = null;
        String[] kwArr = keyword.split(",");
        //已有热词
        Map<String, String> hotWords = getCacheHotWords();
        //新增热词
        Map<String, String> addWordsMap = convertWordMap(kwArr);
        Iterator<Map.Entry<String, String>> itr = addWordsMap.entrySet().iterator();
        if (hotWords == null || hotWords.isEmpty()) {
            while (itr.hasNext()) {
                Map.Entry<String, String> wordEntity = itr.next();
                rtn = cacheManager.addRemoteDict(wordEntity.getKey(), wordEntity.getValue());
                if (rtn != null) {
                    sbf.append(wordEntity.getValue()).append(",");
                }
            }
        } else {
            while (itr.hasNext()) {
                Map.Entry<String, String> addWord = itr.next();
                String addKey = addWord.getKey();
                String addVal = addWord.getValue();
                if (hotWords.containsKey(addKey)) {
                    List<String> addWordList = splitWords(addVal);
                    List<String> oldWordList = splitWords(hotWords.get(addKey));
                    addWordList.addAll(oldWordList);
                    rtn = cacheManager.addRemoteDict(addKey, StringUtils.join(delRepeatWords(addWordList), ","));
                } else {
                    rtn = cacheManager.addRemoteDict(addKey, addVal);
                }
                if (rtn != null) {
                    sbf.append(addVal).append(",");
                }
            }
        }
        if (sbf.lastIndexOf(",") > 0) {
            rtnStr = sbf.toString().substring(0, sbf.length() - 1);
        }
        return rtnStr;
    }

    /**
     * 关键字去重
     *
     * @param words
     * @return
     */
    private List<String> delRepeatWords(List<String> words) {
        HashSet<String> wordSet = new HashSet(words);
        words.clear();
        words.addAll(wordSet);
        return words;
    }

    /**
     * 单词逗号分割
     *
     * @param words
     * @return
     */
    private List<String> splitWords(String words) {
        //LOGGER.info("[splitWords] words:{}", words);
        List<String> wordList = null;
        if (words.indexOf(",") > 0) {
            String[] arr = words.split(",");
            wordList = new ArrayList<>(arr.length);
            for (String str : arr) {
                if (wordList.contains(str)) {
                    continue;
                }
                wordList.add(str);
            }
        } else {
            wordList = new ArrayList<>(1);
            wordList.add(words);
        }
        return wordList;
    }

    /**
     * 汉字转换拼音map
     *
     * @param kwArr
     * @return
     */
    private Map<String, String> convertWordMap(String[] kwArr) {
        Map<String, String> map = new HashMap<>(kwArr.length);
        String wordKey = null;
        for (String kw : kwArr) {
            //wordKey = PinYinUtil.getPinYin(kw);
            wordKey = kw.hashCode() + "";
            if (map.containsKey(wordKey)) {
                map.put(wordKey, map.get(wordKey) + "," + kw);
            } else {
                map.put(wordKey, kw);
            }

        }
        return map;
    }

    @Deprecated
    public Map<String, String> getCacheHotWords() {
        Map<String, String> hotWords = cacheManager.getRemoteDict();
        return hotWords;
    }

    @Deprecated
    public String readRemoteDict() {
        Map<String, String> map = getCacheHotWords();
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder sbf = new StringBuilder();
        Iterator<Map.Entry<String, String>> itr = map.entrySet().iterator();
        while (itr.hasNext()) {
            sbf.append(itr.next().getValue()).append("\r\n");
        }
        return sbf.toString();
    }

    @Deprecated
    public String delRemoteDict(String keyword) {
        String rtnStr = "";
        if (StringUtils.isBlank(keyword)) {
            return rtnStr;
        }
        StringBuilder sbf = new StringBuilder();
        Long rtn = null;
        String[] kwArr = keyword.split(",");
        Map<String, String> hotWords = getCacheHotWords();
        Map<String, String> delWordsMap = convertWordMap(kwArr);
        Iterator<Map.Entry<String, String>> itr = delWordsMap.entrySet().iterator();

        List<String> delWordList = null;
        List<String> oldWordList = null;
        //原来热词为空，则直接退出无须删除
        if (hotWords == null || hotWords.isEmpty()) {
            return rtnStr;
        }

        while (itr.hasNext()) {
            Map.Entry<String, String> delWord = itr.next();
            String delKey = delWord.getKey();
            String delVal = delWord.getValue();
            if (hotWords.containsKey(delKey)) {
                oldWordList = splitWords(hotWords.get(delKey));
                delWordList = splitWords(delVal);
                oldWordList.removeAll(delWordList);
                if (oldWordList.isEmpty()) {
                    rtn = cacheManager.delRemoteDict(delKey);
                } else {
                    rtn = cacheManager.addRemoteDict(delKey, StringUtils.join(oldWordList, ","));
                }
            } else {
                LOGGER.warn("[delRemoteDict] no containsKey:{}", delKey);
                rtn = DEFAULT_SUCCESS;
            }
            if (rtn != null) {
                sbf.append(delVal).append(",");
            }
        }
        if (sbf.lastIndexOf(",") > 0) {
            rtnStr = sbf.toString().substring(0, sbf.length() - 1);
        }
        return rtnStr;
    }

    /**
     * 加载远程词典串
     *
     * @return
     */
    public String loadRemoteDictStr() {
        RemoteDictVoWrap dictVo = getRemoteDictWrap();
        if (dictVo == null) {
            return "";
        }
        String words = dictVo.getWords();
        List<String> wordList = splitWords(words);
        StringBuilder sbf = new StringBuilder();
        for (String word : wordList) {
            sbf.append(word).append("\r\n");
        }
        return sbf.toString();
    }

    /**
     * 获取远程词典
     *
     * @return
     */
    public RemoteDictVoWrap getRemoteDictWrap() {
        RemoteDictVoWrap wrap = null;
        String remoteDict = cacheManager.loadRemoteDictWords();
        if (StringUtils.isBlank(remoteDict)) {
            return new RemoteDictVoWrap();
        }
        wrap = JsonUtils.parse(remoteDict, RemoteDictVoWrap.class);
        return wrap;
    }

    /**
     * 更新远程词典
     *
     * @param words
     * @return
     */
    public Result update(String words) {

        List<String> wordList = splitWords(words);
        RemoteDictVoWrap wrap = new RemoteDictVoWrap();
        wrap.setDate(DateUtil.format(new Date(), DateUtil.DATE_AND_TIME));
        wrap.setWords(StringUtils.join(wordList, ","));
        String save = cacheManager.saveRemoteDictWords(JSONObject.toJSONString(wrap));

        if (StringUtils.isBlank(save)) {
            LOGGER.error("[UPDATE] save rtn:{}", save);
            return ResultUtil.handleFailtureReturn(StatusCode.SET_REMOTE_DICT_ERR.getCode(), StatusCode.SET_REMOTE_DICT_ERR.getMsg());
        }
        return ResultUtil.handleSuccessReturn(save);
    }

    /**
     * 封装并加载远程词典
     *
     * @return
     */
    public Result loadRemoteDictWrap() {
        try {
            RemoteDictVoWrap wrap = getRemoteDictWrap();
            return ResultUtil.handleSuccessReturn(wrap);
        } catch (Exception e) {
            LOGGER.error("[loadRemoteDictWrap] err msg:{}", e.getMessage());
            return ResultUtil.handleFailtureReturn(StatusCode.FAIL.getCode(), e.getMessage());
        }
    }

    /**
     * 根据类型获取词典列表
     *
     * @param model
     * @return
     */
    public Result validWords(SearchWordConfListModel model) {

        int pageSize = model.getPageSize();
        int curPage = model.getCurPage();
        SearchListWrap wrap = new SearchListWrap();
        wrap.setCurPage(curPage);
        wrap.setPageSize(pageSize);

        int count = searchWordConfService.validCountWordsByType(model.getWordType());
        wrap.setTotalCount(count);
        int totalPage = convertTotalPage(count, pageSize);
        wrap.setTotalPage(totalPage);
        if (count == 0) {
            List<SearchWordConfListVO> listVOS = Collections.emptyList();
            wrap.setListVOS(listVOS);
            return ResultUtil.handleSuccessReturn(wrap);
        }

        int offset = (curPage - 1) * pageSize;
        List<SearchWordConfDO> words = searchWordConfService.validPageWordsByType(model.getWordType(), offset, pageSize);
        List<SearchWordConfListVO> detailVOS = convertVaildWords(words);
        wrap.setListVOS(detailVOS);

        return ResultUtil.handleSuccessReturn(wrap);
    }

    private List<SearchWordConfListVO> convertVaildWords(List<SearchWordConfDO> confDOS) {
        if (confDOS == null || confDOS.isEmpty()) {
            return Collections.emptyList();
        }
        List<SearchWordConfListVO> detailVOS = new ArrayList<>(confDOS.size());
        for (SearchWordConfDO confDO : confDOS) {
            SearchWordConfListVO detailVO = new SearchWordConfListVO();
            BeanUtils.copyProperties(confDO, detailVO);
            detailVO.setStartTime(DateUtil.format(confDO.getStartTime(), DateUtil.DATE_AND_TIME));
            detailVO.setEndTime(DateUtil.format(confDO.getEndTime(), DateUtil.DATE_AND_TIME));
            detailVOS.add(detailVO);
        }
        return detailVOS;
    }

    /**
     * 修改单词配置
     *
     * @param model
     * @return
     */
    public Result modifyWordConf(SearchWordConfModifyModel model) {

        SearchWordConfDO confDO = searchWordConfService.selectByPrimaryKey(model.getId());
        Assert.notNull(confDO, StatusCode.NO_FOUND_WORD.getMsg());

        confDO.setStartTime(DateUtil.parse(model.getStartTime(), DateUtil.DATE_AND_TIME));
        confDO.setEndTime(DateUtil.parse(model.getEndTime(), DateUtil.DATE_AND_TIME));
        confDO.setJumpId(model.getJumpId());
        confDO.setJumpName(model.getJumpName());
        confDO.setJumpType(model.getJumpType());
//        confDO.setReleaseTime(model.getReleaseTime());
        confDO.setSortNum(model.getSortNum());
//        confDO.setStatus(model.getst);
        confDO.setWord(model.getWord());
        confDO.setWordType(model.getWordType());

        int rtn = searchWordConfService.updateByPrimaryKeySelective(confDO);
        if (rtn > 0) {
            return ResultUtil.handleSuccessReturn(rtn);
        }
        return ResultUtil.handleFailtureReturn(StatusCode.FAIL.getCode(), StatusCode.FAIL.getMsg());
    }

    /**
     * 单词配置明细
     *
     * @param id
     * @return
     */
    public Result getWordConf(Integer id) {
        SearchWordConfDO confDO = searchWordConfService.selectByPrimaryKey(id);
        if (confDO == null) {
            return ResultUtil.handleFailtureReturn(StatusCode.NO_FOUND_WORD.getCode(), StatusCode.NO_FOUND_WORD.getMsg());
        }
        SearchWordConfDetailVO vo = convetWordConf(confDO);
        return ResultUtil.handleSuccessReturn(vo);
    }

    private SearchWordConfDetailVO convetWordConf(SearchWordConfDO confDO) {
        SearchWordConfDetailVO detailVO = new SearchWordConfDetailVO();
        BeanUtils.copyProperties(confDO, detailVO);

        detailVO.setStartTime(convertEffectiveTime(confDO.getStartTime()));
        detailVO.setEndTime(convertEffectiveTime(confDO.getEndTime()));

        detailVO.setReleaseTime(convertReleaseTime(confDO.getReleaseTime()));
        return detailVO;
    }

    /**
     * 生效时间
     *
     * @param date
     * @return
     */
    private String convertEffectiveTime(Date date) {
        if (date == null) {
            return "";
        }
        return DateUtil.format(date, DateUtil.DATE_AND_TIME);
    }

    /**
     * 发布时间转换
     *
     * @param releaseTime
     * @return
     */
    private String convertReleaseTime(Date releaseTime) {
        if (releaseTime == null) {
            return "";
        }
        return DateUtil.format(releaseTime, DateUtil.DATE_AND_TIME);
    }

    /**
     * 添加单词配置
     *
     * @param model
     * @return
     */
    public Result addWordConf(SearchWordConfAddModel model) {
        SearchWordConfDO confDO = new SearchWordConfDO();
        BeanUtils.copyProperties(model, confDO);
        confDO.setStartTime(DateUtil.parse(model.getStartTime(), DateUtil.DATE_AND_TIME));
        confDO.setEndTime(DateUtil.parse(model.getEndTime(), DateUtil.DATE_AND_TIME));
        int rtn = 0;
        try {
            rtn = searchWordConfService.insertSelective(confDO);
        } catch (Exception e) {
            //e.printStackTrace();
            LOGGER.error("[addWordConf] err msg:{}", e.getMessage());
        }
        if (rtn > 0) {
            return ResultUtil.handleSuccessReturn(rtn);
        }
        return ResultUtil.handleFailtureReturn(StatusCode.FAIL.getCode(), StatusCode.FAIL.getMsg());
    }

    /**
     * 状态设置
     *
     * @param id
     * @param status
     * @return
     */
    public Result setStatus(Integer id, Integer status) {
        int rtn = 0;
        // 发布则检查是否在生效期
        if (Constant.WORD_STATUS_RELEASE_1 == status) {
            SearchWordConfDO confDO = searchWordConfService.selectByPrimaryKey(id);
            if (confDO == null) {
                return ResultUtil.handleFailtureReturn(StatusCode.NO_FOUND_WORD.getCode(), StatusCode.NO_FOUND_WORD.getMsg());
//            } else if (confDO.getStatus() == Constant.WORD_STATUS_STOP_2) {
//                return ResultUtil.handleFailtureReturn(StatusCode.WORD_STATUS_STOP.getCode(), StatusCode.WORD_STATUS_STOP.getMsg());
            }
            Date endTime = confDO.getEndTime();
            Date startTime = confDO.getStartTime();
            boolean flag = isVaildTime(endTime, startTime);
            if (flag) {
                rtn = searchWordConfService.setRelease(id);
            } else {
                return ResultUtil.handleFailtureReturn(StatusCode.UN_VAILD_TIME.getCode(), StatusCode.UN_VAILD_TIME.getMsg());
            }
        } else {
            rtn = searchWordConfService.setStop(id);
        }
        if (rtn > 0) {
            return ResultUtil.handleSuccessReturn(rtn);
        }
        return ResultUtil.handleFailtureReturn(StatusCode.FAIL.getCode(), StatusCode.FAIL.getMsg());
    }

    /**
     * 是否在生效期内
     *
     * @param endTime
     * @param startTime
     * @return
     */
    private boolean isVaildTime(Date endTime, Date startTime) {
        long curDate = DateUtil.getCaleDate().getTime();
        if (startTime.getTime() <= curDate && curDate <= endTime.getTime()) {
            return true;
        }
        return false;
    }

    /**
     * 添加远程词典串
     *
     * @param keyword
     * @return
     */
    public String addRemoteDictStr(String keyword) {
        String rtnStr = "";
        if (StringUtils.isBlank(keyword)) {
            return rtnStr;
        }

        List<String> oldWordList = new ArrayList<>();

        String[] addWordArr = keyword.split(",");
        List<String> addWordList = convertList(addWordArr);

        RemoteDictVoWrap wrap = getRemoteDictWrap();
        String oldWords = wrap.getWords();
        if (StringUtils.isNotBlank(oldWords)) {
            String[] oldWordArr = oldWords.split(",");
            oldWordList = convertList(oldWordArr);
        }
        oldWordList.addAll(addWordList);

        Result rtn = update(StringUtils.join(oldWordList, ","));
        return rtn.getData() + "";
    }

    private List<String> convertList(String[] addWordArr) {
        List<String> list = new ArrayList<>(addWordArr.length);
        for (String word : addWordArr) {
            list.add(word);
        }
        return list;
    }

    /**
     * 删除远程词典串
     *
     * @param keyword
     * @return
     */
    public String delRemoteDictStr(String keyword) {
        String rtnStr = "";
        if (StringUtils.isBlank(keyword)) {
            return rtnStr;
        }

        List<String> oldWordList = new ArrayList<>();

        String[] rmWordArr = keyword.split(",");
        List<String> rmWordList = convertList(rmWordArr);

        RemoteDictVoWrap wrap = getRemoteDictWrap();
        String oldWords = wrap.getWords();
        if (StringUtils.isNotBlank(oldWords)) {
            String[] oldWordArr = oldWords.split(",");
            oldWordList = convertList(oldWordArr);
        }
        oldWordList.removeAll(rmWordList);

        Result rtn = update(StringUtils.join(oldWordList, ","));
        return rtn.getData() + "";
    }

    public boolean openReqCountLimitSwitch() {
        return cacheManager.openReqCountLimitSwitch();
    }

    /**
     * 搜索关键词榜单
     *
     * @param daysAgo
     * @param rankSize
     * @return
     */
    public Result hotSearch(int daysAgo, int rankSize) {

        List<SearchLogWordsVO> list = searchLogService.getLogBeforeDays(daysAgo);
        Map<String, Long> wordsGroup = list.stream().map(item -> item.getSplitWord().trim().split("\\|"))
                .flatMap(array -> Arrays.stream(array)).collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        //根据单词个数降序排列
        List<Map.Entry<String, Long>> sortMapList = new ArrayList<>();
        wordsGroup.entrySet().stream().sorted((m1, m2) -> {
            return m2.getValue().compareTo(m1.getValue());
        }).forEach(sortMap -> sortMapList.add(sortMap));

        int size = getRankSize(rankSize, sortMapList.size());
        List<SearchHotVO> hotVOS = new ArrayList<>(size);
        for (int n = 0; n < size; n++) {
            Map.Entry<String, Long> mapEntity = sortMapList.get(n);
            SearchHotVO hotVO = new SearchHotVO(mapEntity.getKey(), mapEntity.getValue());
            hotVOS.add(hotVO);
        }

        SearchHotRankWrap rankWrap = new SearchHotRankWrap();
        rankWrap.setRankTime(DateUtil.parseDateStr(new Date()));
        rankWrap.setHotVOS(hotVOS);
        return ResultUtil.handleSuccessReturn(rankWrap);
    }

    /**
     * 获取榜单长度
     *
     * @param rankSize
     * @param recordSize
     * @return
     */
    private int getRankSize(int rankSize, int recordSize) {
        if (recordSize > rankSize) {
            return rankSize;
        }
        return recordSize;
    }

    private boolean openSexQryFilterSwitch() {
        return cacheManager.openSexQryFilterSwitch();
    }
}
