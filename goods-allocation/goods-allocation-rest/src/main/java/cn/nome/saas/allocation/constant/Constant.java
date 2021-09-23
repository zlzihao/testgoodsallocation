/**
 * nome.com.cn
 * Copyright (c) 1997-2016 All Rights Reserved.
 */
package cn.nome.saas.allocation.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final String DATE_PATTERN_4 = "yyyyMMddHHmmss";
    public static final String DATE_PATTERN_5 = "yyyyMMddHHmm";
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
    public static final String NO_FOUND_ISSUE_ORDER = "未查询到配发单";
    /**
     * BigDecimal 除法保留四位
     */
    public static final int DIVIDE_SCALE_4 = 4;
    public static int MIN_PACKAGE_QTY_28 = 28;
    public static int SALE_DAY_45 = 45;

    public static int CLOTHING_TYPE_TASK = 1; // 服装

    public static int MARKET_TYPE_TASK = 2; // 百货

    public static final int CLOTHING_TYPE = 1; // 服装

    public static final int MARKET_TYPE = 2; // 百货

    public static final int COLLECT_ALLOCATION_TYPE = 3; // 归并
    public static final int CLOSE_ALLOCATION_TYPE = 4; // 撤店

    public static final String NULL_SHOPID = "NULL_SHOPID";
    public static final String REPEAT_SHOP_CODE = "门店编码{0}重复";
    public static final String NO_DATA_IMPORT = "无数据可导入";
    public static final String BATCH_INSERT_ERR = "批量导入失败";
    public static final String BAK_TAB_ERR = "数据备份异常";
    public static final String GET_SHOPID_NULL = "获取门店ID空";
    public static final String FILE_TYPE_ERROR = "文件格式不正确";

    public static final String TAB_GOODS_INFO = "goods_info";
    public static final String TAB_GOODS_INFO_VIEW = "goods_info_v";
    public static int CATEGORY_BH = 2;//百货
    public static int CATEGORY_FZ = 1;//服装
    public static String SUCCESS = "SUCCESS";
    public static int INVALID_STS = 1;
    public static int VALID_STS = 0;

    public static int SUCC_MATCH = 1;
    public static int FAIL_MATCH = 0;

    public static int FAIL_REASON_NONE = 0;
    public static int FAIL_REASON_FEE = 1;
    public static int FAIL_REASON_RATE = 2;

    public static int BROKEN_FLAG = 0; // 易碎标示(0时需要把易碎的过滤掉)


    public static int PRIORITY_YES = 1;

    /**
     * 资源分割符
     */
    public final static String ATTR_VALUE_SPLIT = ",";

    private Constant() {
    }

    /**
     * 秋冬老品：区域尺码比例
     */
    public static final Map<String, List<String>> sizeScalseMap = new HashMap<>();

    static {
        List<String> bList = new ArrayList<>();
        bList.add("25");
        bList.add("26");
        bList.add("27");
        bList.add("28");
        bList.add("29");
        sizeScalseMap.put("B码段", bList);
        List<String> cList = new ArrayList<>();
        cList.add("28");
        cList.add("29");
        cList.add("30");
        cList.add("31");
        cList.add("32");
        cList.add("33");
        cList.add("34");
        sizeScalseMap.put("C码段", cList);
        List<String> aList = new ArrayList<>();
        aList.add("XXS");
        aList.add("XS");
        aList.add("S");
        aList.add("M");
        aList.add("L");
        aList.add("XL");
        aList.add("XXL");
        sizeScalseMap.put("A码段", aList);
    }

    /**
     * 秋冬老品：内外搭(1-内搭 2-外搭 3-下装)
     */
    public static final Map<String, Integer> matchTypeMap = new HashMap<>();

    static {
        matchTypeMap.put("内搭", 1);
        matchTypeMap.put("外搭", 2);
        matchTypeMap.put("下装", 3);
    }

    public static final String SUB_WAREHOUSE_SHOP_MAPPING_PREFIX = "sub_warehouse_shop_mapping";

    public static final String ISSUE_OUT_STOCK_TABLE_PREFIX = "new_issue_out_stock";
    public static final String ISSUE_IN_STOCK_TABLE_PREFIX = "new_issue_in_stock";
    public static final String ISSUE_NEED_STOCK_TABLE_PREFIX = "new_issue_need_stock";
    public static final String ISSUE_DETAIL_TABLE_PREFIX = "new_issue_detail";
    public static final String ISSUE_GOODS_DATA_TABLE_PREFIX = "new_issue_goods_data";
    public static final String ISSUE_MID_CATAGORY_QTY_TABLE_PREFIX = "new_issue_mid_category_qty";
    public static final String ISSUE_CATEGORY_DATA_TABLE_PREFIX = "new_issue_category_data";
    public static final String ISSUE_MIDCATAGORY_DATA_TABLE_PREFIX = "new_issue_midcategory_data";

    public static final String SB_ISSUE_OUT_STOCK_TABLE_PREFIX = "new_issue_sb_out_stock";
    public static final String SB_ISSUE_IN_STOCK_TABLE_PREFIX = "new_issue_sb_in_stock";
    public static final String SB_ISSUE_NEED_STOCK_TABLE_PREFIX = "new_issue_sb_need_stock";
    public static final String SB_ISSUE_DETAIL_TABLE_PREFIX = "new_issue_sb_detail";
    public static final String SB_ISSUE_GOODS_DATA_TABLE_PREFIX = "new_issue_sb_goods_data";
    public static final String SB_ISSUE_MID_CATAGORY_QTY_TABLE_PREFIX = "new_issue_sb_mid_category_qty";
    public static final String SB_ISSUE_CATEGORY_DATA_TABLE_PREFIX = "new_issue_sb_category_data";
    public static final String SB_ISSUE_MIDCATAGORY_DATA_TABLE_PREFIX = "new_issue_sb_midcategory_data";

    public static final String BAK_ISSUE_OUT_STOCK_TABLE_PREFIX = "new_issue_out_stock_bak";
    public static final String BAK_ISSUE_IN_STOCK_TABLE_PREFIX = "new_issue_in_stock_bak";
    public static final String BAK_ISSUE_NEED_STOCK_TABLE_PREFIX = "new_issue_need_stock_bak";
    public static final String BAK_ISSUE_DETAIL_TABLE_PREFIX = "new_issue_detail_bak";
    public static final String BAK_ISSUE_GOODS_DATA_TABLE_PREFIX = "new_issue_goods_data_bak";
    public static final String BAK_ISSUE_MID_CATAGORY_QTY_TABLE_PREFIX = "new_issue_mid_category_qty_bak";
    public static final String BAK_ISSUE_CATEGORY_DATA_TABLE_PREFIX = "new_issue_category_data_bak";
    public static final String BAK_ISSUE_MIDCATAGORY_DATA_TABLE_PREFIX = "new_issue_midcategory_data_bak";

    /**
     * 预留存明细表名
     */
    public static final String ISSUE_RESERVE_DETAIL_TABLE = "issue_reserve_detail";

    /**
     * 禁配类型
     */
    public static class ForbiddenRuleType {
        public static final Integer Global = 1;//全局
        public static final Integer Store = 2;//单店
    }

    //禁配状态
    public static class ForbiddenRuleStatus {
        public static final Integer Enable = 1;//禁止
        public static final Integer Disable = 2;//解禁
    }

    //仓位调整审核状态  1：已审核 ： 0 未审核
    public static final Integer WAREHOUSE_APPLY_STATUS_OPEN = 1;
    public static final Integer WAREHOUSE_APPLY_STATUS_CLOSE = 0;

    // pdc后台类目顶类
    public static final int TOP_CATEGORY_ID_OTHERS = 70000830;//顶级类目-其它
    public static final int TOP_CATEGORY_ID_CLOTHING = 702621;//顶级类目-成衣
    public static final int TOP_CATEGORY_ID_GM = 701681;//顶级类目-百货
    public static final int TOP_CATEGORY_ID_SEASON = 70000966;//顶级类目-季配
    public static final int TOP_CATEGORY_ID_MAKEUP = 70000967;//顶级类目-美容

    /**
     * 当前本地调试的标识
     * DEBUG_FLAG_USER：是否默认一个调试用户
     * DEBUG_FLAG_GLOBAL_CONFIG_UPLOAD：是否跳过真实的配置上传操作
     * DEBUG_FLAG_LOW_MEMORY：是否用低内存模式（将一些并行的高内存运算分散成串行）
     * DEBUG_FLAG_STOP_CRONTAB_TASK：是否停用crontab上的两个禁配刷新定时任务
     * <p>
     * 测试环境按需选择
     */

