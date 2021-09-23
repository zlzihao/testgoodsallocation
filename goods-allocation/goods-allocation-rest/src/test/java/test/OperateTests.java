package test;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.allocation.model.form.ShopToStockOperateInsertForm;
import cn.nome.saas.allocation.service.allocation.ShopOperateService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author lizihao@nome.com
 */
public class OperateTests extends ApplicationTests {
    private final static Logger logger = LoggerFactory.getLogger(OperateTests.class);

    @Autowired
    private ShopOperateService operateService;


    @Test
    public void getList() {
        operateService.getList(new Page());
    }


    @Test
    public void save(@RequestParam("form") ShopToStockOperateInsertForm forms) {
        operateService.save(forms);
    }
}
