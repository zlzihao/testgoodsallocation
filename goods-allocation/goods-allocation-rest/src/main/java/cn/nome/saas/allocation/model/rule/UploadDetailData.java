package cn.nome.saas.allocation.model.rule;

import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.utils.ToString;
import cn.nome.platform.common.utils.excel.annotation.Column;

import java.util.Date;

/**
 * UploadDetailData
 *
 * @author Bruce01.fan
 * @date 2019/5/27
 */
public class UploadDetailData extends ToString {

    @Column(value = "门店代码",num = 0,isFilter = true,autoLine=true)
    private String shopCode;

    @Column(value = "类型",num = 1,isFilter = true,autoLine=true)
    private String type;

    @Column(value = "对象",num = 2,isFilter = true,autoLine=true)
    private String typeValue;

    @Column(value = "开始日期",num = 3,isFilter = true,autoLine=true)
    private Date startDate;

    @Column(value = "结束日期",num = 4,isFilter = true,autoLine=true)
    private Date endDate;

    @Column(value = "备注",num = 5,isFilter = true,autoLine=true)
    private String remark;

    @Column(value = "最后修改人",num = 6,isFilter = true,autoLine=true)
    private String updateUser;

    private String effectiveTime;

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getEffectiveTime() {
        return DateUtil.format(startDate, DateUtil.DATE_ONLY)+"-"+ DateUtil.format(endDate, DateUtil.DATE_ONLY);
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

}
