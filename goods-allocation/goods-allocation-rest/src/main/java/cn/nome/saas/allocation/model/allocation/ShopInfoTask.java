package cn.nome.saas.allocation.model.allocation;

import java.util.Date;

/**
 * ShopInfoTask
 *
 * @author Bruce01.fan
 * @date 2019/10/18
 */
public class ShopInfoTask {

    private int id;

    private String shopId;

    private int status;

    private int retry;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ShopInfoTask{");
        sb.append("id=").append(id);
        sb.append(", shopId='").append(shopId).append('\'');
        sb.append(", status=").append(status);
        sb.append(", retry=").append(retry);
        sb.append('}');
        return sb.toString();
    }
}
