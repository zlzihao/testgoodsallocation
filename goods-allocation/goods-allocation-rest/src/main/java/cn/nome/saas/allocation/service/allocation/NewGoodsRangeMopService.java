package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.api.result.RpcResult;
import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.saas.allocation.cache.AreaLatitudeCache;
import cn.nome.saas.allocation.cache.GoodsLatitudeCache;
import cn.nome.saas.allocation.feign.api.MopClient;
import cn.nome.saas.allocation.feign.model.DisplayPlan;
import cn.nome.saas.allocation.feign.model.DisplayPlanShop;
import cn.nome.saas.allocation.model.old.allocation.NewGoodsIssueRangeReq;
import cn.nome.saas.allocation.repository.dao.allocation.NewGoodsIssueRangeDetailMapper;
import cn.nome.saas.allocation.repository.dao.allocation.NewGoodsIssueRangeMapper;
import cn.nome.saas.allocation.repository.dao.portal.UserMapper;
import cn.nome.saas.allocation.repository.dao.vertical.DwsGoodsMaterialSizeDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.NewGoodsIssueRangeDO;
import cn.nome.saas.allocation.repository.entity.allocation.NewGoodsIssueRangeDetailDO;
import cn.nome.saas.allocation.repository.entity.vertical.DwsDimGoodsMaterialSizeDO;
import cn.nome.saas.allocation.service.rule.GlobalConfigRuleService;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ForbiddenRuleService
 *
 * @author Bruce01.fan
 * @date 2019/6/18
 */
@Service
public class NewGoodsRangeMopService {

    private static Logger logger = LoggerFactory.getLogger(NewGoodsRangeMopService.class);

    @Autowired
    NewGoodsIssueRangeMapper newGoodsRangeDOMapper;
    @Autowired
    NewGoodsIssueRangeDetailMapper newGoodsIssueRangeDetailMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    AreaLatitudeCache areaLatitudeCache;

    @Autowired
    DwsGoodsMaterialSizeDOMapper dwsGoodsMaterialSizeDOMapper;

    @Autowired
    GoodsLatitudeCache goodsLatitudeCache;

    @Autowired
    GlobalConfigRuleService globalConfigRuleService;

    @Autowired
    @Lazy
    MopClient mopClient;

