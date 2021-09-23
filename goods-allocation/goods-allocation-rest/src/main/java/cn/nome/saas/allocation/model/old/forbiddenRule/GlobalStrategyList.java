package cn.nome.saas.allocation.model.old.forbiddenRule;

import cn.nome.platform.common.utils.ToString;
import cn.nome.platform.common.utils.excel.annotation.Column;

/**
 * 策略作用清单类，对应策略作用清单excel
 *
 * @author Bruce01.fan
 * @date 2019/5/29
 */
public class GlobalStrategyList  extends ToString {

    @Column(value = "策略名",num = 0,isFilter = true,autoLine=true)
    String name;

    @Column(value = "商品代码",num = 1,isFilter = true,autoLine=true)
    String code;

    @Column(value = "策略数",num = 2,isFilter = true,autoLine=true)
    Integer number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