//    // 正式环境
//    public static final boolean DEBUG_FLAG_USER = false;
//    public static final boolean DEBUG_FLAG_GLOBAL_CONFIG_UPLOAD = false;
//    public static final boolean DEBUG_FLAG_LOW_MEMORY = false;
//    public static final boolean DEBUG_FLAG_STOP_CRONTAB_TASK = true;

    // 测试环境
    public static final boolean DEBUG_FLAG_USER = true;
    public static final boolean DEBUG_FLAG_GLOBAL_CONFIG_UPLOAD = false;
    public static final boolean DEBUG_FLAG_LOW_MEMORY = true;
    public static final boolean DEBUG_FLAG_STOP_CRONTAB_TASK = true;


    public static final Integer DEFAULT_OPERATE_TYPE = 1; //运营调仓
    public static final Integer DEFAULT_STORE_TYPE = 0;  //小程序调仓
    public static final Integer DEFAULT_IS_DELETED = 0;  //未删除
    public static final Integer DEFAULT_COMMIT_DELETED = 1; // 删除
    public static final Integer DEFAULT_NOT_SAVE_STATUS = 0; // 未保存
    public static final Integer DEFAULT_COMMIT_SAVE_STATUS = 1; //保存
    public static final Integer DEFAULT_NOT_READ = 0; // 未读
    public static final Integer DEFAULT_IS_READ = 1;  //已读

    public static final Integer DEFAULT_IS_NEW_STATUS = 0;
    public static final Integer DEFAULT_EN_NEW_STATUS = 1;
}
