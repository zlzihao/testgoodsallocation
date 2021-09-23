package cn.nome.saas.cart.constant;

import java.util.Arrays;
import java.util.List;

/**
 * @author chentaikuang 常量值
 */
public class Constant {

	/**
	 * 异步任务操作类型
	 */
	public final static String ASYNC_TASK_TYPE_ADD = "add";
	public final static String ASYNC_TASK_TYPE_MODIFY = "modify";
	public final static String ASYNC_TASK_TYPE_DEL = "del";
	public final static String ASYNC_TASK_TYPE_ADD_OR_MODIFY = "addOrModify";

	public final static String ASYNC_TASK_TYPE_CART_SKU_ADD = "addCartSku";
	public final static String ASYNC_TASK_TYPE_GLOBAL_CART_ADD = "addGlobalCart";
	
	public final static String ASYNC_TASK_TYPE_ADD_SHOPPING_LIST = "addShoppingList";
	public final static String ASYNC_TASK_TYPE_DEL_SHOPPING_LIST = "delShoppingList";
	public final static String ASYNC_TASK_TYPE_SEND_SHOPPING_LIST = "sendShoppingList";
	
	/**
	 * Redis异常标识码-字符串类型:46464646
	 */
	public final static String REDIS_ERROR_STRING = "46464646";
	/**
	 * Redis异常标识码-整形类型:46464646
	 */
	public final static int REDIS_ERROR_INT = 46464646;
	/**
	 * Redis异常标识码-长整型类型:46464646
	 */
	public final static Long REDIS_ERROR_LONG = 46464646L;

	/**
	 * 操作成功：1
	 */
	public final static int FLAG_SUCCESS = 1;
	/**
	 * 操作失败：0（正常情况的失败）
	 */
	public final static int FLAG_FAIL = 0;
	/**
	 * 操作异常：-1
	 */
	public final static int FLAG_ERROR = -1;
	/**
	 * 操作异常：-1L
	 */
	public final static long FLAG_ERROR_LONG = -1L;

	/**
	 * 限制时间：10分钟,10*60*1000
	 */
	public final static long LIMIT_TIME_10_MINUTES = 600000;

	/**
	 * 系统标识：saas-cart
	 */
    public final static String PLATFORM = "saas-cart";
	public static final String WX_SHOPPING_LIST_DEL = "del";
	public static final String WX_SHOPPING_LIST_ADD = "add";
	public static final String ALIAS_DATE_FORMAT = "yyMMddHHmmss";
    /**
	 * 满足活动状态：2
	 */
	public static int ACTIVE_STATUS_2 = 2;
	/**
     * 从db删除冗余sku
     */
    public static final String DEL_REDU_SKU_4_DB = "db";
    public static final List DO_SIDE_LIST = Arrays.asList("db", "cc");

    public static final long FORK_JOIN_AWAIT_TIME = 10;
    public static final int PER_LIST_SIZE = 1000;

    /**
     * 默认的cropId_appId_
     */
    public static final String DEFAULT_ID_2_12 = "2_12_";
    
    /**
     * kafka开关
     */
    public static final String SHOPPING_LIST_KAFKA_SWITCH = "SHOPPING_LIST_KAFKA_SWITCH";
    public static final String SHOPPING_LIST_SWITCH = "SHOPPING_LIST_SWITCH";
    public static final String ADD_LIMIT_MAX_COUNT_SWITCH = "ADD_LIMIT_MAX_COUNT_SWITCH";
    public static final String OPEN_FUNC_NEW_VER_SWITCH = "OPEN_FUNC_NEW_VER_SWITCH";
}
