package cn.nome.saas.sdc.rest.admin;

import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.web.controller.Table;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.model.form.SeasonChangeForm;
import cn.nome.saas.sdc.model.req.SeasonChangeReq;
import cn.nome.saas.sdc.model.vo.SeasonChangeVO;
import cn.nome.saas.sdc.rest.BaseController;
import cn.nome.saas.sdc.service.SeasonChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author lizihao@nome.com
 */
@RequestMapping(value = "/admin")
@RestController
public class SeasonChangeController extends BaseController {

    private final SeasonChangeService seasonChangeService;

    @Autowired
    public SeasonChangeController(SeasonChangeService seasonChangeService) {
        this.seasonChangeService = seasonChangeService;
    }

    @RequestMapping(value = "/Season/all", method = RequestMethod.GET)
    public Result<Table<SeasonChangeVO>> getPageList(SeasonChangeReq req, @Valid Page page) {
        return ResultUtil.handleSuccessReturn(seasonChangeService.getPageList(req, page), page);
    }


    @RequestMapping(value = "/Season/exportExcel", method = RequestMethod.POST)
    public Result<?> exportExcel(HttpServletResponse response, @Valid @RequestParam("userCode") Integer userCode) {
        seasonChangeService.exportExcel(response, userCode);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/Season/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletResponse response, @RequestPart MultipartFile file,
                                 @Valid @RequestParam("userCode") Integer userCode) {
        return ResultUtil.handleSuccessReturn(seasonChangeService.importExcel(response, file));
    }


    @RequestMapping(value = "/Season/edit", method = RequestMethod.POST)
    public Result<?> update(@Valid @RequestBody SeasonChangeForm form, @Valid @RequestParam("userCode") Integer userCode) {
        return ResultUtil.handleSuccessReturn(seasonChangeService.update(form, userCode));
    }

    @RequestMapping(value = "/Season/delete", method = RequestMethod.DELETE)
    public Result<?> delete(@Valid @RequestParam("id") long id, @Valid @RequestParam("userCode") Integer userCode) {
        return ResultUtil.handleSuccessReturn(seasonChangeService.deleted(id, userCode));
    }
}
