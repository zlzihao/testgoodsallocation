package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.model.allocation.TaskStoreCommodity;
import cn.nome.saas.allocation.repository.dao.allocation.AllocationStockDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import cn.nome.saas.allocation.service.basic.GoodsService;
import cn.nome.saas.allocation.service.basic.ShopService;
import org.apache.catalina.util.ParameterMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * AllocationStockService
 *
 * @author Bruce01.fan
 * @date 2019/6/22
 */
@Service
public class AllocationStockService {

    @Autowired
    AllocationStockDOMapper allocationStockDOMapper;

    @Autowired
    OutOfStockGoodsService outOfStockGoodsService;

    @Autowired
    TaskService taskService;

    @Autowired
    ShopService shopService;

    @Autowired
    GoodsService goodsService;

    public List<AllocationStockDO> getAllocationStockList(int taskId,List<String> supplyShopIdList,Set<String> matCodeList) {

        Map<String,Object> param = new HashMap<>();
        param.put("taskId",taskId);

        if (CollectionUtils.isNotEmpty(supplyShopIdList)) {
            param.put("supplyShopList",supplyShopIdList);
        }
        param.put("matCodeList",matCodeList);

        return allocationStockDOMapper.selectAllocationStockByParam(param);
    }

    public Integer batchInsert(List<AllocationStockDO> allocationStockDOList) {

        if (CollectionUtils.isEmpty(allocationStockDOList)) {
            return 0;
        }

        Map<String,Object> param = new HashMap<>();
        param.put("list",allocationStockDOList);

        return allocationStockDOMapper.batchInsert(param);
    }

    public Integer deleteByParam(Integer taskId) {
        Map<String,Object> param = new HashMap<>();
        param.put("taskId",taskId);

        return allocationStockDOMapper.deleteByParam(param);

    }

    public List<TaskStoreDO> getTaskStoreList(int taskId, String areaCode, int storeType, int priceThreshold,
                                              int quantityThreshold, String year, String season) {


        TaskDO taskDO  = taskService.getTask(taskId);

        String demandShopIds = taskDO.getDemandShopIds();
        String supplyShopIds = taskDO.getSupplyShopIds();

        List<String> demandShopIdList = new ArrayList<>();
        List<String> supplyShopIdList = new ArrayList<>();
        List<String> shopIdList = new ArrayList<>();

        if (StringUtils.isNotBlank(demandShopIds)) {
            demandShopIdList = Stream.of(demandShopIds.split(",")).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(supplyShopIds)) {
            supplyShopIdList = Stream.of(supplyShopIds.split(",")).collect(Collectors.toList());
        }

        // 查询shop_id
        if (StringUtils.isNotBlank(areaCode) && !"ALL".equals(areaCode)) {
            shopIdList = shopService.getShopIdByAreaCode(areaCode);
        }
        // 查询mat_code
        List<String> matCodeList = null;

        if (!"ALL".equals(year) || !"ALL".equals(season)) {
            matCodeList = goodsService.getMatCodeListBy(year,season);
        }

        // 按类型查调入、调出门店
        Map<String,Object> param = new ParameterMap<>();

        param.put("taskId",taskId);
        param.put("priceThreshold",priceThreshold);
        param.put("quantityThreshold",quantityThreshold);

        if (CollectionUtils.isNotEmpty(matCodeList)) {
            param.put("matCodeList", matCodeList);
        }
        if (storeType == 1) {

            if (CollectionUtils.isEmpty(shopIdList)) {
                shopIdList.addAll(demandShopIdList);
            }
            param.put("demandShopList",shopIdList.stream().collect(Collectors.toSet()));

            return allocationStockDOMapper.getTaskStoreList(param);
        } else if (storeType == 2) {
            if (CollectionUtils.isEmpty(shopIdList)) {
                shopIdList.addAll(supplyShopIdList);
            }
            param.put("supplyShopList",shopIdList.stream().collect(Collectors.toSet()));

            return allocationStockDOMapper.getTaskStoreSupplyList(param);
        }

        return null;
    }

