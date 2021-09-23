package cn.nome.saas.allocation.repository.entity.allocation;

/**
 * QdGoodsInfoDO
 *
 * @author Bruce01.fan
 * @date 2019/8/15
 */
public class QdGoodsInfoDO {

    private String matCode;

    private String sizeId;

    private String sizeName;

    private String barCode;

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }
}
