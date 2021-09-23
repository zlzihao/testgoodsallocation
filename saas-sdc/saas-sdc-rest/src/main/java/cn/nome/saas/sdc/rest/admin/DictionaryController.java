package cn.nome.saas.sdc.rest.admin;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.manager.DictionaryServiceManager;
import cn.nome.saas.sdc.model.req.QueryDictionaryReq;
import cn.nome.saas.sdc.model.vo.DictionaryVO;
import cn.nome.saas.sdc.model.vo.PureListVO;
import cn.nome.saas.sdc.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/4 13:39
 */
@RestController
@RequestMapping("/admin")
public class DictionaryController extends BaseController {

    private DictionaryServiceManager dictionaryServiceManager;

    @Autowired
    public DictionaryController(DictionaryServiceManager dictionaryServiceManager) {
        this.dictionaryServiceManager = dictionaryServiceManager;
    }

    @RequestMapping(value = "/dictionary/query", method = RequestMethod.GET)
    public Result<?> query(QueryDictionaryReq req) {
        List<DictionaryVO> listVO = dictionaryServiceManager.query(req);
        PureListVO<DictionaryVO> pureListVO = new PureListVO<>();
        pureListVO.setList(listVO);
        return ResultUtil.handleSuccessReturn(pureListVO);
    }

}
