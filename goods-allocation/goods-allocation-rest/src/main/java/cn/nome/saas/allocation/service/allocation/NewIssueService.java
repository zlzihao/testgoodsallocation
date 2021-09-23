package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.utils.DateUtils;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.cache.ShopListCache;
import cn.nome.saas.allocation.component.AsyncTask;
import cn.nome.saas.allocation.constant.old.Constant;
import cn.nome.saas.allocation.model.old.allocation.Dictionary;
import cn.nome.saas.allocation.model.old.allocation.*;
import cn.nome.saas.allocation.model.old.issue.*;
import cn.nome.saas.allocation.repository.dao.allocation.IssueTaskDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.NewIssueDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO;
import cn.nome.saas.allocation.repository.entity.allocation.IssueTaskDO;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueDOMapper2;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueOperateLogMapper2;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueRestDOMapper2;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueUndoDOMapper2;
import cn.nome.saas.allocation.repository.old.allocation.entity.RecalcTaskDo;
import cn.nome.saas.allocation.repository.old.vertica.dao.AllocationDOMapper2;
import cn.nome.saas.allocation.utils.AuthUtil;
import cn.nome.saas.allocation.utils.CommonUtil;
import cn.nome.saas.allocation.utils.old.sort.SortOrderDetail;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static cn.nome.saas.allocation.constant.Constant.*;
import static cn.nome.saas.allocation.constant.old.Constant.DIVIDE_SCALE_2;
import static cn.nome.saas.allocation.constant.old.Constant.DIVIDE_SCALE_4;
import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * NewIssueService
 *
 * @author Bruce01.fan
 * @date 2019/9/6
 */
@Service
public class NewIssueService {

    private static Logger logger = LoggerFactory.getLogger(NewIssueService.class);

    public static ConcurrentHashMap<String, String> GOODS_DATA_MAP = new ConcurrentHashMap<>(256);


    @Autowired
    IssueRestDOMapper2 issueRestDOMapper2;

    @Autowired
    AllocationDOMapper2 allocationDOMapper2;

    @Autowired
    IssueOperateLogMapper2 operateLogMapper;

    @Autowired
    IssueUndoDOMapper2 issueUndoDOMapper2;

    @Autowired
    NewIssueDOMapper newIssueDOMapper;

    @Autowired
    ShopListCache shopListCache;

    @Autowired
    IssueTaskDOMapper issueTaskDOMapper;
    @Autowired
    IssueDOMapper2 issueDOMapper2;
    @Autowired
    NewIssueBasicService newIssueBasicService;
    @Autowired
    NewIssueSkuCalcService newIssueSkuCalcService;
    @Autowired
    NewIssueMatchService newIssueMatchService;
    @Autowired
    NewIssueRecalcService newIssueRecalcService;
    @Autowired
    @Lazy
    AsyncTask asyncTask;

    private BigDecimal DEFAULT_NUM_99 = new BigDecimal(-99);
    private BigDecimal DEFAULT_NUM_1 = new BigDecimal(-1);

    /**
     * 线程池
     */
    private ExecutorService issueReCalcMainPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2, Runtime.getRuntime().availableProcessors() / 2,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("issue-reCalc-main-%d").build());

    /**
     * 生成goodData线程池
     */
    private ExecutorService issueGoodsDataPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("issue-goods-data-%d").build());


    private static final String TOTAL_INFO_KEY = "TOTAL_INFO_KEY";

    //商品图片本地缓存<matCode+sizeId,imgUrl>
    private static Map<String, String> skuImgMap = new HashMap<>();
//    private static Map<String, Integer> weekNumMap = new HashMap<>();
//
    @Value("${matcode.barcode.batch.size:500}")
    private int barCodeBatchSize;
