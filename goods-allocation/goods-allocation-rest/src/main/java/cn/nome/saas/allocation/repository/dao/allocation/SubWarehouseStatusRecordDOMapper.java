package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.SubWarehouseConfigDO;
import cn.nome.saas.allocation.repository.entity.allocation.SubWarehouseStatusRecordDO;

import java.util.List;
import java.util.Map;

/**
 * SubWarehouseStatusRecordDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/7/19
 */
public interface SubWarehouseStatusRecordDOMapper {

    /**
     * 根据参数获取SubWarehouseStatusRecordDO列表
     * @param param
     * @return 1
     */
    List<SubWarehouseStatusRecordDO> selectByPage(Map<String, Object> param);

}
