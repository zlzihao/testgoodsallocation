package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * AllocationDetail
 *
 * @author Bruce01.fan
 * @date 2019/6/21
 */
public class ImportNewGoodsIssueRangeDo extends ToString {

    private String matCode;

    private String sizeId;

    private String sizeName;

    private String type;

    private String obj;

    /**
     * 应用/排除
     */
    private String inExclude;

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }

    public String getInExclude() {
        return inExclude;
    }

    public void setInExclude(String inExclude) {
        this.inExclude = inExclude;
    }
}
