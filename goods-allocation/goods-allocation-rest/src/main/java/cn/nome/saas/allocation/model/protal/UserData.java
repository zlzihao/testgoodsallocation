package cn.nome.saas.allocation.model.protal;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

/**
 * UserData
 *
 * @author Bruce01.fan
 * @date 2019/7/1
 */
public class UserData extends ToString{

    private String userId;

    private String userName;

    private List<Integer> appIdList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Integer> getAppIdList() {
        return appIdList;
    }

    public void setAppIdList(List<Integer> appIdList) {
        this.appIdList = appIdList;
    }
}
