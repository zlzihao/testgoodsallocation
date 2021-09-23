package cn.nome.saas.sdc.constant;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/7 14:26
 */
public class Constant {

    public static final String HEADER_CORP_ID = "HEADER-NOME-CorpID";
    public static final String PARAM_CORP_ID = "corpId";
    public static final String HEADER_UID = "HEADER-NOME-UID";
    public static final String PARAM_UID = "uid";

    public static final String AREA_LOCATIONS_SEPARATOR = ",";

    public static final int DEFAULT_CORP_ID = 2;

    public static final int DEFAULT_LANGUAGE_ID = 1;

    /**
     * 是否已删除-否
     */
    public static final Integer IS_DELETE_FALSE = 0;
    /**
     * 是否已删除-是
     */
    public static final Integer IS_DELETE_TRUE = 1;

    /**
     * 是否启用-是
     */
    public static final Integer IS_ENABLE_TRUE = 1;

    /**
     * 是否启用-否
     */
    public static final Integer IS_ENABLE_FALSE = 0;

    /**
     * 数据来源-店铺
     */
    public static final Integer SOURCE_TYPE_ID_SHOP = 1;
    /**
     * 数据来源-加盟商
     */
    public static final Integer SOURCE_TYPE_ID_FRANCHISEES = 2;

    /**
     * 业务类型-店铺
     */
    public static final Integer BUSINESS_TYPE_SHOP = 1;
    /**
     * 业务类型-加盟商
     */
    public static final Integer BUSINESS_TYPE_FRANCHISEES = 2;

    /**
     * 字典-分类类型(所属页面)
     */
    public static final String ATTRIBUTE_TYPE_CLASSIFICATION_TYPE = "shop_classification_type";
    /**
     * 字典-媒体类型(输入模式)
     */
    public static final String ATTRIBUTE_MEDIA_TYPE = "attribute_media_type";

    /***
     * 固定属性-经营属性
     */
    public static final String FIXED_ATTRIBUTE_BUSINESS_ATTRIBUTE = "经营属性";

    /**
     * 固定属性-加盟商姓名
     */
    public static final String FIXED_ATTRIBUTE_FRANCHISEE_NAME = "客户姓名";

    /**
     * 固定属性-签约主体
     */
    public static final String FIXED_ATTRIBUTE_CONTRACT_SUBJECT = "签约主体";

    /**
     * 固定属性-客户级别
     */
    public static final String FIXED_ATTRIBUTE_CUSTOMER_LEVEL = "客户级别";

    /**
     * 固定属性-已开门店数
     */
    public static final String FIXED_ATTRIBUTE_VALID_SHOPS = "已开业";

    /**
     * 固定属性-省份
     */
    public static final String FIXED_ATTRIBUTE_PROVINCE = "省";
    public static final Integer FIXED_ATTRIBUTE_ID_PROVINCE = 1;
    public static final String FIXED_ATTRIBUTE_CITY = "市";
    public static final String FIXED_ATTRIBUTE_DISTINCT = "区";
    public static final String FIXED_ATTRIBUTE_ADDRESS = "详细地址";
    public static final String FIXED_ATTRIBUTE_SHOP_MANAGER_JOB_NUMBER = "店长工号";
    public static final Integer FIXED_ATTRIBUTE_ID_SHOP_MANAGER_JOB_NUMBER = 161;
    public static final String FIXED_ATTRIBUTE_ENABLE_WECHAT_GROUP_MARKING = "是否微信群营销";
    public static final String FIXED_ATTRIBUTE_LNG = "门店经度";
    public static final String FIXED_ATTRIBUTE_LAT = "门店纬度";


    public static final Integer AREA_TYPE_MARKING = 1;
    public static final Integer AREA_TYPE_CHANNEL = 2;

    public static final String SHOP_STATE_CLOSED = "已结业";
    public static final String SHOP_STATE_OPENED = "已开业";
    public static final String SHOP_STATE_TO_BE_OPENED = "待开业";
    public static final String SHOP_STATE_PAUSE = "暂停营业";

    public static final Integer EnableClothingAllocation = 1;
    public static final Integer DisableClothingAllocation = 0;
}
