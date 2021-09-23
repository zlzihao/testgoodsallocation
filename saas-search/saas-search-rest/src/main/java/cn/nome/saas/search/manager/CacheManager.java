package cn.nome.saas.search.manager;


import cn.nome.platform.common.cache.RedisService;
import cn.nome.saas.search.constant.Constant;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author chentaikuang
 * 缓存服务工具
 */
@Component
public class CacheManager {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final String ES_SEARCH_REMOTE_DICT_HOT_WORD = "ES_SEARCH:REMOTE_DICT:HOT_WORD";
    private static final String ES_SEARCH_REMOTE_DICT_HOT_WORDS = "ES_SEARCH:REMOTE_DICT:HOT_WORDS";
    /**
     * 功能开关
     */
    private static final String SWITCH_ES_SEARCH_CATE_PROD = "ES_SEARCH:SWITCH:CATE_PROD";
    private static final String SWITCH_ES_SEARCH_REMOTE_DICT_REQ = "ES_SEARCH:SWITCH:REMOTE_DICT_REQ";
    private static final String SWITCH_ES_SEARCH_SAVE_LOG = "ES_SEARCH:SWITCH:SAVE_LOG";
    private static final String SWITCH_ES_SEARCH_MULTI_MATCH = "ES_SEARCH:SWITCH:MULTI_MATCH";
    private static final String SWITCH_ES_SEARCH_PROD_NAME = "ES_SEARCH:SWITCH:PROD_NAME";
    private static final String SWITCH_ES_SEARCH_SEX_FILTER = "ES_SEARCH:SWITCH:SEX_FILTER";

    @Autowired
    private RedisService redisService;

    @ApolloConfig
    private Config config;

    /**
     * 添加热词
     *
     * @param key
     * @param val
     * @return
     */
    @Deprecated
    public Long addRemoteDict(String key, String val) {
        Long rtn = redisService.hset(ES_SEARCH_REMOTE_DICT_HOT_WORD, key, val);
        LOGGER.info("[addRemoteDict] rtn:{}", rtn);
        return rtn;
    }

    /**
     * 获取热词
     *
     * @return
     */
    @Deprecated
    public Map<String, String> getRemoteDict() {
        Map<String, String> hotWords = redisService.hgetAll(ES_SEARCH_REMOTE_DICT_HOT_WORD);
        return hotWords;
    }

    /**
     * 删除热词
     *
     * @param kwKey
     * @return
     */
    @Deprecated
    public Long delRemoteDict(String kwKey) {
        Long rtn = redisService.hdel(ES_SEARCH_REMOTE_DICT_HOT_WORD, kwKey);
        LOGGER.info("[delRemoteDict] rtn:{}", rtn);
        return rtn;
    }

    /**
     * 加载远程词典
     *
     * @return
     */
    public String loadRemoteDictWords() {
        String words = redisService.get(ES_SEARCH_REMOTE_DICT_HOT_WORDS);
        return words;
    }

//    public Long delRemoteDictWords() {
//        Long del = redisService.del(ES_SEARCH_REMOTE_DICT_HOT_WORDS);
//        return del;
//    }

    /**
     * 保存远程词典
     *
     * @param words
     * @return
     */
    public String saveRemoteDictWords(String words) {
        String save = redisService.set(ES_SEARCH_REMOTE_DICT_HOT_WORDS, words);
        return save;
    }

    /**
     * 搜索分类-商品名开关，默认打开
     *
     * @return
     */
    public boolean searchCateProdNameSwitch() {

        String flag = config.getProperty(SWITCH_ES_SEARCH_CATE_PROD, Constant.SWITCH_OPEN);
//        String flag = redisService.get(SWITCH_ES_SEARCH_CATE_PROD);
        return Constant.SWITCH_OPEN.equals(flag);


    }

    /**
     * 远程词典请求次数限制开关，默认打开
     *
     * @return
     */
    public boolean openReqCountLimitSwitch() {
//        String flag = redisService.get(SWITCH_ES_SEARCH_REMOTE_DICT_REQ);
//        return StringUtils.isBlank(flag) ? false : true;
        String flag = config.getProperty(SWITCH_ES_SEARCH_REMOTE_DICT_REQ, Constant.SWITCH_CLOSE);
        return Constant.SWITCH_OPEN.equals(flag);
    }

    /**
     * 保存搜索日志开关，默认关闭
     *
     * @return
     */
    public boolean openSaveSearchLog() {
//        String flag = redisService.get(SWITCH_ES_SEARCH_SAVE_LOG);
//        return StringUtils.isBlank(flag) ? false : true;
        String flag = config.getProperty(SWITCH_ES_SEARCH_SAVE_LOG, Constant.SWITCH_CLOSE);
        return Constant.SWITCH_OPEN.equals(flag);
    }

    /**
     * 多字段查询开关
     *
     * @return
     */
    public boolean multiMatchQuerySwitch() {
        String flag = config.getProperty(SWITCH_ES_SEARCH_MULTI_MATCH, Constant.SWITCH_CLOSE);
        return Constant.SWITCH_OPEN.equals(flag);
    }

    /**
     * 查询商品名
     *
     * @return
     */
    public boolean searchProdNameSwitch() {
        String flag = config.getProperty(SWITCH_ES_SEARCH_PROD_NAME, Constant.SWITCH_OPEN);
        return Constant.SWITCH_OPEN.equals(flag);
    }

    /**
     * 性别查询过滤开关
     *
     * @return
     */
    public boolean openSexQryFilterSwitch() {
        String flag = config.getProperty(SWITCH_ES_SEARCH_SEX_FILTER, Constant.SWITCH_OPEN);
        return Constant.SWITCH_OPEN.equals(flag);
    }
}
