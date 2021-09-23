package cn.nome.saas.allocation.service.old.allocation.impl;


import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.utils.DateUtils;
import cn.nome.saas.allocation.cache.ShopListCache;
import cn.nome.saas.allocation.constant.old.Constant;
import cn.nome.saas.allocation.model.issue.IssueOutStock;
import cn.nome.saas.allocation.model.old.allocation.*;
import cn.nome.saas.allocation.model.old.allocation.Dictionary;
import cn.nome.saas.allocation.model.old.issue.*;
import cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueOperateLogMapper2;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueRestDOMapper2;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueUndoDOMapper2;
import cn.nome.saas.allocation.repository.old.allocation.entity.IssueOutStockDO;
import cn.nome.saas.allocation.repository.old.vertica.dao.AllocationDOMapper2;
import cn.nome.saas.allocation.service.old.allocation.IssueRestService;
import cn.nome.saas.allocation.utils.AuthUtil;
import cn.nome.saas.allocation.utils.IssueDayUtil;
import cn.nome.saas.allocation.utils.old.sort.SortOrderDetail;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static cn.nome.saas.allocation.constant.old.Constant.DIVIDE_SCALE_2;
import static cn.nome.saas.allocation.constant.old.Constant.DIVIDE_SCALE_4;
import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * @author chentaikuang
 */
@Service
public class IssueRestServiceImpl implements IssueRestService {

    private static Logger logger = LoggerFactory.getLogger(IssueRestServiceImpl.class);


    @Autowired
    IssueRestDOMapper2 issueRestDOMapper2;

    @Autowired
    AllocationDOMapper2 allocationDOMapper2;

    @Autowired
    IssueOperateLogMapper2 operateLogMapper;

    @Autowired
    IssueUndoDOMapper2 issueUndoDOMapper2;

    @Autowired
    ShopListCache shopListCache;

    private BigDecimal DEFAULT_NUM_99 = new BigDecimal(-99);
    private BigDecimal DEFAULT_NUM_1 = new BigDecimal(-1);

    private static final String TOTAL_INFO_KEY = "TOTAL_INFO_KEY";

    //商品图片本地缓存<matCode+sizeId,imgUrl>
    private static Map<String, String> skuImgMap = new HashMap<>();
    private static Map<String, Integer> weekNumMap = new HashMap<>();

    @Value("${matcode.barcode.batch.size:500}")
    private int barCodeBatchSize;

    @Value("${scheduler.truncate.table.names}")
    private String truncateTabNames;

    ExecutorService matDetailPool = Executors.newFixedThreadPool(4);

    @PostConstruct
    private void init() {
        initNumMapping();
        initLoadImg();
    }

    private void initLoadImg() {
        List<MatBarCodeImg> matBarCodeImgs = loadSkuImg(barCodeBatchSize);
        if (matBarCodeImgs != null && !matBarCodeImgs.isEmpty()) {
            matBarCodeImgs.stream().forEach(img -> skuImgMap.put(img.getMatCode() + img.getSizeID(), img.getImg()));
            logger.debug("LOAD_SKU_IMG_OVER:{}", matBarCodeImgs.size());
        }
    }

    private List<MatBarCodeImg> loadSkuImg(int barCodeBatchSize) {
        List<MatBarCodeImg> imgs = null;
        try {
            imgs = allocationDOMapper2.loadSkuImg(barCodeBatchSize);
        } catch (Exception e) {
            logger.error("loadSkuImg:{}", e.getMessage());
        }
        return imgs;
    }

    private void initNumMapping() {
        weekNumMap.put("一", 1);
        weekNumMap.put("二", 2);
        weekNumMap.put("三", 3);
        weekNumMap.put("四", 4);
        weekNumMap.put("五", 5);
        weekNumMap.put("六", 6);
        weekNumMap.put("日", 7);
    }

    @Override
    public List<String> getRegioneBusNameList() {
        List<String> regioneBusNames = issueRestDOMapper2.getRegioneBusNameList();
        //return regioneBusNames.stream().sorted((s1, s2) -> s1.compareTo(s2)).collect(Collectors.toList());
        sortList(regioneBusNames);
        return regioneBusNames;
    }

    private void sortList(List<String> list) {
        list.sort((s1, s2) -> s1.compareTo(s2));
    }

    @Override
    public List<String> getSubRegioneBusNameList(String regioneBusName) {
        List<String> subRegioneBusNames = null;
        if (StringUtils.isBlank(regioneBusName)) {
            subRegioneBusNames = issueRestDOMapper2.getAllSubRegioneBusNames();
        } else {
            subRegioneBusNames = issueRestDOMapper2.getSubRegioneBusNameList(regioneBusName);
        }
        sortList(subRegioneBusNames);
        return subRegioneBusNames;
    }

    @Override
    public List<Area> getCityList(String subRegioneBusName) {
        if (StringUtils.isBlank(subRegioneBusName)) {
            return issueRestDOMapper2.getAllCitys();
        }
        return issueRestDOMapper2.getCityList(subRegioneBusName);
    }

    @Override
    public List<cn.nome.saas.allocation.model.old.allocation.Dictionary> getDictionaryList() {
        return issueRestDOMapper2.getDictionaryList();
    }

    //-----------------20190521 -----

    @Override
    public OrderListWrap getOrderList(OrderListReq orderListReq) {

        OrderListWrap wrap = new OrderListWrap();
        List<OrderListVo> orderVos = new ArrayList<>();

        OrderListParam param = new OrderListParam();
        BeanUtils.copyProperties(orderListReq, param);
        setShopIdList(param);
        //testSetShopId(param);

        if (StringUtils.isNotBlank(orderListReq.getIssueTime())) {
            String[] issueTimeArr = orderListReq.getIssueTime().split(",");
            param.setIssueTime(Arrays.asList(issueTimeArr));
        }

        if (StringUtils.isNotBlank(orderListReq.getCreatedBegin()) && StringUtils.isNotBlank(orderListReq.getCreatedEnd())) {
            param.setCreatedBegin(DateUtil.parse(orderListReq.getCreatedBegin(), Constant.DATE_PATTERN_FULL));
            param.setCreatedEnd(DateUtil.parse(orderListReq.getCreatedEnd(), Constant.DATE_PATTERN_FULL));
        }

        if (orderListReq.getPageSize() != null && orderListReq.getCurPage() != null) {
            int offset = (orderListReq.getCurPage() - 1) * orderListReq.getPageSize();
            param.setOffset(offset);
        }

        int totalCount = issueRestDOMapper2.getOrderListCount(param);
        if (totalCount > 0) {

            List<OrderListDo> orders = issueRestDOMapper2.getOrderList(param);

            int taskId = orderListReq.getTaskId();
            IssueTask issueTask = issueRestDOMapper2.getIssueTask(taskId);
            if (issueTask != null && orders != null && !orders.isEmpty()) {
                orderListDo2Vo(orderVos, orders, issueTask);
            }
        }
        wrap.setTotalPage(getTotalPage(totalCount, orderListReq.getPageSize()));
        wrap.setCurPage(orderListReq.getCurPage());
        wrap.setTotalCount(totalCount);
        wrap.setOrderList(orderVos);

        return wrap;
    }

    private void testSetShopId(OrderListParam param) {
        param.setShopIds(Arrays.asList("NM000068","NM000193","NM000528"));
    }

    /**
     * 设值用户授权门店ids
     *
     * @param param
     */
    private void setShopIdList(OrderListParam param) {
        String userId = AuthUtil.getSessionUserId();
        UserAdminDo userAdminDo = issueRestDOMapper2.getAdmin(userId);
        logger.info("{} is admin:{}", userId, userAdminDo != null);
        if (userAdminDo == null) {
            List<String> shopIds = issueRestDOMapper2.getShopIdByUserId(userId);
            if (shopIds == null || shopIds.isEmpty()) {
                param.setShopIds(Arrays.asList(Constant.NULL_SHOPID));
            } else {
                param.setShopIds(shopIds);
            }
        }
    }

    private void orderListDo2Vo(List<OrderListVo> orderVos, List<OrderListDo> orders, IssueTask issueTask) {
        Map<String, String> m = getIssueTime(Constant.DICT_ISSUETIME);
        Iterator<OrderListDo> itr = orders.iterator();
        String issueTime = null;
        String createTime = null;
        while (itr.hasNext()) {
            OrderListDo orderListDo = itr.next();
            OrderListVo vo = new OrderListVo();
            BeanUtils.copyProperties(orderListDo, vo);
            vo.setTaskId(issueTask.getId());

            if (orderListDo.getRecalcTime() != null) {
                createTime = DateUtil.format(orderListDo.getRecalcTime(), Constant.DATE_PATTERN_FULL2);
            } else {
                createTime = DateUtil.format(issueTask.getCreatedAt(), Constant.DATE_PATTERN_FULL2);
            }
            vo.setCreatedAt(createTime);

            issueTime = m.get(orderListDo.getIssueTime());
            vo.setIssueTime(StringUtils.isNotBlank(issueTime) ? issueTime : orderListDo.getIssueTime());
            orderVos.add(vo);
        }
    }

