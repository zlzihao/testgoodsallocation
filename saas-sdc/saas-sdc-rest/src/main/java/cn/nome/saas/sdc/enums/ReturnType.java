package cn.nome.saas.sdc.enums;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/7 14:00
 */
public enum ReturnType {

    //返回类型取值范围(错误码) 50000 ~ 50999
    //http://wiki.nome.com/pages/viewpage.action?pageId=8487748

    SYSTEM_FAIL("50000", "店铺中心-系统异常"),
    METHOD_NOT_FOUND("50001", "店铺中心-HTTP METHOD不支持"),
    HEADER_AUTH_FAIL("50002", "店铺中心-HEADER参数检验失败"),
    VALIDATION_FAIL("50010", "店铺中心-参数校验失败"),
    IMPORT_EXCEL_FAIL("50011", "店铺中心-导入EXCEL文件读取异常"),
    EXPORT_EXCEL_FAIL("50012", "店铺中心-数据异常，导出失败"),
    GEN_SSO_URL_FAIL("50013", "店铺中心-生成观远单点登录链接异常");

    private String type;
    private String msg;

    ReturnType(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
