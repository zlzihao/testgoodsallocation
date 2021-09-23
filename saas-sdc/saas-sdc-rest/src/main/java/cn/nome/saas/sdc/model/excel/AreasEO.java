package cn.nome.saas.sdc.model.excel;

import cn.nome.platform.common.utils.ToString;
import cn.nome.platform.common.utils.excel.annotation.Column;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/11/8 15:48
 */
public class AreasEO extends ToString {

    @Column(num = 0, value = "大区", width = 64)
    private String bigAreaName;

    @Column(num = 1, value = "小区", width = 64)
    private String smallAreaName;

    @Column(num = 2, value = "大区经理", width = 32)
    private String bigAreaManager;

    @Column(num = 3, value = "大区经理工号", width = 32)
    private String bigAreaManagerJobNumber;

    @Column(num = 4, value = "小区经理", width = 32)
    private String smallAreaManager;

    @Column(num = 5, value = "小区经理工号", width = 32)
    private String smallAreaManagerJobNumber;

    public String getBigAreaName() {
        return bigAreaName;
    }

    public void setBigAreaName(String bigAreaName) {
        this.bigAreaName = bigAreaName;
    }

    public String getSmallAreaName() {
        return smallAreaName;
    }

    public void setSmallAreaName(String smallAreaName) {
        this.smallAreaName = smallAreaName;
    }

    public String getBigAreaManager() {
        return bigAreaManager;
    }

    public void setBigAreaManager(String bigAreaManager) {
        this.bigAreaManager = bigAreaManager;
    }

    public String getBigAreaManagerJobNumber() {
        return bigAreaManagerJobNumber;
    }

    public void setBigAreaManagerJobNumber(String bigAreaManagerJobNumber) {
        this.bigAreaManagerJobNumber = bigAreaManagerJobNumber;
    }

    public String getSmallAreaManager() {
        return smallAreaManager;
    }

    public void setSmallAreaManager(String smallAreaManager) {
        this.smallAreaManager = smallAreaManager;
    }

    public String getSmallAreaManagerJobNumber() {
        return smallAreaManagerJobNumber;
    }

    public void setSmallAreaManagerJobNumber(String smallAreaManagerJobNumber) {
        this.smallAreaManagerJobNumber = smallAreaManagerJobNumber;
    }
}
