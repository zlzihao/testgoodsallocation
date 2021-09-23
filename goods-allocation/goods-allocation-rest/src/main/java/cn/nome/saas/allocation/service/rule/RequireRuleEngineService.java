package cn.nome.saas.allocation.service.rule;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.model.allocation.Task;
import cn.nome.saas.allocation.repository.dao.allocation.GoodsInfoDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.MinSecuritySkcDOMapper;
import cn.nome.saas.allocation.repository.dao.vertical.IssueExtraDataMapper;
import cn.nome.saas.allocation.repository.entity.allocation.ForbiddenSingleItemDO;
import cn.nome.saas.allocation.repository.entity.allocation.MinSecuritySkcDO;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.repository.dao.vertical.QdIssueExtraDataMapper;
import cn.nome.saas.allocation.repository.entity.allocation.OutOfStockGoodsDO;
import cn.nome.saas.allocation.repository.entity.allocation.SecuritySingleRuleDO;
import cn.nome.saas.allocation.repository.entity.vertical.IssueRejectSupplyDO;
import cn.nome.saas.allocation.repository.entity.allocation.QdGoodsInfoDO;
import cn.nome.saas.allocation.service.basic.GoodsService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 需求规则计算引擎
 *
 * @author Bruce01.fan
 * @date 2019/6/21
 */
@Service
public class RequireRuleEngineService {

    private static Logger logger = LoggerFactory.getLogger(RequireRuleEngineService.class);


    @Autowired
    ForbiddenRuleService forbiddenRuleService;

    @Autowired
    SecurityRuleService securityRuleService;

    @Autowired
    GoodsService goodsService;
    @Autowired
    IssueExtraDataMapper issueExtraDataMapper;

    @Value("${forbidden.switch}")
    private String forbiddenSwitch;


    /**
     * 计算需求库存门店禁配规则
     * 如果商品在禁配列表中，将理想库存设置为0，并将禁配标识设置为true
     * @param stockList
     */
    public void calcForbiddenRule(int brokenFlag,List<OutOfStockGoodsDO> stockList, List<Map<String,String>> forbiddenSingleItemDOList) {

        //Set<String> matCodeList = stockList.stream().map(OutOfStockGoodsDO::getMatCode).collect(Collectors.toSet());
        //List<Map<String,String>> forbiddenSingleItemDOList =  forbiddenRuleService.getForbiddenDetailList(matCodeList);

        // 易碎品禁配列表
        List<String> forbiddenMatCodeList = goodsService.getForbiddenAllocationGoods();

        for (OutOfStockGoodsDO stock : stockList) {

            if (OutOfStockGoodsDO.FORBIDDEN_TYPE == stock.getForbiddenFlag()) {continue;} // 已经禁配的，直接跳出

            for (Map<String,String> map : forbiddenSingleItemDOList) {

                String shopId = map.get("shopId");
                String matCode = map.get("matCode");

                // 基础禁配
                if ("open".equals(forbiddenSwitch)) {
                    if ((stock.getShopId().equals(shopId) && stock.getMatCode().equals(matCode))) {
                        stock.setIdealStockQty(0); // 理想库存设置为0
                        stock.setForbiddenFlag(OutOfStockGoodsDO.FORBIDDEN_TYPE);
                    }
                }
            }

            // 易碎品禁配
            if (brokenFlag == Constant.BROKEN_FLAG && (CollectionUtils.isNotEmpty(forbiddenMatCodeList) && forbiddenMatCodeList.contains(stock.getMatCode()))) {
                stock.setIdealStockQty(0); // 理想库存设置为0
                stock.setForbiddenFlag(OutOfStockGoodsDO.FORBIDDEN_TYPE);
            }

        }
    }

