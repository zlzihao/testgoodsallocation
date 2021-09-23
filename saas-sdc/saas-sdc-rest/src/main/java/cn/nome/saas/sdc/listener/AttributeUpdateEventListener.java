package cn.nome.saas.sdc.listener;

import cn.nome.saas.sdc.event.AttributeUpdateEvent;
import cn.nome.saas.sdc.manager.ShopsServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/10/31 14:30
 */
@Component
public class AttributeUpdateEventListener implements ApplicationListener<AttributeUpdateEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttributeUpdateEventListener.class);

    private ShopsServiceManager shopsServiceManager;

    @Autowired
    public AttributeUpdateEventListener(ShopsServiceManager shopsServiceManager) {
        this.shopsServiceManager = shopsServiceManager;
    }

    @Override
    public void onApplicationEvent(AttributeUpdateEvent attributeUpdateEvent) {
        LOGGER.info("AttributeUpdateEventListener shop_id = {}", attributeUpdateEvent.getShopId());
        shopsServiceManager.syncChannelArea(attributeUpdateEvent.getCorpId(), attributeUpdateEvent.getShopId());
    }
}
