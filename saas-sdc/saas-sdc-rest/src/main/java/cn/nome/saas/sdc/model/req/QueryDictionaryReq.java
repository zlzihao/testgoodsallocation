package cn.nome.saas.sdc.model.req;

import cn.nome.platform.common.utils.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
public class QueryDictionaryReq extends ToString {


    @NotEmpty(message = "字典编码不能为空")
    private String dictionaryCode;

    public String getDictionaryCode() {
        return dictionaryCode;
    }

    public void setDictionaryCode(String dictionaryCode) {
        this.dictionaryCode = dictionaryCode;
    }
}
