package cn.nome.saas.sdc.service;

import cn.nome.saas.sdc.model.vo.ShopMappingPositionVO;

import java.util.List;

/**
 * @author lizihao@nome.com
 */
public interface ShopMappingPositionService {
    public String batchInsert(List<ShopMappingPositionVO> list);

    public String batchUpdate(List<ShopMappingPositionVO> list);

    List<ShopMappingPositionVO> selectByCondition(List<ShopMappingPositionVO> list);
}
