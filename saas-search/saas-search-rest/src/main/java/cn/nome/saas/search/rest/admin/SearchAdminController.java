package cn.nome.saas.search.rest.admin;

import cn.nome.platform.common.web.controller.BaseController;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.search.manager.NmSearchManager;
import cn.nome.saas.search.model.SearchWordConfAddModel;
import cn.nome.saas.search.model.SearchWordConfListModel;
import cn.nome.saas.search.model.SearchWordConfModifyModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author chentaikuang
 */
@Api(description = "后台-搜索配置接口控制器")
@RestController
@RequestMapping("/admin/{appId}")
public class SearchAdminController extends BaseController {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NmSearchManager nmSearchManager;

    /**
     * visit url：http://localhost:8330/admin/666/search/test
     *
     * @param datas
     * @return
     */
    @ApiOperation("测试方法")
    @RequestMapping(value = "/search/test", method = RequestMethod.GET)
    public Result<String> test(String datas) {
        return ResultUtil.handleSuccessReturn(datas);
    }

    @ApiOperation("自定义词典加载接口")
    @RequestMapping(value = "/remoteDict/load", method = RequestMethod.GET)
    public Result load() {
        return nmSearchManager.loadRemoteDictWrap();
    }

    @ApiOperation("自定义词典更新接口")
    @RequestMapping(value = "/remoteDict/update", method = RequestMethod.POST)
    public Result update(@RequestBody @NotNull String words) {
        return nmSearchManager.update(words);
    }

    @ApiOperation(value = "新增单词配置接口")
    @RequestMapping(value = "/wordConf/add", method = RequestMethod.POST)
    public Result add(@Valid @RequestBody SearchWordConfAddModel model) {
        return nmSearchManager.addWordConf(model);
    }

    @ApiOperation(value = "获取单词配置接口")
    @RequestMapping(value = "/wordConf/getDetail", method = RequestMethod.GET)
    public Result getDetail(@NotNull @RequestParam("id") Integer id) {
        return nmSearchManager.getWordConf(id);
    }

    @ApiOperation(value = "设置单词配置发布状态接口")
    @RequestMapping(value = "/wordConf/setStatus", method = RequestMethod.POST)
    public Result setStatus(@Min(value = 1) @RequestParam("id") Integer id, @NotNull @RequestParam("status") Integer status) {
        return nmSearchManager.setStatus(id, status);
    }

    @ApiOperation(value = "修改单词配置接口")
    @RequestMapping(value = "/wordConf/modify", method = RequestMethod.POST)
    public Result modify(@Valid @RequestBody SearchWordConfModifyModel model) {
        return nmSearchManager.modifyWordConf(model);
    }

    @ApiOperation(value = "获取单词配置分页列表")
    @RequestMapping(value = "/wordConf/getPageList", method = RequestMethod.POST)
    public Result getPageList(@Valid @RequestBody SearchWordConfListModel model) {
        return nmSearchManager.validWords(model);
    }
}
