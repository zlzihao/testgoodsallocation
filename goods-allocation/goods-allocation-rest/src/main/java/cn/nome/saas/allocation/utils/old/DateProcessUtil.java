package cn.nome.saas.allocation.utils.old;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 日期处理工具
 * 
 * @author yangxiaoxiong
 *
 */
public class DateProcessUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(DateProcessUtil.class);

	/**
	 * 获取环比开始日期
	 * 
	 * @param beginDate
	 * @return
	 */
	public static String getLinkRatioBeginDate(String beginDate) {
		Date b = DateUtil.parse(beginDate, DateUtil.DATE_ONLY);
		Date linkEndDate = DateUtil.addDate(b, -1, DateUtil.DAY);
		return DateUtil.format(linkEndDate, DateUtil.DATE_ONLY);
	}

	/**
	 * 获取环比结束日期
	 * 
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public static String getLinkRatioEndDate(String beginDate, String endDate) {
		Date b = DateUtil.parse(beginDate, DateUtil.DATE_ONLY);
		Date e = DateUtil.parse(endDate, DateUtil.DATE_ONLY);
		int diff = DateUtil.differentDays(b, e);
		LoggerUtil.debug(LOGGER, "{0}，{1}相差天数：{2}", beginDate, endDate, diff);
		Date linkBeginDate = DateUtil.addDate(b, -diff - 1, DateUtil.DAY);
		return DateUtil.format(linkBeginDate, DateUtil.DATE_ONLY);
	}

	public static int getMinute(Date date1) {
		Date date2 = new Date();
		long diff = date2.getTime() - date1.getTime();
		int minute = (int) diff / 1000 / 60;
		if (minute < 1) {
			return 1;
		}
		return minute;
	}
}
