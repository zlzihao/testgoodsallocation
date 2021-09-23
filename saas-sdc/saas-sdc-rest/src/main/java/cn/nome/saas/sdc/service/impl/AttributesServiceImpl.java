package cn.nome.saas.sdc.service.impl;

import cn.nome.platform.common.mybatis.rw.annotation.Master;
import cn.nome.platform.common.mybatis.rw.annotation.Slave;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.AttributesReq;
import cn.nome.saas.sdc.model.vo.AttributesVO;
import cn.nome.saas.sdc.repository.dao.AttributesMapper;
import cn.nome.saas.sdc.repository.entity.AttributesDO;
import cn.nome.saas.sdc.service.AttributesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@Service
public class AttributesServiceImpl implements AttributesService {

    private AttributesMapper attributesMapper;

    @Autowired
    public AttributesServiceImpl(AttributesMapper attributesMapper) {
        this.attributesMapper = attributesMapper;
    }

    @Master
    @Override
    @Transactional
    public int deleteByPrimaryKey(Integer id) {
        return attributesMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public void insertSelective(AttributesDO record) {
        attributesMapper.insertSelective(record);
    }

    @Master
    @Override
    @Transactional
    public void update(AttributesDO record) {
        attributesMapper.update(record);
    }

    @Slave
    @Override
    public List<AttributesVO> search(AttributesReq req, Page page) {
        if (page != null) {
            Integer count = attributesMapper.pageCount(req);
            page.setTotalRecord(count);
        }
        List<AttributesDO> listDO = attributesMapper.search(req, page);

        return BaseConvertor.convertList(listDO, AttributesVO.class);
    }

    @Slave
    @Override
    public AttributesVO selectByPrimaryKey(Integer id) {
        AttributesDO record = attributesMapper.selectByPrimaryKey(id);
        return BaseConvertor.convert(record, AttributesVO.class);
    }

    @Slave
    @Override
    public AttributesVO nameExist(AttributesReq req) {
        AttributesDO record = attributesMapper.nameExist(req);

        return BaseConvertor.convert(record, AttributesVO.class);
    }
}
