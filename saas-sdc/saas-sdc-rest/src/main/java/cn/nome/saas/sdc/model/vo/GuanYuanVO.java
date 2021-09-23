package cn.nome.saas.sdc.model.vo;

import cn.nome.platform.common.utils.ToString;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2020/2/11 11:33
 */
public class GuanYuanVO extends ToString {

    private String ssoUrl;

    public String getSsoUrl() {
        return ssoUrl;
    }

    public void setSsoUrl(String ssoUrl) {
        this.ssoUrl = ssoUrl;
    }
}
