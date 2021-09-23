package cn.nome.saas.allocation.repository.entity.allocation;

/**
 * AllocationDisplayDesignDO
 *
 * @author Bruce01.fan
 * @date 2019/12/5
 */
public class AllocationDisplayDesignDO {

    private String shopCode;

    private String shopId;

    private int maleLowStandard;

    private int femaleLowStandard;

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public int getMaleLowStandard() {
        return maleLowStandard;
    }

    public void setMaleLowStandard(int maleLowStandard) {
        this.maleLowStandard = maleLowStandard;
    }

    public int getFemaleLowStandard() {
        return femaleLowStandard;
    }

    public void setFemaleLowStandard(int femaleLowStandard) {
        this.femaleLowStandard = femaleLowStandard;
    }
}
