package cn.nome.saas.cart.utils;

import cn.nome.saas.cart.constant.Constant;
import cn.nome.saas.cart.manager.CartServiceManager;
import cn.nome.saas.cart.scheduled.DelRudunSkuByAllGroupTask;
import cn.nome.saas.cart.scheduled.DelRudunSkuByUidsGroupTask;
import cn.nome.saas.cart.scheduled.SyncAliasByRangeGroupTask;
import cn.nome.saas.cart.scheduled.SyncAliasByUidsGroupTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

@Component
public class JoinForkUtil {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * 根据范围并行执行
     *
     * @param minUid
     * @param maxUid
     * @param delDbSizeSku
     * @param cartServiceManager
     * @return
     */
    public String delSkusByRange(int minUid, int maxUid, boolean delDbSizeSku, CartServiceManager cartServiceManager) {
        String rst = "";
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask taskRst = pool.submit(new DelRudunSkuByAllGroupTask(minUid, maxUid, delDbSizeSku, cartServiceManager));
        rst = getAndClose(rst, pool, taskRst);
        return rst;
    }

    /**
     * 根据uids并行执行
     *
     * @param uidsArr
     * @param delDbSizeSku
     */
    public String delSkusByUids(String[] uidsArr, boolean delDbSizeSku, CartServiceManager cartServiceManager) {
        String rst = "";
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask taskRst = pool.submit(new DelRudunSkuByUidsGroupTask(0, uidsArr, delDbSizeSku, cartServiceManager));
        rst = getAndClose(rst, pool, taskRst);
        return rst;
    }

    private String getAndClose(String rst, ForkJoinPool pool, ForkJoinTask taskRst) {
        try {
            rst = String.valueOf(taskRst.get());
            LOGGER.info("[taskRst] fail uids:{}", rst);
            pool.awaitTermination(Constant.FORK_JOIN_AWAIT_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("[taskRst] err:{}", e.getMessage());
        } finally {
            pool.shutdown();
        }
        return rst;
    }

    public String syncAliasByRange(int minUid, int maxUid, CartServiceManager cartServiceManager) {
        String rst = "";
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask taskRst = pool.submit(new SyncAliasByRangeGroupTask(minUid, maxUid, cartServiceManager));
        rst = getAndClose(rst, pool, taskRst);
        return rst;
    }

    public String syncAliasByUids(String[] uidsArr, CartServiceManager cartServiceManager) {
        String rst = "";
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask taskRst = pool.submit(new SyncAliasByUidsGroupTask(0, uidsArr, cartServiceManager));
        rst = getAndClose(rst, pool, taskRst);
        return rst;
    }
}
