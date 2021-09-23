package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.utils.Page;
import cn.nome.saas.allocation.cache.GoodsInfoCache;
import cn.nome.saas.allocation.cache.ShopInfoCache;
import cn.nome.saas.allocation.cache.ShopListCache;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.allocation.*;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.repository.dao.allocation.DwsDimGoodsDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.OutOfStockGoodsDOMapper;
import cn.nome.saas.allocation.repository.dao.vertical.AllocationExtraDataMapper;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import cn.nome.saas.allocation.service.basic.GoodsService;
import cn.nome.saas.allocation.service.basic.ShopService;
import org.apache.catalina.util.ParameterMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * OutOfStockGoodsService
 *
 * @author Bruce01.fan
 * @date 2019/6/21
 */
@Service
public class OutOfStockGoodsService {

    private static Logger logger = LoggerFactory.getLogger(OutOfStockGoodsService.class);

    //private static final int SKU_PAGE_SIZE = 500; //
    private static final int DEMAND_PAGE_SIZE = 50000; //

    @Autowired
    private OutOfStockGoodsDOMapper outOfStockGoodsDOMapper;

    @Autowired
    TaskService taskService;

    @Autowired
    ShopService shopService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    DwsDimGoodsDOMapper dwsDimGoodsDOMapper;

    @Autowired
    AllocationExtraDataMapper allocationExtraDataMapper;

    @Autowired
    ShopInfoCache shopInfoCache;

    @Autowired
    ShopListCache shopListCache;

    @Autowired
    GoodsInfoCache goodsInfoCache;

    @Autowired
    ExecutorService commonPool;

    List<String> getAllMatCodeList(Set<String> shopIdList,Task task) {
        Map<String,Object> param = new HashMap<>();
        param.put("tableName",getTableName());
        if (CollectionUtils.isNotEmpty(shopIdList)) {
            param.put("shopIdList",shopIdList);
        }

        LoggerUtil.info(logger,"[GET_ALL_CODE] msg = task:{0}",task);
        // 撤店任务，直接返回所有的matcode
        if (Task.ALLOCATION_TYPE_REJECT == task.getAllocationType()) {
            List<String> matCodeList = outOfStockGoodsDOMapper.getAllMatCodeList(param);
            LoggerUtil.info(logger,"[GET_ALL_CODE] msg = size:{0},param:{1}",matCodeList.size(),param);
            return matCodeList;
        }

        if (task.getTaskType() == Constant.CLOTHING_TYPE_TASK) {
            param.put("clothing", 1);
        } else if (task.getTaskType() == Constant.MARKET_TYPE_TASK) {
            param.put("market", 1);
        }

        // 品类名称
        CategoryList categoryList = this.splitCategoryNames(task.getCategoryNames());
        if (categoryList != null) {
            param.put("bigCategoryList",categoryList.getBigCategory());

            if (CollectionUtils.isNotEmpty(categoryList.getMiddleCategory())) {
                param.put("middleCategoryList",categoryList.getMiddleCategory());
            }

            if (CollectionUtils.isNotEmpty(categoryList.getSmallCategory())) {
                param.put("smallCategoryList",categoryList.getSmallCategory());
            }
        }


        List<String> matCodeList = outOfStockGoodsDOMapper.getAllMatCodeList(param);

        // 年份+季节过滤
        if (StringUtils.isNotBlank(task.getYear()) || StringUtils.isNotBlank(task.getSeason())) {

            Map<String,Object> subParam = new HashMap<>();
            if  (StringUtils.isNotBlank(task.getYear())) {

                subParam.put("yearNo",Stream.of(task.getYear().split(",")).collect(Collectors.toList()));
            }
            if  (StringUtils.isNotBlank(task.getSeason())) {
                subParam.put("seasonName",Stream.of(task.getSeason().split(",")).collect(Collectors.toList()));
            }
            subParam.put("matCodeList",matCodeList);

            matCodeList = dwsDimGoodsDOMapper.getMatCodeList(subParam);

        }

        return matCodeList;
    }

