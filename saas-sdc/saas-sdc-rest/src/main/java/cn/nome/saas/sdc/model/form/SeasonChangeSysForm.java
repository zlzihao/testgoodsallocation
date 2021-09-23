package cn.nome.saas.sdc.model.form;

import javax.validation.constraints.NotNull;

/**
 * @author lizihao@nome.com
 */
public class SeasonChangeSysForm {
    private String shopCode;
    @NotNull(message = "转季日期不能为空")
    private String seasonsAlternateDay;

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getSeasonsAlternateDay() {
        return seasonsAlternateDay;
    }

    public void setSeasonsAlternateDay(String seasonsAlternateDay) {
        this.seasonsAlternateDay = seasonsAlternateDay;
    }
}
