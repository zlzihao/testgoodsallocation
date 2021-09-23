package cn.nome.saas.sdc.listener;

import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.event.BusinessAttributeValueSaveEvent;
import cn.nome.saas.sdc.manager.ShopWechatServiceManager;
import cn.nome.saas.sdc.manager.ShopsServiceManager;
import cn.nome.saas.sdc.repository.entity.BusinessAttributeValuesDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2020/3/4 09:50
 */
public class BusinessAttributeValueSaveEventListener implements ApplicationListener<BusinessAttributeValueSaveEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessAttributeValueSaveEventListener.class);

    private ShopsServiceManager shopsServiceManager;
    private ShopWechatServiceManager shopWechatServiceManager;

    @Autowired
    public BusinessAttributeValueSaveEventListener(ShopsServiceManager shopsServiceManager, ShopWechatServiceManager shopWechatServiceManager) {
        this.shopsServiceManager = shopsServiceManager;
        this.shopWechatServiceManager = shopWechatServiceManager;
    }

    @Override
    public void onApplicationEvent(BusinessAttributeValueSaveEvent businessAttributeValueSaveEvent) {
        LOGGER.info("BusinessAttributeValueSaveEventListener event = {}", businessAttributeValueSaveEvent);
        BusinessAttributeValuesDO businessAttributeValuesDO = businessAttributeValueSaveEvent.getBusinessAttributeValuesDO();
        if (!businessAttributeValuesDO.getBusinessType().equals(Constant.BUSINESS_TYPE_SHOP)) {
            return;
        }
        if (businessAttributeValuesDO.getAttributeId().equals(Constant.FIXED_ATTRIBUTE_ID_PROVINCE)) {
            provinceChange(businessAttributeValuesDO.getCorpId(), businessAttributeValuesDO.getBusinessId());
            return;
        }
        if (businessAttributeValuesDO.getAttributeId().equals(Constant.FIXED_ATTRIBUTE_ID_SHOP_MANAGER_JOB_NUMBER)) {
            String newAttributeValue = businessAttributeValuesDO.getAttributeValue();
            if (!newAttributeValue.isEmpty() && !newAttributeValue.equals(businessAttributeValueSaveEvent.getOldAttributeValue())) {
                jobNumberChange(businessAttributeValuesDO.getCorpId(), businessAttributeValuesDO.getBusinessId(), businessAttributeValuesDO.getAttributeValue());
            }
        }
    }

    /**
     * 省份变更时，自动触发渠道区域更新事件
     *
     * @param corpId
     * @param shopId
     */
    private void provinceChange(Integer corpId, Integer shopId) {
        LOGGER.info("BusinessAttributeValueSaveEventListener provinceChange corpId = {}, shopId = {}", corpId, shopId);
        shopsServiceManager.syncChannelArea(corpId, shopId);
    }

    private void jobNumberChange(Integer corpId, Integer shopId, String jobNumber) {
        LOGGER.info("BusinessAttributeValueSaveEventListener jobNumberChange corpId = {}, shopId = {}, jobNumber = {}", corpId, shopId, jobNumber);
        shopWechatServiceManager.jobNumberChange(corpId, shopId, jobNumber);
    }
}