    public Map<String,List<OutOfStockGoodsDO>> getStockByPage(int type,int allocationType,List<String> matCodeList,Set<String> shopIdList) {

        Map<String,Object> stockParam = new HashMap<>();
        stockParam.put("tableName",getTableName());

        stockParam.put("matCodeList",matCodeList);

        if (CollectionUtils.isNotEmpty(shopIdList)) {
            stockParam.put("shopIdList",shopIdList);
        }

        if (Task.ALLOCATION_TYPE_REJECT != allocationType) {
            if (type == Constant.CLOTHING_TYPE_TASK) {
                stockParam.put("clothing", 1);
            } else if (type == Constant.MARKET_TYPE_TASK) {
                stockParam.put("market", 1);
            }
        }

        List<OutOfStockGoodsDO> ofStockGoodsDOList = outOfStockGoodsDOMapper.selectStockGoodsBySku(stockParam);

        Map<String,List<OutOfStockGoodsDO>> map = ofStockGoodsDOList.stream().collect(Collectors.groupingBy(outOfStockGoodsDO->{
            return outOfStockGoodsDO.getMatCode()+","+ outOfStockGoodsDO.getSizeId();
        }));

        return map;
    }

    public Map<String,List<OutOfStockGoodsDO>> getShopClothingGoodsMap(Task task,Set<String> shopIdList,List<String> matCodeList) {
        Map<String,Object> stockParam = new HashMap<>();
        stockParam.put("tableName",getTaskTableName(task.getTaskId(),task.getRunTime()));

        stockParam.put("matCodeList",matCodeList);

        if (CollectionUtils.isNotEmpty(shopIdList)) {
            stockParam.put("shopIdList",shopIdList);
        }
        stockParam.put("clothing", 1);

        List<OutOfStockGoodsDO> ofStockGoodsDOList = outOfStockGoodsDOMapper.selectStockGoodsBySku(stockParam);
        ofStockGoodsDOList = ofStockGoodsDOList.stream().filter(outOfStockGoodsDO -> outOfStockGoodsDO.getSupplyStockQty()>0).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(ofStockGoodsDOList)) {
            return null;
        }

        Map<String,List<OutOfStockGoodsDO>> map = ofStockGoodsDOList.stream().collect(Collectors.groupingBy(outOfStockGoodsDO->{
            return outOfStockGoodsDO.getShopId()+","+outOfStockGoodsDO.getMatCode();
        }));

        // 组内排序(按日均销升序排序)
        for (String key : map.keySet()) {
            List<OutOfStockGoodsDO> newList = map.get(key).stream().sorted(Comparator.comparing(OutOfStockGoodsDO::getAvgSaleQty)).collect(Collectors.toList());
            map.put(key,newList);
        }

