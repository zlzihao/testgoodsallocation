package cn.nome.saas.cart.model;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

/**
 * 购物车有效商品集，含多个sku
 *
 * @author chentaikuang
 */
public class ValidSkuModel extends ToString {

    private List<CartSkuModel> skuModels;
    private String promoTips; // 有效提示
    @Deprecated
    private int productSetId;//商品集id
//活动id
    private int id;

    private PromoVo promoVo;

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PromoVo getPromoVo() {
        return promoVo;
    }

    public void setPromoVo(PromoVo promoVo) {
        this.promoVo = promoVo;
    }

    public List<CartSkuModel> getSkuModels() {
        return skuModels;
    }

    public void setSkuModels(List<CartSkuModel> skuModels) {
        this.skuModels = skuModels;
    }
    @Deprecated
    public int getProductSetId() {
        return productSetId;
    }
    @Deprecated
    public void setProductSetId(int productSetId) {
        this.productSetId = productSetId;
    }

    public String getPromoTips() {
        return promoTips;
    }

    public void setPromoTips(String promoTips) {
        this.promoTips = promoTips;
    }

}