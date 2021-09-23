package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * AllocationClothingInvalidGoods
 *
 * @author Bruce01.fan
 * @date 2019/12/13
 */
public class AllocationClothingInvalidGoods extends ToString {

    private int id;

    private int taskId;

    private String shopId;

    private String shopName;

    private int minDisplayQty;

    private String matCode;

    private String matName;

    private String sizeId;

    private Double avgSaleQty; // 日均销

    private Integer applyStockQty; // 在配库存

    private Integer stockQty = 0; // 店仓库存

    private Integer pathStockQty  = 0; // 在途库存

    private Double saleDays; // 可售天数

    private int allocationType ; // 调拨前、调拨后

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getMatName() {
        return matName;
    }

    public void setMatName(String matName) {
        this.matName = matName;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public Double getAvgSaleQty() {
        return avgSaleQty;
    }

    public void setAvgSaleQty(Double avgSaleQty) {
        this.avgSaleQty = avgSaleQty;
    }

    public Integer getApplyStockQty() {
        return applyStockQty;
    }

    public void setApplyStockQty(Integer applyStockQty) {
        this.applyStockQty = applyStockQty;
    }

    public Integer getStockQty() {
        return stockQty;
    }

    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }

    public Integer getPathStockQty() {
        return pathStockQty;
    }

    public void setPathStockQty(Integer pathStockQty) {
        this.pathStockQty = pathStockQty;
    }

    public Double getSaleDays() {
        return saleDays;
    }

    public void setSaleDays(Double saleDays) {
        this.saleDays = saleDays;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMinDisplayQty() {
        return minDisplayQty;
    }

    public void setMinDisplayQty(int minDisplayQty) {
        this.minDisplayQty = minDisplayQty;
    }

    public int getAllocationType() {
        return allocationType;
    }

    public void setAllocationType(int allocationType) {
        this.allocationType = allocationType;
    }
}
