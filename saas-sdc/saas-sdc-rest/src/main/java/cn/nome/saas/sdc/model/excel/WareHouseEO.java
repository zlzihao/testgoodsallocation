package cn.nome.saas.sdc.model.excel;

import cn.nome.platform.common.utils.ToString;
import cn.nome.platform.common.utils.excel.annotation.Column;

/**
 * @author lizihao@nome.com
 */
public class WareHouseEO extends ToString {
    @Column(num = 0, value = "省份", width = 20)
    private String province;
    @Column(num = 1, value = "仓库", width = 20)
    private String warehouse;

    public WareHouseEO() {
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }
}
