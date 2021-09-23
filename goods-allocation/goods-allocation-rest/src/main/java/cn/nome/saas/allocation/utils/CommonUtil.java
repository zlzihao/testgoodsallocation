package cn.nome.saas.allocation.utils;

import cn.nome.platform.common.utils.DateUtil;

import java.util.Date;

/**
 * CommonUtil
 *
 * @author Bruce01.fan
 * @date 2019/9/9
 */
public class CommonUtil {

    public static String getTableName(String tablePrefix) {
        Date date = DateUtil.getCurrentDate();
        String currentDate = DateUtil.format(DateUtil.addDate(date,-1,DateUtil.DAY),DateUtil.DATE_ONLY);
        currentDate = currentDate.replaceAll("-","").substring(2);

        return tablePrefix+"_"+ currentDate;
    }

    /*public static String getTaskTableName(String tablePrefix,int taskId) {
        Date date = DateUtil.getCurrentDate();
        String currentDate = DateUtil.format(DateUtil.addDate(date,-1,DateUtil.DAY),DateUtil.DATE_ONLY);
        currentDate = currentDate.replaceAll("-","").substring(2);
        return tablePrefix+"_"+taskId+"_"+ currentDate;
    }*/

    public static String getTaskTableName(String tablePrefix,int taskId,Date runtime) {
        String currentDate = DateUtil.format(DateUtil.addDate(runtime,-1,DateUtil.DAY),DateUtil.DATE_ONLY);
        currentDate = currentDate.replaceAll("-","").substring(2);

        return tablePrefix+"_"+taskId+"_"+ currentDate;
    }
}
