package cn.nome.saas.sdc.model.vo;

import cn.nome.platform.common.utils.ToString;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/25 10:13
 */
public class ShopOptionVO extends ToString {

    private Integer id;

    private String shopName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
