package cn.nome.saas.allocation.utils;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ：godsfer
 * @date ：Created in 2019/8/9 17:01
 * @description：检查辅助
 * @modified By：
 * @version: 1.0.0$
 */
public class CheckUtil {

    private static Logger logger = LoggerFactory.getLogger(CheckUtil.class);

    public static void checkExcelFile(String fileName) {
        if (!isExcel(fileName)) {
            throw new BusinessException(Constant.FILE_TYPE_ERROR);
        }
    }

    protected static boolean isExcel(String fileName) {
        return fileName.endsWith("xls") || fileName.endsWith("xlsx");
    }


    public static String specialStrForMysql(String str){
        str = str.replace("\\","\\\\");
        str = str.replace("'","\\'");
        str = str.replace("\"","\\\"");
        return str;
    }

    /**
     * 检查是否整型
     *
     * @param str
     * @return
     */
    public static boolean checkInteger(String str){
        try{
            Integer.parseInt(str);
        }catch (Exception e){
            LoggerUtil.error(logger,"转换整型错误={0}",e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 检查是否DOUBLE
     *
     * @param str
     * @return
     */
    public static boolean checkDouble(String str){
        try{
            Double.parseDouble(str);
        }catch (Exception e){
            LoggerUtil.error(logger,"转换DOUBLE错误={0}",e.getMessage());
            return false;
        }
        return true;
    }
}
