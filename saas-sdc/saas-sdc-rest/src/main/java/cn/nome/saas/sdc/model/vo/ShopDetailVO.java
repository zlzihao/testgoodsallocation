package cn.nome.saas.sdc.model.vo;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/17 10:52
 */
public class ShopDetailVO extends ToString {

    private ShopsVO shop;

    private List<SearchBusinessAttributesVO> attributes;

    public ShopsVO getShop() {
        return shop;
    }

    public void setShop(ShopsVO shop) {
        this.shop = shop;
    }

    public List<SearchBusinessAttributesVO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<SearchBusinessAttributesVO> attributes) {
        this.attributes = attributes;
    }
}