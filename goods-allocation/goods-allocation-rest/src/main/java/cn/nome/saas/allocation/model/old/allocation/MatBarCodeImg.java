package cn.nome.saas.allocation.model.old.allocation;

import cn.nome.platform.common.utils.ToString;

public class MatBarCodeImg extends ToString {

    private String MatCode;
    private String SizeID;
    private String BarCode;
    private String img;

    public String getMatCode() {
        return MatCode;
    }

    public void setMatCode(String matCode) {
        MatCode = matCode;
    }

    public String getSizeID() {
        return SizeID;
    }

    public void setSizeID(String sizeID) {
        SizeID = sizeID;
    }

    public String getBarCode() {
        return BarCode;
    }

    public void setBarCode(String barCode) {
        BarCode = barCode;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}