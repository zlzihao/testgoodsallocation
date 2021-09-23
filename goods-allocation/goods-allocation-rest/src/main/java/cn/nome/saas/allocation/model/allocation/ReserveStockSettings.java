package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/12/11 10:21
 */
public class ReserveStockSettings extends ToString {

    private Integer id;
    private Integer isEnable;
    private String reserveDate;
    private Integer useSalePredict;
    private Date createdAt;
    private Date updatedAt;

    /**
     * 最后一次计算时间
     */
    private Date lastCalcTime;

    public Date getLastCalcTime() {
        return lastCalcTime;
    }

    public void setLastCalcTime(Date lastCalcTime) {
        this.lastCalcTime = lastCalcTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
    }

    public String getReserveDate() {
        return reserveDate;
    }

    public void setReserveDate(String reserveDate) {
        this.reserveDate = reserveDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUseSalePredict() {
        return useSalePredict;
    }

    public void setUseSalePredict(Integer useSalePredict) {
        this.useSalePredict = useSalePredict;
    }

}
