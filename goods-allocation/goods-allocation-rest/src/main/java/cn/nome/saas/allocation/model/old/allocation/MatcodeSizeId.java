package cn.nome.saas.allocation.model.old.allocation;

/**
 * @author chentaikuang
 */
public class MatcodeSizeId {
    private String MatCode;
    private String SizeId;

    public String getMatCode() {
        return MatCode;
    }

    public void setMatCode(String matCode) {
        MatCode = matCode;
    }

    public String getSizeId() {
        return SizeId;
    }

    public void setSizeId(String sizeId) {
        SizeId = sizeId;
    }

    public MatcodeSizeId(String matCode, String sizeId) {
        MatCode = matCode;
        SizeId = sizeId;
    }
}
