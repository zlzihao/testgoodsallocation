package cn.nome.saas.allocation.mapper;

import cn.nome.saas.allocation.repository.entity.allocation.QdIssueDetailDO;
import cn.nome.saas.allocation.repository.entity.allocation.QdIssueInStockDO;

/**
 * QdIssueDataMapper
 *
 * @author Bruce01.fan
 * @date 2019/8/14
 */
public class QdIssueDataMapper {

    public static QdIssueDetailDO mapperTo(int taskId,QdIssueInStockDO shopDemand,String order) {
        QdIssueDetailDO qdIssueDetailDO = new QdIssueDetailDO();

        qdIssueDetailDO.setTaskId(taskId);
        qdIssueDetailDO.setShopId(shopDemand.getShopId());
        qdIssueDetailDO.setCategoryName(shopDemand.getCategoryName());
        qdIssueDetailDO.setMidCategoryName(shopDemand.getMidCategoryName());
        qdIssueDetailDO.setMatCode(shopDemand.getMatCode());
        qdIssueDetailDO.setSizeId(shopDemand.getSizeId());
        qdIssueDetailDO.setSizeName(shopDemand.getSizeName());
        qdIssueDetailDO.setCategoryName(shopDemand.getCategoryName());
        qdIssueDetailDO.setMidCategoryName(shopDemand.getMidCategoryName());
        qdIssueDetailDO.setStockQty(shopDemand.getStockQty());
        qdIssueDetailDO.setPathQty(shopDemand.getPathQty());
        qdIssueDetailDO.setApplyQty(shopDemand.getApplyQty());
        qdIssueDetailDO.setDemandQty(shopDemand.getDemandQty());
        qdIssueDetailDO.setQty(shopDemand.getIssueQty());

        qdIssueDetailDO.setOrder(order);
        qdIssueDetailDO.setAreaTotal(shopDemand.getRegionDemandQty());
        qdIssueDetailDO.setProvinceTotal(shopDemand.getProvinceDemandQty());
        qdIssueDetailDO.setShopTotal(shopDemand.getDemandQty());

        return qdIssueDetailDO;
    }
}
