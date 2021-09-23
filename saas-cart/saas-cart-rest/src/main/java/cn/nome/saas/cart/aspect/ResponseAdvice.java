package cn.nome.saas.cart.aspect;

import cn.nome.saas.cart.CartBootstrap;
import com.alibaba.fastjson.JSON;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chentaikuang
 * 打印返回结果
 */
@Aspect
@Component
@ControllerAdvice(basePackageClasses = CartBootstrap.class)
public class ResponseAdvice implements ResponseBodyAdvice {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Nullable
    @Override
    public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        LOGGER.info("request url:{}", request.getURI().toString());
        LOGGER.info("response body:{}", JSON.toJSONString(body));
        return body;
    }
}

