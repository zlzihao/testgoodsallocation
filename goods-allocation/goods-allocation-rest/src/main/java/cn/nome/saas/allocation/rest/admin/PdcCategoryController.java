package cn.nome.saas.allocation.rest.admin;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.feign.model.CategoriesVO;
import cn.nome.saas.allocation.manager.PdcManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lizihao@nome.com
 */
@RestController
@RequestMapping("/allocation/pdc")
public class PdcCategoryController {

    private final Logger logger = LoggerFactory.getLogger(PdcCategoryController.class);

    private final PdcManager pdcManager;

    @Autowired
    public PdcCategoryController(PdcManager pdcManager) {
        this.pdcManager = pdcManager;
    }

    @RequestMapping(value = "/category/getBigCategory", method = RequestMethod.GET)
    public Result<List<CategoriesVO>> getBigCategory() {
        return ResultUtil.handleSuccessReturn(pdcManager.getBigCategory());
    }

    @RequestMapping(value = "/category/getMidCategory", method = RequestMethod.GET)
    public Result<List<CategoriesVO>> getMidCategory() {
        return ResultUtil.handleSuccessReturn(pdcManager.getMidCategory());
    }

    @RequestMapping(value = "/category/getSmallCategory", method = RequestMethod.GET)
    public Result<List<CategoriesVO>> getSmallCategory() {
        return ResultUtil.handleSuccessReturn(pdcManager.getSmallCategory());
    }
}
