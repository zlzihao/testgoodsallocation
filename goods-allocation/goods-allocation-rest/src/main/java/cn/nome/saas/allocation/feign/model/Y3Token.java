package cn.nome.saas.allocation.feign.model;

import cn.nome.platform.common.utils.ToString;

/**
 * Y3Token
 *
 * @author Bruce01.fan
 * @date 2020/2/9
 */
public class Y3Token extends ToString {

    private int expires_in;

    private String access_token;

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
