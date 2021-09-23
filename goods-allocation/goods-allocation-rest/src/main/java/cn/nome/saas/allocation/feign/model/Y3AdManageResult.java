package cn.nome.saas.allocation.feign.model;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

/**
 * Y3AdManageResult
 *
 * @author Bruce01.fan
 * @date 2020/2/9
 */
public class Y3AdManageResult extends ToString {

    private int errmsg;

    private boolean succeed;

    private String msgTime;

    private List<Y3AdManage> values;

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

    public List<Y3AdManage> getValues() {
        return values;
    }

    public void setValues(List<Y3AdManage> values) {
        this.values = values;
    }
}