        return  map;
    }

    public void processOutStockSaleQty() {
        String tableName = getTableName();

        if (outOfStockGoodsDOMapper.checkTableExists(tableName) == 0) {
            return;
        }

        List<String> shopList = shopListCache.getShopList().stream().map(DwsDimShopDO::getShopId).collect(Collectors.toList());

        int size = 10;
        int offsize = 0;
        while(true) {

            List<String> subShopList = shopList.stream().skip(offsize*size).limit(size).collect(Collectors.toList());

            if (subShopList.isEmpty()) {
                return;
            }

            List<OutOfStockGoodsDO> outOfStockGoodsDOList = allocationExtraDataMapper.getAvaliableSaleQty(subShopList);

            commonPool.submit(()->{
                Long start = System.currentTimeMillis();

                try {
                    outOfStockGoodsDOMapper.batchUpdateSaleQty(tableName, outOfStockGoodsDOList);
                }catch (Exception e) {
                    e.printStackTrace();
                }

                Long end = System.currentTimeMillis();

                LoggerUtil.info(logger,"[CALC_SALE_QTY] msg=time:{0}",(end - start) / 1000);
            });

            offsize++;
        }

    }


    boolean checkTableExists(Task task) {
        String tableName = this.getTaskTableName(task.getTaskId(),task.getRunTime());
        return outOfStockGoodsDOMapper.checkTableExists(tableName)>0 ? true : false;
    }

    void clearAll(Task task) {
        String tableName = this.getTaskTableName(task.getTaskId(),task.getRunTime());
        outOfStockGoodsDOMapper.clearAll(tableName);
    }

    public void createNewTable(Task task) {
        String tableName = this.getTaskTableName(task.getTaskId(),task.getRunTime());
        outOfStockGoodsDOMapper.createNewTable(tableName);
    }

    public Integer insertSelective(Task task,List<OutOfStockGoodsDO> record) {

        Map<String,Object> stockParam = new HashMap<>();
        stockParam.put("tableName",getTaskTableName(task.getTaskId(),task.getRunTime()));
        stockParam.put("list",record);

        return outOfStockGoodsDOMapper.insertSelective(stockParam);
    }

    public Integer updateSelective(Task task,List<OutOfStockGoodsDO> record) {
        Map<String,Object> stockParam = new HashMap<>();
        stockParam.put("tableName",getTaskTableName(task.getTaskId(),task.getRunTime()));
        stockParam.put("list",record);

        return outOfStockGoodsDOMapper.updateSelective(stockParam);
    }

    public void addIndex(Task task) {
        try {
            outOfStockGoodsDOMapper.addIndex(getTaskTableName(task.getTaskId(),task.getRunTime()));
        } catch (Exception e) {

        }
    }

    public List<OutOfStockGoodsDO> getDemandStockList(Task task,List<String> shopIdList,int offset) {
        Map<String,Object> stockParam = new HashMap<>();
        stockParam.put("tableName",getTaskTableName(task.getTaskId(),task.getRunTime()));

        if (Task.ALLOCATION_TYPE_REJECT != task.getAllocationType()) {
            if (task.getTaskType() == Constant.CLOTHING_TYPE_TASK) {
                stockParam.put("clothing", 1);
            } else if (task.getTaskType() == Constant.MARKET_TYPE_TASK) {
                stockParam.put("market", 1);
            }
        }
        if (CollectionUtils.isNotEmpty(shopIdList)) {
            stockParam.put("shopIdList",shopIdList);
        }

        stockParam.put("offset",offset * DEMAND_PAGE_SIZE);
        stockParam.put("pageSize",DEMAND_PAGE_SIZE);

        return outOfStockGoodsDOMapper.selectDemandList(stockParam);
    }

    public List<OutOfStockGoodsDO> getSupplyStockList(Task task,List<String> shopIdList,Set<String> matCodeList) {

        Map<String,Object> stockParam = new HashMap<>();
        stockParam.put("tableName",getTaskTableName(task.getTaskId(),task.getRunTime()));

        if (CollectionUtils.isNotEmpty(shopIdList)) {
            stockParam.put("shopIdList",shopIdList);
        }

        if (CollectionUtils.isNotEmpty(matCodeList)) {
            stockParam.put("matCodeList",matCodeList);
        }

        return outOfStockGoodsDOMapper.selectSupplyList(stockParam);
    }

    public List<DemandStock> demandStockStatsPage(InStockReq req, Page page) {

        TaskDO taskDO = taskService.getTask(req.getTaskId());

        if (taskDO == null) {
            return null;
        }

        List<String> shopIdList = new ArrayList<>();
        if (StringUtils.isNotBlank(req.getShopName())) {
            // 按门店名称
            List<String> subShopIdList = shopService.getShopIdByName(req.getShopName());
            if (CollectionUtils.isNotEmpty(subShopIdList)) {
                shopIdList = subShopIdList;
            }
        } else {
            // 按区域过滤门店
            if (StringUtils.isNotBlank(taskDO.getDemandShopIds())) {
                shopIdList = Stream.of(taskDO.getDemandShopIds().split(",")).collect(Collectors.toList());
            }

            // 查门店
            if (StringUtils.isNotBlank(req.getCityCode())) {
                List<String> subshopIdList = shopService.getShopIdByAreaCode(req.getCityCode());

                if (CollectionUtils.isNotEmpty(shopIdList)) {
                    // 取交集
                    shopIdList = shopIdList.stream().filter(shopid->subshopIdList.contains(shopid)).collect(Collectors.toList());
                } else {
                    shopIdList.addAll(subshopIdList);
                }

            }
        }

        // 查matcode
        List<String> matCodeList = null;
        Set<String> matCodeSet = null;
        if (req.getCategoryCode() != null || req.getMidCategoryCode() != null || req.getSmallCategoryCode() != null) {
            matCodeList = goodsService.getMatCodeLisyByCategoryCode(req.getCategoryCode(),req.getMidCategoryCode(),req.getSmallCategoryCode());
            matCodeSet = matCodeList.stream().collect(Collectors.toSet());
        }

        // 查需求队列
        Map<String,Object> param = new ParameterMap<>();
        param.put("tableName",getTaskTableName(req.getTaskId(),taskDO.getRunTime()));
        param.put("shopIdList",shopIdList);
        param.put("matCodeList",matCodeSet);

        // 获取总数量
        int count = outOfStockGoodsDOMapper.getDemandStockStatsCount(param);
        if (page != null) {
            page.setTotalRecord(count);
        }
        param.put("page",page);

        List<DemandStock> demandStockList = outOfStockGoodsDOMapper.getDemandStockStats(param);

        if (CollectionUtils.isEmpty(demandStockList)) {
            return null;
        }


        Map<String,Object> supplyParam = new ParameterMap<>();
        supplyParam.put("tableName",getTaskTableName(req.getTaskId(),taskDO.getRunTime()));

        supplyParam.put("shopId",demandStockList.get(0).getShopID());
        supplyParam.put("matCodeList",matCodeSet);
        if (demandStockList.size() > 1) {
            supplyParam.put("shopIdList", demandStockList.stream().skip(1).map(DemandStock::getShopID).collect(Collectors.toSet()));
        }
        if (StringUtils.isNotBlank(taskDO.getSupplyShopIds())) {
            supplyParam.put("supplyShopIdList",Stream.of(taskDO.getSupplyShopIds().split(",")).collect(Collectors.toSet()));
        }

        supplyParam.put("priceLatch",1000);

        supplyParam.put("matCodeList",matCodeList);
        supplyParam.put("priceLatch",1000);

        demandStockList.parallelStream().forEach(demandStock->{
            supplyParam.put("shopId",demandStock.getShopID());
            int outShopCount = outOfStockGoodsDOMapper.getSupplyShopCount(supplyParam);
            demandStock.setOutShopidQty(outShopCount);
        });

        return demandStockList;
    }

    public List<DemandStockDetail> demandStockDetailPage(InStockReq req, Page page) {

        TaskDO taskDO = taskService.getTask(req.getTaskId());

        if (taskDO == null) {
            return null;
        }

        // 查matcode
        List<String> matCodeList = null;
        Set<String> matCodeSet = null;
        if (req.getCategoryCode() != null || req.getMidCategoryCode() != null || req.getSmallCategoryCode() != null) {
            matCodeList = goodsService.getMatCodeLisyByCategoryCode(req.getCategoryCode(),req.getMidCategoryCode(),req.getSmallCategoryCode());
            matCodeSet = matCodeList.stream().collect(Collectors.toSet());
        }

        // 查需求队列
        Map<String,Object> param = new ParameterMap<>();
        param.put("tableName",getTaskTableName(req.getTaskId(),taskDO.getRunTime()));
        if (StringUtils.isNotBlank(req.getShopId())) {
            param.put("shopId", req.getShopId());
        }
        param.put("matCodeList",matCodeSet);

        int count = outOfStockGoodsDOMapper.getDemandStockDetailCount(param);
        if (page != null) {
            page.setTotalRecord(count);
        }
        param.put("page",page);

        return outOfStockGoodsDOMapper.getDemandStockDetailList(param);
    }

    public List<SupplyStock> getSupplyStockStats(InStockReq req, Page page) {

        TaskDO taskDO = taskService.getTask(req.getTaskId());

        if (taskDO == null) {
            return null;
        }

        // 查matcode
        List<String> matCodeList = null;
        Set<String> matCodeSet = null;
        if (req.getCategoryCode() != null || req.getMidCategoryCode() != null || req.getSmallCategoryCode() != null) {
            matCodeList = goodsService.getMatCodeLisyByCategoryCode(req.getCategoryCode(),req.getMidCategoryCode(),req.getSmallCategoryCode());
            matCodeSet = matCodeList.stream().collect(Collectors.toSet());
        }

        Map<String,Object> param = new ParameterMap<>();
        param.put("tableName",getTaskTableName(req.getTaskId(),taskDO.getRunTime()));

        if (StringUtils.isNotBlank(req.getInShopId())) {
            param.put("inshopId", req.getInShopId());
        }
        if (StringUtils.isNotBlank(taskDO.getSupplyShopIds())) {
            param.put("supplyShopIds", Stream.of(taskDO.getSupplyShopIds().split(",")).collect(Collectors.toSet()));
        }
        param.put("matCodeList",matCodeSet);
        param.put("priceLatch",1000);

        int count = outOfStockGoodsDOMapper.getSupplyStockStatsCount(param);
        if (page != null) {
            page.setTotalRecord(count);
        }
        param.put("page",page);

        return outOfStockGoodsDOMapper.getSupplyStockStats(param);

    }

    public void insertRejectOutOfStockGoods(Task task,String shopId) {

        List<OutOfStockGoodsDO> stockGoodsList = allocationExtraDataMapper.getRejectOutStockList(shopId);

        List<ShopInfoData> shopDOList = shopInfoCache.getShopList();
        List<DwsDimShopDO> dimShopDOS = shopService.getAllShop();
        List<DwsDimGoodsDO> dwsDimGoodsDOList = dwsDimGoodsDOMapper.getSimpleList();

        for (OutOfStockGoodsDO outOfStockGoodsDO : stockGoodsList) {
            DwsDimShopDO dwsDimShopDO = dimShopDOS.stream().filter(shop->shop.getShopId().equals(outOfStockGoodsDO.getShopId())).findFirst().orElse(null);
            ShopInfoData shopDO = shopDOList.stream().filter(shop->shop.getShopID().equals(outOfStockGoodsDO.getShopId())).findFirst().orElse(null);
            DwsDimGoodsDO dwsDimGoodsDO = dwsDimGoodsDOList.stream().filter(goods->goods.getMatCode().equals(outOfStockGoodsDO.getMatCode())).findFirst().orElse(null);


            if (shopDO != null) {
                outOfStockGoodsDO.setShopLevel(shopDO.getShopLevel());
            }

            if (dwsDimShopDO != null) {
                outOfStockGoodsDO.setShopName(dwsDimShopDO.getShopName());
                outOfStockGoodsDO.setCityName(dwsDimShopDO.getCityName());
            }

            if (dwsDimGoodsDO != null) {
                outOfStockGoodsDO.setMatName(dwsDimGoodsDO.getMatName());
                outOfStockGoodsDO.setSumSaleQty((int)Math.round(outOfStockGoodsDO.getAvgSaleQty() * 28));
            } else {
                outOfStockGoodsDO.setMatName("");
                outOfStockGoodsDO.setSumSaleQty(0);
            }

            outOfStockGoodsDO.setSizeCode("");
            outOfStockGoodsDO.setBatCode("");
            outOfStockGoodsDO.setMinDisplayQty(0);
            outOfStockGoodsDO.setSafeStockQty(0);
            outOfStockGoodsDO.setIdealStockQty(0);
            outOfStockGoodsDO.setDemandStockQty(0);
            outOfStockGoodsDO.setSupplyStockQty(outOfStockGoodsDO.getStoreQty());
        }

        this.insertSelective(task,stockGoodsList);

    }


    private String getTableName() {
        Date date = DateUtil.getCurrentDate();
        String currentDate = DateUtil.format(DateUtil.addDate(date,-1,DateUtil.DAY),DateUtil.DATE_ONLY);
        currentDate = currentDate.replaceAll("-","").substring(2);

        //currentDate = "190625";
        return "out_of_stock_goods"+"_"+ currentDate;
    }

    /*private String getTaskTableName(int taskId) {
        Date date = DateUtil.getCurrentDate();
        String currentDate = DateUtil.format(DateUtil.addDate(date,-1,DateUtil.DAY),DateUtil.DATE_ONLY);
        currentDate = currentDate.replaceAll("-","").substring(2);

        //currentDate = "190625";
        return "out_of_stock_goods"+"_"+taskId+"_"+ currentDate;
    }*/

    private String getTaskTableName(int taskId,Date runtime) {
        Date date = DateUtil.getCurrentDate();
        String currentDate = DateUtil.format(DateUtil.addDate(runtime,-1,DateUtil.DAY),DateUtil.DATE_ONLY);
        currentDate = currentDate.replaceAll("-","").substring(2);

        return "out_of_stock_goods"+"_"+taskId+"_"+ currentDate;
    }

    private CategoryList splitCategoryNames(String categoryNames) {

        if (StringUtils.isBlank(categoryNames)) {
            return null;
        }

        String[] categoryArray = categoryNames.split(",");

        CategoryList categoryList = new CategoryList();
        Set<String> bigCategory = new HashSet<>();

        Set<String> middleCategory = new HashSet<>();

        Set<String> smallCategory = new HashSet<>();

        for (String name : categoryArray) {

            String[] nameArray = name.split("_");

            if (nameArray.length == 1) {
                bigCategory.add(nameArray[0]);
            } else if (nameArray.length == 2) {
                bigCategory.add(nameArray[0]);
                middleCategory.add(nameArray[1]);
            } else if (nameArray.length == 3) {
                bigCategory.add(nameArray[0]);
                middleCategory.add(nameArray[1]);
                smallCategory.add(nameArray[2]);
            }

        }

        categoryList.setBigCategory(bigCategory);
        categoryList.setMiddleCategory(middleCategory);
        categoryList.setSmallCategory(smallCategory);

        return categoryList;
    }
}
