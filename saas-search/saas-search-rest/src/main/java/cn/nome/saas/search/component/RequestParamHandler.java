package cn.nome.saas.search.component;

import cn.nome.saas.search.constant.Constant;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author chentaikuang
 */
@Component
@Order(100)
public class RequestParamHandler extends OncePerRequestFilter implements HandlerInterceptor {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private static ThreadLocal<Long> curThreadCostTime = new ThreadLocal<Long>();
    private final String ACTUATOR_HEALTH_URL = "/actuator/health";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request instanceof HttpServletRequest && !ACTUATOR_HEALTH_URL.equalsIgnoreCase(request.getRequestURI())) {
            LOGGER.debug("request url:{}", request.getRequestURI());
            String method = request.getMethod();
            String contentType = request.getContentType();
            LOGGER.debug("method:{},contentType:{}", method, contentType);

            if (StringUtils.isNoneBlank(contentType) && contentType.contains(";")) {
                String[] contentTypeArr = contentType.split(";");
                contentType = contentTypeArr[0];
                LOGGER.debug("SPLIT_CONTENT_TYPE,get arr first one:{}", contentType);
            }
            if (!ContentType.MULTIPART_FORM_DATA.getMimeType().equalsIgnoreCase(contentType)) {
                request = new BufferServletReqWrap(request);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        curThreadCostTime.set(System.currentTimeMillis());

        LOGGER.info("[preHandle]request url:{}", request.getRequestURL());

        // 刷出了字节数组，但需要关注下post stream时候能否获取到request body
        if (request instanceof BufferServletReqWrap) {
            BufferServletReqWrap requestWrap = new BufferServletReqWrap(request);
            LOGGER.info("[preHandle]request body:{}", new String(requestWrap.getBuffer()));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) throws Exception {

        long cost = System.currentTimeMillis() - curThreadCostTime.get();
        if (cost > Constant.PRINT_LOG_TIME) {
            LOGGER.info("[postHandle]request cost time:{} ms", cost);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        curThreadCostTime.remove();
    }
}
