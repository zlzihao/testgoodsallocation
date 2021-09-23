package cn.nome.saas.cart.scheduled;

import cn.nome.saas.cart.constant.Constant;
import cn.nome.saas.cart.manager.CartServiceManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RecursiveTask;

public class SyncAliasByRangeGroupTask extends RecursiveTask<String> {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private int start = 0;
    private int end = 0;
    private CartServiceManager cartServiceManager;

    private StringBuffer rstSbf = new StringBuffer();

    public SyncAliasByRangeGroupTask(int start, int maxUid, CartServiceManager cartServiceManager) {
        this.start = start;
        this.end = maxUid;
        this.cartServiceManager = cartServiceManager;
    }

    @Override
    protected String compute() {
        boolean canCompute = (end - start) <= Constant.PER_LIST_SIZE;
        if (canCompute) {
            int rst = 0;
            for (int uid = start; uid <= end; uid++) {
                rst = cartServiceManager.execSyncAliasByUid(uid);
                LOGGER.warn("rst:{},uid:{}", rst, uid);
                if (rst == 0) {
                    rstSbf.append(uid).append(",");
                }
            }
        } else {
            int mid = (start + end) / 2;
            SyncAliasByRangeGroupTask leftTask = new SyncAliasByRangeGroupTask(start, mid, cartServiceManager);
            SyncAliasByRangeGroupTask rightTask = new SyncAliasByRangeGroupTask(mid, end, cartServiceManager);
            invokeAll(leftTask, rightTask);
            String lRst = leftTask.join();
            String rRst = rightTask.join();
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
