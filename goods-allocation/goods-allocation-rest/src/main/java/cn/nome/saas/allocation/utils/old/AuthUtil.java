//package cn.nome.saas.allocation.utils.old;
//
//import org.jasig.cas.client.validation.Assertion;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import java.io.UnsupportedEncodingException;
//
//public class AuthUtil {
//
//	public static String getUserid(HttpServletRequest request) {
//		String userid = null;
//		// 从单点登陆中获取
//		Assertion a = org.jasig.cas.client.util.AssertionHolder.getAssertion();
//		if (a != null && a.getPrincipal() != null) {
//			userid = a.getPrincipal().getName();
//		}
//		// 从自家登陆中获取
//		Cookie[] arr = request.getCookies();
//		if (arr != null) {
//			for (int i = 0; i < arr.length; i++) {
//				if (arr[i].getName().equals("nome_token")) {
//					String token = arr[i].getValue();
//					userid = JWTUtils.verify(token);
//					return userid;
//				}
//			}
//		}
//		return userid;
//
//	}
//
//	/**
//	 * 是否包括某中文
//	 *
//	 * @param content
//	 * @param key
//	 * @return
//	 */
//	public static boolean isContainsCn(String content, String key) {
//		try {
//			byte[] utf8 = key.getBytes("UTF-8");
//			String n = new String(utf8, "UTF-8");
//			return content.contains(n);
//		} catch (UnsupportedEncodingException e) {
//			return false;
//		}
//
//	}
//
//	public static String getSessionUserId() {
//		ServletRequestAttributes reqAttr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//		if (reqAttr == null){
//			return "";
//		}
//		HttpServletRequest request = reqAttr.getRequest();
//		String userId = getUserid(request);
//		return userId;
//	}
//}
