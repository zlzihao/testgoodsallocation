package cn.nome.saas.allocation.utils;

import cn.nome.saas.allocation.constant.Constant;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

public class AuthUtil {

    public static String getUserid(HttpServletRequest request) {

        String userid = null;
        // 从单点登陆中获取
        Assertion a = org.jasig.cas.client.util.AssertionHolder.getAssertion();
        if (a != null && a.getPrincipal() != null) {
            userid = a.getPrincipal().getName();
        }
        // 从自家登陆中获取
        HttpSession session = request.getSession();
        if (session != null && session.getAttribute("userid") != null) {
            userid = (String) session.getAttribute("userid");
        }

        // TODO 测试环境写死
        if (Constant.DEBUG_FLAG_USER) {
            userid = "90000402";
        }

        return userid;

    }

    /**
     * 是否包括某中文
     *
     * @param content
     * @param key
     * @return
     */
    public static boolean isContainsCn(String content, String key) {
        try {
            byte[] utf8 = key.getBytes("UTF-8");
            String n = new String(utf8, "UTF-8");
            return content.contains(n);
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    public static String getSessionUserId() {

        if (Constant.DEBUG_FLAG_USER) {
            return "90000402";
        }

        RequestAttributes res = RequestContextHolder.getRequestAttributes();
        if (res == null){
            return "";
        }
        HttpServletRequest request = ((ServletRequestAttributes) res).getRequest();
        String userId = getUserid(request);
        return userId;
    }
}