//
//    @Value("${scheduler.truncate.table.names}")
//    private String truncateTabNames;

    @PostConstruct
    private void init() {
//        initNumMapping();
        initLoadImg();
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

    private void initLoadImg() {
        List<MatBarCodeImg> matBarCodeImgs = loadSkuImg(barCodeBatchSize);
        if (matBarCodeImgs != null && !matBarCodeImgs.isEmpty()) {
            matBarCodeImgs.stream().forEach(img -> skuImgMap.put(img.getMatCode() + img.getSizeID(), img.getImg()));
            logger.debug("LOAD_SKU_IMG_OVER:{}", matBarCodeImgs.size());
        }
    }

    public OrderListWrap getOrderList(OrderListReq orderListReq) {

        OrderListWrap wrap = new OrderListWrap();
        List<OrderListVo> orderVos = new ArrayList<>();

        OrderListParam param = new OrderListParam();
        BeanUtils.copyProperties(orderListReq, param);
        //设置非管理员只能查看自己管理的门店
        setShopIdList(param);

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

        IssueTaskDO issueTask = newIssueDOMapper.getIssueTask( orderListReq.getTaskId());

        // TODO bug ，修复空指针问题
        int totalCount = 0;
        if (issueTask != null) {
            totalCount = newIssueDOMapper.getOrderListCount(CommonUtil.getTaskTableName(ISSUE_DETAIL_TABLE_PREFIX, orderListReq.getTaskId(),issueTask.getRunTime()), param);
            if (totalCount > 0) {

                List<OrderListDo> orders = newIssueDOMapper.getOrderList(CommonUtil.getTaskTableName(ISSUE_DETAIL_TABLE_PREFIX, orderListReq.getTaskId(),issueTask.getRunTime()), param);

                if (orders != null && !orders.isEmpty()) {
                    orderListDo2Vo(orderVos, orders, issueTask);
                }
            }
        }

        wrap.setTotalPage(getTotalPage(totalCount, orderListReq.getPageSize()));
        wrap.setCurPage(orderListReq.getCurPage());
        wrap.setTotalCount(totalCount);
        wrap.setOrderList(orderVos);

        return wrap;
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

    private void orderListDo2Vo(List<OrderListVo> orderVos, List<OrderListDo> orders, IssueTaskDO issueTask) {
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
        List<cn.nome.saas.allocation.model.old.allocation.Dictionary> dict = issueRestDOMapper2.getDictionaryByType(dictionaryType);
        if (dict == null) {
            return Collections.emptyMap();
        }
        return dict.stream().collect(Collectors.toMap(cn.nome.saas.allocation.model.old.allocation.Dictionary::getParaKey, Dictionary::getParaValue));
    }

    public OrderDetailWrap getOrderDetail(OrderDetailReq param) {
        OrderDetailWrap wrap = new OrderDetailWrap();

        int taskId = param.getTaskId();

        ShopInDo shopInDo = issueRestDOMapper2.getShopById(param.getShopId());
        wrap.setShopName(shopInDo.getShopName());
        wrap.setShopId(shopInDo.getShopID());

        String img = null;
        List<OrderDetailVo> detailVos = new ArrayList<>();
        List<OrderDetailDo> allDetailDos = new ArrayList<>();
        List<OrderDetailDo> detailDos = null;
        List<OrderDetailDo> undoData = null;

        IssueTaskDO issueTask = newIssueDOMapper.getIssueTask( taskId);
        String inTabName = CommonUtil.getTaskTableName(ISSUE_IN_STOCK_TABLE_PREFIX,taskId,issueTask.getRunTime());
        String outTabName = CommonUtil.getTaskTableName(ISSUE_OUT_STOCK_TABLE_PREFIX,taskId,issueTask.getRunTime());
        String needTabName = CommonUtil.getTaskTableName(ISSUE_NEED_STOCK_TABLE_PREFIX,taskId,issueTask.getRunTime());
        String detailTabName = CommonUtil.getTaskTableName(ISSUE_DETAIL_TABLE_PREFIX,taskId,issueTask.getRunTime());
        String goodsDataTabName = CommonUtil.getTaskTableName(ISSUE_GOODS_DATA_TABLE_PREFIX,taskId,issueTask.getRunTime());

        //生成数据
        createTabData(goodsDataTabName, shopInDo.getShopID(), issueTask);

        Date date = new Date();
        detailDos = newIssueDOMapper.getOrderDetail(inTabName, outTabName, needTabName, detailTabName, goodsDataTabName, param);
        undoData = newIssueDOMapper.getIssueUndoDetail(inTabName, outTabName, needTabName, detailTabName, goodsDataTabName, param);

        //undoData存在重复数据，需要去重 by zdw 2020-06-18
        undoData = undoData.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(o -> o.getShopId() + "_" + o.getMatCode() + "_" + o.getSizeName()))), ArrayList::new
                )
        );

        costTime("getData", date.getTime());
        if (detailDos != null && !detailDos.isEmpty()) {
            allDetailDos.addAll(detailDos);
        }
        if (undoData != null && !undoData.isEmpty()) {
            allDetailDos.addAll(undoData);
        }

        List<MatcodeSizeId> matcodeSizeIds = new ArrayList<>();
        for (OrderDetailDo detailDo : allDetailDos) {
            if (skuImgMap.containsKey(detailDo.getMatCode() + detailDo.getSizeId())) {
                //缓存已存在图片
                continue;
            }
            matcodeSizeIds.add(new MatcodeSizeId(detailDo.getMatCode(), detailDo.getSizeId()));
        }
        date = new Date();
        List<MatBarCodeImg> matBarCodeImgs = getMatBarCodes(matcodeSizeIds);
        mergeMatCodeSizeImg(matBarCodeImgs);

        costTime("mergeMatCodeSizeImg", date.getTime());


        String matcodeSizeId = null;
        date = new Date();
        if (detailDos != null && !detailDos.isEmpty()) {

            for (OrderDetailDo detailDo : detailDos) {
                //补货周期天数
                detailDo.setIssueDay(shopInDo.getIssueDay());
                // 安全天数
                detailDo.setSafeDay(shopInDo.getSafeDay());
                OrderDetailVo detailVo = new OrderDetailVo();
                BeanUtils.copyProperties(detailDo, detailVo);

                matcodeSizeId = detailDo.getMatCode() + detailDo.getSizeId();

                img = skuImgMap.get(matcodeSizeId);
                detailVo.setImg(StringUtils.isBlank(img) ? "/" : img);
                //门店库存改为门店总库存. 增加本次配发数量 190726
                detailVo.setOrderQty(detailVo.getOrderPackage().multiply(new BigDecimal(detailVo.getMinPackageQty())));

                //补货前周转天数 = 门店库存/日均销
                if (detailVo.getAvgSaleQty() != null && detailVo.getAvgSaleQty().doubleValue() > 0) {
                    detailVo.setExIssueTurnoverDay(detailVo.getTotalStockQty().divide(detailVo.getAvgSaleQty(), 2, ROUND_HALF_UP).toString());
                } else {
                    detailVo.setExIssueTurnoverDay(Constant.DEFAULT_VAL_SLASH);
                }

                detailVo.setTotalStockQty(detailVo.getTotalStockQty().add(detailVo.getOrderQty()));

                //有效日均销与7日日均销相等
                detailVo.setValidSaleQty(detailVo.getAvgSaleQty());

                setSaleDays(detailVo);

                detailVo.setPercentAvgSaleQty(detailDo.getPercentCategory() == null ? "0%" : getPercentFormat().format(detailDo.getPercentCategory().doubleValue()));

                detailVos.add(detailVo);
            }
            costTime("issue_detail", date.getTime());
        }

        date = new Date();
        if (undoData != null && !undoData.isEmpty()) {
            for (OrderDetailDo detailDo : undoData) {
                OrderDetailVo detailVo = new OrderDetailVo();
                //补货周期天数
                detailDo.setIssueDay(shopInDo.getIssueDay());
                // 安全天数
                detailDo.setSafeDay(shopInDo.getSafeDay());
                BeanUtils.copyProperties(detailDo, detailVo);

                matcodeSizeId = detailDo.getMatCode() + detailDo.getSizeId();

                img = skuImgMap.get(matcodeSizeId);
                detailVo.setImg(StringUtils.isBlank(img) ? "/" : img);

                //补货前周转天数 = 门店库存/日均销
                if (detailVo.getAvgSaleQty() != null && detailVo.getAvgSaleQty().doubleValue() > 0) {
                    detailVo.setExIssueTurnoverDay(detailDo.getTotalStockQty().divide(detailVo.getAvgSaleQty(), 2, ROUND_HALF_UP).toString());
                } else {
                    detailVo.setExIssueTurnoverDay(Constant.DEFAULT_VAL_SLASH);
                }

                //门店库存改为门店总库存. 增加本次配发数量 190726
                detailVo.setOrderQty(detailVo.getOrderPackage().multiply(new BigDecimal(detailVo.getMinPackageQty())));
                detailVo.setTotalStockQty(detailDo.getTotalStockQty().add(detailVo.getOrderQty()));

                //有效日均销与7日日均销相等
                detailVo.setValidSaleQty(detailVo.getAvgSaleQty());

                setSaleDays(detailVo);

                convertIssueFlag(detailVo);

                detailVo.setPercentAvgSaleQty(detailDo.getPercentCategory() == null ? "0%" : getPercentFormat().format(detailDo.getPercentCategory().doubleValue()));

                detailVos.add(detailVo);
            }
            costTime("issue_undo", date.getTime());
        }
        detailVos.sort(new SortOrderDetail());

        // 填充门店代码与名称
        List<DwsDimShopDO> dwsDimShopDOList = shopListCache.getShopList();
        DwsDimShopDO dwsDimShopDO = dwsDimShopDOList.stream().filter(shop->shop.getShopId().equals(param.getShopId())).findFirst().orElse(null);

        if (dwsDimShopDO != null) {
            detailVos.forEach(detailDo->{
                detailDo.setShopCode(dwsDimShopDO.getShopCode());
                detailDo.setShopName(dwsDimShopDO.getShopName());
            });
        }

        List<OrderDetailVo> subDetailVos;
        wrap.setTotal(detailVos.size());
        if (param.getCurPage() == null || param.getPageSize() == null) {
            subDetailVos = detailVos;
        } else {
            int formIndex = (param.getCurPage() - 1) * param.getPageSize();
            int toIndex = param.getCurPage() * param.getPageSize();
            subDetailVos = detailVos.subList(formIndex, toIndex);
        }

        wrap.setOrderDetailVo(subDetailVos);
        return wrap;
    }

    public List<OrderDetailVo> getOrderDetailMulti(OrderDetailReq param) throws ExecutionException, InterruptedException {
        List<String> shopIds = param.getShopIds();

        if (CollectionUtils.isEmpty(shopIds)) {
            return new ArrayList<>();
        }

        List<Future<List<OrderDetailVo>>> futureList = new ArrayList<>();
        for (String shopId : shopIds) {

            // 对这种id，需要去掉空格和换行符
            final String processedShopId = shopId.trim().replaceAll("\\s*|\t|\r|\n", "");

            LoggerUtil.info(logger, "downloadOrderDetailMulti, get shopId: {0}", processedShopId);
            if (Strings.isNullOrEmpty(processedShopId)) {
                LoggerUtil.warn(logger, "downloadOrderDetailMulti, shopId is null, oriShopId: {0}, processedShopId: {1}", shopId, processedShopId);
                continue;
            }

            Future<List<OrderDetailVo>> future = issueGoodsDataPool.submit(() -> {
                try {
                    OrderDetailReq paramClone = param.clone();
                    paramClone.setShopId(processedShopId);
                    OrderDetailWrap wrap = getOrderDetail(paramClone);
                    return wrap.getOrderDetailVo();
                } catch (Exception e) {
                    LoggerUtil.error(e, logger, "getOrderDetailMulti catch exception,shopId:{0}", processedShopId);
                    return new ArrayList<>();
                }

            });
            futureList.add(future);
        }

        List<OrderDetailVo> orderDetailVoList = new ArrayList<>();
        for (Future future : futureList) {
            orderDetailVoList.addAll((List<OrderDetailVo>) future.get());
        }

        return orderDetailVoList;
    }

    /**
     * 生成配发需要数据
     * @param goodsDataTabName goodsDataTabName
     * @param shopId shopId
     * @param issueTask  issueTask
     */
    private void createTabData(String goodsDataTabName, String shopId, IssueTaskDO issueTask) {
        //第一次初始化时
        int count = 6;
        while (count > 0) {
            if (newIssueDOMapper.getGoodsDataCount(goodsDataTabName, shopId) <= 0) {
                if (!GOODS_DATA_MAP.containsKey(shopId)) {
                    try {
                        GOODS_DATA_MAP.put(shopId, "1");
                        newIssueMatchService.processIssueGoodsData(issueTask.getId(),new HashSet<>(Arrays.asList(shopId)),issueTask.getRunTime(), false);
                        newIssueMatchService.processCategorySkcData(issueTask.getId(),new HashSet<>(Arrays.asList(shopId)),issueTask.getRunTime(), false);
                    } finally {
                        GOODS_DATA_MAP.remove(shopId);
                    }
                } else {//正在生成中
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }
                if (count-- == 0) {
                    throw new BusinessException("12000", "数据正在生成中, 请稍后再试!");
                }
            } else {
                break;
            }
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

    public Map<String, BigDecimal> getStockRemainDos(int taskId, Set<String> goodsDataMatCodeSet, Set<String> goodsDataSizeIdSet) {
        List<IssueOutStockRemainDo> list = issueRestDOMapper2.matcodeStockRemain(taskId, goodsDataMatCodeSet, goodsDataSizeIdSet);
        if (list == null) {
            list = Collections.emptyList();
        }
        Map<String, BigDecimal> stockRemainMap = new HashMap<>();
        list.stream().forEach(remainDo -> stockRemainMap.put(remainDo.getMatCode() + remainDo.getSizeID(), remainDo.getStockQty()));
        return stockRemainMap;
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
            if (issueInstock.getMinQty() != null) {
                detailDo.setSecurityQty(issueInstock.getMinQty().intValue());
            }
        }
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
            detailVo.setSaleDays(Constant.DEFAULT_VAL_SLASH);
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

    private long costTime(String tips, long start) {
        long end = System.currentTimeMillis();
        logger.info(tips + " cost," + (end - start));
        return end;
    }

    /**
     * 门品类统计明细
     *
     * @param taskId
     * @param shopId
     * @param categoryName
     * @return
     */
    public MatDetailWrap getMatDetail(int taskId, String shopId, String categoryName) {

        MatDetailWrap wrap = new MatDetailWrap();

        IssueTaskDO task = newIssueDOMapper.getIssueTask(taskId);

        ShopInDo shopInDo = issueRestDOMapper2.getShopById(shopId);
        wrap.setShopName(shopInDo.getShopName());

        List<MatCategoryDetailVo> categoryVos = new ArrayList<>();

        String inTableName = CommonUtil.getTaskTableName(ISSUE_IN_STOCK_TABLE_PREFIX, taskId,task.getRunTime());
        String detailTableName = CommonUtil.getTaskTableName(ISSUE_DETAIL_TABLE_PREFIX, taskId,task.getRunTime());
        String midTableName = CommonUtil.getTaskTableName(ISSUE_MID_CATAGORY_QTY_TABLE_PREFIX, taskId,task.getRunTime());
        String categoryDataTableName = CommonUtil.getTaskTableName(ISSUE_CATEGORY_DATA_TABLE_PREFIX, taskId,task.getRunTime());
        List<MatCategoryDetailDo> categoryDoList = newIssueDOMapper.getMatCategoryDetail(inTableName, detailTableName, midTableName, categoryDataTableName, shopId, categoryName);

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
        percent.setMaximumFractionDigits(4);
        return percent;
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
     * 获取最后运行的配发任务
     * @return
     */
    public IssueTaskVo getLastTask() {
        IssueTaskVo taskVo = newIssueDOMapper.getLastTask();
        if (taskVo == null) {
            taskVo = new IssueTaskVo();
        }
        IssueTaskVo reRunTaskVo = newIssueDOMapper.getLastReRunTask();
        if (reRunTaskVo != null && reRunTaskVo.getTaskId() > taskVo.getTaskId()) {
            taskVo.setReRun(Constant.STATUS_RERUN);
        }
        return taskVo;
    }

    public IssueTaskDO getTaskById(int taskId) {
        return newIssueDOMapper.getIssueTask(taskId);
    }

    public IssueTaskDO createTask(int runStatus, int taskType, String remark, int ready) {
        Date now = new Date();
        IssueTaskDO task = new IssueTaskDO();
        task.setName(DateUtils.toString(now, "yyyy-MM-dd HH:mm:ss"));
        task.setRunTime(now);
        task.setCreatedAt(now);
        task.setTaskStatus(runStatus);
        task.setTaskType(taskType);
        task.setOperator(AuthUtil.getSessionUserId());
        task.setReady(ready);

        // 重算重跑任务
        if (StringUtils.isNotBlank(remark)) {
            task.setRemark(remark);
        } else if (runStatus == 2) {
            task.setRemark("RERUN");
        } else {
            task.setRemark("RUN");
        }

        issueTaskDOMapper.addTask(task);

        return task;
    }

    /**
     * 门店中类统计明细
     *
     * @param taskId
     * @param shopId
     * @param categoryName
     * @return
     */
    public MatDetailWrap getMatMidCategoryDetail(int taskId, String shopId, String categoryName) {
        MatDetailWrap wrap = new MatDetailWrap();

        List<MatCategoryDetailVo> categoryVos = new ArrayList<>();
        List<String> midCategoryList = issueRestDOMapper2.getAllMidCategory(categoryName);
        if (midCategoryList == null || midCategoryList.isEmpty()) {
            wrap.setCategoryVo(categoryVos);
            return wrap;
        }

        IssueTaskDO task = newIssueDOMapper.getIssueTask(taskId);
        Date runTime = task.getRunTime();
        String inTableName = CommonUtil.getTaskTableName(ISSUE_IN_STOCK_TABLE_PREFIX, taskId,runTime);
        String detailTableName = CommonUtil.getTaskTableName(ISSUE_DETAIL_TABLE_PREFIX, taskId,runTime);
        String midTableName = CommonUtil.getTaskTableName(ISSUE_MID_CATAGORY_QTY_TABLE_PREFIX, taskId,runTime);
        List<MatCategoryDetailDo> categoryDoList = newIssueDOMapper.getMatMidCategoryDetail(inTableName, detailTableName, midTableName, shopId, categoryName);

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

    /**
     * 获取大类列表
     * @param taskId
     * @param shopId
     * @return
     */
    public List<String> categoryList(int taskId, String shopId) {
        IssueTaskDO task = newIssueDOMapper.getIssueTask(taskId);
        Date runTime = task.getRunTime();
        String detailTableName = CommonUtil.getTaskTableName(ISSUE_DETAIL_TABLE_PREFIX, taskId,runTime);
        List<String> categoryList = newIssueDOMapper.categoryList(detailTableName, shopId);
        //TODO
//        List<String> undoCategory = issueUndoDOMapper2.categoryList(taskId, shopId);
//        if (undoCategory !=null && !undoCategory.isEmpty()){
//            categoryList.removeAll(undoCategory);
//            categoryList.addAll(undoCategory);
//        }
        categoryList.sort(String::compareTo);
        return categoryList;
    }

    public List<String> midCategoryList(int taskId, String shopId, String categoryName) {
        List<String> midCategoryList = null;
//        List<String> undoMidCategoryList = null;

        IssueTaskDO task = newIssueDOMapper.getIssueTask(taskId);
        Date runTime = task.getRunTime();
        String detailTableName = CommonUtil.getTaskTableName(ISSUE_DETAIL_TABLE_PREFIX, taskId,runTime);

        if (StringUtils.isBlank(categoryName)) {
            midCategoryList = newIssueDOMapper.allMidCategorys(detailTableName, shopId);
//            undoMidCategoryList = newIssueDOMapper.allMidCategorys(detailTableName, shopId);
        } else {
            midCategoryList = newIssueDOMapper.midCategorys(detailTableName, categoryName, shopId);
//            undoMidCategoryList = newIssueDOMapper.midCategorys(categoryName, taskId, shopId);
        }
//        if (undoMidCategoryList !=null && !undoMidCategoryList.isEmpty()){
//            midCategoryList.removeAll(undoMidCategoryList);
//            midCategoryList.addAll(undoMidCategoryList);
//        }
        midCategoryList.sort(String::compareTo);
        return midCategoryList;
    }

    public List<String> smallCategoryList(int taskId, String shopId, String midCategoryName) {
        List<String> smallCategoryList = null;
//        List<String> undoSmallCategoryList = null;

        IssueTaskDO task = newIssueDOMapper.getIssueTask(taskId);
        Date runTime = task.getRunTime();
        String detailTableName = CommonUtil.getTaskTableName(ISSUE_DETAIL_TABLE_PREFIX, taskId,runTime);

        if (StringUtils.isBlank(midCategoryName)) {
            smallCategoryList = newIssueDOMapper.allSmallCategorys(detailTableName, shopId);
//            undoSmallCategoryList = newIssueDOMapper.allSmallCategorys(taskId, shopId);
        } else {
            smallCategoryList = newIssueDOMapper.smallCategorys(detailTableName, midCategoryName, shopId);
//            undoSmallCategoryList = newIssueDOMapper.smallCategorys(midCategoryName, taskId, shopId);
        }
//        if (undoSmallCategoryList != null && !undoSmallCategoryList.isEmpty()) {
//            smallCategoryList.removeAll(undoSmallCategoryList);
//            smallCategoryList.addAll(undoSmallCategoryList);
//        }
        smallCategoryList.sort(String::compareTo);
        return smallCategoryList;
    }

//    public int batchInsertUndoData(int taskId, String shopId) {
//        try {
//            List<IssueUndoData> issueUndoData = issueDOMapper2.getIssueUndoData(taskId, shopId);
//            if (issueUndoData == null || issueUndoData.isEmpty()) {
//                return 0;
//            }
//            Map<String, BigDecimal> percentMap = getMidCategoryPercentAvgSaleQty(taskId, shopId, Constant.STATUS_VALID);
//            List<Stock> stocks = issueDOMapper2.getIssueNeedStockList(taskId, shopId, Constant.STATUS_VALID);
//            Map<String, List<BigDecimal>> needStockMap = new HashMap<>();
//            for (Stock stock : stocks) {
//                needStockMap.putIfAbsent(stock.getMatCode() + "_" + stock.getSizeID(), Stream.of(stock.getPercentCategory()).collect(Collectors.toList()));
//            }
//            List<IssueUndoDO> issueUndos = convertIssueUndoData(issueUndoData, percentMap, needStockMap);
//            int rst = issueUndoDOMapper2.batchInsertTab(issueUndos);
//            logger.debug("batchInsertUndoData shopId:{},rst:{}", shopId, rst);
//            return rst;
//        } catch (Exception e) {
//            LoggerUtil.error(e, logger, "batchInsertUndoData, catch exception");
//            return 0;
//        }
//
//    }

    public synchronized Result add(int taskId, String shopId) {

        //检查是否有全局重算
        IssueTaskVo taskVo = getLastTask();
        if (taskVo != null) {
            if (taskId < taskVo.getTaskId()) {
                return ResultUtil.handleSysFailtureReturn("该门店重算任务ID过低，请刷新页面再试");
            } else if (taskVo.getReRun() == Constant.STATUS_RERUN) {
                return ResultUtil.handleSysFailtureReturn("当前正在执行全部门店重算，请稍候再试");
            }
        }

        //检查是否有效门店
        ShopInfoDo shopInfoDo = issueDOMapper2.getShop(shopId);
        if (shopInfoDo == null) {
            return ResultUtil.handleSysFailtureReturn("查询门店[" + shopId + "]不存在，添加失败");
        }

        RecalcTaskDo taskDo = new RecalcTaskDo();
        taskDo.setShopId(shopId);
        taskDo.setTaskId(taskId);
        taskDo.setName(Constant.TASK_FLAG_SR + "-" + shopId + "-" + taskId);
        taskDo.setOperator(AuthUtil.getSessionUserId());
        taskDo.setRunTime(taskVo.getRunTime());
        taskDo.setStatus(Constant.STATUS_VALID);

        if (newIssueDOMapper.shopReCalcCount(taskId, shopId) > 0) {
            return ResultUtil.handleSysFailtureReturn("该门店重算任务已排队中");
        }

        Integer rst = newIssueDOMapper.addReCalcTask(taskDo);
        if (rst == null || rst == 0) {
            return ResultUtil.handleSysFailtureReturn("该门店重算任务添加失败");
        }

        if (newIssueDOMapper.hasReCalcTask(taskId) == 0){
            asyncTask.newIssueSingleReCalc(taskDo.getId(), taskDo.getTaskId(), taskDo.getShopId());
            logger.info("NEW RECALC TASK ADD RUN NOW:{}", taskDo.getId());
        }

        return ResultUtil.handleSuccessReturn(taskDo);
    }

    @Async("taskExecutor")
    public void schedulerReCalcTask() {
        IssueTaskVo task = getLastTask();
        if (task == null) {
            logger.warn("SCHEDULER RECALC SINGLE SHOP getLastTask Null");
            return;
        }
        int taskId = task.getTaskId();

        //是否有正在执行的任务
        RecalcTaskDo doingTask = newIssueDOMapper.hasDoingReCalcTask(taskId);
        if (doingTask != null && doingTask.getStatus() == cn.nome.saas.allocation.constant.old.Constant.STATUS_RECALC) {
            logger.warn("SCHEDULER RECALC SINGLE SHOP hasDoingTask:{}", doingTask.getId());
            return;
        }

        RecalcTaskDo recalcTaskDo = newIssueDOMapper.getValidReCalcTask(taskId);
        if (recalcTaskDo == null) {
            logger.info("SCHEDULER RECALC SINGLE SHOP getOneValidTask Null");
            return;
        }
        Result rst = newIssueRecalcService.shopReCalc(recalcTaskDo.getId(), recalcTaskDo.getTaskId(), recalcTaskDo.getShopId());
        if (rst != null && "SUCCESS".equals(rst.getCode())){
            newIssueDOMapper.updateReCalcStatusFin(recalcTaskDo.getId());
            logger.info("SCHEDULER RECALC SINGLE SHOP DONE:{}", recalcTaskDo.getId());
        }
    }


    class GetOrderDetailMultiTask extends RecursiveTask<List<OrderDetailVo>> {
        private int start;
        private int end;
        List<String> shopIds;
        OrderDetailReq param;

        public GetOrderDetailMultiTask(int start, int end, List<String> shopIds, OrderDetailReq param) {
            this.start = start;
            this.end = end;
            this.shopIds = shopIds;
            this.param = param;
        }

        @Override
        protected List<OrderDetailVo> compute() {
            List<OrderDetailVo> orderDetailVos = new ArrayList<>();
            boolean compute = (this.end - this.start) < 1;
            if (compute) {
                try {
                    OrderDetailReq paramClone = param.clone();
                    paramClone.setShopId(shopIds.get(start));
                    OrderDetailWrap wrap = getOrderDetail(paramClone);
                    return wrap.getOrderDetailVo();
                } catch (Exception e) {
                    logger.error("loadSkuImg:{}", e.getMessage());
                    LoggerUtil.error(e, logger, "getOrderDetailMultiTask catch exception,shopId:{0}", shopIds.get(start));
                    return new ArrayList<>();
                }
            } else {
                //如果长度大于阈值，则分割为小任务
                int mid = (start + end) / 2;
                GetOrderDetailMultiTask left = new GetOrderDetailMultiTask(this.start, mid, this.shopIds, this.param);
                GetOrderDetailMultiTask right = new GetOrderDetailMultiTask(mid + 1, end, this.shopIds, this.param);
                invokeAll(left, right);
                orderDetailVos.addAll(left.join());
                orderDetailVos.addAll(right.join());
            }
            return orderDetailVos;
        }
    }

}
