package cn.nome.saas.sdc.model.form;

import cn.nome.platform.common.utils.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/9 11:48
 */
public class IdForm extends ToString {
    private static final Long serialVersionUID = 1L;

    @NotNull(message = "Id不能为空")
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
