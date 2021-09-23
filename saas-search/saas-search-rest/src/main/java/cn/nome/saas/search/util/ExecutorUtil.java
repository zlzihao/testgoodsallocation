package cn.nome.saas.search.util;

import java.util.concurrent.*;

/**
 * 线程共享池
 */
public class ExecutorUtil {
    private static ExecutorService executorService = new ThreadPoolExecutor(8, 16,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(3000));

    /**
     * 有返回结果
     *
     * @param task
     * @param <V>
     * @return
     */
    public static <V> Future<V> execute(Callable<V> task) {
        return executorService.submit(task);
    }

    /**
     * 无返回结果
     *
     * @param task
     */
    public static void submit(Callable task) {
        executorService.submit(task);
    }

    /**
     * 无返回结果
     *
     * @param task
     */
    public static void execute(Runnable task) {
        executorService.execute(task);
    }
}
