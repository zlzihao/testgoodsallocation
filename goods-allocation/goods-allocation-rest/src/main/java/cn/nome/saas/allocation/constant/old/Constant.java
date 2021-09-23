/**
 * nome.com.cn
 * Copyright (c) 1997-2016 All Rights Reserved.
 */
package cn.nome.saas.allocation.constant.old;

/**
 *
 */
public final class Constant {
    // 当前
    public static final String CURRENT = "current";
    // 环比
    public static final String LINKRATIO = "linkRatio";
    public static final String DATE_PATTERN_FULL = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN_1 = "yyyy/MM/dd";
    public static final String DATE_PATTERN_2 = "yyyyMMdd";
    public static final String DATE_PATTERN_3 = "MMdd";
    public static final String DATE_PATTERN_FULL2 = "yyyy/MM/dd HH:mm:ss";
    public static final String DATE_PATTERN_4 = "yyyyMMddHHmmss";
    public static final String DICT_ISSUETIME = "ISSUETIME";
    public static final String FAIL_MSG = "操作失败";
    public static final String SUCCESS_MSG = "操作成功";
    public static final String SUCCESS_FLAG = "1";
    public static final String STOCK_NO_ENOUGH = "库存不足,请检查后修改";
    public static final String CHECK_RULE_FAIL = "修改数量大于{0},且可售天数大于45时不可修改，请检查";
    public static final String STOCK_ISSUE_ERR = "库存配发异常";
    /**
     * 修改配发sku
     */
    public static final int MODIFY_ISSUE_SKU = 1;
    public static final String NO_FOUND_ISSUE_ORDER = "未查询到配发数据";
    /**
     * BigDecimal 除法保留4位
     */
    public static final int DIVIDE_SCALE_4 = 4;
    /**
     * BigDecimal 除法保留2位
     */
    public static final int DIVIDE_SCALE_2 = 2;
    public static final String FILE_TYPE_ERROR = "文件格式不正确";
    public static final int MIN_PACKAGE_QTY_28 = 28;
    public static final int SALE_DAY_45 = 45;
    public static final String NULL_SHOPID = "NULL_SHOPID";
    public static final String REPEAT_SHOP_CODE = "门店编码{0}重复";
    public static final String NO_DATA_IMPORT = "无数据可导入";
    public static final String BATCH_INSERT_ERR = "批量导入失败";
    public static final String BAK_TAB_ERR = "数据备份异常";
    public static final String GET_SHOPID_NULL = "获取门店ID空";
    public static final String CHECK_ERR_TIPS = "验证异常，请联系管理员";
    public static final String SYS_ERR_TIPS = "系统异常，请联系管理员";
    public static final String PROHIBITED_TIPS = "策略禁配";
    public static final String TAB_SHOP_INFO = "shop_info";
    public static final String TAB_DISPLAY = "display";
    public static final String TAB_GOODS_INFO = "goods_info";
    public static final String TAB_SHOP_DISPLAY_DESIGN = "shop_display_design";
    public static final String TAB_ISSUE_GOODS_DATA = "issue_goods_data";
    public static final String DEFAULT_VAL_SLASH = "/";
    /**
     * 重算状态
     */
    public static final int STATUS_RECALC = 2;
    public static final int STATUS_INVALID = 1;
    /**
     * 有效状态
     */
    public static final int STATUS_VALID = 0;
    /**
     * 单店重算
     */
    public static final String TASK_FLAG_SR = "SR";
    /**
     * 全局重算
     */
    public static final String TASK_FLAG_GR = "GR";
    public static final int STATUS_RERUN = 1;
    public static String RECALC_PERCENT_START = "1";

    private Constant() {
    }

    public static void main(String[] args) {
        String ddd = TAB_SHOP_INFO + "5555";
        System.out.printf("d=" + ddd);
    }

}
