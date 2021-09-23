package cn.nome.saas.sdc.service.impl;

import cn.nome.platform.common.mybatis.rw.annotation.Master;
import cn.nome.platform.common.mybatis.rw.annotation.Slave;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.FranchiseesReq;
import cn.nome.saas.sdc.model.vo.FranchiseesVO;
import cn.nome.saas.sdc.repository.dao.FranchiseesMapper;
import cn.nome.saas.sdc.repository.entity.FranchiseesDO;
import cn.nome.saas.sdc.service.FranchiseesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/25 13:54
 */
@Service
public class FranchiseesServiceImpl implements FranchiseesService {

    private FranchiseesMapper franchiseesMapper;

    @Autowired
    public FranchiseesServiceImpl(FranchiseesMapper franchiseesMapper) {
        this.franchiseesMapper = franchiseesMapper;
    }

    @Override
    @Transactional
    public int insertSelective(FranchiseesDO record) {
        return franchiseesMapper.insertSelective(record);
    }

    @Master
    @Override
    @Transactional
    public int updateByPrimaryKeySelective(FranchiseesDO record) {
        return franchiseesMapper.updateByPrimaryKeySelective(record);
    }

    @Slave
    @Override
    public List<FranchiseesVO> search(FranchiseesReq req, Page page) {
        if (page != null) {
            Integer count = franchiseesMapper.pageCount(req);
            page.setTotalRecord(count);
        }
        List<FranchiseesDO> listDO = franchiseesMapper.search(req, page);

        return BaseConvertor.convertList(listDO, FranchiseesVO.class);
    }

    @Slave
    @Override
    public FranchiseesVO selectByPrimaryKey(Integer id) {
        FranchiseesDO record = franchiseesMapper.selectByPrimaryKey(id);
        return BaseConvertor.convert(record, FranchiseesVO.class);
    }

}
