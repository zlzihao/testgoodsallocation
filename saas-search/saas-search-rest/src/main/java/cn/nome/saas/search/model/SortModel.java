package cn.nome.saas.search.model;

/**
 * 排序参数
 */
public class SortModel {
    /**
     * 排序字段
     */
    private String fieldName;
    /**
     * 1:DESC,0:ASC
     */
    private String dire;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDire() {
        return dire;
    }

    public void setDire(String dire) {
        this.dire = dire;
    }
}