    //同步MOP配货计划
    @Transactional(value = "allocationTransactionManager", rollbackFor = Exception.class)
    public void syncMopRangePlan() {
        RpcResult<List<DisplayPlan>> rpcResult = mopClient.getRangePlan();
        LoggerUtil.info(logger, "[syncMopRangePlan] rpcResult:{0}" , rpcResult);
        if (rpcResult == null || rpcResult.getData() == null) {
            throw new BusinessException("12000", "取配货计划失败");
        }

        List<NewGoodsIssueRangeDO> newGoodsRangeDoList = new ArrayList<>();
        List<NewGoodsIssueRangeDetailDO> newGoodsRangeDetailDoList = new ArrayList<>();
        List<String> matCodes = new ArrayList<>();//所有skucode
        // List<String> regionList = new ArrayList<>();//所有地区
        String sizeName,sizeValue;
        Map<String, String> shopInfoMap = globalConfigRuleService.getShopCodeMap();
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = "2099-12-31 00:00:00";
        Date elderYear;
        try {
            elderYear = formatter.parse(timeStr);
        }catch (Exception e){
            throw new BusinessException("12000", "时间转换错误");
        }

        m:for (DisplayPlan each :  rpcResult.getData()) {

            Map<String, Integer> rangeIdWithSizeNameMap = new HashMap<>();//计划id 尺码名键值对
            //Map<matCode_sizeId, Set<shopId>> matCodeShopIdsMap 商品尺码名称 店铺对应
            Map<String, Set<String>> matCodeShopIdsMap = new HashMap<>();
            
            NewGoodsIssueRangeDO newGoodsRangeDO = new NewGoodsIssueRangeDO(); //每个尺码一条记录
            newGoodsRangeDO.setMatCode(each.getGoodsNo());
            newGoodsRangeDO.setMatName(each.getGoodsName());
            newGoodsRangeDO.setIssueFin(0);
//            Integer hasId = 0;
            if (!each.getSizeScale().isEmpty()) {
                String[] str = each.getSizeScale().split(",");
                for (String s : str) {
                    ////尺码
                     sizeName = s.split(":")[0];
                    ////数量
                     sizeValue = s.split(":")[1];
                    if("0".equals(sizeValue)){
                        //若为单F情况 不跳过
                        if (!"F".equals(sizeName)) {
                            continue;//跳过外层循环
                        } else if (str.length > 1) {
                            continue;//跳过外层循环
                        }
                    }
                    newGoodsRangeDO.setSizeName(sizeName);

                    //如果从mop过来修改过的计划已完成首配则不作修改
                    NewGoodsIssueRangeDO hasNewGoodsIssueRangeDO =  newGoodsRangeDOMapper.selectByMatCodeSizeName(each.getGoodsNo(),sizeName);
                    if(hasNewGoodsIssueRangeDO != null && (hasNewGoodsIssueRangeDO.getIssueFin() == 1 || !"SYSTEM".equals(hasNewGoodsIssueRangeDO.getUpdatedBy()))){
                        continue m;//跳过外层循环
                    }
                    //计划已存在不重复插入
                    if(hasNewGoodsIssueRangeDO != null){
                        rangeIdWithSizeNameMap.put(sizeName, hasNewGoodsIssueRangeDO.getId());
                        continue ;//跳过当前循环
                    }

                    newGoodsRangeDoList.add(newGoodsRangeDO);
                    newGoodsRangeDO.setCreatedAt(now);
                    newGoodsRangeDO.setUpdatedAt(now);
                    newGoodsRangeDO.setInvalidAt(elderYear);
                    newGoodsRangeDO.setUpdatedBy("SYSTEM");
                    newGoodsRangeDOMapper.insertSelective(newGoodsRangeDO);//插入配货表,每个尺码一条配货记录

                    rangeIdWithSizeNameMap.put(sizeName,newGoodsRangeDO.getId());

                }
            }
//            if(hasId > 0){
//                newGoodsIssueRangeDetailMapper.delByRangeIdWithFin(hasId,0);//把未完成首配的全部删掉，计划+白名单
//            }
            //把未完成首配的全部删掉，计划+白名单
            newGoodsIssueRangeDetailMapper.delByMatCodeWithFin(each.getGoodsNo(),0);

            if (!each.getDisplayPlanStoreList().isEmpty()) {//铺货门店处理
                for (DisplayPlanShop dps :  each.getDisplayPlanStoreList()) {
                    //新增配货明细
                    if (!dps.getSizeScaleValue().isEmpty() && !"".equals(dps.getSizeScaleValue())) { //多尺码
                        String[] ssvDps = dps.getSizeScaleValue().split(",");
                        for (String ssv : ssvDps) {
                            String[] scaleValueStr = ssv.split(":");

                            //已存在的,首配为1的跳过
                            Integer rangeId;
                            if ((rangeId = rangeIdWithSizeNameMap.get(scaleValueStr[0])) == null) {
                                continue;
                            }
                            List<NewGoodsIssueRangeDetailDO> ngList =  newGoodsIssueRangeDetailMapper.selectByShopCode(rangeId, dps.getShopCode());
                            if(!ngList.isEmpty()){
                                continue;
                            }

                            NewGoodsIssueRangeDetailDO newGoodsIssueRangeDetailRuleDO = new NewGoodsIssueRangeDetailDO(); //插入配货门店 白名单
                            newGoodsIssueRangeDetailRuleDO.setRangeId(rangeId);
                            newGoodsIssueRangeDetailRuleDO.setShopCode(dps.getShopCode());
                            String shopId = shopInfoMap.get(dps.getShopCode());
                            newGoodsIssueRangeDetailRuleDO.setShopId(shopId);
                            Set<String> shopIdsSet = matCodeShopIdsMap.computeIfAbsent(each.getGoodsNo() + "_" + scaleValueStr[0], k -> Sets.newHashSet());
                            shopIdsSet.add(shopId);
                            //regionList.add(dps.getDistrict());
                            newGoodsIssueRangeDetailRuleDO.setNum(Integer.parseInt(scaleValueStr[1]));//尺码首配量
                            newGoodsIssueRangeDetailRuleDO.setIssueFin(0);
                            newGoodsIssueRangeDetailRuleDO.setPlanFlag(0);
                            newGoodsIssueRangeDetailRuleDO.setSaleTime(dps.getSaleTime());
                            NewGoodsIssueRangeDetailDO newGoodsIssueRangeDetailDO = BaseConvertor.convert(newGoodsIssueRangeDetailRuleDO,NewGoodsIssueRangeDetailDO.class);//插入配货门店 计划
                            newGoodsIssueRangeDetailDO.setPlanFlag(1);
                            newGoodsRangeDetailDoList.add(newGoodsIssueRangeDetailDO);// 1-计划
                            newGoodsRangeDetailDoList.add(newGoodsIssueRangeDetailRuleDO);// 0-白名单
                        }
                    }
                }
//                rangeIdWithSizeNameMap = new HashMap<>();
            }
//            matCodes.add(each.getGoodsNo());

//            //从数仓获取所有matcode对应的尺码信息，对应查找sizeId
//            if(!matCodeShopIdsMap.isEmpty()){
//                //增加关联shopId，地区regionId 逗号拼接
//                matCodeShopIdsMap.forEach(matCode ->{
//                    NewGoodsIssueRangeReq req = new NewGoodsIssueRangeReq();
//                    req.setShopIdInclude(String.join(",", new ArrayList(new HashSet(shopIdList))));
//                    //req.setRegionInclude(String.join(",", new ArrayList(new HashSet(regionList))));
//                    req.setMatCode(matCode);
//                    newGoodsRangeDOMapper.updateByPrimaryKeySelective(req);//修改配货表 加入sizeId
//                }
//                );
//            }
            for (Map.Entry<String, Set<String>> entry : matCodeShopIdsMap.entrySet()) {
                String matCodeSizeId = entry.getKey();
                String [] values = matCodeSizeId.split("_");
                Set<String> shopIds = entry.getValue();
                NewGoodsIssueRangeReq req = new NewGoodsIssueRangeReq();
                req.setShopIdInclude(String.join(",", shopIds));
                //req.setRegionInclude(String.join(",", new ArrayList(new HashSet(regionList))));
                req.setMatCode(values[0]);
                req.setSizeName(values[1]);
                newGoodsRangeDOMapper.updateByMatCodeSizeName(req);
            }
        }

        //添加配货尺码详细
        if(!newGoodsRangeDetailDoList.isEmpty()){
            newGoodsIssueRangeDetailMapper.batchInsert(newGoodsRangeDetailDoList);
        }

    }

