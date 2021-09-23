package cn.nome.saas.sdc.service.impl;

import cn.nome.platform.common.mybatis.rw.annotation.Master;
import cn.nome.platform.common.mybatis.rw.annotation.Slave;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.AttributeTypesReq;
import cn.nome.saas.sdc.model.vo.AttributeTypesVO;
import cn.nome.saas.sdc.repository.dao.AttributeTypesMapper;
import cn.nome.saas.sdc.repository.entity.AttributeTypesDO;
import cn.nome.saas.sdc.service.AttributeTypesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 13:55
 */
@Service
public class AttributeTypesServiceImpl implements AttributeTypesService {

    private AttributeTypesMapper attributeTypesMapper;

    @Autowired
    public AttributeTypesServiceImpl(AttributeTypesMapper attributeTypesMapper) {
        this.attributeTypesMapper = attributeTypesMapper;
    }

    @Override
    @Transactional
    public void insertSelective(AttributeTypesDO record) {
        attributeTypesMapper.insertSelective(record);
    }

    @Master
    @Override
    @Transactional
    public void update(AttributeTypesDO record) {
        attributeTypesMapper.update(record);
    }

    @Slave
    @Override
    public List<AttributeTypesVO> search(AttributeTypesReq req, Page page) {
        if (page != null) {
            Integer count = attributeTypesMapper.pageCount(req);
            page.setTotalRecord(count);
        }
        List<AttributeTypesDO> listDO = attributeTypesMapper.search(req, page);

        return BaseConvertor.convertList(listDO, AttributeTypesVO.class);
    }

    @Slave
    @Override
    public AttributeTypesVO selectByPrimaryKey(Integer id) {
        AttributeTypesDO record = attributeTypesMapper.selectByPrimaryKey(id);
        return BaseConvertor.convert(record, AttributeTypesVO.class);
    }

    @Slave
    @Override
    public List<AttributeTypesVO> query(AttributeTypesReq req) {
        List<AttributeTypesDO> listDO = attributeTypesMapper.query(req);

        return BaseConvertor.convertList(listDO, AttributeTypesVO.class);
    }

    @Slave
    @Override
    public AttributeTypesVO nameExist(AttributeTypesReq req) {
        AttributeTypesDO record = attributeTypesMapper.nameExist(req);
        return BaseConvertor.convert(record, AttributeTypesVO.class);
    }
}
