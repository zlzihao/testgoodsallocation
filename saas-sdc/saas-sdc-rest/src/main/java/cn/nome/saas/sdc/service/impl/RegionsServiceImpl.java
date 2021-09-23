package cn.nome.saas.sdc.service.impl;

import cn.nome.platform.common.mybatis.rw.annotation.Slave;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.saas.sdc.model.req.RegionsReq;
import cn.nome.saas.sdc.model.vo.RegionsVO;
import cn.nome.saas.sdc.repository.dao.RegionsMapper;
import cn.nome.saas.sdc.repository.entity.RegionsDO;
import cn.nome.saas.sdc.service.RegionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/10/12 14:02
 */
@Service
public class RegionsServiceImpl implements RegionsService {

    private RegionsMapper regionsMapper;

    @Autowired
    public RegionsServiceImpl(RegionsMapper regionsMapper) {
        this.regionsMapper = regionsMapper;
    }

    @Slave
    @Override
    public List<RegionsVO> getList(RegionsReq req) {
        List<RegionsDO> listDO = regionsMapper.getList(req);

        return BaseConvertor.convertList(listDO, RegionsVO.class);
    }

}
