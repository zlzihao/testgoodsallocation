package cn.nome.saas.search.constant;

/**
 * 常量
 */
public class Constant {
    public static final String QRY_FIELD_NAME = "name";
    public static final String QRY_FIELD_FULL_NAME = "fullName";
    public static final String QRY_FIELD_CATE_PROD_NAME = "cateProdName";
    public static final String QRY_FIELD_CATE_NAME = "cateName";
    public static final String QRY_FIELD_PROD_NAME = "prodName";
    public static final String QRY_FIELD_DISPLAY = "displayed";
    public static final String QRY_FIELD_SPU_STOCK_COUNT = "spuStockCount";

    public static final String SORT_BY_ID = "id";
    public static final String SORT_FIELD_DESC = "1";
    public static final String SORT_BY_CREATED_AT = "createdAt";
//    public static final String SEARCH_TYPE_FULL_NAME = "1";

    /*远程词典功能专用*/
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";
    public static final String HEADER_ETAG = "ETag";
    public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String CONTENTTYPE_TEXT_PLAIN_UTF8 = "text/plain;charset=utf-8";
    public static final String CHART_SET_UTF8 = "UTF-8";
    /*远程词典功能专用*/

    /**
     * 发布
     */
    public static final int WORD_STATUS_RELEASE_1 = 1;
    /**
     * 停止
     */
    public static final int WORD_STATUS_STOP_2 = 2;

    public static final int WORD_TYPE_GUIDE_1 = 1;
    public static final int WORD_TYPE_HOT_2 = 2;
    /**
     * 请求限制次数1w
     */
    public static final int REQ_COUNT_10000 = 10000;
    public static final String I_AM_SVR = "iamsvr";

//    public static final String IMG_DOMAIN_URL = "http://storage2.nome.cn/";
    /**
     * 商品索引名
     */
    public static final String PRODUCT_INDEX = "nm_products";
    public static final String PRODUCT_INDEX_ALIAS = "nm_products_alias";
    /**
     * 分词器类型ik
     */
    public static final String TOKENIZER_IK_MAX_WORD = "ik_max_word";
    public static final String TOKENIZER_IK_IK_SMART = "ik_smart";
    public static final String TOKENIZER_IK = "ik";
    /**
     * 请求来源：小程序,LP
     */
    public static final String REQ_SOURCE_LP = "LP";

    /**
     * 分词类型，单词
     */
    public static final String IK_TOKEN_CN_WORD = "CN_WORD";
    public static final String IK_TOKEN_SYNONYM = "SYNONYM";

    /**
     * 开关，1开、0关
     */
    public static final String SWITCH_OPEN = "1";
    public static final String SWITCH_CLOSE = "0";
    /**
     * 打印日志耗时
     */
    public static final long PRINT_LOG_TIME = 100;
    /**
     * 最少匹配精度
     */
    public static final String MINI_SHOULD_MATCH = "MINI_SHOULD_MATCH";
    public static final String MINI_SHOULD_MATCH_DEFAULT_VAL = "50%";
    public static final String SEX_MAN = "男";
    public static final String SEX_WOMEN = "女";
    public static final String SUCCESS_STR = "SUCCESS";
    public static final String SUCCESS_CODE = "code";
    public static final String DATA_STR = "data";
}
