package cn.nome.saas.allocation.model.issue;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * IssueOutStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/20
 */
public class NewIssueOutStockDo extends ToString {

    private Integer id;

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 商品编码
     */
    private String matCode;

//    /**
//     * 商品尺码ID
//     */
//    private String sizeId;
    /**
     * 商品尺码名称
     */
    private String sizeName;

    /**
     * 总库存数量
     */
    private Integer stockQty;

    /**
     * 剩余库存数量
     */
    private Integer remainStockQty;

    private Date createdAt;

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

//    public String getSizeId() {
//        return sizeId;
//    }
//
//    public void setSizeId(String sizeId) {
//        this.sizeId = sizeId;
//    }

    public Integer getStockQty() {
        return stockQty;
    }

    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getRemainStockQty() {
        return remainStockQty;
    }

    public void setRemainStockQty(Integer remainStockQty) {
        this.remainStockQty = remainStockQty;
    }

    public String getWarehouseMatCodeSizeNameKey() {
        return this.getWarehouseCode() +"_"+this.getMatCode()+"_"+this.getSizeName();
    }

//    public String getMatCodeSizeIdKey() {
//        return this.getMatCode()+"_"+this.getSizeName();
//    }

    public String getMatCodeSizeNameKey() {
        return this.getMatCode()+"_"+this.getSizeName();
    }
}
