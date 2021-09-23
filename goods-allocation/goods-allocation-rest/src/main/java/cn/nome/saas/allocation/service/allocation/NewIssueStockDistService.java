package cn.nome.saas.allocation.service.allocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 库存分配服务
 */

@Service
public class NewIssueStockDistService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 根据分仓、门店列表分配库存
     *
     * @param taskId
     * @param warehouse
     * @param shopIds
     */
    public void addSkuEnoughStock(int taskId, String warehouse, List<String> shopIds) {
        List skus = getEnoughStockSku(taskId);
        if (skus == null || skus.isEmpty()) {
            logger.warn("getEnoughStockSku Null,taskId:{},warehouse:{},shopIds:{}", taskId, warehouse, shopIds);
            return;
        }
        //todo 库存充足直接生成配发单(不考虑中包数)
        //todo 更新分仓剩余库存
        //todo 更新sku剩余需求量
    }

    /**
     * 根据分仓、门店列表分配库存
     *
     * @param taskId
     * @param warehouse
     * @param shopIds
     */
    public void addSkuNoEnoughStock(int taskId, String warehouse, List<String> shopIds) {
        List skus = getNoEnoughStockSku(taskId);
        if (skus == null || skus.isEmpty()) {
            logger.warn("getNoEnoughStockSku Null,taskId:{},warehouse:{},shopIds:{}", taskId, warehouse, shopIds);
            return;
        }
        //todo 库存不足要按照中包拆分需求量
        //todo 更新分仓剩余库存
        //todo 更新sku剩余需求量
    }

    private List getNoEnoughStockSku(int taskId) {
        return null;
    }

    private List getEnoughStockSku(int taskId) {

        return null;
    }

}
