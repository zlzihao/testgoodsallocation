package cn.nome.saas.sdc.service.impl;

import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.saas.sdc.model.vo.ShopMappingPositionVO;
import cn.nome.saas.sdc.repository.dao.ShopMappingPositionMapper;
import cn.nome.saas.sdc.repository.entity.ShopMappingPositionDO;
import cn.nome.saas.sdc.service.ShopMappingPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author lizihao@nome.com
 */

@Service
public class ShopMappingPositionServiceImpl implements ShopMappingPositionService {

    private final ShopMappingPositionMapper shopMappingPositionMapper;

    @Autowired
    public ShopMappingPositionServiceImpl(ShopMappingPositionMapper shopMappingPositionMapper) {
        this.shopMappingPositionMapper = shopMappingPositionMapper;
    }

    @Override
    @Transactional
    public String batchInsert(List<ShopMappingPositionVO> list) {
        if (CollectionUtils.isEmpty(list)) return "传参为null";
        List<ShopMappingPositionDO> doList = BaseConvertor.convertList(list, ShopMappingPositionDO.class);
        doList.forEach(vos -> {
            vos.setCreateTime(new Date());
            vos.setIsDeleted(0);
        });
        if (shopMappingPositionMapper.batchInsert(doList) < 0) {
            return "新增记录失败";
        }
        return null;
    }

    @Override
    @Transactional
    public String batchUpdate(List<ShopMappingPositionVO> list) {
        if (CollectionUtils.isEmpty(list)) return "传参为null";
        List<ShopMappingPositionDO> doList = BaseConvertor.convertList(list, ShopMappingPositionDO.class);
        doList.forEach(vos -> {
            vos.setUpdateTime(new Date());
            vos.setIsDeleted(0);
        });
        if (shopMappingPositionMapper.batchUpdate(doList) < 0) {
            return "更新记录失败";
        }
        return null;
    }

    @Override
    public List<ShopMappingPositionVO> selectByCondition(List<ShopMappingPositionVO> list) {
        return BaseConvertor.convertList(shopMappingPositionMapper.selectByCondition(list),
                ShopMappingPositionVO.class);
    }
}
