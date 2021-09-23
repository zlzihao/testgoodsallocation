package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;
import cn.nome.saas.allocation.repository.entity.allocation.AllocationStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.ShopExpressDO;

import java.util.List;

/**
 * AllocationDetail
 *
 * @author Bruce01.fan
 * @date 2019/6/21
 */
public class ImportRuleFileVo extends ToString {

    private String type;

    private String obj;

    private String name;

    private Integer num;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
