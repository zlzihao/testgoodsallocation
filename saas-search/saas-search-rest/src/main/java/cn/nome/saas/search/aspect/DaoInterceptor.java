package cn.nome.saas.search.aspect;

import cn.nome.saas.search.constant.Constant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import cn.nome.platform.common.logger.LoggerUtil;

@Aspect
@Order(1) // 优先级，数字越大，优先级越低
@Component
public class DaoInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DaoInterceptor.class);

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

            Object o = pjp.proceed();
            long diffTime = System.currentTimeMillis() - startTime;
            if (diffTime > Constant.PRINT_LOG_TIME) {
                printDaoLog(pjp, diffTime);
            }
            return o;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private void printDaoLog(ProceedingJoinPoint pjp, long diffTime) {
        StringBuilder sb = new StringBuilder(" ");

        String clazzName = pjp.getSignature().getDeclaringTypeName();
        String method = pjp.getSignature().getName();
        sb.append(clazzName).append(" ").append(method).append(" ").append(diffTime).append("ms");
        LoggerUtil.info(LOGGER, "DAO{0}", sb.toString());
    }

}
