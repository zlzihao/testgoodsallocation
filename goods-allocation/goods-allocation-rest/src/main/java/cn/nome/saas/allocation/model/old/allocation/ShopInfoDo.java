package cn.nome.saas.allocation.model.old.allocation;

public class ShopInfoDo {

    private String ShopID;
    private String ShopCode;
    //是否儿童专区，0有 1无
    private int HaveChild;
    private int ID;

    public String getShopID() {
        return ShopID;
    }

    public void setShopID(String shopID) {
        ShopID = shopID;
    }

    public String getShopCode() {
        return ShopCode;
    }

    public void setShopCode(String shopCode) {
        ShopCode = shopCode;
    }

    public int getHaveChild() {
        return HaveChild;
    }

    public void setHaveChild(int haveChild) {
        HaveChild = haveChild;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
