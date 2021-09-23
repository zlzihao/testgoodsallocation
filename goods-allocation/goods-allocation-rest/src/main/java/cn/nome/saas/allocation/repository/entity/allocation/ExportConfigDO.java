package cn.nome.saas.allocation.repository.entity.allocation;

/**
 * @author ：godsfer
 * @date ：Created in 2019/8/15 14:13
 * @description：导出配置
 * @modified By：
 * @version: 1.0.0$
 */
public class ExportConfigDO {
    private Integer id;

    /**
     * 模板编码
     */
    private String tplCode;

    /**
     * 导出列名
     */
    private String tplColumnName;

    /**
     * 导出列号
     */
    private Integer tplColumnNum;

    /**
     * 导出列类型
     */
    private String  tplColumnType;

    /**
     * 单元格宽度
     */
    private Integer width;

    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 校验类型，NOT_NULL：不等于空或空字符，NUMBER：数字类型，RELATION：关联查询
     */
    private String checkType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTplCode() {
        return tplCode;
    }

    public void setTplCode(String tplCode) {
        this.tplCode = tplCode;
    }

    public String getTplColumnName() {
        return tplColumnName;
    }

    public void setTplColumnName(String tplColumnName) {
        this.tplColumnName = tplColumnName;
    }

    public Integer getTplColumnNum() {
        return tplColumnNum;
    }

    public void setTplColumnNum(Integer tplColumnNum) {
        this.tplColumnNum = tplColumnNum;
    }

    public String getTplColumnType() {
        return tplColumnType;
    }

    public void setTplColumnType(String tplColumnType) {
        this.tplColumnType = tplColumnType;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
