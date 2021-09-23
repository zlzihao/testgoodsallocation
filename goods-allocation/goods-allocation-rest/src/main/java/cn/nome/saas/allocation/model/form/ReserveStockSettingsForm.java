package cn.nome.saas.allocation.model.form;

import javax.validation.constraints.NotNull;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/12/11 10:34
 */
public class ReserveStockSettingsForm {

    @NotNull(message = "ID不能为空")
    private Integer id;

    @NotNull(message = "库存预留模式设置不能为空")
    private Integer isEnable;

    @NotNull(message = "预留日期不能为空")
    private String reserveDate;

    @NotNull(message = "是否使用销售预测不能为空")
    private Integer useSalePredict;

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

    public Integer getUseSalePredict() {
        return useSalePredict;
    }

    public void setUseSalePredict(Integer useSalePredict) {
        this.useSalePredict = useSalePredict;
    }

}
