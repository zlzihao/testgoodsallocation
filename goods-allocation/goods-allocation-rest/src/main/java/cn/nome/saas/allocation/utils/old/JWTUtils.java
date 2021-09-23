//package cn.nome.saas.allocation.utils.old;
//
//import cn.nome.platform.common.logger.LoggerUtil;
//import cn.nome.platform.common.utils.JsonUtils;
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.JWTCreator;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.exceptions.JWTCreationException;
//import com.auth0.jwt.exceptions.JWTVerificationException;
//import com.auth0.jwt.interfaces.Claim;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import com.auth0.jwt.interfaces.JWTVerifier;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Date;
//import java.util.Map;
//
//public class JWTUtils {
//	private static Logger logger = LoggerFactory.getLogger(JWTUtils.class);
//
//	private JWTUtils() {
//	}
//
//	// 在验证或签名实例中使用的密钥(自定义和密码加盐差不多)
//	private static final String SECRET = "nome!123";
//	private static final String KEYID = "claim";
//
//	// 使用HS256算法
//	private static Algorithm algorithm = Algorithm.HMAC256(SECRET);
//
//	/**
//	 * 加密
//	 *
//	 * @param data
//	 * @return
//	 */
//	public static String encrypt(Object data) {
//		try {
//			// 通过调用 JWT.create()来创建 jwt实例
//			JWTCreator.Builder builder = JWT.create();
//			builder.withJWTId(KEYID);
//			// 设置过期时间7天
//			builder.withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000 * 24 * 7));
//			// 索赔:添加自定义声明值,完成荷载的信息
//			builder.withClaim(KEYID, JsonUtils.toJson(data));
//			// 签署:调用sign()传递算法实例
//			return builder.sign(algorithm);
//		} catch (JWTCreationException e) {
//			LoggerUtil.info(logger, "校验失败或token已过期:{0}", e.getMessage());
//		}
//		return null;
//	}
//
//	/**
//	 * 校验
//	 *
//	 * @param token
//	 * @return
//	 */
//	public static String verify(String token) {
//		try {
//			// 这将用于验证令牌的签名
//			JWTVerifier verifier = JWT.require(algorithm).build();
//			// 针对给定令牌执行验证
//			DecodedJWT jwt = verifier.verify(token);
//			// 获取令牌中定义的声明
//			Map<String, Claim> claims = jwt.getClaims();
//			// 返回指定键映射到的值
//			return claims.get(JWTUtils.KEYID).asString().replace("\"", "");
//		} catch (JWTVerificationException e) {
//			LoggerUtil.info(logger, "无效的签名配置:{0}", e.getMessage());
//		}
//		return null;
//	}
//}