    public List<TaskStoreDOV2> getTaskAllocationStoreList(int taskId, String areaCode, int priceThreshold,
                                                          int quantityThreshold, String year, String season) {

        Map<String,Object> param = new ParameterMap<>();
        param.put("taskId",taskId);
        param.put("priceThreshold",priceThreshold);
        param.put("quantityThreshold",quantityThreshold);

        List<String> shopIdList = null;

        // 查询mat_code
        List<String> matCodeList = null;
        if (!"ALL".equals(year) || !"ALL".equals(season)) {
            matCodeList = goodsService.getMatCodeListBy(year,season);
            param.put("matCodeList", matCodeList);
        }

        // 查询shop_id
        if (StringUtils.isNotBlank(areaCode)) {
            shopIdList = shopService.getShopIdByAreaCode(areaCode);
            param.put("demandShopList",shopIdList);
        }

        return allocationStockDOMapper.getTaskAllocationStoreList(param);
    }


    public List<TaskStoreCommodity> getTaskStoreCommodityList(int taskId, String shopIds, int storeType,
                                                                   String year, String season, Page page) {

        Map<String,Object> param = new ParameterMap<>();
        param.put("taskId",taskId);

        // 查询mat_code
        List<String> matCodeList = null;
        if (!"ALL".equals(year) || !"ALL".equals(season)) {
            matCodeList = goodsService.getMatCodeListBy(year,season);
            param.put("matCodeList", matCodeList);
        }

        List<String> shopIdList = null;
        // 查询shop_id
        if (StringUtils.isNotBlank(shopIds)) {
            shopIdList = Stream.of(shopIds.split(",")).collect(Collectors.toList());
        }

        //  调入
        if (storeType == 1) {
            if (CollectionUtils.isNotEmpty(shopIdList)) {
                param.put("demandShopList", shopIdList);
            }
        }
        // 调出
        else if(storeType == 2) {
            if (CollectionUtils.isNotEmpty(shopIdList)) {
                param.put("supplyShopList", shopIdList);
            }
        }

        int count = allocationStockDOMapper.getTaskStoreCommodityCount(param);
        if (page != null) {
            page.setTotalRecord(count);
        }
        param.put("page", page);

        List<TaskStoreCommodity> list = allocationStockDOMapper.getTaskStoreCommodityList(param);


        if (CollectionUtils.isNotEmpty(list)) {

            List<DwsDimShopDO> dwsDimShopDOList = shopService.getAllShop();

            list.stream().forEach(taskStoreCommodity -> {
                String inshopCode = dwsDimShopDOList.stream().filter(shop->shop.getShopId().equals(taskStoreCommodity.getInshopId())).map(DwsDimShopDO::getShopCode).findFirst().orElse(null);
                String outshopCode = dwsDimShopDOList.stream().filter(shop->shop.getShopId().equals(taskStoreCommodity.getOutshopId())).map(DwsDimShopDO::getShopCode).findFirst().orElse(null);

                taskStoreCommodity.setInshopCode(inshopCode);
                taskStoreCommodity.setOutshopCode(outshopCode);
            });
        }

        return list;
    }

    public List<TaskStoreCommodity> getTaskStorePairCommodityList(int taskId, String inshopId, String outshopId,
                                                           String year, String season) {

        Map<String,Object> param = new ParameterMap<>();
        param.put("taskId",taskId);
        param.put("inshopId",inshopId);
        param.put("outshopId",outshopId);
        param.put("year",year);
        param.put("season",season);

        List<TaskStoreCommodity> list = allocationStockDOMapper.getTaskStorePairCommodityList(param);

        if (CollectionUtils.isNotEmpty(list)) {

            List<DwsDimShopDO> dwsDimShopDOList = shopService.getAllShop();

            list.stream().forEach(taskStoreCommodity -> {
                String inshopCode = dwsDimShopDOList.stream().filter(shop->shop.getShopId().equals(taskStoreCommodity.getInshopId())).map(DwsDimShopDO::getShopCode).findFirst().orElse(null);
                String outshopCode = dwsDimShopDOList.stream().filter(shop->shop.getShopId().equals(taskStoreCommodity.getOutshopId())).map(DwsDimShopDO::getShopCode).findFirst().orElse(null);

                taskStoreCommodity.setInshopCode(inshopCode);
                taskStoreCommodity.setOutshopCode(outshopCode);
            });
        }

        return  list;
    }


}
