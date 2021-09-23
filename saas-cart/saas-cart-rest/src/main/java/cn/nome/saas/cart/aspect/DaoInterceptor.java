package cn.nome.saas.cart.aspect;

import cn.nome.platform.common.logger.LoggerUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Order(1) // 优先级，数字越大，优先级越低
@Component
public class DaoInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DaoInterceptor.class);
    private final long PRINT_LOG_MILLIS_TIME = 100;

    /**
     * 正则定义过滤范围
     */
    @Pointcut("execution(public * cn.nome.saas.*.repository.dao..*.*(..))")
    public void daoLog() {
    }

    @Around("daoLog()")
    public Object arround(ProceedingJoinPoint pjp) {
        try {
            long startTime = System.currentTimeMillis();

            //Object[] args = printInfo(pjp);
            Object[] args = pjp.getArgs();
            Object o = pjp.proceed(args);
            long diffTime = System.currentTimeMillis() - startTime;
            if (diffTime > PRINT_LOG_MILLIS_TIME) {
                printLog(pjp, diffTime);
            }
            return o;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private void printLog(ProceedingJoinPoint pjp, long diffTime) {
        StringBuilder sb = new StringBuilder(" ");

        String clazzName = pjp.getSignature().getDeclaringTypeName();
        String method = pjp.getSignature().getName();
        sb.append(clazzName).append(" ").append(method).append(" ").append(diffTime).append("ms");
        LoggerUtil.info(LOGGER, "DAO{0}", sb.toString());
    }

    private Object[] printInfo(ProceedingJoinPoint pjp) throws ClassNotFoundException {
        Object[] args = pjp.getArgs();
        String clzType = pjp.getTarget().getClass().getName();
        Class<?> clz = Class.forName(clzType);
        String clzName = clz.getName();

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        String methodName = methodSignature.getName();
        LOGGER.info("clzType:{}", clzType);
        LOGGER.info("clzName:{}", clzName);
        LOGGER.info("methodName:{}", methodName);
        LOGGER.info("args:{}", args);

        LOGGER.info("methodName:{}", methodSignature.getMethod().getName());
        Parameter[] params = methodSignature.getMethod().getParameters();

        Map<String, Object> paramsMap = new HashMap<>();
        int n = 0;
        for (Parameter param : params) {
            LOGGER.info("param [type:{}],[name:{}]", param.getType().getName(), param.getName());
            paramsMap.put(param.getName(), args[n]);
            n++;
        }
        LOGGER.info("paramsMap:{}", paramsMap.toString());
        return args;
    }

}
