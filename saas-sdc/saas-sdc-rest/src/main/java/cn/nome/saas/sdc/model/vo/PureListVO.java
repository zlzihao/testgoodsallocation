package cn.nome.saas.sdc.model.vo;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/10 15:37
 */
public class PureListVO<T> extends ToString {
    private static final Long serialVersionUID = 1L;

    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
