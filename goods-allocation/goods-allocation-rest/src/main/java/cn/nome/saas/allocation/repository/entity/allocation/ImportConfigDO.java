package cn.nome.saas.allocation.repository.entity.allocation;

/**
 * @author ：godsfer
 * @date ：Created in 2019/8/9 16:11
 * @description：导入配置
 * @modified By：
 * @version: 1.0.0$
 */
public class ImportConfigDO {
    private Integer id;

    /**
     * 模板编码
     */
    private String tplCode;

    /**
     * 导入文件列名
     */
    private String tplColumnName;

    /**
     * 导入文件列号
     */
    private Integer tplColumnNum;

    /**
     * 导入文件列类型
     */
    private String  tplColumnType;

    /**
     * 数据库表名
     */
    private String tableName;

    /**
     * 数据库字段
     */
    private String columnName;

    /**
     * 校验类型，NOT_NULL：不等于空或空字符，NUMBER：数字类型，RELATION：关联查询
     */
    private String checkType;

    /**
     * 关联表名
     */
    private String relationTableName;

    /**
     * 关联字段名
     */
    private String relationColumnName;

    /**
     * 转换JSON串
     */
    private String transferJson;

    public String getTransferJson() {
        return transferJson;
    }

    public void setTransferJson(String transferJson) {
        this.transferJson = transferJson;
    }

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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getRelationTableName() {
        return relationTableName;
    }

    public void setRelationTableName(String relationTableName) {
        this.relationTableName = relationTableName;
    }

    public String getRelationColumnName() {
        return relationColumnName;
    }

    public void setRelationColumnName(String relationColumnName) {
        this.relationColumnName = relationColumnName;
    }

    public String getTplColumnType() {
        return tplColumnType;
    }

    public void setTplColumnType(String tplColumnType) {
        this.tplColumnType = tplColumnType;
    }
}
