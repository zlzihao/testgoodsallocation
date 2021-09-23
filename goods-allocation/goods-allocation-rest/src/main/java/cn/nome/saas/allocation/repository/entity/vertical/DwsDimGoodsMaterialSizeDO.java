package cn.nome.saas.allocation.repository.entity.vertical;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DimShop
 *
 * @author Bruce01.fan
 * @date 2019/5/24
 */
public class DwsDimGoodsMaterialSizeDO extends ToString {

    private String matCode;
    private String sizeID;
    private String barCode;
    private String etlDate;
    private String extrSource;
    private String sizeCode;
    private String sizeName;

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getSizeID() {
        return sizeID;
    }

    public void setSizeID(String sizeID) {
        this.sizeID = sizeID;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getEtlDate() {
        return etlDate;
    }

    public void setEtlDate(String etlDate) {
        this.etlDate = etlDate;
    }

    public String getExtrSource() {
        return extrSource;
    }

    public void setExtrSource(String extrSource) {
        this.extrSource = extrSource;
    }

    public String getSizeCode() {
        return sizeCode;
    }

    public void setSizeCode(String sizeCode) {
        this.sizeCode = sizeCode;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }
}

