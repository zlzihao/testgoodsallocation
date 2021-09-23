package cn.nome.saas.allocation.repository.entity.vertical;

/**
 * AllocationGoodsSKC
 *
 * @author Bruce01.fan
 * @date 2019/12/5
 */
public class AllocationGoodsSKC {

    private String shopId;

    private String categoryCode;

    // 有效skc(2个尺码以上+5个商品数以上)
    private int skcCount = 0;

    // 无效skc款数
    private int invalidSkcStyle;

    // 无效的skc件数
    private int invaildSkcNum;

    // skc下限
    private int lowStandardSkcCount = 0;

    private int standardSkcCount = 0;

    private int highStandardSkcCount = 0;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public int getSkcCount() {
        return skcCount;
    }

    public void setSkcCount(int skcCount) {
        this.skcCount = skcCount;
    }

    public int getLowStandardSkcCount() {
        return lowStandardSkcCount;
    }

    public void setLowStandardSkcCount(int lowStandardSkcCount) {
        this.lowStandardSkcCount = lowStandardSkcCount;
    }

    public int getStandardSkcCount() {
        return standardSkcCount;
    }

    public void setStandardSkcCount(int standardSkcCount) {
        this.standardSkcCount = standardSkcCount;
    }

    public int getHighStandardSkcCount() {
        return highStandardSkcCount;
    }

    public void setHighStandardSkcCount(int highStandardSkcCount) {
        this.highStandardSkcCount = highStandardSkcCount;
    }

    public int getInvalidSkcStyle() {
        return invalidSkcStyle;
    }

    public void setInvalidSkcStyle(int invalidSkcStyle) {
        this.invalidSkcStyle = invalidSkcStyle;
    }

    public int getInvaildSkcNum() {
        return invaildSkcNum;
    }

    public void setInvaildSkcNum(int invaildSkcNum) {
        this.invaildSkcNum = invaildSkcNum;
    }
}
