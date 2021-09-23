package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * AllocationClothingResultVO
 *
 * @author Bruce01.fan
 * @date 2019/12/26
 */
public class AllocationClothingResultVO extends ToString {

    private int id;

    private int taskId;

    private String shopId;

    private String shopName;

    private String categoryCode;

    private String categoryName;

    private int lowSkc;

    private int standardSkc;

    private int highSkc;

    private String beforeFullRate;

    private String afterFullRate;

    private int beforeSkc;

    private int afterSkc;

    private int beforeInvalidStyle;

    private int beforeInvalidNum;

    private int afterInvalidStyle;

    private int afterInvalidNum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getLowSkc() {
        return lowSkc;
    }

    public void setLowSkc(int lowSkc) {
        this.lowSkc = lowSkc;
    }

    public int getStandardSkc() {
        return standardSkc;
    }

    public void setStandardSkc(int standardSkc) {
        this.standardSkc = standardSkc;
    }

    public int getHighSkc() {
        return highSkc;
    }

    public void setHighSkc(int highSkc) {
        this.highSkc = highSkc;
    }

    public String getBeforeFullRate() {
        return beforeFullRate;
    }

    public void setBeforeFullRate(String beforeFullRate) {
        this.beforeFullRate = beforeFullRate;
    }

    public String getAfterFullRate() {
        return afterFullRate;
    }

    public void setAfterFullRate(String afterFullRate) {
        this.afterFullRate = afterFullRate;
    }

    public int getBeforeSkc() {
        return beforeSkc;
    }

    public void setBeforeSkc(int beforeSkc) {
        this.beforeSkc = beforeSkc;
    }

    public int getAfterSkc() {
        return afterSkc;
    }

    public void setAfterSkc(int afterSkc) {
        this.afterSkc = afterSkc;
    }

    public int getBeforeInvalidStyle() {
        return beforeInvalidStyle;
    }

    public void setBeforeInvalidStyle(int beforeInvalidStyle) {
        this.beforeInvalidStyle = beforeInvalidStyle;
    }

    public int getBeforeInvalidNum() {
        return beforeInvalidNum;
    }

    public void setBeforeInvalidNum(int beforeInvalidNum) {
        this.beforeInvalidNum = beforeInvalidNum;
    }

    public int getAfterInvalidStyle() {
        return afterInvalidStyle;
    }

    public void setAfterInvalidStyle(int afterInvalidStyle) {
        this.afterInvalidStyle = afterInvalidStyle;
    }

    public int getAfterInvalidNum() {
        return afterInvalidNum;
    }

    public void setAfterInvalidNum(int afterInvalidNum) {
        this.afterInvalidNum = afterInvalidNum;
    }
}
