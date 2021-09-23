package test;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.AllocationBootstrap;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AllocationBootstrap.class)
@WebAppConfiguration
public class ApplicationTests {
	private static Logger logger = LoggerFactory.getLogger(ApplicationTests.class);
	public static final int CORPID = 2;
	public static final int APPID = 12;

	@Before
	public void init() {
		LoggerUtil.info(logger, "测试开始--------------------------------");
	}

	@After
	public void after() {
		LoggerUtil.info(logger, "测试结束--------------------------------");
	}
}