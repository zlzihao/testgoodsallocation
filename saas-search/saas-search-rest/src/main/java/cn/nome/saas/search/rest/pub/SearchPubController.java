package cn.nome.saas.search.rest.pub;

import cn.nome.platform.common.web.controller.BaseController;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.saas.search.constant.Constant;
import cn.nome.saas.search.manager.NmSearchManager;
import cn.nome.saas.search.model.*;
import cn.nome.saas.search.util.IpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(description = "对外提供查询接口控制器")
@RestController
@RequestMapping("/public/search")
public class SearchPubController extends BaseController {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private NmSearchManager nmSearchManager;


    @ApiOperation("根据关键字模糊查询商品接口")
    @RequestMapping(value = "/fuzzyProduct", method = {RequestMethod.POST})
    public Result fuzzyProduct(@RequestBody @Valid ProductFuzzyModel model) {
        return nmSearchManager.fuzzyProduct(model);
    }

    @ApiOperation("根据关键字全文检索商品接口")
    @RequestMapping(value = "/fullNameProduct", method = {RequestMethod.POST})
    public Result fullNameProduct(@RequestBody @Valid ProductFullNameModel model) {
        return nmSearchManager.fullNameProduct(model);
    }

    @ApiOperation("根据关键字符串分词后检索商品接口")
    @RequestMapping(value = "/matchProduct", method = {RequestMethod.POST})
    public Result matchProduct(@RequestBody @Valid ProductMatchModel model) {
        LogBaseModel logBaseModel = null;
        try {
            logBaseModel = new LogBaseModel();
            setLogBase(logBaseModel);
        } catch (Exception e) {
            LOGGER.error("[matchProduct] err:{}", e.getMessage());
        }
        return nmSearchManager.matchProduct(model, logBaseModel);
    }

    private void setLogBase(LogBaseModel logBaseModel) {
        logBaseModel.setAppId(getAppId());
        logBaseModel.setUid(getUid());
        logBaseModel.setCorpId(getCorpId());
        logBaseModel.setIp(IpUtil.getReqIp());
        logBaseModel.setSource(Constant.REQ_SOURCE_LP);
    }

    @ApiOperation("根据关键字前缀查询商品接口")
    @RequestMapping(value = "/prefixProduct", method = {RequestMethod.POST})
    public Result prefixProduct(@RequestBody @Valid ProductPrefixModel model) {
        return nmSearchManager.prefixProduct(model);
    }

    @ApiOperation("热搜榜")
    @RequestMapping(value = "/hotRank", method = {RequestMethod.GET})
    public Result hot(@RequestParam(value = "daysAgo", defaultValue = "7") int daysAgo,
                      @RequestParam(value = "rankSize", defaultValue = "10") int rankSize) {
        if (daysAgo < 0) {
            daysAgo = 7;
        }
        if (rankSize <= 0) {
            rankSize = 10;
        }
        return nmSearchManager.hotSearch(daysAgo, rankSize);
    }
}