    private Map<String, String> getIssueTime(String dictionaryType) {
        List<Dictionary> dict = issueRestDOMapper2.getDictionaryByType(dictionaryType);
        if (dict == null) {
            return Collections.emptyMap();
        }
        return dict.stream().collect(Collectors.toMap(Dictionary::getParaKey, Dictionary::getParaValue));
    }

    @Override
    public Map<String, String> getDictByType(String type) {
        Map map = new LinkedHashMap();
        List<Dictionary> dict = issueRestDOMapper2.getDictionaryByType(type);
        if (dict == null) {
            return map;
        }
        //配发时间需要排序下
        if (Constant.DICT_ISSUETIME.equals(type)) {
            Collections.sort(dict, new Comparator<Dictionary>() {
                @Override
                public int compare(Dictionary o1, Dictionary o2) {
                    int val = convertNum(o1.getParaKey()) - convertNum(o2.getParaKey());
                    if (val == 0) {
                        return o1.getParaKey().length() - o2.getParaKey().length();
                    }
                    return val;
                }

                private int convertNum(String paraKey) {
                    String firstChar = paraKey.substring(0, 1);
                    if (weekNumMap.containsKey(firstChar)) {
                        return weekNumMap.get(firstChar);
                    }
                    return 0;
                }
            });
        }
        dict.stream().forEach(d -> map.put(d.getParaValue(), d.getParaKey()));
        return map;
    }

    @Override
    public Map<String, String> issueTimeList() {
        Map map = new LinkedHashMap();
        List<Dictionary> dict = issueRestDOMapper2.getDictionaryByType(Constant.DICT_ISSUETIME);
        if (dict == null) {
            return map;
        }
        //配发时间需要排序下
        Collections.sort(dict, new Comparator<Dictionary>() {
            @Override
            public int compare(Dictionary o1, Dictionary o2) {
                int val = convertNum(o1.getParaKey()) - convertNum(o2.getParaKey());
                if (val == 0) {
                    return o1.getParaKey().length() - o2.getParaKey().length();
                }
                return val;
            }

            private int convertNum(String paraKey) {
                String firstChar = paraKey.substring(0, 1);
                if (weekNumMap.containsKey(firstChar)) {
                    return weekNumMap.get(firstChar);
                }
                return 0;
            }
        });
        dict.stream().forEach(d -> map.put(d.getParaValue(), d.getParaKey()));
        return map;
    }

    @Override
    public IssueTask createTask(int runStatus) {
        Date now = new Date();
        IssueTask task = new IssueTask();
        task.setName(DateUtils.toString(now, "yyyy-MM-dd HH:mm:ss"));
        task.setRunTime(now);
        task.setCreatedAt(now);
        task.setTaskStatus(runStatus);
        task.setOperator(AuthUtil.getSessionUserId());

        // 重算重跑任务
        if (runStatus == 2) {
            task.setRemark("RERUN");
        } else {
            task.setRemark("RUN");
        }
        return task;
    }

    @Override
    public void bakIssueTab() {
        logger.debug("BAK TAB NAMES:{}", truncateTabNames);
        if (StringUtils.isBlank(truncateTabNames)) {
            return;
        }

        String[] tabNames = truncateTabNames.split(",");
        int rst = 0;
        for (String tabName : tabNames) {

            if (StringUtils.isBlank(tabName)) {
                logger.debug("BAK TAB NAME NULL:{}", tabName);
                continue;
            }

            rst = bakTab(tabName);

            logger.debug("BAK {},rst:{}", tabName, rst);
        }
        logger.debug("BAK TAB DONE!");
    }

    @Override
    public List<String> getSkcCategorys(int taskId, String shopId) {
        List<String> categorys = issueRestDOMapper2.skcCountCategorys(taskId,shopId);
        return categorys;
    }

    @Override
    public List<String> getRecalcSkcCategorys(int taskId, String shopId) {
        List<String> categorys = issueRestDOMapper2.recalcSkcCountCategorys(taskId,shopId);
        return categorys;
    }

    @Override
    public Map<String, Integer> categoryCanSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.categoryCanSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("categoryCanSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, Integer> map = list.stream().collect(Collectors.toMap(CategorySkcData::getCategoryName, CategorySkcData::getSkcCount));
        return map;
    }

    @Override
    public Map<String, Integer> categoryKeepSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.categoryKeepSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("categoryKeepSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, Integer> map = list.stream().collect(Collectors.toMap(CategorySkcData::getCategoryName, CategorySkcData::getSkcCount));
        return map;
    }

    @Override
    public Map<String, Integer> categoryNewSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.categoryNewSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("categoryNewSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, Integer> map = list.stream().collect(Collectors.toMap(CategorySkcData::getCategoryName, CategorySkcData::getSkcCount));
        return map;
    }

    @Override
    public Map<String, Integer> categoryProhibitedSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.categoryProhibitedSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("categoryProhibitedSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, Integer> map = list.stream().collect(Collectors.toMap(CategorySkcData::getCategoryName, CategorySkcData::getSkcCount));
        return map;
    }

    @Override
    public Map<String, Integer> categoryValidSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.categoryValidSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("categoryValidSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, Integer> map = list.stream().collect(Collectors.toMap(CategorySkcData::getCategoryName, CategorySkcData::getSkcCount));
        return map;
    }

    @Override
    public Integer insertCategoryCountData(List<IssueCategorySkcData> categorySkcData) {
        Integer rst = issueRestDOMapper2.batchInsertCategoryCountData(categorySkcData);
        return rst;
    }

    @Override
    public Map<String, List<CategorySkcData>> midCategoryCanSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.midCategoryCanSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("midCategoryCanSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, List<CategorySkcData>> groupBy = list.stream().collect(Collectors.groupingBy(CategorySkcData::getCategoryName));
        return groupBy;
    }

    @Override
    public Map<String, List<CategorySkcData>> midCategoryKeepSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.midCategoryKeepSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("midCategoryKeepSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, List<CategorySkcData>> groupBy = list.stream().collect(Collectors.groupingBy(CategorySkcData::getCategoryName));
        return groupBy;
    }

    @Override
    public Map<String, List<CategorySkcData>> midCategoryNewSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.midCategoryNewSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("midCategoryNewSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, List<CategorySkcData>> groupBy = list.stream().collect(Collectors.groupingBy(CategorySkcData::getCategoryName));
        return groupBy;
    }

    @Override
    public Map<String, List<CategorySkcData>> midCategoryProhibitedSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.midCategoryProhibitedSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("midCategoryProhibitedSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, List<CategorySkcData>> groupBy = list.stream().collect(Collectors.groupingBy(CategorySkcData::getCategoryName));
        return groupBy;
    }

    @Override
    public Map<String, List<CategorySkcData>> midCategoryValidSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.midCategoryValidSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("midCategoryValidSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, List<CategorySkcData>> groupBy = list.stream().collect(Collectors.groupingBy(CategorySkcData::getCategoryName));
        return groupBy;
    }

    @Override
    public Integer insertMidCategoryCountData(List<IssueCategorySkcData> midCategorySkcData) {
        Integer rst = issueRestDOMapper2.batchInsertMidCategoryCountData(midCategorySkcData);
        return rst;
    }

    @Override
    public Integer syncIssueTime() {
        int rts = 0;
        List<String> issueTimeList = issueRestDOMapper2.syncIssueTime();
        if (issueTimeList == null || issueTimeList.isEmpty()){
            return -1;
        }
        List<Dictionary> issueTimes = issueRestDOMapper2.getDictionaryByType(Constant.DICT_ISSUETIME);
        if (issueTimes == null){
            issueTimes = Collections.EMPTY_LIST;
        }
        Map<String, String> map = issueTimes.stream().filter(dictionary -> StringUtils.isNotBlank(dictionary.getParaKey()) && StringUtils.isNotBlank(dictionary.getParaValue())).collect(Collectors.toMap(Dictionary::getParaKey, Dictionary::getParaValue));
        Iterator<String> itr = issueTimeList.iterator();
        String key = null;
        List<Dictionary> addDic = new ArrayList<>();
        while (itr.hasNext()) {
            key = itr.next();
            if (StringUtils.isBlank(key) || map.containsKey(key)) {
                continue;
            }
            Dictionary dictionary = new Dictionary();
            dictionary.setParaKey(key);
            dictionary.setParaValue(key);
            dictionary.setType(Constant.DICT_ISSUETIME);
            addDic.add(dictionary);
        }
        if (addDic != null && !addDic.isEmpty()){
            rts = issueRestDOMapper2.batchInsertDicy(addDic);
        }
        return rts;
    }

    @Override
    public IssueOutStock getIssueOutStock(int taskId, String matCode, String sizeId) {
        IssueOutStock issueOutStock = issueRestDOMapper2.getIssueOutStock(taskId, matCode, sizeId);
        return issueOutStock;
    }

    @Override
    public List<IssueOutStockDO> issueOutStock(int taskId) {
        List<IssueOutStockDO> all = issueRestDOMapper2.issueOutStock(taskId);
        return all;
    }

