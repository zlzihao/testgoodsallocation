package cn.nome.saas.search.rest.user;

import cn.nome.platform.common.web.controller.BaseController;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.saas.search.constant.Constant;
import cn.nome.saas.search.manager.NmSearchManager;
import cn.nome.saas.search.model.LogBaseModel;
import cn.nome.saas.search.model.ProductMatchModel;
import cn.nome.saas.search.model.SearchWordConfListModel;
import cn.nome.saas.search.util.IpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author chentaikuang
 */
@Api(description = "小程序-搜索接口控制器")
@RestController
@RequestMapping("/user/{uid}/search")
public class SearchController extends BaseController {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NmSearchManager nmSearchManager;

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
        Result rtn = nmSearchManager.matchProduct(model, logBaseModel);
        return rtn;
    }

    @ApiOperation(value = "获取单词配置分页列表")
    @RequestMapping(value = "/getPageList", method = RequestMethod.POST)
    public Result getPageList(@Valid @RequestBody SearchWordConfListModel model) {
        return nmSearchManager.validWords(model);
    }

    private void setLogBase(LogBaseModel logBaseModel) {
        logBaseModel.setAppId(getAppId());
        logBaseModel.setUid(getUid());
        logBaseModel.setCorpId(getCorpId());
        logBaseModel.setIp(IpUtil.getReqIp());
        logBaseModel.setSource(Constant.REQ_SOURCE_LP);
    }

}
