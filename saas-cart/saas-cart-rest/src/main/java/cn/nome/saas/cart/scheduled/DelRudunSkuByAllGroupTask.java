package cn.nome.saas.cart.scheduled;

import cn.nome.saas.cart.constant.Constant;
import cn.nome.saas.cart.manager.CartServiceManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RecursiveTask;

/**
 * @author chentaikuang
 */
public class DelRudunSkuByAllGroupTask extends RecursiveTask<String> {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private int start = 0;
    private int end = 0;
    private boolean delDbSizeSku;
    private CartServiceManager cartServiceManager;

    private StringBuffer rstSbf = new StringBuffer();

    public DelRudunSkuByAllGroupTask(int start, int maxUid, boolean delDbSizeSku, CartServiceManager cartServiceManager) {
        this.start = start;
        this.end = maxUid;
        this.delDbSizeSku = delDbSizeSku;
        this.cartServiceManager = cartServiceManager;
    }

    @Override
    protected String compute() {
        boolean canCompute = (end - start) <= Constant.PER_LIST_SIZE;
        if (canCompute) {
            int rst = 0;
            for (int uid = start; uid <= end; uid++) {
                rst = cartServiceManager.execDelRedunSkus(delDbSizeSku,uid);
                LOGGER.warn("rst:{},uid:{}",rst,uid);
                if (rst == 0/* || start % 10 == 0*/) {
                    rstSbf.append(uid).append(",");
                }
            }
        } else {
            //一分为二、二分执行
            int mid = (start + end) / 2;
            DelRudunSkuByAllGroupTask leftTask = new DelRudunSkuByAllGroupTask(start, mid, delDbSizeSku,cartServiceManager);
            DelRudunSkuByAllGroupTask rightTask = new DelRudunSkuByAllGroupTask(mid, end, delDbSizeSku,cartServiceManager);
            invokeAll(leftTask, rightTask);
            String lRst = leftTask.join();
            String rRst = rightTask.join();
            //合并结果
            if (StringUtils.isNotBlank(lRst)){
                rstSbf.append(lRst);
            }
            if (StringUtils.isNotBlank(rRst)){
                rstSbf.append(rRst);
            }
        }
        return rstSbf.toString();
    }

}
