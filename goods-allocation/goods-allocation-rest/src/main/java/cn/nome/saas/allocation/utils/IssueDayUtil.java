package cn.nome.saas.allocation.utils;

import cn.nome.platform.common.utils.DateUtil;
import com.alibaba.druid.sql.visitor.functions.Char;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chentaikuang
 */
public class IssueDayUtil {

    private static Logger logger = LoggerFactory.getLogger(IssueDayUtil.class);

    private static Map<String, Integer> map = new HashMap<String, Integer>() {
        {
            put("一", 1);
            put("二", 2);
            put("三", 3);
            put("四", 4);
            put("五", 5);
            put("六", 6);
            put("日", 7);
            put("七", 7);
        }
    };

    private static Map<String, Integer> mapV2 = new HashMap<String, Integer>() {
        {
            put("一", 1);
            put("二", 2);
            put("三", 3);
            put("四", 4);
            put("五", 5);
            put("六", 6);
            put("日", 0);
            put("七", 0);
        }
    };

    public static void main(String[] args) {

/*//        initMap();
        //在途天数
        int roadDays = 3;
        roadDays = 1;
        //补货周期
        String issueTime = "二五七";
        issueTime = "一";
//        issueTime = "四五六";
//        issueTime = "六五四";
//        issueTime = "七五二";
//        int days = getDays(issueTime);


        int days = getIssueDayByDate(issueTime, roadDays, "20190910");

        logger.info("days:" + days);*/

        //int days = getIssueDay(5,"四六一", 2);

        int days2 = getIssueDayV2("2019-08-25",3,"四五六");

        System.out.println(" " + days2);

        Calendar calBegin = Calendar.getInstance();
        calBegin.add(Calendar.DATE, -7);
        Calendar calEnd = Calendar.getInstance();
        calEnd.add(Calendar.DATE, 7);

        System.out.println(IssueDayUtil.convertIssueTimeV2("一三五"));
        for (Calendar calendar : IssueDayUtil.getDayOfWeekWithinDateInterval(calBegin, calEnd, new HashSet<>(IssueDayUtil.convertIssueTimeV2("一三五")))) {
            System.out.println(calendar.getTime());
        }
    }

    /**
     * 获取某段时间内的周一（二等等）的日期
     * @param calBegin 开始日期
     * @param calEnd 结束日期
     * @param weekDays 获取周几，1－6代表周一到周六。0代表周日
     * @return 返回日期List
     */
    public static List<Calendar> getDayOfWeekWithinDateInterval(Calendar calBegin, Calendar calEnd, Set<Integer> weekDays) {
        List<Calendar> dateResult = new ArrayList<>();
        calBegin = (Calendar) calBegin.clone();
        calEnd = (Calendar) calEnd.clone();
        Calendar[] dateInterval = {calBegin, calEnd};
        for (Calendar cal = dateInterval[0]; cal.compareTo(dateInterval[1]) <= 0; ) {
            if (weekDays.contains(cal.get(Calendar.DAY_OF_WEEK) - 1)) {
                dateResult.add((Calendar) cal.clone());
            }
            cal.add(Calendar.DATE, 1);
        }
        return dateResult;
    }

