package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TaskStoreCommodity
 *
 * @author Bruce01.fan
 * @date 2019/7/9
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskStoreCommodity extends ToString{

    @JsonProperty("commodity_id")
    private String commodityId;

    @JsonProperty("commodity_name")
    private String commodityName;

    @JsonProperty("size_id")
    private String sizeId;

    @JsonProperty("size_name")
    private String sizeName;

    @JsonProperty("commodity_num")
    private int commodityNum;

    @JsonProperty("inshop_id")
    private String inshopId;

    @JsonProperty("inshop_code")
    private String inshopCode;

    @JsonProperty("inshop_name")
    private String inshopName;

    @JsonProperty("outshop_id")
    private String outshopId;

    @JsonProperty("outshop_code")
    private String outshopCode;

    @JsonProperty("outshop_name")
    private String outshopName;

    private double inAvgSaleAmt;
    private double inAvgSaleQty;
    private double inStockQty;
    private double inMoveQty;
    private double inPathStockQty;
    private double inNeedStockQty;
    private int inIsComplement;

    private double outAvgSaleAmt;
    private double outAvgSaleQty;
    private double outStockQty;

    private String seasonName;
    private String yearNo;

    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public int getCommodityNum() {
        return commodityNum;
    }

    public void setCommodityNum(int commodityNum) {
        this.commodityNum = commodityNum;
    }

    public String getInshopId() {
        return inshopId;
    }

    public void setInshopId(String inshopId) {
        this.inshopId = inshopId;
    }

    public String getInshopCode() {
        return inshopCode;
    }

    public void setInshopCode(String inshopCode) {
        this.inshopCode = inshopCode;
    }

    public String getInshopName() {
        return inshopName;
    }

    public void setInshopName(String inshopName) {
        this.inshopName = inshopName;
    }

    public String getOutshopId() {
        return outshopId;
    }

    public void setOutshopId(String outshopId) {
        this.outshopId = outshopId;
    }

    public String getOutshopCode() {
        return outshopCode;
    }

    public void setOutshopCode(String outshopCode) {
        this.outshopCode = outshopCode;
    }

    public String getOutshopName() {
        return outshopName;
    }

    public void setOutshopName(String outshopName) {
        this.outshopName = outshopName;
    }

    public double getInAvgSaleAmt() {
        return inAvgSaleAmt;
    }

    public void setInAvgSaleAmt(double inAvgSaleAmt) {
        this.inAvgSaleAmt = inAvgSaleAmt;
    }

    public double getInAvgSaleQty() {
        return inAvgSaleQty;
    }

    public void setInAvgSaleQty(double inAvgSaleQty) {
        this.inAvgSaleQty = inAvgSaleQty;
    }

    public double getInStockQty() {
        return inStockQty;
    }

    public void setInStockQty(double inStockQty) {
        this.inStockQty = inStockQty;
    }

    public double getInMoveQty() {
        return inMoveQty;
    }

    public void setInMoveQty(double inMoveQty) {
        this.inMoveQty = inMoveQty;
    }

    public double getInPathStockQty() {
        return inPathStockQty;
    }

    public void setInPathStockQty(double inPathStockQty) {
        this.inPathStockQty = inPathStockQty;
    }

    public double getInNeedStockQty() {
        return inNeedStockQty;
    }

    public void setInNeedStockQty(double inNeedStockQty) {
        this.inNeedStockQty = inNeedStockQty;
    }

    public int getInIsComplement() {
        return inIsComplement;
    }

    public void setInIsComplement(int inIsComplement) {
        this.inIsComplement = inIsComplement;
    }

    public double getOutAvgSaleAmt() {
        return outAvgSaleAmt;
    }

    public void setOutAvgSaleAmt(double outAvgSaleAmt) {
        this.outAvgSaleAmt = outAvgSaleAmt;
    }

    public double getOutAvgSaleQty() {
        return outAvgSaleQty;
    }

    public void setOutAvgSaleQty(double outAvgSaleQty) {
        this.outAvgSaleQty = outAvgSaleQty;
    }

    public double getOutStockQty() {
        return outStockQty;
    }

    public void setOutStockQty(double outStockQty) {
        this.outStockQty = outStockQty;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public String getYearNo() {
        return yearNo;
    }

    public void setYearNo(String yearNo) {
        this.yearNo = yearNo;
    }
}
