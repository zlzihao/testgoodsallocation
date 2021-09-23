package cn.nome.saas.cart.utils;

import cn.nome.saas.cart.repository.entity.SysConfDO;
import cn.nome.saas.cart.service.SysConfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 缓存分代判断
 *
 * @author chentaikuang
 */
@Component
public class CacheGenerateUtil {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * 1分钟=60*1000毫秒
     */
    private final int SECOND_NUM = 60000;
    /**
     * 新生代节点数
     */
    private static final String CACHE_NG_TIME = "CACHE_NG_TIME";
    /**
     * 年轻代节点数
     */
    private static final String CACHE_YG_TIME = "CACHE_YG_TIME";

    /**
     * 新生代缓存区间数
     */
    @Value("${CACHE_NG_TIME_DEFAULT_VAL:4320}")
    private int CACHE_NG_TIME_DEFAULT_VAL;

    /**
     * 年轻代缓存区间数
     */
    @Value("${CACHE_YG_TIME_DEFAULT_VAL:7500}")
    private int CACHE_YG_TIME_DEFAULT_VAL;

    @Autowired
    private SysConfService sysConfService;

    public boolean isNewGeneration(long addTime) {
        long time = getCurTime() - addTime;
        if (time < 0 || compareNgTime(time)) {
            return true;
        }
        return false;
    }

    public boolean isYoungGeneration(long addTime) {
        long time = getCurTime() - addTime;
        if (compareYgTime(time)) {
            return true;
        }
        return false;
    }

    private int getNgTime() {
        SysConfDO sysConfDO = sysConfService.selectByCode(CACHE_NG_TIME);
        if (sysConfDO != null) {
            return Integer.valueOf(sysConfDO.getKeyVal());
        }
        LOGGER.warn("getNgTime sysConfDo Null,get default val:{}", CACHE_NG_TIME_DEFAULT_VAL);
        return CACHE_NG_TIME_DEFAULT_VAL;
    }

    private int getYgTime() {
        SysConfDO sysConfDO = sysConfService.selectByCode(CACHE_YG_TIME);
        if (sysConfDO != null) {
            return Integer.valueOf(sysConfDO.getKeyVal());
        }
        LOGGER.warn("getYgTime sysConfDo Null,get default val:{}", CACHE_YG_TIME_DEFAULT_VAL);
        return CACHE_YG_TIME_DEFAULT_VAL;
    }

    /**
     * 过去时间小于目标时间
     *
     * @param pastTime
     * @return
     */
    private boolean compareNgTime(long pastTime) {
        long ng = getNgTime() * SECOND_NUM;
        LOGGER.debug("pastTime:{},new generate area is[0:{}]", pastTime, ng);
        return pastTime <= ng;
    }

    private boolean compareYgTime(long pastTime) {
        long ng = getNgTime() * SECOND_NUM;
        long yg = getYgTime() * SECOND_NUM;
        LOGGER.debug("pastTime:{},young generate area is({}:{}]", pastTime, ng, yg);
        return ng < pastTime && pastTime <= yg;
    }

    public long getCurTime() {
        return System.currentTimeMillis();
    }

}
