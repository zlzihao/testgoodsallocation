package cn.nome.saas.cart.enums;

/**
 * 状态码，错误码范围：14001～14999
 */
public enum StatusCode {

    SUCCESS("SUCCESS","成功"),
    FAIL("ERR", "失败"),

    SKU_NO_FOUND("14001", "商品不存在"),
    SKU_NULL("14002", "商品为空"),
    STORE_NOT_ENOUGH("14003", "库存不足"),
    ADD_FAIL("14004", "添加商品失败"),
    CART_NO_FOUND("14005", "购物车不存在"),
    CART_SKU_NO_FOUND("14006", "购物车不存在该商品"),
    STORE_LIMIT("14007", "库存不足，您购物车已加购数量:{0}"),
    ADD_LIMIT_MAX_COUNT("14008", "购物车已加满，请先结算或清空其它商品"),

    GET_MAX_UID_ERR("14009", "获取最大用户ID错误,maxUid:{0}"),
    CACHE_UNAVALID("14010", "缓存不可用"),
    PARAMS_ERR("14011", "参数错误"),
    CART_ALIAS_NULL("14012", "cart alias null"),
    CALC_SKU_SIZE_NEQ("14013", "加购商品信息已变更，无法结算"),
    CALC_SKU_NO_EXIST("14014", "商品未匹配,无法结算")

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
