package cn.nome.saas.cart.model.test;

import cn.nome.saas.cart.model.BaseModel;

public class TabIndexModel extends BaseModel {
    private String tabName;
    private int tabIndex;

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

}