    private static Date parseDate(String todayDateSr) {
        Date todayDate = null;
        if (StringUtils.isNotBlank(todayDateSr)) {
            DateFormat format = new SimpleDateFormat("yyyyMMdd");
            try {
                todayDate = format.parse(todayDateSr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            todayDate = new Date();
        }
        return todayDate;
    }

    private static int getIssueDayByDate(String issueTime, int roadDays, String dateStr) {

        int startDay = 0;
        int endDay = 0;
        char[] chars = issueTime.toCharArray();
        List<Integer> weekDays = convertIssueTime(chars);
        logger.info("weekDays:" + weekDays);
        if (weekDays == null || weekDays.isEmpty()) {
            logger.error("weekDays isEmpty :{}", issueTime);
            return 0;
        }

        Calendar curCal = Calendar.getInstance();
        Date curDate = null;
        if (StringUtils.isBlank(dateStr)) {
            curDate = curCal.getTime();
        } else {
            curDate = parseDate(dateStr);
            curCal.setTime(curDate);
        }
//        int todayWeek = covertAmc2ChWeek(curCal.get(Calendar.DAY_OF_WEEK));

        Calendar issueDayCalc = curCal;
        //已加上在途天数
        issueDayCalc.add(Calendar.DATE, roadDays + 1);
        int amcWeek = issueDayCalc.get(Calendar.DAY_OF_WEEK);
        int issueDayWeek = covertAmc2ChWeek(amcWeek);
        logger.info("amcWeek:{},issueDayWeek:{}", amcWeek, issueDayWeek);

        int length = weekDays.size();
        if (length == 1) {

            startDay = weekDays.get(0);
            int preDays = calcBeforeDays(startDay, issueDayCalc, curDate, issueDayWeek);

            return 7 + preDays;
        }

        //到货天数对应周几
//        int issueDay = (todayWeek + roadDays + 1);
//        if (issueDay > 7) {
//            issueDay = issueDay % 7;
//        }

        int issueDay = issueDayWeek;

        logger.info("issueDay:{}",issueDayWeek);
        if (weekDays.contains(issueDay)) {
            startDay = issueDay;
            //寻找下一个
            int startIdx = weekDays.indexOf(issueDay);
            if (startIdx + 1 == length) {
                endDay = weekDays.get(0);
            } else if (startIdx < length) {
                endDay = weekDays.get(startIdx + 1);
            } else {
                logger.info("----------->> 奇怪的场景 startIdx:{},issueDay:{}", startIdx, issueDay);
            }

            logger.info("startDay:" + startDay);
            logger.info("endDay:" + endDay);

            int preDays = calcBeforeDays(startDay, issueDayCalc, curDate, issueDayWeek);

            if (endDay < startDay) {
                return preDays + endDay + 7 - startDay;
            }
            return preDays + endDay - startDay;
        }


        startDay = weekDays.get(0);
        endDay = weekDays.get(1);
        if (length == 2) {
            boolean between = startDay < issueDay && issueDay < endDay;
            if (issueDay == startDay || between) {
                int tem = 0;
                tem = startDay;
                startDay = endDay;
                endDay = tem;
            }
        } else {

            if (issueDay == endDay) {
                startDay = endDay;
                endDay = weekDays.get(2);
            } else if (weekDays.get(length - 1) == issueDay) {
                startDay = weekDays.get(length - 1);
                endDay = weekDays.get(length);
            } else {
                //中间
                int tem = 0;
                for (int n = 0; n < length; n++) {
                    if (issueDay == weekDays.get(n)) {
                        tem = weekDays.get(n);
                        startDay = tem;
                        endDay = weekDays.get(n + 1);
                        break;
                    } else if (issueDay < weekDays.get(n)) {
                        tem = startDay;
                        startDay = weekDays.get(n);
                        if (n + 1 == length) {
                            endDay = tem;
                        } else {
                            endDay = weekDays.get(n + 1);
                        }
                        break;
                    }
                }
            }
        }

        logger.info("startDay:{},endDay:{}", startDay, endDay);
        int preDays = calcBeforeDays(startDay, issueDayCalc, curDate, issueDayWeek);
        if (endDay < startDay) {
            return preDays + endDay + 7 - startDay;
        }
        return preDays + endDay - startDay;
    }

    private static int calcBeforeDays(int startDay, Calendar issueDayCalc, Date curDate, int issueDayWeek) {
        if (issueDayWeek > startDay) {
            issueDayCalc.add(Calendar.WEEK_OF_MONTH, 1);
        }
        int amcWeek = convertCn2AmcWeek(startDay);
        issueDayCalc.set(Calendar.DAY_OF_WEEK, amcWeek);
        Date firstDate = issueDayCalc.getTime();
        long longtime = firstDate.getTime() - curDate.getTime();
        //1000 * 60 * 60 * 24 = 86400000
        int calcDay = (int) longtime / 86400000;
        return calcDay < 0 ? 7 + calcDay : calcDay;
    }

    private static int covertAmc2ChWeek(int dw) {
        return dw == 1 ? 7 : dw - 1;
    }

    /**
     * 中国周几转成外国的周几
     *
     * @param startDay
     * @return
     */
    private static int convertCn2AmcWeek(int startDay) {
        return (startDay == 7 ? 1 : startDay + 1);
    }

    private static int getIssueDay(String issueTime, int roadDays, int todayWeek) {
        if (todayWeek == 0) {
            todayWeek = getWeekday();
            logger.info("-->> realtime day of week:{}", todayWeek);
        }
        int startDay = 0;
        int endDay = 0;
        char[] chars = issueTime.toCharArray();
//        System.out.println("chars:" + chars);
        List<Integer> weekDays = convertIssueTime(chars);
        logger.info("weekDays:" + weekDays);
        if (weekDays == null || weekDays.isEmpty()) {
            logger.error("weekDays isEmpty :{}", issueTime);
            return 0;
        }

        int length = weekDays.size();

        if (length == 1) {
            return 7;
        }

        //到货日期、周几
        int issueDay = (todayWeek + roadDays + 1);
        if (issueDay > 7) {
            issueDay = issueDay % 7;
        }
        if (weekDays.contains(issueDay)) {
            startDay = issueDay;
            //寻找下一个
            int startIdx = weekDays.indexOf(issueDay);
            if (startIdx + 1 == length) {
                endDay = weekDays.get(0);
            } else if (startIdx < length) {
                endDay = weekDays.get(startIdx + 1);
            } else {
                logger.info("----------->> 奇怪的场景：" + startIdx + "," + issueDay);
            }

            logger.info("startDay:" + startDay);
            logger.info("endDay:" + endDay);

            if (endDay < startDay) {
                return endDay + 7 - startDay;
            }
            return endDay - startDay;
        }

        int f0 = weekDays.get(0);
        int f1 = weekDays.get(1);

        startDay = f0;
        endDay = f1;

        if (length == 2) {

            if (issueDay == f0 || f0 < issueDay && issueDay < f1) {
                startDay = f1;
                endDay = f0;
            }


        } else {

            if (issueDay == f1) {
                startDay = f1;
                endDay = weekDays.get(2);
            } else if (weekDays.get(length - 1) == issueDay) {
                startDay = weekDays.get(length - 1);
                endDay = weekDays.get(length);
            } else {
                //中间
                for (int n = 0; n < length; n++) {
                    int tem = weekDays.get(n);
                    if (issueDay == tem) {
                        startDay = tem;
                        endDay = weekDays.get(n + 1);
                        break;
                    } else if (issueDay < tem) {
                        startDay = tem;
                        if (n + 1 == length) {
                            endDay = f0;
                        } else {
                            endDay = weekDays.get(n + 1);
                        }
                        break;
                    }
                }
            }
        }

        logger.info("startDay:" + startDay);
        logger.info("endDay:" + endDay);

        if (endDay < startDay) {
            return endDay + 7 - startDay;
        }
        return endDay - startDay;
    }

    private static List<Integer> convertIssueTime(char[] chars) {
        List<Integer> list = new ArrayList<>();
        String issueNum = null;
        for (int n = 0; n < chars.length; n++) {
            issueNum = String.valueOf(chars[n]);
            if (map.containsKey(issueNum)) {
                list.add(map.get(issueNum));
            } else {
                logger.error("no contain issueTime:{}", issueNum);
            }
        }

        Collections.sort(list);
        return list;
    }

    /**
     * 获取当天是星期几
     * @return
     */
    private static int getWeekday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        //当前周几
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        //dayOfWeek = 7;

        logger.info("今天周" + dayOfWeek);
        return dayOfWeek;
    }

    private static void idxList() {
        List<Integer> idx = new ArrayList<>();
        idx.add(1);
        idx.add(4);
        idx.add(2);
        logger.info("idx:" + idx);
        idx.add(4);
        logger.info("idx:" + idx);
    }

    private static void initMap() {
        map.put("一", 1);
        map.put("二", 2);
        map.put("三", 3);
        map.put("四", 4);
        map.put("五", 5);
        map.put("六", 6);
        map.put("日", 7);
    }

    @Deprecated
    private static int getDays(String issueTime) {

        if (StringUtils.isBlank(issueTime)) {
            logger.error("issueTime isBlank:{},default return 0", issueTime);
            return 0;
        }

        int dayOfWeek = getWeekday();
        int startDay = 0;
        int endDay = 0;

        char[] chars = issueTime.toCharArray();
        List<Integer> weekDays = convertIssueTime(chars);
        if (weekDays == null || weekDays.isEmpty()) {
            logger.error("weekDays isEmpty :{}", issueTime);
            return 0;
        }

        int length = chars.length;
        if (length == 1) {
            return 7;
        } else if (length == 2) {

            int f0 = weekDays.get(0);
            int f1 = weekDays.get(1);

            if (dayOfWeek < f0 || dayOfWeek >= f1) {
                startDay = f0;
                endDay = f1;
            } else {
                startDay = f1;
                endDay = f0;
            }
            if (startDay < endDay) {
                return startDay + 8 - endDay;
            } else {
                return startDay - endDay - 1;
            }
        } else {
            // >=2

            int f0 = weekDays.get(0);
            int f1 = weekDays.get(1);

            if (dayOfWeek < f0 || dayOfWeek > f1) {
                startDay = f0;
                endDay = f1;
            } else if (dayOfWeek == f1) {
                startDay = weekDays.get(2);
                ;
                endDay = f0;
            } else if (dayOfWeek == f0) {
                startDay = f1;
                endDay = weekDays.get(2);
            } else if (chars[length - 1] == dayOfWeek) {
                startDay = weekDays.get(length);
                endDay = f0;
            } else {
                //中间
                for (int n = 0; n < length; n++) {
                    int tem = weekDays.get(n);
                    if (dayOfWeek == tem) {
                        startDay = weekDays.get(n + 1);
                        endDay = weekDays.get(n + 2);
                        break;
                    } else if (dayOfWeek < tem) {
                        startDay = tem;
                        endDay = weekDays.get(n + 1);
                    }
                }
            }
            if (startDay < endDay) {
                return startDay + 8 - endDay;
            } else {
                return startDay - endDay - 1;
            }
        }
    }


    public static int getIssueDay(int roadDay, String issueTime, int dayWeek) {
        return getIssueDay(issueTime, roadDay, dayWeek);
    }

    public static int getIssueDayV2(Date date,int onRoadDays,String issueTime) {
        return getIssueDayV2(onRoadDays,issueTime,getWeek(onRoadDays,date));
    }

    private static int getWeek(int onRoadDays,Date date) {
        Date targetDate = DateUtil.addDate(date,onRoadDays + 1,DateUtil.DAY); // 目标日期

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(targetDate);

        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // 做一个转换
        if (week <=0) {
            week = 7;
        }
        return week;
    }

    /**
     *
     * @param date 日期
     * @param onRoadDays 在途天数
     * @param issueTime 补货周期
     * @return
     */
    public static int getIssueDayV2(String date,int onRoadDays,String issueTime) {
        Date dateTime = DateUtil.parse(date,DateUtil.DATE_ONLY);

        return getIssueDayV2(dateTime,onRoadDays,issueTime);
    }

    private static int getIssueDayV2(int roadDay, String issueTime, int week) {
        List<Integer> numberList = convertIssueTime(issueTime);

        int firstPeriodNumber = 0;
        int secondPeriodNumber = 0;

        if (CollectionUtils.isEmpty(numberList)){return 0;}

        if (numberList.size() == 1) {
            firstPeriodNumber = numberList.get(0);
        } else {

            int last = numberList.get(0);
            for (int i = 1; i < numberList.size(); i++) {
                int now = numberList.get(i);

                if (week <= last) {
                    firstPeriodNumber = last;
                    break;
                } else if (week > last && week <= now) {
                    firstPeriodNumber = now;
                    break;
                }

                last = now;
            }

            if (firstPeriodNumber == 0) {
                firstPeriodNumber = numberList.get(0);
            }

        }

        int number = firstPeriodNumber;
        secondPeriodNumber = numberList.stream().filter(n -> n > number).findFirst().orElse(0);

        secondPeriodNumber = secondPeriodNumber == 0 ? numberList.get(0) : secondPeriodNumber;

        if (firstPeriodNumber >= secondPeriodNumber) {
            secondPeriodNumber += 7;
        }

        System.out.println("first:" + firstPeriodNumber + ",second:"+secondPeriodNumber);

        // 当前日期～第二个周期的总天数 公式：在途天数+1+（第二个周期-第一个周期）天数 + （第一个周期-目标天数)

        int period = 0;
        if (week > firstPeriodNumber) {
            period = firstPeriodNumber + 7 - week;
        } else {
            period = firstPeriodNumber - week;
        }

        int addDays = roadDay + 1 + (secondPeriodNumber - firstPeriodNumber) + period;

        return addDays;
    }


    public static List<Integer> convertIssueTime(String issueTime) {
        char[] chars = issueTime.toCharArray();
        List<Integer> list = new ArrayList<>();
        for (int i=0;i<chars.length;i++) {
            Integer value = map.get(String.valueOf(chars[i]));
            if (value != null) {
                list.add(value);
            }
        }

        if (CollectionUtils.isEmpty(list)) {
            return list;
        }

        return list.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

    public static List<Integer> convertIssueTimeV2(String issueTime) {
        char[] chars = issueTime.toCharArray();
        List<Integer> list = new ArrayList<>();
        for (int i=0;i<chars.length;i++) {
            Integer value = mapV2.get(String.valueOf(chars[i]));
            if (value != null) {
                list.add(value);
            }
        }

        if (CollectionUtils.isEmpty(list)) {
            return list;
        }

        return list.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

}