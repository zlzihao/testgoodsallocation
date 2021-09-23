package cn.nome.saas.allocation.configuration.old;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.saas.allocation.constant.old.Message;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringToDateConverter implements Converter<String, Date> {
	private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
	private static final String shortDateFormat = "yyyy-MM-dd";
	private static final String dateFormat2 = "yyyy/MM/dd HH:mm:ss";
	private static final String shortDateFormat2 = "yyyy/MM/dd";

	@Override
	public Date convert(String source) {
		if (StringUtils.isBlank(source)) {
			return null;
		}
		source = source.trim();
		try {
			SimpleDateFormat formatter;
			if (source.contains("-")) {
				if (source.contains(":")) {
					formatter = new SimpleDateFormat(dateFormat);
				} else {
					formatter = new SimpleDateFormat(shortDateFormat);
				}
				Date dtDate = formatter.parse(source);
				return dtDate;
			} else if (source.contains("/")) {
				if (source.contains(":")) {
					formatter = new SimpleDateFormat(dateFormat2);
				} else {
					formatter = new SimpleDateFormat(shortDateFormat2);
				}
				Date dtDate = formatter.parse(source);
				return dtDate;
			}
		} catch (Exception e) {
			throw new BusinessException(Message.COMMON0002);
		}

		throw new BusinessException(Message.COMMON0002);

	}
}