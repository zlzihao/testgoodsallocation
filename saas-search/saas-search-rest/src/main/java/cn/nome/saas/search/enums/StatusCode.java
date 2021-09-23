package cn.nome.saas.search.enums;

/**
 * 状态码，错误码范围：24001～24999
 */
public enum StatusCode {

    FAIL("0", "失败"),
    SUCCESS("1", "成功"),
    PAGE_PARAM_ERR("24001", "分页参数有误"),
    DEL_REMOTE_DICT_ERR("24002", "删除词典失败"),
    SET_REMOTE_DICT_ERR("24003", "设置词典失败"),
    NO_FOUND_WORD("24004", "记录不存在"),
    UN_VAILD_TIME("24005", "该词未生效，不可操作"),
    WORD_STATUS_STOP("24006", "该词状态已停止，不可操作"),
    NO_NODE_AVAILABLE_ERR("24007", "当前搜索用户过多，请稍候再试"),

    ;

    StatusCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
