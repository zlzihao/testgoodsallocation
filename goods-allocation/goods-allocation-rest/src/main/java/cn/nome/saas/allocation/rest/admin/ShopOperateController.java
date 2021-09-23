package cn.nome.saas.allocation.rest.admin;

import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.web.controller.Table;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.model.allocation.ShopToStockExportVO;
import cn.nome.saas.allocation.model.allocation.ShopToStockVo;
import cn.nome.saas.allocation.model.form.ShopOperateDeletedForm;
import cn.nome.saas.allocation.model.form.ShopToStockOperateInsertForm;
import cn.nome.saas.allocation.model.vo.ShopOperateVO;
import cn.nome.saas.allocation.repository.entity.allocation.ShopToStockDo;
import cn.nome.saas.allocation.service.allocation.ShopOperateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author lizihao@nome.com
 */
@RestController
@RequestMapping("/allocation/operate")
public class ShopOperateController {
    @Autowired
    ShopOperateService operateService;

    /*
     * @describe 运营仓位报表
     * */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<Table<ShopOperateVO>> operationReport(Page page) {
        return ResultUtil.handleSuccessReturn(operateService.getList(page), page);
    }

    /*
     *@describe  保存未提交
     * */

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Result<?> save(@RequestBody ShopToStockOperateInsertForm forms) {
        operateService.save(forms);
        return ResultUtil.handleSuccessReturn();
    }

    /*
     *@describe  保存并提交
     * */

    @RequestMapping(value = "/commit", method = RequestMethod.POST)
    public Result<?> commit(@RequestBody ShopToStockOperateInsertForm forms) {
        return ResultUtil.handleSuccessReturn(operateService.commit(forms));
    }

    /*
     * @describe  运营调仓报表删除
     * */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result<?> delete(@RequestBody ShopOperateDeletedForm form) {
        operateService.delete(form.getIds());
        return ResultUtil.handleSuccessReturn();
    }


    /*
     * @describe 获取详情
     * */
    @RequestMapping(value = "/getDetail", method = RequestMethod.GET)


    public Result<Table<ShopToStockVo>> getDetail(@RequestParam("id") Integer id, Page page) {
        return ResultUtil.handleSuccessReturn(operateService.selectByOperate(id, page), page);
    }

    /*
     * @describe  运营调仓详情删除
     * */
    @RequestMapping(value = "/detailDelete", method = RequestMethod.POST)
    public Result<?> detailDelete(@RequestBody ShopOperateDeletedForm form) {
        operateService.detailDelete(form.getIds());
        return ResultUtil.handleSuccessReturn();
    }

    /*
     * @describe 根据门店 和陈列中类查询仓位数
     * */
    @RequestMapping(value = "/getOldNum", method = RequestMethod.GET)
    public Result<List<ShopToStockDo>> getOldNum(@RequestParam("shopCode") String shopCode,
                                                 @RequestParam("midCategoryIds") List<Integer> midCategoryIds) {
        return ResultUtil.handleSuccessReturn(operateService.getOldNum(shopCode, midCategoryIds));
    }

    /*
     * @describe  导出模板
     * */
    @RequestMapping(value = "/exportMode", method = RequestMethod.GET)
    public Result<?> exportMode(HttpServletResponse response) {
        operateService.exportMode(response);
        return ResultUtil.handleSuccessReturn();

    }

    /*
     *@describe 导入运营调仓
     * */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<List<ShopToStockExportVO>> importExcel(@RequestPart MultipartFile file) {
        return ResultUtil.handleSuccessReturn(operateService.importExcel(file));
    }

    /*
     *@describe 初始化sdc shop_mapping_position数据
     * */
    @RequestMapping(value = "/init", method = RequestMethod.POST)
    public Result<?> initMappingPosition() {
        operateService.init();
        return ResultUtil.handleSuccessReturn();
    }
}