    @Transactional(value = "allocationTransactionManager")
    public void getSizeIdFromBD(){
        NewGoodsIssueRangeReq req = new NewGoodsIssueRangeReq();
        req.setNoSizeId("1");
        List<NewGoodsIssueRangeDO> newGoodsRangeDOList = newGoodsRangeDOMapper.pageList(req);
        if(newGoodsRangeDOList.size() == 0){
            return;
        }
        List<String> matCodes = new ArrayList<>();
        newGoodsRangeDOList.forEach(each ->{
            if(!matCodes.contains(each.getMatCode())){
                matCodes.add(each.getMatCode());
            }
        });
        List<DwsDimGoodsMaterialSizeDO>  dwsDimGoodsMaterialSizeDOList = dwsGoodsMaterialSizeDOMapper.selectGoodsListByMatCode(matCodes);
        dwsDimGoodsMaterialSizeDOList.forEach(each ->{
            newGoodsRangeDOList.forEach(ngr ->{
                if(each.getMatCode().equals(ngr.getMatCode()) && each.getSizeName().equals(ngr.getSizeName())){
                    NewGoodsIssueRangeReq reqSizeId = new NewGoodsIssueRangeReq();
                    reqSizeId.setMatCode(ngr.getMatCode());
                    reqSizeId.setSizeId(each.getSizeID());
                    reqSizeId.setSizeName(ngr.getSizeName());
                    newGoodsRangeDOMapper.updateByMatCodeSizeName(reqSizeId);//修改配货表 加入sizeId
                }
            });
        });
    }
}