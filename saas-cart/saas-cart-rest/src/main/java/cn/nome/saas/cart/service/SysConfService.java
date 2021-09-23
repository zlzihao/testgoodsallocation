package cn.nome.saas.cart.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.nome.saas.cart.manager.CacheManager;
import cn.nome.saas.cart.repository.dao.SysConfDOMapper;
import cn.nome.saas.cart.repository.entity.SysConfDO;

/**
 * @author chentaikuang
 */
@Component
public class SysConfService {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final static Integer VALID_STATUS = 1;

    @Autowired
    private SysConfDOMapper sysConfDOMapper;
    @Autowired
    private CacheManager cacheManager;

    /**
     * 初始化加载配置数据入缓存
     */
    public void initLoad() {

        LOGGER.info("LOAD SYSTEM CONF 2 CACHE.");
        List<SysConfDO> sysConfDOs = sysConfDOMapper.selectConfByStatus(VALID_STATUS);
        if (sysConfDOs == null || sysConfDOs.isEmpty()) {
            LOGGER.info("SYS_CONF IS NULL.");
        }
        LOGGER.info("SYS_CONF Load Size:{}", sysConfDOs.size());
        int n = 0;
        for (SysConfDO sc : sysConfDOs) {
            LOGGER.info("[" + (++n) + "]{}", sc);
            cacheManager.setConf(sc.getKeyCode(), sc);
        }
    }

    /**
     * 根据配置码查询配置信息
     *
     * @param keyCode
     * @return
     */
    public SysConfDO selectByCode(String keyCode) {
        SysConfDO sc = cacheManager.getConf(keyCode);
        LOGGER.debug("SYS_CONF,SELECT_BY_CONF_CODE 4 cache,{}:{}", keyCode, sc);
        if (sc == null) {
            sc = sysConfDOMapper.selectOneByKeyCode(keyCode);
            LOGGER.debug("SYS_CONF,SELECT_BY_CONF_CODE 4 table,{}:{}", keyCode, sc);

            if (sc != null && VALID_STATUS.equals(sc.getStatus())) {
                LOGGER.debug("SYS_CONF,reset to cache,keyCode:{}", keyCode);
                cacheManager.setConf(keyCode, sc);
            }
        }
        return sc;
    }

    /**
     * 不查缓存，从表取
     *
     * @param keyCode
     * @return
     */
    public SysConfDO selectTabByCode(String keyCode) {
        SysConfDO sysConfDO = sysConfDOMapper.selectOneByKeyCode(keyCode);
        LOGGER.debug("SYS_CONF,selectTabByCode,{}:{}", keyCode, sysConfDO);
        return sysConfDO;
    }

    /**
     * 返回keycode配置的val值，默认0
     *
     * @param keyCode
     * @return
     */
    public int getVal(String keyCode) {
        SysConfDO sysConfDO = selectByCode(keyCode);
        if (sysConfDO != null && StringUtils.isNotBlank(sysConfDO.getKeyVal())) {
            try {
                return Integer.valueOf(sysConfDO.getKeyVal());
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("SYS_CONF,getVal keyCode:{},sysConfDO:{}", keyCode, sysConfDO);
            }
        }
        LOGGER.warn("SYS_CONF,getVal null,return 0,keyCode:{}", keyCode);
        return 0;
    }

}
