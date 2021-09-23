package cn.nome.saas.allocation.feign.model;

import java.util.List;

/**
 * @author lizihao@nome.com
 */
public class ShopMappingPositionForm {
    private List<ShopMappingPositionVO> list;

    public List<ShopMappingPositionVO> getList() {
        return list;
    }

    public void setList(List<ShopMappingPositionVO> list) {
        this.list = list;
    }
}
