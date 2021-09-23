package cn.nome.saas.sdc.rest.admin;

import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.web.controller.Table;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.bigData.model.DwsViewWarehouseInfoVO;
import cn.nome.saas.sdc.model.req.WarehouseReq;
import cn.nome.saas.sdc.model.vo.WarehouseVO;
import cn.nome.saas.sdc.rest.BaseController;
import cn.nome.saas.sdc.service.WarehouseConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author lizihao@nome.com
 */
@RequestMapping(value = "/admin")
@RestController
public class WareHouseController extends BaseController {
    private final WarehouseConfigService service;

    @Autowired
    public WareHouseController(WarehouseConfigService service) {
        this.service = service;
    }

    @RequestMapping(value = "/wareHouse/all", method = RequestMethod.GET)
    public Result<Table<WarehouseVO>> getPageList(WarehouseReq req, @Valid Page page) {

        return ResultUtil.handleSuccessReturn(service.getPageList(req, page), page);
    }

    @RequestMapping(value = "/wareHouse/exportExcel", method = RequestMethod.POST)
    public Result<?> exportExcel(HttpServletResponse response, @Valid @RequestParam("userCode") Integer userCode) {
        service.exportExcel(response, userCode);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/wareHouse/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletResponse response,
                                 @Valid @RequestPart @NotNull(message = "上传文件不能为空") MultipartFile file,
                                 @Valid @RequestParam("userCode") Integer userCode) {
        return ResultUtil.handleSuccessReturn(service.importExcel(response, file));
    }


    @RequestMapping(value = "/wareHouse/delete", method = RequestMethod.DELETE)
    public Result<?> delete(@Valid @RequestParam("id") Long id, @Valid @RequestParam("userCode") Integer userCode) {
        service.delete(id, userCode);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/wareHouse/allWareHouse", method = RequestMethod.GET)
    public Result<List<DwsViewWarehouseInfoVO>> allWareHouse() {
        return ResultUtil.handleSuccessReturn(service.getAllWareHouse());
    }
}
