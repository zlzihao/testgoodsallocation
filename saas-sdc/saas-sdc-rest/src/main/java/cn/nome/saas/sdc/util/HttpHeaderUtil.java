package cn.nome.saas.sdc.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/16 14:41
 */
public class HttpHeaderUtil {
    public static String getParameter(String header, String param) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return request.getHeader(header) == null ? request.getParameter(param) : request.getHeader(header);
    }
}
