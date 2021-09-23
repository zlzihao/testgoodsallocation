package cn.nome.saas.sdc.manager;

import cn.nome.saas.sdc.model.req.RegionsReq;
import cn.nome.saas.sdc.model.vo.RegionsVO;
import cn.nome.saas.sdc.service.RegionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/10/12 14:02
 */
@Component
public class RegionsServiceManager {

    private RegionsService regionsService;

    @Autowired
    public RegionsServiceManager(RegionsService regionsService) {
        this.regionsService = regionsService;
    }

    public List<RegionsVO> getList(RegionsReq req) {

        return regionsService.getList(req);
    }

}
