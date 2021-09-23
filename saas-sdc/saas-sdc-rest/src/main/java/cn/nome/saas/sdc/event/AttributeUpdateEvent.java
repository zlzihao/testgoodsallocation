package cn.nome.saas.sdc.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/10/31 14:26
 */
public class AttributeUpdateEvent extends ApplicationEvent {

    private Integer corpId;

    private Integer shopId;

    public AttributeUpdateEvent(Object source, Integer corpId, Integer shopId) {
        super(source);
        this.corpId = corpId;
        this.shopId = shopId;
    }

    public Integer getShopId() {
        return this.shopId;
    }

    public Integer getCorpId() {
        return corpId;
    }
}