    @Override
    public IssueDetailDistStock issueDeatilDistStock(int taskId, String matCode, String sizeID) {
        IssueDetailDistStock detailDistStock = issueRestDOMapper2.issueDeatilDistStock(taskId,matCode,sizeID);
        return detailDistStock;
    }

    @Override
    public List<IssueDetailDistStock> getDetailStock(int taskId, String shopId) {
        List<IssueDetailDistStock> list = issueRestDOMapper2.getDetailStock(taskId, shopId);
        return list;
    }

    @Override
    public IssueOutStockRemainDo getRemainStock(int taskId, String matCode, String sizeID) {
        IssueOutStockRemainDo remainDo = issueRestDOMapper2.getRemainStock(taskId,matCode,sizeID);
        return remainDo;
    }

    @Override
    public int addRecalcRemainStock(IssueOutStockRemainDo remainStock) {
        int rst = issueRestDOMapper2.addRecalcRemainStock(remainStock);
        return rst;
    }

    @Override
    public List<IssueGoodsData> getRecalcIssueGoodsData(int taskId, String shopID) {
        return issueRestDOMapper2.getRecalcIssueGoodsData(taskId,shopID);
    }

    private int bakTab(String tabName) {
        try {
            String dateStr = DateUtil.format(new Date(), "yyMMddHHmmss");
            issueRestDOMapper2.createAndCopyTab((tabName + "_" + dateStr), tabName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("BAK TAB ERR:{}", e.getMessage());
            return 0;
        }
        return 1;
    }

    @Override
    public TaskDataWrap checkTaskData(int status) {
        TaskDataWrap taskDataWrap = new TaskDataWrap();
        IssueTaskVo task = issueRestDOMapper2.getTaskByStatus(status);
        if (task == null){
            return taskDataWrap;
        }
        int taskId = task.getTaskId();
        Integer issueInStockCount = issueRestDOMapper2.issueInStockCount(taskId);
        Integer issueOutStockCount = issueRestDOMapper2.issueOutStockCount(taskId);
        Integer issueNeedStockCount = issueRestDOMapper2.issueNeedStockCount(taskId);
        Integer issueMidCategoryCount = issueRestDOMapper2.issueMidCategoryCount(taskId);
        Integer issueDetailCount = issueRestDOMapper2.issueDetailCount(taskId);
        Integer issueUndoCount = issueRestDOMapper2.issueUndoCount(taskId);
        Integer issueGoodsDataCount = issueRestDOMapper2.issueGoodsDataCount(taskId);
        Integer categoryDataCount = issueRestDOMapper2.issueCategoryDataCount(taskId);
        Integer midCategoryDataCount = issueRestDOMapper2.issueMidCategoryDataCount(taskId);


        taskDataWrap.setTaskId(taskId);
        taskDataWrap.setInStockCount(issueInStockCount);
        taskDataWrap.setOutStockCount(issueOutStockCount);
        taskDataWrap.setMidCategoryCount(issueMidCategoryCount);
        taskDataWrap.setNeedStockCount(issueNeedStockCount);
        taskDataWrap.setDetailCount(issueDetailCount);
        taskDataWrap.setUndoCount(issueUndoCount);
        taskDataWrap.setStartTime(task.getRunTime());
        taskDataWrap.setGoodsDataCount(issueGoodsDataCount);
        taskDataWrap.setCategoryDataCount(categoryDataCount);
        taskDataWrap.setMidCategoryDataCount(midCategoryDataCount);

        if (task.getUpdateTime() != null){
            taskDataWrap.setEndTime(task.getUpdateTime());
            long time = task.getUpdateTime().getTime() - task.getRunTime().getTime();
            taskDataWrap.setCostTime((int) (time/(1000*60)));
        }
        return taskDataWrap;
    }

    @Override
    public List<IssueGoodsData> getIssueGoodsData(int taskId, String shopID) {
        return issueRestDOMapper2.queryIssueGoodsData(taskId,shopID);
    }

    @Override
    public int insertGoodsData(List<IssueGoodsData> issueGoodsData) {
        return issueRestDOMapper2.batchInsertGoodsData(issueGoodsData);
    }

    @Override
    public int delGoodsData(int taskId, String shopID) {
        return issueRestDOMapper2.batchDelGoodsData(taskId,shopID);
    }

    @Override
    public List<String> issueInStockShopIds(int taskId) {
        return issueRestDOMapper2.issueInStockShopIds(taskId);
    }

    @Override
    public OrderDetailWrap getOrderDetail(OrderDetailReq detailReq) {
        OrderDetailWrap wrap = new OrderDetailWrap();

        OrderDetailParam param = new OrderDetailParam();
        BeanUtils.copyProperties(detailReq, param);

//        去除分页
//        if (detailReq.getPageSize() != null && detailReq.getCurPage() != null){
//            int offset = (detailReq.getCurPage() - 1) * detailReq.getPageSize();
//            param.setOffset(offset);
//        }

        ShopInDo shopInDo = issueRestDOMapper2.getShopById(param.getShopId());
        wrap.setShopName(shopInDo.getShopName());
        wrap.setShopId(shopInDo.getShopID());

        String img = null;
        List<OrderDetailVo> detailVos = new ArrayList<>();
        List<OrderDetailDo> allDetailDos = new ArrayList<>();
        List<OrderDetailDo> detailDos = null;
        List<OrderDetailDo> undoData = null;

        Date date = new Date();
        //特殊处理：去除分页，只处理第一页
        if (detailReq.getCurPage() == null || detailReq.getCurPage() <= 1) {
            detailDos = issueRestDOMapper2.getOrderDetail(param);
            undoData = issueUndoDOMapper2.issueUndoDetail(param);

            costTime("getData", date.getTime());
        }
        if (detailDos != null && !detailDos.isEmpty()) {
            allDetailDos.addAll(detailDos);
        }
        if (undoData != null && !undoData.isEmpty()) {
            allDetailDos.addAll(undoData);
        }

        List<MatcodeSizeId> matcodeSizeIds = new ArrayList<>();
        Set<String> goodsDataMatCodeSet = new HashSet<>();
        Set<String> goodsDataSizeIdSet = new HashSet<>();
        Iterator<OrderDetailDo> allItr = allDetailDos.iterator();
        while (allItr.hasNext()) {
            OrderDetailDo detailDo = allItr.next();

            goodsDataMatCodeSet.add(detailDo.getMatCode());
            goodsDataSizeIdSet.add(detailDo.getSizeId());

            if (skuImgMap.containsKey(detailDo.getMatCode() + detailDo.getSizeId())) {
                //缓存已存在图片
                continue;
            }
            matcodeSizeIds.add(new MatcodeSizeId(detailDo.getMatCode(),detailDo.getSizeId()));
        }
        date = new Date();
        List<MatBarCodeImg> matBarCodeImgs = getMatBarCodes(matcodeSizeIds);
        mergeMatCodeSizeImg(matBarCodeImgs);

        costTime("mergeMatCodeSizeImg", date.getTime());
        date = new Date();

        List<IssueGoodsData> goodsDataList = issueRestDOMapper2.queryGoodsData(param.getTaskId(), param.getShopId(), goodsDataMatCodeSet,goodsDataSizeIdSet);
        Map<String, IssueGoodsData> goodsDataMap = new HashMap<>();
        goodsDataList.stream().forEach(data -> goodsDataMap.put(data.getMatCode() + data.getSizeID(), data));
        costTime("queryGoodsData", date.getTime());

        Map<String, BigDecimal> stockRemainMap = getStockRemainDos(param.getTaskId(), goodsDataMatCodeSet, goodsDataSizeIdSet);
        String matcodeSizeId = null;
        date = new Date();
        NumberFormat percent = getPercentFormat();
        if (detailDos != null && !detailDos.isEmpty()) {

            Map<String, IssueInStockDo> issueInstockMap = convertInStockVo(detailDos,param.getTaskId(),param.getShopId());
            Iterator<OrderDetailDo> itr = detailDos.iterator();
            while (itr.hasNext()) {
                OrderDetailDo detailDo = itr.next();

                convertByIssueInStock(issueInstockMap,detailDo);
                //补货周期天数
                detailDo.setIssueDay(shopInDo.getIssueDay());
                // 安全天数
                detailDo.setSafeDay(shopInDo.getSafeDay());

                OrderDetailVo detailVo = new OrderDetailVo();
                BeanUtils.copyProperties(detailDo, detailVo);

                matcodeSizeId = detailDo.getMatCode() + detailDo.getSizeId();
                IssueGoodsData goodsData = goodsDataMap.get(matcodeSizeId);
                convertSetDetailVo(detailVo, goodsData);

                img = skuImgMap.get(matcodeSizeId);
                detailVo.setImg(StringUtils.isBlank(img) ? "/" : img);
                //门店库存改为门店总库存. 增加本次配发数量 190726
                detailVo.setOrderQty(detailVo.getOrderPackage().multiply(new BigDecimal(detailVo.getMinPackageQty())));

                //补货前周转天数 = 门店库存/日均销
                if (detailVo.getAvgSaleQty() != null && detailVo.getAvgSaleQty().doubleValue() > 0) {
                    detailVo.setExIssueTurnoverDay(detailVo.getTotalStockQty().divide(detailVo.getAvgSaleQty(), 2, ROUND_HALF_UP).toString());
                } else {
                    detailVo.setExIssueTurnoverDay("0");
                }

                detailVo.setTotalStockQty(detailVo.getTotalStockQty().add(detailVo.getOrderQty()));

                //有效日均销与7日日均销相等
                detailVo.setValidSaleQty(detailVo.getAvgSaleQty());

                setSaleDays(detailVo);

                //剩余库存
                detailVo.setRemainStockQty(stockRemainMap.get(matcodeSizeId) == null ? 0 : stockRemainMap.get(matcodeSizeId).intValue());

                detailVo.setPercentAvgSaleQty(percent.format(detailDo.getPercentCategory().doubleValue()));

                detailVos.add(detailVo);
            }
            costTime("issue_detail", date.getTime());
        }

        date = new Date();
        if (undoData != null && !undoData.isEmpty()) {
            Iterator<OrderDetailDo> undoItr = undoData.iterator();
            while (undoItr.hasNext()) {
                OrderDetailDo detailDo = undoItr.next();


                OrderDetailVo detailVo = new OrderDetailVo();
                //补货周期天数
                detailDo.setIssueDay(shopInDo.getIssueDay());
                // 安全天数
                detailDo.setSafeDay(shopInDo.getSafeDay());
                BeanUtils.copyProperties(detailDo, detailVo);

                matcodeSizeId = detailDo.getMatCode() + detailDo.getSizeId();

                IssueGoodsData goodsData = goodsDataMap.get(matcodeSizeId);
                convertSetDetailVo(detailVo, goodsData);

                img = skuImgMap.get(matcodeSizeId);
                detailVo.setImg(StringUtils.isBlank(img) ? "/" : img);

                //补货前周转天数 = 门店库存/日均销
                if (detailVo.getAvgSaleQty() != null && detailVo.getAvgSaleQty().doubleValue() > 0) {
                    detailVo.setExIssueTurnoverDay(detailDo.getTotalStockQty().divide(detailVo.getAvgSaleQty(), 2, ROUND_HALF_UP).toString());
                } else {
                    detailVo.setExIssueTurnoverDay("0");
                }

                //门店库存改为门店总库存. 增加本次配发数量 190726
                detailVo.setOrderQty(detailVo.getOrderPackage().multiply(new BigDecimal(detailVo.getMinPackageQty())));
                detailVo.setTotalStockQty(detailDo.getTotalStockQty().add(detailVo.getOrderQty()));

                //有效日均销与7日日均销相等
                detailVo.setValidSaleQty(detailVo.getAvgSaleQty());

                setSaleDays(detailVo);

                convertIssueFlag(detailVo);

                //剩余库存
                detailVo.setRemainStockQty(stockRemainMap.get(matcodeSizeId) == null ? 0 : stockRemainMap.get(matcodeSizeId).intValue());

                detailVo.setPercentAvgSaleQty(percent.format(detailDo.getPercentCategory().doubleValue()));

                detailVos.add(detailVo);
            }
            costTime("issue_undo", date.getTime());
        }
        Collections.sort(detailVos, new SortOrderDetail());

        // 填充门店代码与名称
        List<DwsDimShopDO> dwsDimShopDOList = shopListCache.getShopList();
        DwsDimShopDO dwsDimShopDO = dwsDimShopDOList.stream().filter(shop->shop.getShopId().equals(detailReq.getShopId())).findFirst().orElse(null);

        if (dwsDimShopDO != null) {
            detailVos.forEach(detailDo->{
                detailDo.setShopCode(dwsDimShopDO.getShopCode());
                detailDo.setShopName(dwsDimShopDO.getShopName());
            });
        }

        wrap.setOrderDetailVo(detailVos);
        return wrap;
    }

    public Map<String, BigDecimal> getStockRemainDos(int taskId, Set<String> goodsDataMatCodeSet, Set<String> goodsDataSizeIdSet) {
        List<IssueOutStockRemainDo> list = issueRestDOMapper2.matcodeStockRemain(taskId, goodsDataMatCodeSet, goodsDataSizeIdSet);
        if (list == null) {
            list = Collections.emptyList();
        }
        Map<String, BigDecimal> stockRemainMap = new HashMap<>();
        list.stream().forEach(remainDo -> stockRemainMap.put(remainDo.getMatCode() + remainDo.getSizeID(), remainDo.getStockQty()));
        return stockRemainMap;
    }

    /**
     * issue_in_stock数据设置到issueDetail
     * @param issueInstockMap
     * @param detailDo
     */
    private void convertByIssueInStock(Map<String, IssueInStockDo> issueInstockMap, OrderDetailDo detailDo) {
        IssueInStockDo issueInstock = issueInstockMap.get(detailDo.getMatCode() + detailDo.getSizeId());
        if (issueInstock != null) {
            detailDo.setAvgSaleQty(issueInstock.getAvgSaleQty());
            detailDo.setTotalStockQty(issueInstock.getTotalStockQty());
            detailDo.setYearNo(issueInstock.getYearNo());
            detailDo.setSeasonName(issueInstock.getSeasonName());
            detailDo.setInStockQty(issueInstock.getStockQty());
            detailDo.setPathStockQty(issueInstock.getPathStockQty());
            detailDo.setMoveQty(issueInstock.getMoveQty());
            detailDo.setRuleName(issueInstock.getRuleName());
            detailDo.setIsEliminate(issueInstock.getIsEliminate());
            if (issueInstock.getMinQty() != null) {
                detailDo.setSecurityQty(issueInstock.getMinQty().intValue());
            }
        }
    }

    private Map<String, IssueInStockDo> convertInStockVo(List<OrderDetailDo> detailDos, int taskId, String shopId) {
        Set<String> matCodes = new HashSet<>();
        Set<String> sizeIds = new HashSet<>();
        for (OrderDetailDo detailDo : detailDos){
            matCodes.add(detailDo.getMatCode());
            sizeIds.add(detailDo.getSizeId());
        }
        List<IssueInStockDo> issueInStockDos = issueRestDOMapper2.moreIssueInStock(taskId, shopId, matCodes,sizeIds);

        Map<String,IssueInStockDo> rstMap = new HashMap<>();
        if (issueInStockDos == null || issueInStockDos.isEmpty()){
            return rstMap;
        }
        issueInStockDos.stream().forEach(inStockDo -> rstMap.put((inStockDo.getMatCode() + inStockDo.getSizeID()), inStockDo));
        return rstMap;
    }

    /**
     * 设置goodsData数据回DetailVo
     *
     * @param detailVo
     * @param goodsData
     */
    private void convertSetDetailVo(OrderDetailVo detailVo, IssueGoodsData goodsData) {
        if (goodsData != null) {
            detailVo.setGoodsLevel(goodsData.getGoodsLevel());
            detailVo.setSaleQty28(goodsData.getSaleQty28().intValue());
            detailVo.setSaleQty7(goodsData.getSaleQty7().intValue());
            detailVo.setDisplayPercent(goodsData.getDisplayPercent());
            detailVo.setMidDisplaydepth(goodsData.getMidDisplaydepth());
            detailVo.setSmallDisplaydepth(goodsData.getSmallDisplaydepth());
            detailVo.setSizeCount(goodsData.getSizeCount());
            detailVo.setShopRank(goodsData.getShopRank());
            detailVo.setNationalRank(goodsData.getNationalRank());
            if (goodsData.getDisplayQty() != null) {
                detailVo.setDisplayQty(goodsData.getDisplayQty().toString());
            }
        }
    }

    /**
     * 补货标识转换
     *
     * @param detailVo
     */
    private void convertIssueFlag(OrderDetailVo detailVo) {
        if (detailVo.getIsProhibited() == 1) {
            detailVo.setIssueFlag(Constant.PROHIBITED_TIPS);
        }
    }

    /**
     * 补货后周转天数
     *
     * @param detailVo
     */
    private void setSaleDays(OrderDetailVo detailVo) {
        //totalStockQty 已含补货量
        BigDecimal totalStockQty = detailVo.getTotalStockQty();
        if (detailVo.getAvgSaleQty() != null && detailVo.getAvgSaleQty().doubleValue() > 0) {
            detailVo.setSaleDays(totalStockQty.divide(detailVo.getAvgSaleQty(), DIVIDE_SCALE_2, ROUND_HALF_UP).toString());
        } else {
            detailVo.setSaleDays("0");
        }
    }

    /**
     * 获取matcode对应的barCode集
     *
     * @param matcodeSizeIds
     * @return
     */
    private List<MatBarCodeImg> getMatBarCodes(List<MatcodeSizeId> matcodeSizeIds) {
        if (matcodeSizeIds.isEmpty()) {
            logger.debug("GET MATBARCODES IS EMPTY");
            return Collections.emptyList();
        }
        return allocationDOMapper2.getMatCodeSizeIdImgs(matcodeSizeIds);
    }

    private void mergeMatCodeSizeImg(List<MatBarCodeImg> imgList) {
        logger.debug("skuImgMap size(before):{}", skuImgMap.size());
        Map<String, String> imgMap = new HashMap<>();
        imgList.stream().forEach(img -> imgMap.put(img.getMatCode() + img.getSizeID(), img.getImg()));
        Set<Map.Entry<String, String>> imgSet = imgMap.entrySet();
        imgSet.stream()
                .filter(matBarCodeImg -> !skuImgMap.containsKey(matBarCodeImg.getKey()))
                .forEach(matBarCodeImg -> skuImgMap.put(matBarCodeImg.getKey(), matBarCodeImg.getValue()));
        logger.debug("skuImgMap size(after):{}", skuImgMap.size());
    }

    @Override
    public IssueTaskVo getLastTask() {
        IssueTaskVo taskVo = issueRestDOMapper2.getLastTask();
        if (taskVo == null) {
            taskVo = new IssueTaskVo();
        }
        IssueTaskVo reRunTaskVo = issueRestDOMapper2.getLastReRunTask();
        if (reRunTaskVo != null && reRunTaskVo.getTaskId() > taskVo.getTaskId()) {
            taskVo.setReRun(Constant.STATUS_RERUN);
        }
        return taskVo;
    }

    @Override
    public IssueTask getTaskById(int taskId) {
        IssueTask issueTask = issueRestDOMapper2.getIssueTask(taskId);
        return issueTask;
    }

    @Override
    public IssueTaskVo getLastReRunTask() {
        IssueTaskVo reRunTaskVo = issueRestDOMapper2.getLastReRunTask();
        return reRunTaskVo;
    }

    @Override
    public List<OrderListVo> getOrderByIds(Integer taskId, String shopIds) {

        List<OrderListVo> orderVos = new ArrayList<>();
        List<String> shopIdList = Arrays.asList(shopIds.split(","));
        List<OrderListDo> orders = issueRestDOMapper2.getOrderByIds(taskId, shopIdList);
        IssueTask issueTask = issueRestDOMapper2.getIssueTask(taskId);
        if (issueTask != null && orders != null && !orders.isEmpty()) {
            orderListDo2Vo(orderVos, orders, issueTask);
        }
        return orderVos;
    }

    /**
     * 门品类统计明细
     *
     * @param taskId
     * @param shopId
     * @param categoryName
     * @return
     */
    @Override
    public MatDetailWrap getMatDetail(int taskId, String shopId, String categoryName) {

        MatDetailWrap wrap = new MatDetailWrap();

        ShopInDo shopInDo = issueRestDOMapper2.getShopById(shopId);
        wrap.setShopName(shopInDo.getShopName());

        List<MatCategoryDetailVo> categoryVos = new ArrayList<>();

        List<MatCategoryDetailDo> categoryDoList = issueRestDOMapper2.getMatCategoryDetail(taskId, shopId, categoryName);

        List<ShopStockYesterday> stockYesterday = allocationDOMapper2.getShopStockYesterday(shopId);
        Map<String, ShopStockVo> stockGroupByCategory = convertShopStockYesterday(stockYesterday, 0);

        List<SkuSaleStock> saleByDay28 = allocationDOMapper2.getSaleDay28(shopId);
        Map<String, ShopSaleVo> sale28GroupByCategory = convertSkuStockAmt(saleByDay28, 0);

        List<SkuSaleStock> saleByDay7 = allocationDOMapper2.getSaleDay7(shopId);
        Map<String, ShopSaleVo> sale7GroupByCategory = convertSkuStockAmt(saleByDay7, 0);

        categoryDo2Vo(categoryVos, categoryDoList, stockGroupByCategory, sale28GroupByCategory,sale7GroupByCategory);
        wrap.setCategoryVo(categoryVos);

        getTotalData(wrap, categoryVos);

        return wrap;
    }

    @Override
    public List<MatDetailTree> downloadMatDetail(int taskId, List<String> shopIdList) {


        List<DwsDimShopDO> shopList = shopListCache.getShopList();
        List<Future<List<MatDetailTree>>> futures = new ArrayList<>();
        for (String shopId : shopIdList) {

            Future<List<MatDetailTree>> future = matDetailPool.submit(()-> {
                List<MatDetailTree> matDetailTree = new ArrayList<>();
                MatDetailWrap matDetailWrap = this.getMatDetail(taskId,shopId,null);

                List<MatCategoryDetailVo> rootList = matDetailWrap.getCategoryVo(); // 根节点列表

                for (MatCategoryDetailVo root : rootList) {

                    MatDetailTree tree = new MatDetailTree();
                    tree.setShopId(shopId);
                    tree.setShopCode(shopList.stream().filter(shop->shop.getShopId().equals(shopId)).map(DwsDimShopDO::getShopCode).findFirst().orElse(""));
                    tree.setShopName(matDetailWrap.getShopName());
                    tree.setTotalOrderAmt(matDetailWrap.getTotalOrderAmt());
                    tree.setTotalOrderQty(matDetailWrap.getTotalOrderQty());

                    MatDetailWrap secondMatDetail =  getMatMidCategoryDetail(taskId,shopId,root.getCategoryName());

                    tree.setParent(root);
                    tree.setChildList(secondMatDetail.getCategoryVo());

                    matDetailTree.add(tree);
                }

                return matDetailTree;
            });

            futures.add(future);

        }

        List<MatDetailTree> matDetailTree = new ArrayList<>();
        for (Future<List<MatDetailTree>> future : futures) {
            try {
                matDetailTree.addAll(future.get());
            } catch (Exception e) {

            }
        }

        return matDetailTree;

    }




    /**
     * 计算大类汇总数据
     *
     * @param wrap
     * @param categoryVos
     */
    private void getTotalData(MatDetailWrap wrap, List<MatCategoryDetailVo> categoryVos) {
        Iterator<MatCategoryDetailVo> itr = categoryVos.iterator();
        BigDecimal totalAmt = new BigDecimal(0);
        BigDecimal totalQty = new BigDecimal(0);
        while (itr.hasNext()) {
            MatCategoryDetailVo item = itr.next();
            totalAmt = totalAmt.add(item.getOrderAmt());
            totalQty = totalQty.add(item.getOrderQty());
        }
        wrap.setTotalOrderQty(totalQty.doubleValue());
        wrap.setTotalOrderAmt(totalAmt.doubleValue());
    }

    /**
     * @param skuSaleStocks
     * @param categoryType  0:大类 、1中类
     * @return
     */
    private Map<String, ShopSaleVo> convertSkuStockAmt(List<SkuSaleStock> skuSaleStocks, int categoryType) {
        BigDecimal totalStockAmt = new BigDecimal(0);
        BigDecimal totalStockQty = new BigDecimal(0);
        Map<String, ShopSaleVo> map = new HashMap<>();

        if (skuSaleStocks != null && !skuSaleStocks.isEmpty()) {
            String categoryName = null;
            for (SkuSaleStock stock : skuSaleStocks) {
                if (categoryType == 0) {
                    categoryName = stock.getCategoryName();
                } else {
                    categoryName = stock.getMidCategoryName();
                }

                if (map.containsKey(categoryName)) {
                    ShopSaleVo shopStockVo = map.get(categoryName);
                    shopStockVo.setSaleAmt(shopStockVo.getSaleAmt().add(stock.getSaleAmt()));
                    shopStockVo.setSaleQty(shopStockVo.getSaleQty().add(stock.getSaleQty()));
                    map.put(categoryName, shopStockVo);
                } else {
                    ShopSaleVo shopStockVo = new ShopSaleVo();
                    shopStockVo.setSaleAmt(stock.getSaleAmt());
                    shopStockVo.setSaleQty(stock.getSaleQty());
                    map.put(categoryName, shopStockVo);
                }
                totalStockAmt = totalStockAmt.add(stock.getSaleAmt());
                totalStockQty = totalStockQty.add(stock.getSaleQty());
            }
        }
        ShopSaleVo saleVo = new ShopSaleVo();
        saleVo.setTotalSaleQty(totalStockQty);
        saleVo.setTotalSaleAmt(totalStockAmt);
        map.put(TOTAL_INFO_KEY, saleVo);
        return map;
    }

    /**
     * 门店中类统计明细
     *
     * @param taskId
     * @param shopId
     * @param categoryName
     * @return
     */
    @Override
    public MatDetailWrap getMatMidCategoryDetail(int taskId, String shopId, String categoryName) {
        MatDetailWrap wrap = new MatDetailWrap();

        List<MatCategoryDetailVo> categoryVos = new ArrayList<>();
        List<String> midCategoryList = issueRestDOMapper2.getAllMidCategory(categoryName);
        if (midCategoryList == null || midCategoryList.isEmpty()) {
            wrap.setCategoryVo(categoryVos);
            return wrap;
        }
        List<MatCategoryDetailDo> categoryDoList = issueRestDOMapper2.getMatMidCategoryDetail(taskId, shopId, categoryName);

        List<ShopStockYesterday> stockYesterday = allocationDOMapper2.getShopStockYesterday(shopId);
        Map<String, ShopStockVo> stockGroupByCategory = convertShopStockYesterday(stockYesterday, 1);

        List<SkuSaleStock> saleByDay28 = allocationDOMapper2.getSaleDay28(shopId);
        Map<String, ShopSaleVo> sale28GroupByCategory = convertSkuStockAmt(saleByDay28, 1);

        List<SkuSaleStock> saleByDay7 = allocationDOMapper2.getSaleDay7(shopId);
        Map<String, ShopSaleVo> sale7GroupByCategory = convertSkuStockAmt(saleByDay7, 1);

        categoryDo2Vo(categoryVos, categoryDoList, stockGroupByCategory, sale28GroupByCategory, sale7GroupByCategory);
        wrap.setCategoryVo(categoryVos);

        return wrap;
    }

    private Map<String, ShopStockVo> convertShopStockYesterday(List<ShopStockYesterday> stockYesterday, int categoryType) {
        Map<String, ShopStockVo> map = new HashMap<>();
        if (stockYesterday != null && !stockYesterday.isEmpty()) {

            String categoryName = null;

            BigDecimal totalAmt = new BigDecimal(0);
            BigDecimal totalStockQty = new BigDecimal(0);

            BigDecimal onlyShopStockTotalAmt = new BigDecimal(0);
            //在店总数量
            BigDecimal onlyShopStockTotalQty = new BigDecimal(0);


            for (ShopStockYesterday stock : stockYesterday) {

                if (categoryType == 0) {
                    categoryName = stock.getCategoryName();
                } else {
                    categoryName = stock.getMidCategoryName();
                }

                if (map.containsKey(categoryName)) {

                    ShopStockVo obj = map.get(categoryName);

                    obj.setShopStockAmtYd(obj.getShopStockAmtYd().add(stock.getShopStockAmtYd()));
                    obj.setShopStockQtyYd(obj.getShopStockQtyYd().add(stock.getShopStockQtyYd()));

                    obj.setOnlyShopStockAmtYd(obj.getOnlyShopStockAmtYd().add(stock.getOnlyShopStockAmtYd()));
                    obj.setOnlyShopStockQtyYd(obj.getOnlyShopStockQtyYd().add(stock.getOnlyShopStockQtyYd()));
                    map.put(categoryName, obj);

                } else {
                    ShopStockVo shopStockAndAmt = new ShopStockVo();

                    shopStockAndAmt.setShopStockQtyYd(stock.getShopStockQtyYd());
                    shopStockAndAmt.setShopStockAmtYd(stock.getShopStockAmtYd());

                    shopStockAndAmt.setOnlyShopStockQtyYd(stock.getOnlyShopStockQtyYd());
                    shopStockAndAmt.setOnlyShopStockAmtYd(stock.getOnlyShopStockAmtYd());
                    map.put(categoryName, shopStockAndAmt);
                }
                totalAmt = totalAmt.add(stock.getShopStockAmtYd());
                totalStockQty = totalStockQty.add(stock.getShopStockQtyYd());

                onlyShopStockTotalAmt = onlyShopStockTotalAmt.add(stock.getOnlyShopStockAmtYd());
                onlyShopStockTotalQty = onlyShopStockTotalQty.add(stock.getOnlyShopStockQtyYd());
            }

            ShopStockVo totalAmtVo = new ShopStockVo();
            //全量总计
            totalAmtVo.setTotalAmt(totalAmt);
            totalAmtVo.setTotalStockQty(totalStockQty);
            //仅在店总计
            totalAmtVo.setOnlyShopStockTotalAmt(onlyShopStockTotalAmt);
            totalAmtVo.setOnlyShopStockTotalQty(onlyShopStockTotalQty);
            map.put(TOTAL_INFO_KEY, totalAmtVo);
        }
        return map;
    }

    @Override
    public OrderModifyWrap modifyOrderSku(OrderSkuModifyReq req) {

        OrderModifyWrap wrap = new OrderModifyWrap();

        String matCode = req.getMatCode();
        String sizeId = req.getSizeId();
        int taskId = req.getTaskId();
        String shopId = req.getShopId();
        int orderPakeage = req.getCount();
        int isIssue = req.getIsIssue();

        IssueDetailDo detailInfo = null;
        if (isIssue == 0) {
            detailInfo = issueRestDOMapper2.getDetail(shopId, matCode, sizeId, taskId);
        } else {
            detailInfo = issueUndoDOMapper2.getDetail(shopId, matCode, sizeId, taskId);
        }
        Assert.notNull(detailInfo, Constant.NO_FOUND_ISSUE_ORDER);

        int totalOutStock = issueRestDOMapper2.getTotalOutStock(matCode, sizeId, taskId);
        int minPackageQty = detailInfo.getMinPackageQty();
        //总配发数量
        int modifyTotalQty = orderPakeage * minPackageQty;
        logger.debug("isIssue:{},modifyOrderSku -> modifyTotalQty:{},totalOutStock:{}", isIssue, modifyTotalQty, totalOutStock);

        int otherShopStockQty = issueRestDOMapper2.otherShopStockQty(shopId, matCode, sizeId, taskId);
        boolean isEnoughStock = totalOutStock > 0 && (otherShopStockQty + modifyTotalQty) <= totalOutStock;
        Assert.isTrue(isEnoughStock, Constant.STOCK_NO_ENOUGH);

        // 配发数量 > getMaxCompare28(最小中包即规格数量，28个数) && 可售天数 > 45 ,不满足则执行修改
        int maxPackageQty = getMaxCompare28(minPackageQty);
        boolean packageFlag = (modifyTotalQty > maxPackageQty);

        String saleDays = null;
        IssueInStockDo stockDo = issueRestDOMapper2.getIssueInStock(shopId, matCode, sizeId, taskId);
        BigDecimal avgSaleQty = stockDo.getAvgSaleQty();
        if (avgSaleQty != null && avgSaleQty.doubleValue() > 0) {
            saleDays = (stockDo.getTotalStockQty().add(new BigDecimal(modifyTotalQty))).divide(avgSaleQty, DIVIDE_SCALE_2, ROUND_HALF_UP).toString();
        } else {
            logger.warn("-----> avgSaleQty null or zero,saleDays = / ");
            saleDays = Constant.DEFAULT_VAL_SLASH;
        }
        boolean saleDayFlag = saleDaysMoreThan45(saleDays);
        logger.info("packageFlag:{},maxPackageQty:{}|totalQty:{},saleDays:{},saleDayFlag:{}", packageFlag, maxPackageQty, modifyTotalQty, saleDays, saleDayFlag);

        boolean flag = packageFlag && saleDayFlag;
        Assert.isTrue(!flag, Constant.CHECK_RULE_FAIL.replace("{0}", maxPackageQty + ""));

        OrderSkuModifyParam param = new OrderSkuModifyParam();
        BeanUtils.copyProperties(req, param);
        param.setTotalQty(modifyTotalQty);
        int rst = 0;
        if (isIssue == 0) {
            rst = issueRestDOMapper2.modifySkuPackageQty(param);
        } else {
            rst = issueUndoDOMapper2.modifySkuPackageQty(param);
        }
        Assert.isTrue(rst > 0, Constant.FAIL_MSG);

        saveModifySkuLog(param, detailInfo);

        //(isd.OrderPackage * isd.MinPackageQty + iis.StockQty)/iis.AvgSaleQty
        BigDecimal totalStock = stockDo.getStockQty().add(new BigDecimal(orderPakeage * detailInfo.getMinPackageQty()));
        String canSaleDays = null;
        if (avgSaleQty != null && avgSaleQty.doubleValue() > 0) {
            canSaleDays = totalStock.divide(stockDo.getAvgSaleQty(), DIVIDE_SCALE_2, ROUND_HALF_UP).toString();
        } else {
            canSaleDays = Constant.DEFAULT_VAL_SLASH;
        }

        wrap.setSaleDays(canSaleDays);
        wrap.setOrderQty(modifyTotalQty);

        return wrap;
    }

    @Override
    public ShopUserNameWrap getUserNames() {
        ShopUserNameWrap wrap = new ShopUserNameWrap();
        List<String> list = issueRestDOMapper2.getShopUserNames();
        list = list.stream().filter(str -> StringUtils.isNotBlank(str)).collect(Collectors.toList());
        wrap.setUserNames(list);
        return wrap;
    }

    @Override
    public int truncateTab(String tabName) {
        try {
            issueRestDOMapper2.truncateTable(tabName);
        } catch (Exception e) {
            logger.error("truncateTab err:{},tabName:{}", e.getMessage(), tabName);
            return 0;
        }
        return 1;
    }

    @Override
    public List<String> categoryList(int taskId, String shopId) {
        List<String> categoryList = issueRestDOMapper2.categoryList(taskId, shopId);
        List<String> undoCategory = issueUndoDOMapper2.categoryList(taskId, shopId);
        if (undoCategory !=null && !undoCategory.isEmpty()){
            categoryList.removeAll(undoCategory);
            categoryList.addAll(undoCategory);
        }
        sortList(categoryList);
        return categoryList;
    }

    @Override
    public List<String> midCategoryList(int taskId, String shopId, String categoryName) {
        List<String> midCategoryList = null;
        List<String> undoMidCategoryList = null;
        if (StringUtils.isBlank(categoryName)) {
            midCategoryList = issueRestDOMapper2.allMidCategorys(taskId, shopId);
            undoMidCategoryList = issueUndoDOMapper2.allMidCategorys(taskId, shopId);
        } else {
            midCategoryList = issueRestDOMapper2.midCategorys(categoryName, taskId, shopId);
            undoMidCategoryList = issueUndoDOMapper2.midCategorys(categoryName, taskId, shopId);
        }
        if (undoMidCategoryList !=null && !undoMidCategoryList.isEmpty()){
            midCategoryList.removeAll(undoMidCategoryList);
            midCategoryList.addAll(undoMidCategoryList);
        }
        sortList(midCategoryList);
        return midCategoryList;
    }

    @Override
    public List<String> smallCategoryList(int taskId, String shopId, String midCategoryName) {
        List<String> smallCategoryList = null;
        List<String> undoSmallCategoryList = null;
        if (StringUtils.isBlank(midCategoryName)) {
            smallCategoryList = issueRestDOMapper2.allSmallCategorys(taskId, shopId);
            undoSmallCategoryList = issueUndoDOMapper2.allSmallCategorys(taskId, shopId);
        } else {
            smallCategoryList = issueRestDOMapper2.smallCategorys(midCategoryName, taskId, shopId);
            undoSmallCategoryList = issueUndoDOMapper2.smallCategorys(midCategoryName, taskId, shopId);
        }
        if (undoSmallCategoryList != null && !undoSmallCategoryList.isEmpty()) {
            smallCategoryList.removeAll(undoSmallCategoryList);
            smallCategoryList.addAll(undoSmallCategoryList);
        }
        sortList(smallCategoryList);
        return smallCategoryList;
    }

    /**
     * 保存修改日志
     *
     * @param param
     * @param detailDo
     */
    private void saveModifySkuLog(OrderSkuModifyParam param, IssueDetailDo detailDo) {
        try {
            StringBuffer sbf = new StringBuffer();
            sbf.append("IsIssue:").append(detailDo.getIsIssue())
                    .append("|orderPackage:").append(detailDo.getOrderPackage()).append("-").append(param.getCount());
            OperateLog log = new OperateLog();
            log.setContent(sbf.toString());
            log.setTaskId(param.getTaskId());
            log.setMatCode(param.getMatCode());
            log.setOperateType(Constant.MODIFY_ISSUE_SKU);
            log.setShopId(param.getShopId());
            log.setSizeId(param.getSizeId());
            log.setOperator(AuthUtil.getSessionUserId());
            operateLogMapper.saveLog(log);
        } catch (Exception e) {
            logger.error("saveModifySkuLog err:{},param:{},detailDo:{}", e.getMessage(), param, detailDo);
        }
    }

    private boolean saleDaysMoreThan45(String saleDays) {
        if (StringUtils.isNotBlank(saleDays) || Constant.DEFAULT_VAL_SLASH.equals(saleDays)) {
            return false;
        }
        return Double.parseDouble(saleDays) > Constant.SALE_DAY_45;
    }

    private int getMaxCompare28(int minPackageQty) {
        if (minPackageQty < Constant.MIN_PACKAGE_QTY_28) {
            minPackageQty = Constant.MIN_PACKAGE_QTY_28;
        }
        return minPackageQty;
    }

    private void categoryDo2Vo(List<MatCategoryDetailVo> categoryVos, List<MatCategoryDetailDo> categoryDoList,
                               Map<String, ShopStockVo> stockGroupByCategory, Map<String, ShopSaleVo> saleGroupByCategory,
                               Map<String, ShopSaleVo> sale7GroupByCategory) {

        String categoryName = null;
        NumberFormat percent = getPercentFormat();
        Iterator<MatCategoryDetailDo> itr = categoryDoList.iterator();
        while (itr.hasNext()) {
            MatCategoryDetailDo detailDo = itr.next();
            MatCategoryDetailVo detailVo = new MatCategoryDetailVo();
            BeanUtils.copyProperties(detailDo, detailVo);

            categoryName = detailDo.getCategoryName();
            //有总额才计算
            ShopStockVo stockTotal = stockGroupByCategory.get(TOTAL_INFO_KEY);
            ShopStockVo stockVo = stockGroupByCategory.get(categoryName);
            if (stockTotal != null && stockVo != null) {
                //该分类总库存
                BigDecimal categoryStockQty = stockVo.getShopStockQtyYd();
                detailVo.setShopStock(categoryStockQty);
                if (detailVo.getAvgSaleQty() != null && detailVo.getAvgSaleQty().doubleValue() > 0) {
                    detailVo.setTurnOverDays(categoryStockQty.divide(detailVo.getAvgSaleQty(), DIVIDE_SCALE_2, ROUND_HALF_UP));
                } else {
                    logger.warn("-----> setTurnOverDays null or zero");
                    detailVo.setTurnOverDays(DEFAULT_NUM_99);
                }

                //该分类总库存金额
                BigDecimal categoryStockAmt = stockVo.getShopStockAmtYd();

                //在店库存金额
                BigDecimal categoryOnlyShopStockAmtYd = stockVo.getOnlyShopStockAmtYd();
                detailVo.setOnlyShopStockAmtYd(categoryOnlyShopStockAmtYd);
                BigDecimal categoryOnlyShopStockQtyYd = stockVo.getOnlyShopStockQtyYd();

                ShopStockVo totalVo = stockGroupByCategory.get(TOTAL_INFO_KEY);
                BigDecimal totalStockAmt = totalVo.getTotalAmt();
//                BigDecimal totalStockQty = totalVo.getTotalStockQty();

                //总在店
                BigDecimal shopStockTotalAmt = totalVo.getOnlyShopStockTotalAmt();
                BigDecimal shopStockTotalQty = totalVo.getOnlyShopStockTotalQty();

                if (totalStockAmt != null && totalStockAmt.doubleValue() > 0) {

                    double beforeStockRate = categoryStockAmt.divide(totalStockAmt, DIVIDE_SCALE_4, ROUND_HALF_UP).doubleValue();
                    detailVo.setBeforeStockRate(percent.format(beforeStockRate));

                    //库存占比(订货后)=(昨日库存金额+配发库存金额)/(总库存金额+配发库存金额)
                    BigDecimal orderAmtAfter = categoryStockAmt.add(detailDo.getOrderAmt());
                    BigDecimal totalAmtAfter = totalStockAmt.add(detailDo.getOrderAmt());
                    double afterStockRate = orderAmtAfter.divide(totalAmtAfter, DIVIDE_SCALE_4, ROUND_HALF_UP).doubleValue();
                    detailVo.setAfterStockRate(percent.format(afterStockRate));

                    //总库存金额
                    detailVo.setTotalStockAmt(categoryStockAmt);
                    detailVo.setShopStockTotalAmt(categoryOnlyShopStockAmtYd);

                    //总库存金额占比
                    double totalStockAmtRate = categoryStockAmt.divide(totalStockAmt, DIVIDE_SCALE_4, ROUND_HALF_UP).doubleValue();
                    detailVo.setTotalStockAmtRate(percent.format(totalStockAmtRate));

                    //在店库存金额占比
                    double shopStockAmtRate = categoryOnlyShopStockAmtYd.divide(shopStockTotalAmt, DIVIDE_SCALE_4, ROUND_HALF_UP).doubleValue();
                    detailVo.setShopStockAmtRate(percent.format(shopStockAmtRate));

                    //在店库存占比
                    double onlyShopStockQtyRate = categoryOnlyShopStockQtyYd.divide(shopStockTotalQty, DIVIDE_SCALE_4, ROUND_HALF_UP).doubleValue();
                    detailVo.setOnlyShopStockQtyRate(percent.format(onlyShopStockQtyRate));


                } else {

                    logger.warn("-----> setBeforeStockRate|setAfterStockRate null or zero");

                    detailVo.setBeforeStockRate(DEFAULT_NUM_99.intValue() + "");
                    detailVo.setAfterStockRate(DEFAULT_NUM_99.intValue() + "");

                    //总库存金额
                    detailVo.setTotalStockAmt(new BigDecimal(0));
                    detailVo.setShopStockTotalAmt(shopStockTotalAmt);

                    detailVo.setTotalStockAmtRate(DEFAULT_NUM_99.intValue() + "");
                    detailVo.setShopStockAmtRate(DEFAULT_NUM_99.intValue() + "");
                }

            } else {
                logger.debug("stockGroupByCategory null,category:{}", categoryName);
                detailVo.setShopStock(DEFAULT_NUM_1);
                detailVo.setBeforeStockRate(DEFAULT_NUM_1.intValue() + "");
                detailVo.setAfterStockRate(DEFAULT_NUM_1.intValue() + "");
                detailVo.setTurnOverDays(DEFAULT_NUM_1);
            }

            ShopSaleVo sale28Total = saleGroupByCategory.get(TOTAL_INFO_KEY);
            ShopSaleVo sale28Vo = saleGroupByCategory.get(categoryName);
            if (sale28Total != null && sale28Vo != null) {
                //销售占比=销售金额/总销售金额
                BigDecimal saleAmt = sale28Vo.getSaleAmt();
                BigDecimal saleQty = sale28Vo.getSaleQty();
                detailVo.setSaleQty28(saleQty);

                BigDecimal totalSaleAmt = sale28Total.getTotalSaleAmt();
                BigDecimal totalSaleQty = sale28Total.getTotalSaleQty();

                ShopSaleVo sale7 = sale7GroupByCategory.get(categoryName);
                if (sale7 != null && sale7.getSaleQty() != null) {
                    detailVo.setSaleQty7(sale7.getSaleQty());
                }

                if (totalSaleAmt != null && totalSaleAmt.doubleValue() > 0) {
                    double soldRate = saleAmt.divide(totalSaleAmt, DIVIDE_SCALE_4, ROUND_HALF_UP).doubleValue();
                    detailVo.setSoldRate(percent.format(soldRate));

                    double saleQtyRate = saleQty.divide(totalSaleQty, DIVIDE_SCALE_4, ROUND_HALF_UP).doubleValue();
                    detailVo.setSaleQtyRate(percent.format(saleQtyRate));
                } else {
                    logger.warn("-----> setSoldRate null or zero");
                    detailVo.setSoldRate(DEFAULT_NUM_99.intValue() + "");
                    detailVo.setSaleQtyRate(DEFAULT_NUM_99.intValue() + "");
                }
            } else {
                logger.warn("saleGroupByCategory null, category:{}", categoryName);
                detailVo.setSoldRate(DEFAULT_NUM_1.intValue() + "");
                detailVo.setSaleQtyRate(DEFAULT_NUM_1.intValue() + "");
            }

            if (detailVo.getAvgSaleQty() != null && detailVo.getAvgSaleQty().doubleValue() > 0) {
                //预计周转=（配发量+在店库存量）/日均销量
                detailVo.setTurnOverEstimate(detailVo.getOrderQty().add(detailVo.getShopStock())
                        .divide(detailVo.getAvgSaleQty(), DIVIDE_SCALE_2, ROUND_HALF_UP));
            } else {
                logger.warn("-----> setTurnOverEstimate null or zero");
                detailVo.setTurnOverEstimate(DEFAULT_NUM_99);
            }
            detailVo.setNeedQty(detailVo.getNeedQty().setScale(0, ROUND_HALF_UP));
            detailVo.setOrderQty(detailVo.getOrderQty().setScale(0, ROUND_HALF_UP));

            detailVo.setDeviationNum(detailVo.getShopStock().subtract(detailVo.getNeedQty()));
            detailVo.setDisplayNeedQty(detailVo.getDisplayDepth().multiply(detailVo.getDisplayQty()));

            categoryVos.add(detailVo);
        }
    }

    /**
     * 百分比格式对象
     *
     * @return
     */
    private NumberFormat getPercentFormat() {
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(2);
        return percent;
    }

    /**
     * 转换总页数
     *
     * @param totalCount
     * @param pageSize
     * @return
     */
    private int getTotalPage(long totalCount, Integer pageSize) {
        if (totalCount <= 0 || pageSize == null || pageSize <= 0) {
            return 1;
        }
        return (int) ((totalCount - 1) / pageSize + 1);
    }

    private long costTime(String tips, long start) {
        long end = System.currentTimeMillis();
        logger.info(tips + " cost," + (end - start));
        return end;
    }

    @Override
    public Map<String, Integer> recalcCategoryCanSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.recalcCategoryCanSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("recalcCategoryCanSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, Integer> map = list.stream().collect(Collectors.toMap(CategorySkcData::getCategoryName, CategorySkcData::getSkcCount));
        return map;
    }

    @Override
    public Map<String, Integer> recalcCategoryKeepSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.recalcCategoryKeepSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("recalcCategoryKeepSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, Integer> map = list.stream().collect(Collectors.toMap(CategorySkcData::getCategoryName, CategorySkcData::getSkcCount));
        return map;
    }

    @Override
    public Map<String, Integer> recalcCategoryNewSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.recalcCategoryNewSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("recalcCategoryNewSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, Integer> map = list.stream().collect(Collectors.toMap(CategorySkcData::getCategoryName, CategorySkcData::getSkcCount));
        return map;
    }

    @Override
    public Map<String, Integer> recalcCategoryProhibitedSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.recalcCategoryProhibitedSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("recalcCategoryProhibitedSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, Integer> map = list.stream().collect(Collectors.toMap(CategorySkcData::getCategoryName, CategorySkcData::getSkcCount));
        return map;
    }

    @Override
    public Map<String, Integer> recalcCategoryValidSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.recalcCategoryValidSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("recalcCategoryValidSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, Integer> map = list.stream().collect(Collectors.toMap(CategorySkcData::getCategoryName, CategorySkcData::getSkcCount));
        return map;
    }

    @Override
    public Integer insertRecalcCategoryCountData(List<IssueCategorySkcData> categorySkcData) {
        Integer rst = issueRestDOMapper2.batchInsertRecalcCategoryCountData(categorySkcData);
        return rst;
    }

    @Override
    public Map<String, List<CategorySkcData>> recalcMidCategoryCanSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.recalcMidCategoryCanSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("recalcMidCategoryCanSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, List<CategorySkcData>> groupBy = list.stream().collect(Collectors.groupingBy(CategorySkcData::getCategoryName));
        return groupBy;
    }

    @Override
    public Map<String, List<CategorySkcData>> recalcMidCategoryKeepSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.recalcMidCategoryKeepSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("recalcMidCategoryKeepSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, List<CategorySkcData>> groupBy = list.stream().collect(Collectors.groupingBy(CategorySkcData::getCategoryName));
        return groupBy;
    }

    @Override
    public Map<String, List<CategorySkcData>> recalcMidCategoryNewSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.recalcMidCategoryNewSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("recalcMidCategoryNewSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, List<CategorySkcData>> groupBy = list.stream().collect(Collectors.groupingBy(CategorySkcData::getCategoryName));
        return groupBy;
    }

    @Override
    public Map<String, List<CategorySkcData>> recalcMidCategoryProhibitedSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.recalcMidCategoryProhibitedSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("recalcMidCategoryProhibitedSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, List<CategorySkcData>> groupBy = list.stream().collect(Collectors.groupingBy(CategorySkcData::getCategoryName));
        return groupBy;
    }

    @Override
    public Map<String, List<CategorySkcData>> recalcMidCategoryValidSkcCount(int taskId, String shopId, List<String> categorys) {
        List<CategorySkcData> list = issueRestDOMapper2.recalcMidCategoryValidSkcCount(taskId,shopId,categorys);
        if (list == null){
            logger.warn("recalcMidCategoryValidSkcCount null,shopId:{},taskId:{}", shopId, taskId);
            list = Collections.emptyList();
        }
        Map<String, List<CategorySkcData>> groupBy = list.stream().collect(Collectors.groupingBy(CategorySkcData::getCategoryName));
        return groupBy;
    }

    @Override
    public Integer insertRecalcMidCategoryCountData(List<IssueCategorySkcData> midCategorySkcData) {
        Integer rst = issueRestDOMapper2.insertRecalcMidCategoryCountData(midCategorySkcData);
        return rst;
    }

    @Override
    public List<IssueOutStockRemainDo> issueOutStockRemainStock(int taskId) {
        List<IssueOutStockRemainDo> list = issueRestDOMapper2.issueOutStockRemainStock(taskId);
        return list;
    }

    @Override
    public IssueDetailDistStock issueDeatilShopDistStock(int taskId, String shopId, String matCode, String sizeID) {
        IssueDetailDistStock detailDistStock = issueRestDOMapper2.issueDeatilShopDistStock(taskId,shopId,matCode,sizeID);
        return detailDistStock;
    }

    @Override
    public int delRecalcGoodsData(int taskId, String shopID) {
        return issueRestDOMapper2.delRecalcGoodsData(taskId,shopID);
    }

    @Override
    public List<IssueDetailDistStock> getRecalcDetailStock(int taskId, String shopId) {
        List<IssueDetailDistStock> list = issueRestDOMapper2.getRecalcDetailStock(taskId, shopId);
        return list;
    }

    @Override
    public IssueOutStockRemainDo getRecalcRemainStock(int taskId, String matCode, String sizeID) {
        return issueRestDOMapper2.getRecalcRemainStock(taskId,matCode,sizeID);
    }

    @Override
    public int getIssueDay(int roadDay, String issueTime, int dayWeek) {
        return IssueDayUtil.getIssueDay(roadDay,issueTime,dayWeek);
    }

    @Override
    public List<IssueMidCategoryQtyDo> getMidCategoryPercentAvgSaleQty(int taskId, String shopId, int status) {
        return issueRestDOMapper2.getMidCategoryPercentAvgSaleQty(taskId,shopId,status);
    }

}
