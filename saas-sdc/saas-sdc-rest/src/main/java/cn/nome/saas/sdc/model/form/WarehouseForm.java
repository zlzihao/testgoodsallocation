package cn.nome.saas.sdc.model.form;

/**
 * @author lizihao@nome.com
 */
public class WarehouseForm {
    private Long id;
    private String province;
    private String warehouse;

    public WarehouseForm() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
