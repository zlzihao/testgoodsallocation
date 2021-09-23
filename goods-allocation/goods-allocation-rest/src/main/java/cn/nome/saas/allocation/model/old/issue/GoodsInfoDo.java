package cn.nome.saas.allocation.model.old.issue;

public class GoodsInfoDo {
    private String MatId;
    private String MatCode;
    private int MinPackageQty;

    public String getMatId() {
        return MatId;
    }

    public void setMatId(String matId) {
        MatId = matId;
    }

    public String getMatCode() {
        return MatCode;
    }

    public void setMatCode(String matCode) {
        MatCode = matCode;
    }

    public int getMinPackageQty() {
        return MinPackageQty;
    }

    public void setMinPackageQty(int minPackageQty) {
        MinPackageQty = minPackageQty;
    }
}
