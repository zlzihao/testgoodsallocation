package cn.nome.saas.sdc.service.impl;

import cn.nome.platform.common.mybatis.rw.annotation.Master;
import cn.nome.platform.common.mybatis.rw.annotation.Slave;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.saas.sdc.model.req.AttributeValuesReq;
import cn.nome.saas.sdc.model.vo.AttributeValuesVO;
import cn.nome.saas.sdc.repository.dao.AttributeValuesMapper;
import cn.nome.saas.sdc.repository.entity.AttributeValuesDO;
import cn.nome.saas.sdc.service.AttributeValuesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@Service
public class AttributeValuesServiceImpl implements AttributeValuesService {

    private AttributeValuesMapper attributeValuesMapper;

    @Autowired
    public AttributeValuesServiceImpl(AttributeValuesMapper attributeValuesMapper) {
        this.attributeValuesMapper = attributeValuesMapper;
    }

    @Master
    @Override
    @Transactional
    public int deleteByPrimaryKey(Integer id) {
        return attributeValuesMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public void insertSelective(AttributeValuesDO record) {
        attributeValuesMapper.insertSelective(record);
    }

    @Master
    @Override
    @Transactional
    public int update(AttributeValuesDO record) {
        return attributeValuesMapper.update(record);
    }

    @Slave
    @Override
    public List<AttributeValuesVO> search(AttributeValuesReq req) {
        List<AttributeValuesDO> listDO = attributeValuesMapper.search(req);

        return BaseConvertor.convertList(listDO, AttributeValuesVO.class);
    }

    @Slave
    @Override
    public AttributeValuesVO selectByPrimaryKey(Integer id) {
        AttributeValuesDO record = attributeValuesMapper.selectByPrimaryKey(id);
        return BaseConvertor.convert(record, AttributeValuesVO.class);
    }

    @Slave
    @Override
    public AttributeValuesVO nameExist(AttributeValuesReq req) {
        AttributeValuesDO record = attributeValuesMapper.nameExist(req);
        return BaseConvertor.convert(record, AttributeValuesVO.class);
    }
}
