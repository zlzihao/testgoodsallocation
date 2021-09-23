package test;


import cn.nome.platform.common.utils.DateUtils;
import cn.nome.saas.allocation.constant.old.Constant;
import cn.nome.saas.allocation.model.old.allocation.IssueTask;
import cn.nome.saas.allocation.model.old.allocation.ShopInfoDo;
import cn.nome.saas.allocation.model.old.allocation.Stock;
import cn.nome.saas.allocation.model.old.issue.IssueDetailDistStock;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueDOMapper2;
import cn.nome.saas.allocation.service.old.allocation.IssueService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class IssueTest extends ApplicationTests {
    private static Logger logger = LoggerFactory.getLogger(IssueTest.class);

    @Autowired
    IssueService issueService;

    @Autowired
    IssueDOMapper2 issueDOMapper;

    @Test
    public void testIssueInStock() {
        IssueTask task = new IssueTask();
        task.setId(3);
        issueService.issueInStock(task, 1, 1, new HashMap<>(), 0);
    }

    @Test
    public void testIssueOutStock() {
        IssueTask task = new IssueTask();
        task.setId(118);
        issueService.issueOutStock(task);
    }

    @Test
    public void testProcessEnoughStock() {
        IssueTask task = new IssueTask();
        task.setId(2);
        issueService.processEnoughStock(task);
    }

    @Test
    public void testProcessNotEnoughStock() {
        IssueTask task = new IssueTask();
        task.setId(2);
        issueService.processNotEnoughStock(task);
    }

    @Test
    public void testMidMidCategoryQty() {
        issueDOMapper.midMidCategoryQty(118, null, Constant.TAB_GOODS_INFO);
    }

    @Test
    public void testAddNeedSkuStock() {
        List<String> list = issueDOMapper.getShopIdList();
        for (String id : list) {
            issueDOMapper.addNeedSkuStock(118, id, Constant.TAB_GOODS_INFO);
        }
    }

    @Test
    public void testIssueInNewSkcStock() {
        IssueTask task = new IssueTask();
        task.setId(1);
        issueService.issueInNewSkcStock(task, new HashMap<>(), 0);
    }

    @Test
    public void testAddTask() {
        IssueTask task = new IssueTask();
        task.setId(319);
        issueService.issueProcess(task);
    }

    @Test
    public void testGoods() {
        Map<String, Stock> map = issueService.getGoodsInfo();
        System.out.println(map.keySet());
    }

    @Test
    public void testIssueProcess() {
        IssueTask task = new IssueTask();
        Date now = new Date();
        task.setName(DateUtils.toString(now, "yyyy-MM-dd HH:mm:ss"));
        task.setRunTime(now);
        issueDOMapper.addTask(task);

        System.out.println(task);
    }

//    @Test
//    public void testProhibited() {
//        IssueTask task = new IssueTask();
//        task.setId(119);
//        issueService.prohibitedGoods(task);
//    }

    @Test
    public void testIssueUndoProcess() {
        List<String> shopIds = new ArrayList<>();

        //全部门店
//        List<ShopInfoDo> shopInfos = issueDOMapper.getShops();
//        shopInfos.stream().forEach(ShopInfoDo -> shopIds.add(ShopInfoDo.getShopID()));
        //指定门店
        shopIds.add("NM000068");
        shopIds.add("NM000076");
        shopIds.add("NM000193");
        issueService.processIssueUndo(3, shopIds);
    }

    @Test
    public void testShops() {
        List<ShopInfoDo> shopInfos = issueDOMapper.shops();
        Collections.sort(shopInfos, new Comparator<ShopInfoDo>() {
            @Override
            public int compare(ShopInfoDo o1, ShopInfoDo o2) {
                return o1.getID() - o2.getID();
            }
        });
        for (ShopInfoDo shopInfoDo : shopInfos) {
            logger.info(shopInfoDo.getID() + "|" + shopInfoDo.getShopID());
        }
    }

    @Test
    public void testIssueGoodsData() {

        int rst = 0;
        int taskId = 2;
        String shopId = "NM000068";
        List<String> shopIds = new ArrayList<>();
        shopIds.add(shopId);
//		rst = issueService.insertGoodsData(taskId, shopId);
//		logger.info("rst:{}",rst);
        rst = issueService.processIssueGoodsData(taskId, shopIds);
        logger.info("==================== over:{}", rst);
    }

    @Test
    public void testIssueCategorySkcData() {

        int rst = 0;
        int taskId = 2;
        String shopId = "NM000068";
        List<String> shopIds = new ArrayList<>();
        shopIds.add(shopId);
        rst = issueService.processCategorySkcCount(taskId, shopIds);
        logger.info("==================== over:{}", rst);
    }

    @Test
    public void processRemainStock() {
        int taskId = 2;
        Integer rst = issueService.processRemainStock(taskId);
        logger.info("==================== over:{}", rst);
    }

    @Test
    public void updateIssueDays() {
        issueService.updateIssueDays();
        logger.info("==================== over:{}");
    }
}
