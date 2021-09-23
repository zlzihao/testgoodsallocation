package test;

import cn.nome.saas.allocation.model.old.allocation.Area;
import cn.nome.saas.allocation.model.old.allocation.Dictionary;
import cn.nome.saas.allocation.model.old.issue.OrderListReq;
import cn.nome.saas.allocation.model.old.issue.OrderListWrap;
import cn.nome.saas.allocation.service.allocation.NewIssueMatchService;
import cn.nome.saas.allocation.service.old.allocation.IssueRestService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewIssueRestTest extends ApplicationTests {
	private static Logger logger = LoggerFactory.getLogger(NewIssueRestTest.class);

	private int taskId = 1;
	private String shopId = "NM000068";

	@Autowired
	NewIssueMatchService newIssueMatchService;

	@Test
	public void testBase() {
//		Map<Integer, Map<String, Set<String>>>  map = newIssueMatchService.getMap();
	}

}
