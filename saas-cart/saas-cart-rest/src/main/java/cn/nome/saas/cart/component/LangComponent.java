package cn.nome.saas.cart.component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.nome.platform.common.logger.LoggerUtil;

@Component
public class LangComponent implements Serializable {

	private static final long serialVersionUID = -9011395649667435423L;

	private static final Logger LOGGER = LoggerFactory.getLogger(LangComponent.class);
	private static final String FILE = "Biz_cn.properties";
	/**
	 * 配置文件缓存区
	 */
	private static Properties properties = null;

	static {
		loadProps();
	}

	private static void loadProps() {
		LoggerUtil.info(LOGGER, "开始加载【properties】.......");
		Properties props = new Properties();
		try (InputStream in = LangComponent.class.getClassLoader().getResourceAsStream(FILE);) {
			props.load(new InputStreamReader(in, "UTF-8"));

			properties = props;

		} catch (Exception e) {
			LoggerUtil.error(e, LOGGER, "加载【properties】出错！！");
		}
		LoggerUtil.info(LOGGER, "加载【properties】完成！");
	}

	public String getMessage(String code) {
		return this.getMessage(code, null);
	}

	public String getMessage(String code, String defaultValue) {
		LoggerUtil.debug(LOGGER, "获取code:{0},defaultValue:{1}", code, defaultValue);
		return properties.getProperty(code, defaultValue);
	}

}
