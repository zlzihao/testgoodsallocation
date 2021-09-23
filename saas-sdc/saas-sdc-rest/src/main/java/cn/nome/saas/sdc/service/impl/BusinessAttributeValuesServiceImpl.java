package cn.nome.saas.sdc.service.impl;

import cn.nome.platform.common.mybatis.rw.annotation.Master;
import cn.nome.platform.common.mybatis.rw.annotation.Slave;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.saas.sdc.model.req.BusinessAttributeValuesReq;
import cn.nome.saas.sdc.model.vo.BusinessAttributeValuesVO;
import cn.nome.saas.sdc.repository.dao.BusinessAttributeValuesMapper;
import cn.nome.saas.sdc.repository.entity.BusinessAttributeValuesDO;
import cn.nome.saas.sdc.service.BusinessAttributeValuesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@Service
public class BusinessAttributeValuesServiceImpl implements BusinessAttributeValuesService {

    private BusinessAttributeValuesMapper businessAttributeValuesMapper;

    @Autowired
    public BusinessAttributeValuesServiceImpl(BusinessAttributeValuesMapper businessAttributeValuesMapper) {
        this.businessAttributeValuesMapper = businessAttributeValuesMapper;
    }

    @Master
    @Override
    @Transactional
    public int deleteByPrimaryKey(Integer id) {
        return businessAttributeValuesMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public int insertSelective(BusinessAttributeValuesDO record) {
        return businessAttributeValuesMapper.insertSelective(record);
    }

    @Master
    @Override
    @Transactional
    public void updateByPrimaryKeySelective(BusinessAttributeValuesDO record) {
        businessAttributeValuesMapper.updateByPrimaryKeySelective(record);
    }

    @Slave
    @Override
    public List<BusinessAttributeValuesVO> query(BusinessAttributeValuesReq req) {
        List<BusinessAttributeValuesDO> listDO = businessAttributeValuesMapper.query(req);

        return BaseConvertor.convertList(listDO, BusinessAttributeValuesVO.class);
    }

    @Slave
    @Override
    public BusinessAttributeValuesVO selectByPrimaryKey(Integer id) {
        BusinessAttributeValuesDO record = businessAttributeValuesMapper.selectByPrimaryKey(id);
        return BaseConvertor.convert(record, BusinessAttributeValuesVO.class);
    }

}
