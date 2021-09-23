package cn.nome.saas.cart.utils;

import cn.nome.saas.cart.constant.Constant;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.UUID;

/**
 * @author chentaikuang
 */
public class GenerateRandomStrUtil {
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * @param len 字符串长度，默认8位
     * @return
     */
    public static String call(int len) {
        if (len == 0) {
            len = 8;
        }
        return RandomStringUtils.randomAlphanumeric(len);
    }

    public static String curTimeAndRdStr() {
        return new StringBuffer(String.valueOf(System.currentTimeMillis())).append(RandomStringUtils.randomAlphanumeric(6)).toString();
    }

    public static String newCartAlias(int uid) {
        return DateFormatUtils.format(new Date(), Constant.ALIAS_DATE_FORMAT) + RandomStringUtils.randomAlphabetic(2) + uid;
    }
}

