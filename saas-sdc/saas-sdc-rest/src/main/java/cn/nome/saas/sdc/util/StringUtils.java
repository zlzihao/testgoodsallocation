package cn.nome.saas.sdc.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lizihao@nome.com
 */
public class StringUtils {

    /*
     *  字符串转日期，是否匹配格式
     * */

    public static boolean stringConvertDateFormat(String regex, String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(regex);
        boolean convertSuccess = true;
        try {
            dateFormat.setLenient(false);
            dateFormat.parse(date);
        } catch (ParseException e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public static String formatRegexString(String regex, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(regex);
        return dateFormat.format(date);
    }

    public static boolean stringConvertBigDecimal(String str) {
        if (str == null) return true;
        try {
            BigDecimal number = new BigDecimal(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
