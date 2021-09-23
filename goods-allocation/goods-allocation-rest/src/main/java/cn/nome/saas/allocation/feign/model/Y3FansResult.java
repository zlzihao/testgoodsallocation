package cn.nome.saas.allocation.feign.model;

import cn.nome.platform.common.utils.ToString;

/**
 * Y3FanResult
 *
 * @author Bruce01.fan
 * @date 2020/2/9
 */
public class Y3FansResult extends ToString {

    private int errmsg;

    private boolean succeed;

    private String msgTime;

    private int values;

    public int getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(int errmsg) {
        this.errmsg = errmsg;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public int getValues() {
        return values;
    }

    public void setValues(int values) {
        this.values = values;
    }
}
