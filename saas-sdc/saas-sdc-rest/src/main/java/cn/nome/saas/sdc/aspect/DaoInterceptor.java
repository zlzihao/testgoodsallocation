package cn.nome.saas.sdc.aspect;

import cn.nome.platform.common.logger.LoggerUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@Aspect
@Order(1) // 优先级，数字越大，优先级越低
@Component
public class DaoInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DaoInterceptor.class);

    @Pointcut("execution(public * cn.nome.saas.sdc.repository.dao..*.*(..))")
    public void daoLog() {
    }

    @Around("daoLog()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        try {
            long startTime = System.currentTimeMillis();

            Object o = pjp.proceed();
            long diffTime = System.currentTimeMillis() - startTime;
            StringBuilder sb = new StringBuilder(" ");

            String clazzName = pjp.getSignature().getDeclaringTypeName();
            String method = pjp.getSignature().getName();
            sb.append(clazzName).append(" ").append(method).append(" ").append(diffTime).append("ms");
            LoggerUtil.info(LOGGER, String.format("DAO{%s}", sb.toString()));
            return o;
        } catch (Throwable e) {
            LoggerUtil.error(e, LOGGER, String.format("[DAO_CATCH_EXCEPTION]|e:{%s}", e.getMessage()));
            throw e;
        }
    }

}
