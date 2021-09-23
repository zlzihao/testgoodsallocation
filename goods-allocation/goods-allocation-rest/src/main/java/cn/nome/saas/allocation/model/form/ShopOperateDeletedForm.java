package cn.nome.saas.allocation.model.form;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

/**
 * @author lizihao@nome.com
 */
public class ShopOperateDeletedForm extends ToString {

    private List<Integer> ids;

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }
}
