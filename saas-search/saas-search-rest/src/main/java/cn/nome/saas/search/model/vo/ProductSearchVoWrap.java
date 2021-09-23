package cn.nome.saas.search.model.vo;

import java.util.List;

/**
 * 商品搜索结果
 *
 * @author chentaikuang
 */
public class ProductSearchVoWrap {

    List<ProductVo> productVos;
    private PageVo pageVo;

    public PageVo getPageVo() {
        return pageVo;
    }

    public void setPageVo(PageVo pageVo) {
        this.pageVo = pageVo;
    }

    public List<ProductVo> getProductVos() {
        return productVos;
    }

    public void setProductVos(List<ProductVo> productVos) {
        this.productVos = productVos;
    }

}
