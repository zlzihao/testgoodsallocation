package cn.nome.saas.allocation.repository.entity.allocation;

/**
 * @author ：godsfer
 * @date ：Created in 2019/8/8 13:53
 * @description：下载模板实例
 * @modified By：
 * @version: 1.0.0$
 */
public class DownTemplateDO {
    private Integer id;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 路径
     */
    private String fileDir;

    /**
     * 业务编码
     */
    private String businessCode;

    /**
     * 业务名称
     */
    private String businessName;

    /**
     * 服务实现编码
     */
    private String serviceCode;

    /**
     * 是否清理
     */
    private Integer isClear;

    /**
     * 可执行SQL语句
     */
    private String exeSql;

    /**
     * 导出模板编码
     */
    private String exportCode;

    public Integer getIsClear() {
        return isClear;
    }

    public void setIsClear(Integer isClear) {
        this.isClear = isClear;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getExeSql() {
        return exeSql;
    }

    public void setExeSql(String exeSql) {
        this.exeSql = exeSql;
    }

    public String getExportCode() {
        return exportCode;
    }

    public void setExportCode(String exportCode) {
        this.exportCode = exportCode;
    }
}
