package cn.nome.saas.allocation.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author ：godsfer
 * @date ：Created in 2019/5/28 15:56
 * @description：打印堆栈
 * @modified By：
 * @version: 1.0.0$
 */
public class StackUtil {
    public static String getStackTrace(Throwable throwable)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try
        {
            throwable.printStackTrace(pw);
            return sw.toString();
        } finally
        {
            pw.close();
        }
    }
}
