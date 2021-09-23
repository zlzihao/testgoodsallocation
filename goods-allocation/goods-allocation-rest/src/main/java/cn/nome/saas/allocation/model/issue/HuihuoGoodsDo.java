package cn.nome.saas.allocation.model.issue;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * IssueOutStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/20
 */
public class HuihuoGoodsDo extends ToString {


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
     * 回货日期
     */
    private Date huihuoDay;

    /**
     * 回货数量
     */
    private BigDecimal huihuoQty;

    public Date getHuihuoDay() {
        return huihuoDay;
    }

    public void setHuihuoDay(Date huihuoDay) {
        this.huihuoDay = huihuoDay;
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


    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public BigDecimal getHuihuoQty() {
        return huihuoQty;
    }

    public void setHuihuoQty(BigDecimal huihuoQty) {
        this.huihuoQty = huihuoQty;
    }

    public String getWarehouseMatCodeSizeNameKey() {
        return this.getWarehouseCode() +"_"+this.getMatCode()+"_"+this.getSizeName();
    }

    public String getMatCodeSizeNameKey() {
        return this.getMatCode()+"_"+this.getSizeName();
    }
}