    public void calcBestQty(int taskType,List<OutOfStockGoodsDO> stockList,int days,boolean skipMinDisplay,List<QdGoodsInfoDO> qdGoodsInfoDOList) {

        // 要把禁配的剔除掉
        int totalStoreCnt = stockList.stream().filter(stock->OutOfStockGoodsDO.ENABLE_TYPE == stock.getForbiddenFlag())
                .collect(Collectors.summingInt(OutOfStockGoodsDO::getStoreQty));


        // 计算理想库存
        for (OutOfStockGoodsDO stock : stockList) {
            if (OutOfStockGoodsDO.FORBIDDEN_TYPE == stock.getForbiddenFlag()) {
                continue;
            }

            int bestMatchQty = 0;
            if (stock.getStoreQty() > 0) {

                // 服装的sku最小陈列量处理
                int skcMinDisplay = stock.getMinDisplayQty(); // skc纬度的最小陈列
                if (taskType == Constant.CLOTHING_TYPE && skcMinDisplay > 0) {
                    long skuCount = qdGoodsInfoDOList.stream()
                            .filter(qdGoodsInfoDO -> qdGoodsInfoDO.getMatCode().equals(stock.getMatCode()))
                            .count();

                    int skuMinDisplay = skcMinDisplay;
                    if (skuCount != 0) {
                        skuMinDisplay = Math.round(skcMinDisplay / skuCount);
                    }


                    stock.setMinDisplayQty(skuMinDisplay);
                    LoggerUtil.info(logger,"[CLOTHING_MIN_DISPLAY] msg = matcode:{0},skuCount:{1},oldMinDisplay:{2},newMinDisplay:{3}",stock.getMatCode(),skuCount,skcMinDisplay,skuMinDisplay);
                }

                // 门店有库存
                if (skipMinDisplay) {
                    // 北货南调场景不需要考虑最小陈列
                    bestMatchQty = stock.getSafeStockQty();
                } else {
                    /**
                     * 逻辑调整
                     */
                    int idealQty = (int)Math.round(stock.getMinDisplayQty() * 0.6);
                    if (stock.getDays70SaleQty() >= idealQty) {
                        if (stock.getSafeStockQty() >= idealQty) {
                            bestMatchQty = Math.max(stock.getSafeStockQty(),stock.getMinDisplayQty());
                        } else {
                            bestMatchQty = stock.getStoreQty(); // 门店库存
                        }
                    } else {
                        bestMatchQty = 0;
                    }

                    /*
                    if (stock.getDays70SaleQty() >= stock.getSafeStockQty()) {
                        bestMatchQty = Math.max(stock.getSafeStockQty(),stock.getMinDisplayQty());
                    } else {
                        bestMatchQty = 0;
                    }*/

                }

                if (bestMatchQty <= totalStoreCnt) {
                    stock.setIdealStockQty(bestMatchQty);
                    totalStoreCnt -= bestMatchQty;
                } else {
                    // 可售天数 = 门店库存 / 预计日销量
                    int enableSalesDays = stock.getAvgSaleQty() > 0 ? ((Double)(stock.getStoreQty() / stock.getAvgSaleQty())).intValue() : 9999;
                    // 理想库存=现有库存，保证不断卖
                    if (enableSalesDays <= days) {
                        bestMatchQty = stock.getStoreQty();
                    }
                }

            } else {
                // 门店无库存
                if (stock.getSafeStockQty() > stock.getMinDisplayQty()) {
                    bestMatchQty =  Math.min(stock.getSafeStockQty(),stock.getDays70SaleQty());

                    if (bestMatchQty <= totalStoreCnt) {
                        totalStoreCnt -= bestMatchQty;
                    } else {
                        bestMatchQty = 0;
                    }
                } else {
                    bestMatchQty = 0;
                }

            }
            stock.setIdealStockQty(bestMatchQty);
        }

    }

    /**
     * 撤店计算
     * @param stockList
     * @param shopIdList
     */
    public void calcBestQtyReject(List<OutOfStockGoodsDO> stockList, Set<String> shopIdList) {
        //从数仓获取可供应库存(在店+在配+在途)
        try {
            List<IssueRejectSupplyDO> list = issueExtraDataMapper.getIssueRejectSupplyList(shopIdList);
            Map<String, Integer> supplyMap = list.stream().collect(Collectors.toMap(
                    issueRejectSupplyDO -> issueRejectSupplyDO.getShopId() + "_" + issueRejectSupplyDO.getMatCode() + "_" + issueRejectSupplyDO.getSizeId(), IssueRejectSupplyDO::getSupplyStockQty));

            //设置理想库存为0, 店铺库存为可供应库存
            for (OutOfStockGoodsDO stock : stockList) {
                Integer supplyQty = supplyMap.get(stock.getShopId() + "_" + stock.getMatCode() + "_" + stock.getSizeId());
                stock.setStockQty(supplyQty == null ? 0 : supplyQty);
                stock.setIdealStockQty(0);
            }
        } catch (Exception e) {
            logger.error("calcBestQtyReject catch exception", e.getMessage());
            return;
        }

    }

    /**
     *
     * @param stockList
     */
    public void calcSercurityRule(List<OutOfStockGoodsDO> stockList) {

        /*Set<String> shopIdList = stockList.stream().map(OutOfStockGoodsDO::getShopId).collect(Collectors.toSet());
        Set<String> matCodeList = stockList.stream().map(OutOfStockGoodsDO::getMatCode).collect(Collectors.toSet());

        // 保底列表
        List<SecuritySingleRuleDO> securitySingleRuleDOList = securityRuleService.selectSecurityList(shopIdList,matCodeList);

        for (OutOfStockGoodsDO stock : stockList) {
            if (OutOfStockGoodsDO.FORBIDDEN_TYPE == stock.getForbiddenFlag()) {
                continue;
            }

            for (SecuritySingleRuleDO minSecuritySkcDO : securitySingleRuleDOList) {

                if (minSecuritySkcDO.getShopId().equals(stock.getShopId()) &&
                        minSecuritySkcDO.getTypeValue().equals(stock.getMatCode())) {

                    int securityQty = minSecuritySkcDO.getNum();
                    int bestMatchQty = Math.max(stock.getMatchStockQty(),securityQty);

                    stock.setMatchStockQty(bestMatchQty);
                }
            }
        }*/
    }
}
