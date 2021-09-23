package test;

import cn.nome.saas.allocation.model.old.allocation.Area;
import cn.nome.saas.allocation.model.old.allocation.Dictionary;
import cn.nome.saas.allocation.model.old.issue.OrderListReq;
import cn.nome.saas.allocation.model.old.issue.OrderListWrap;
import cn.nome.saas.allocation.service.old.allocation.IssueRestService;
import org.bouncycastle.cert.ocsp.Req;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class IssueRestTest extends ApplicationTests {
	private static Logger logger = LoggerFactory.getLogger(IssueRestTest.class);

	private int taskId = 1;
	private String shopId = "NM000068";

	@Autowired
	IssueRestService issueRestService;

	@Test
	public void testBase() {
		List<String> list = issueRestService.getRegioneBusNameList();
		List<String> list2 = issueRestService.getSubRegioneBusNameList("华北");
		List<Area> list3 = issueRestService.getCityList("北九区");
		List<Dictionary> list4 = issueRestService.getDictionaryList();
		System.out.println(list);
		System.out.println(list2);
		System.out.println(list3);
		System.out.println(list4);
	}

	@Test
	public void orderList() {
		OrderListReq reqParam = new OrderListReq();
		reqParam.setCurPage(1);
		reqParam.setTaskId(2);
		reqParam.setPageSize(10);
		OrderListWrap wrap = issueRestService.getOrderList(reqParam);
		logger.info("---->" + wrap.toString());
	}

	@Test
	public void categoryList() {
		List<String> cas = issueRestService.categoryList(taskId, shopId);
		logger.info("->"+cas);
	}

	@Test
	public void midCategoryList() {
		List<String> cas = issueRestService.midCategoryList(taskId, shopId,"家居");
		logger.info("->"+cas);
	}

	@Test
	public void smallCategoryList() {
		List<String> cas = issueRestService.smallCategoryList(taskId, shopId,"卫浴用品");
		logger.info("->"+cas);
	}

}
