package cn.nome.saas.sdc.event;

import cn.nome.saas.sdc.repository.entity.BusinessAttributeValuesDO;
import org.springframework.context.ApplicationEvent;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2020/3/4 09:25
 */
public class BusinessAttributeValueSaveEvent extends ApplicationEvent {

    private BusinessAttributeValuesDO businessAttributeValuesDO;

    private String oldAttributeValue;

    public BusinessAttributeValueSaveEvent(Object source, BusinessAttributeValuesDO businessAttributeValuesDO, String oldAttributeValue) {
        super(source);
        this.businessAttributeValuesDO = businessAttributeValuesDO;
        this.oldAttributeValue = oldAttributeValue;
    }

    public BusinessAttributeValuesDO getBusinessAttributeValuesDO() {
        return businessAttributeValuesDO;
    }

    public void setBusinessAttributeValuesDO(BusinessAttributeValuesDO businessAttributeValuesDO) {
        this.businessAttributeValuesDO = businessAttributeValuesDO;
    }

    public String getOldAttributeValue() {
        return oldAttributeValue;
    }

    public void setOldAttributeValue(String oldAttributeValue) {
        this.oldAttributeValue = oldAttributeValue;
    }
}
