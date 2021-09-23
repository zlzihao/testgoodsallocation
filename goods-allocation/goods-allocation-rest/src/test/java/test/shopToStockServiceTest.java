package test;

import cn.nome.saas.allocation.model.allocation.ShopToStockVo;
import cn.nome.saas.allocation.service.allocation.ShopToStockService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lizihao@nome.com
 */
public class shopToStockServiceTest extends ApplicationTests {

    private Logger logger = LoggerFactory.getLogger(shopToStockServiceTest.class);


    @Autowired
    private ShopToStockService stockService;

    @Test
    public void selectByConditionTest() {
        String str = "1,2,3,";
        List<String> list = Arrays.asList(str.split(","));

        System.out.println(list);
/*
        ShopToStockReq vo = new ShopToStockReq();
        vo.setShopCode("NM442108J");
        List<String> stringList = new ArrayList<>();
        stringList.add("包");
        stringList.add("服配");
        stringList.add("数码");

        List<String> stringList1 = new ArrayList<>();
        stringList1.add("BTI双肩包");
        stringList1.add("BTI围巾");
        stringList1.add("BTI大耳机");
        vo.setCategoryName(stringList);
        vo.setMidCategoryName(stringList1);
        Page page = new Page();
        List<ShopToStockVo> list = stockService.selectByCondition(vo, page);
        logger.info(JSON.toJSONString(list) + "list value is ");*/
    }

    @Test
    public void selectParam() {
    }

    @Test
    public void update() {
        List<ShopToStockVo> list = new ArrayList<>();
        ShopToStockVo vo = new ShopToStockVo();
        vo.setShopCode("NM442108J");
        vo.setMidCategoryName("中类1");
        vo.setNewStockNum(new BigDecimal(12));
        vo.setOldStockNum(new BigDecimal(0));
        vo.setShopName("广东广州海珠琶洲保利广场店");
        vo.setStatus(1);
        list.add(vo);
        ShopToStockVo vo1 = new ShopToStockVo();
        vo1.setShopCode("NM442108J");
        vo1.setMidCategoryName("毛巾类");
        vo1.setNewStockNum(new BigDecimal(2));
        vo1.setOldStockNum(new BigDecimal(0));
        vo1.setShopName("广东广州海珠琶洲保利广场店");
        vo1.setStatus(1);
        list.add(vo1);
        stockService.batchUpdate(list);
    }

    @Test
    public void testUpdate() {
        stockService.update("8ec25077-0fb0-4b28-b97b-d00b2528d979", "NM00632");

    }

    @Test
    public void inst() {
        List<ShopToStockVo> list = new ArrayList<>();
        ShopToStockVo vo = new ShopToStockVo();
        vo.setShopCode("222");
        vo.setOldStockNum(new BigDecimal("0"));
        vo.setNewStockNum(new BigDecimal("1.2"));
        list.add(vo);
        stockService.batchUpdate(list);

    }

   /* @Test
    public void export() throws UnsupportedEncodingException {
        ShopToStockReq req = new ShopToStockReq();
        String shopName = "%E9%BB%91%E9%BE%99%E6%B1%9F%E4%BD%B3%E6%9C%A8%E6%96%AF%E5%89%8D%E8%BF%9B%E5%A4%A7%E5%95%86%E6%96%B0%E7%8E%9B%E7%89%B9%E5%BA%97";
        String categoryName = "%E5%AD%A3%E8%8A%82%E5%93%81,%E5%86%85%E8%A1%A3";
        String midCategoryName = "%E5%85%B6%E4%BB%96%E5%AD%A3%E8%8A%82%E9%85%8D%E4%BB%B6,%E5%B8%BD%E5%AD%90";
        if (!StringUtils.isEmpty(shopName)) {
            req.setShopNameReq(Arrays.asList(URLDecoder.decode(shopName).split(",")));
        }
        if (!StringUtils.isEmpty(categoryName)) {
            req.setCategoryName(Arrays.asList(URLDecoder.decode(categoryName).split(",")));
        }
        if (!StringUtils.isEmpty(midCategoryName)) {
            req.setMidCategoryName(Arrays.asList(URLDecoder.decode(midCategoryName).split(",")));
        }
        req.setStatus(Constant.WAREHOUSE_APPLY_STATUS_OPEN);
        req.setIsNewStatus(Constant.WAREHOUSE_APPLY_STATUS_OPEN);
        Page page = new Page();
        List<ShopToStockVo> list= stockService.selectByCondition(req, page);
        logger.info(list.size()+"--------------");
    }*/
}
