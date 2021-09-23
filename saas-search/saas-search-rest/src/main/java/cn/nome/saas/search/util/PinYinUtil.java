package cn.nome.saas.search.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 中文转换拼音工具
 *
 * @author chentaikuang
 */
public class PinYinUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(PinYinUtil.class);
    private static HanyuPinyinOutputFormat format = null;

    static {
        //设置汉字拼音输出的格式
        format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    /**
     * 将汉字转换为全拼
     *
     * @param src
     * @return
     */
    public static String getPinYin(String src) {
        char[] hz = src.toCharArray();//该方法的作用是返回一个字符数组，该字符数组中存放了当前字符串中的所有字符
        int len = hz.length;
        String[] py = new String[len];//该数组用来存储
        StringBuilder pySbf = new StringBuilder(); //存放拼音字符串

        try {
            for (int i = 0; i < len; i++) {
                //先判断是否为汉字字符
                if (Character.toString(hz[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    //将汉字的几种全拼都存到py数组中
                    py = PinyinHelper.toHanyuPinyinStringArray(hz[i], format);
                    //取出改汉字全拼的第一种读音，并存放到字符串pys后
                    pySbf.append(py[0]);
                } else {
                    //如果不是汉字字符，直接取出字符并连接到 pys 后
                    pySbf.append(Character.toString(hz[i]));
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
            LOGGER.error("[getPinYin] errMsg:{}", e.getMessage());
        }
        return pySbf.toString();
    }

    /**
     * 提取每个汉字的首字母（未调试优化，慎用）
     *
     * @param str
     * @return
     */
    @Deprecated
    public static String getPinYinHeadChar(String str) {
        String convert = "";
        for (int i = 0; i < str.length(); i++) {
            char word = str.charAt(i);
            //提取汉字的首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert.toUpperCase();
    }

    /**
     * 将字符串转换成ASCII码（未调试优化，慎用）
     */
    @Deprecated
    public static String getCnASCII(String str) {
        StringBuffer buf = new StringBuffer();
        //将字符串转换成字节序列
        byte[] bGBK = str.getBytes();
        for (int i = 0; i < bGBK.length; i++) {
            //将每个字符转换成ASCII码
            buf.append(Integer.toHexString(bGBK[i] & 0xff));
        }
        return buf.toString();
    }

    /**
     * 测试
     */
    public static void main(String[] args) {
//        String str = "陈太旷China2019";
//        System.out.println(getPinYin(str));
//        System.out.println(getPinYinHeadChar(str));
//        System.out.println(getCnASCII(str));
//
//        List l1 = new ArrayList(1);
//        l1.addWordConf(22);
//        List l2 = new ArrayList(2);
//        l2.addWordConf(22);
//        l2.addWordConf(11);
//        l1.removeAll(l2);
//        LOGGER.info("00:{}",l1.isEmpty());

        AtomicInteger atomicInteger = new AtomicInteger(0);
        int c = 0;
        for (int n = 1; n <= 50; n++) {
            c = atomicInteger.incrementAndGet() % 10;
            if (c == 0) {
                LOGGER.info("n:{},c:{}", n, c);
                LOGGER.info("atomicInteger:{}", atomicInteger.get());
            }
        }
    }
}

