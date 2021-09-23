package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * 保底
 *
 * @author Bruce01.fan
 * @date 2019/6/1
 */
public class MinSecuritySkcDO extends ToString{

    private int type;

    private String typeValue;

    private int qty;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
