package cn.nome.saas.sdc.model.form;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lizihao@nome.com
 */
public class SeasonChangeForm {
    @NotNull(message = "id不能为空")
    private Long id;
    private String year;
    private String seasonsAlternate;
    private Date seasonsAlternateDay;
    private BigDecimal seasonsAlternateCoefficient;

    public SeasonChangeForm() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSeasonsAlternate() {
        return seasonsAlternate;
    }

    public void setSeasonsAlternate(String seasonsAlternate) {
        this.seasonsAlternate = seasonsAlternate;
    }

    public Date getSeasonsAlternateDay() {
        return seasonsAlternateDay;
    }

    public void setSeasonsAlternateDay(Date seasonsAlternateDay) {
        this.seasonsAlternateDay = seasonsAlternateDay;
    }

    public BigDecimal getSeasonsAlternateCoefficient() {
        return seasonsAlternateCoefficient;
    }

    public void setSeasonsAlternateCoefficient(BigDecimal seasonsAlternateCoefficient) {
        this.seasonsAlternateCoefficient = seasonsAlternateCoefficient;
    }
}
