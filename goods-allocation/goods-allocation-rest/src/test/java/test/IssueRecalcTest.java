package test;

import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.old.allocation.ProhibitedGoods;
import cn.nome.saas.allocation.model.old.allocation.ShopInfoDo;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueDOMapper2;
import cn.nome.saas.allocation.service.allocation.IssueRecalcService;
import cn.nome.saas.allocation.service.allocation.RecalcTaskService;
import cn.nome.saas.allocation.service.old.allocation.IssueRestService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IssueRecalcTest extends ApplicationTests {
	private static Logger logger = LoggerFactory.getLogger(IssueRecalcTest.class);

	@Autowired
	IssueDOMapper2 issueDOMapper2;

	@Autowired
	IssueRecalcService recalcService;

	@Autowired
	IssueRestService issueRestService;

	@Autowired
	RecalcTaskService recalcTaskService;

	private int taskId = 3;
	private String shopId = "NM000068";
	private boolean hasChildShopFlag = false;
	private Map<String, Map<String, ProhibitedGoods>> map = new HashMap<>();

	@PostConstruct
	public void init(){
		map = recalcService.getShopProhibitedGoods(taskId, shopId);
		ShopInfoDo shopInfoDo = issueDOMapper2.getShop(shopId);
		hasChildShopFlag = shopInfoDo.getHaveChild() == 0;

		logger.info("init over----------> m:{},flag:{} ", map.size() ,hasChildShopFlag);
	}

	@Test
	public void getShopProhibitedGoods() {
		Map<String, Map<String, ProhibitedGoods>> map = recalcService.getShopProhibitedGoods(taskId, shopId);
		Iterator<Map.Entry<String, Map<String, ProhibitedGoods>>> itr = map.entrySet().iterator();
		while (itr.hasNext()){
			Map.Entry<String, Map<String, ProhibitedGoods>> nx = itr.next();
			String key = nx.getKey();
			Map<String, ProhibitedGoods> val = nx.getValue();
			logger.info("------>"+key);
			logger.info("------>"+val.toString());
		}
	}

	@Test
	public void recalcIssueInStockBH() {
		recalcService.recalcIssueInStock(taskId,shopId, Constant.CATEGORY_BH,this.map);
	}

	@Test
	public void recalcIssueInStockFZ() {
		recalcService.recalcIssueInStock(taskId,shopId, Constant.CATEGORY_FZ,this.map);
	}

	@Test
	public void recalcIssueInNewSkcStock() {
		recalcService.recalcIssueInNewSkcStock(taskId, shopId, this.map);
	}

	@Test
	public void recalcFreedIssueOutStockRemain() {
		recalcService.recalcFreedIssueOutStockRemain(taskId,shopId);
	}

	@Test
	public void recalcMidCategoryQty() {
		recalcService.recalcIssueMidCategoryQty(taskId, shopId, hasChildShopFlag);
	}

	@Test
	public void recalcIssueNeedStock() {
		recalcService.recalcIssueNeedStock(taskId, shopId, hasChildShopFlag);
	}

	@Test
	public void recalcIssueEnoughStock() {
		recalcService.recalcIssueEnoughStock(taskId, shopId);
	}

	@Test
	public void recalcIssueNotEnoughStock() {
		recalcService.recalcIssueNotEnoughStock(taskId, shopId);
	}

	@Test
	public void recalcIssueUndo() {
		recalcService.recalcIssueUndo(taskId, shopId);
	}

	@Test
	public void recalcIssueGoodsData() {
		recalcService.recalcIssueGoodsData(taskId, shopId);
	}

	@Test
	public void recalcCategorySkcCount() {
		recalcService.recalcCategorySkcCount(taskId, shopId);
	}

	@Test
	public void setInvalidSts() {
		recalcService.setInvalidSts(taskId, shopId);
	}

	@Test
	public void setValidSts() {
		recalcService.setValidSts(taskId, shopId);
	}

	@Test
	public void restStockRemain() {
		recalcService.resetStockRemain(taskId);
	}

	@Test
	public void recalcTaskAdd() {
		recalcTaskService.add(taskId,shopId);
	}

	@Test
	public void recalcDeductIssueOutStockRemain() {
		recalcService.recalcDeductIssueOutStockRemain(taskId,shopId);
	}
}
