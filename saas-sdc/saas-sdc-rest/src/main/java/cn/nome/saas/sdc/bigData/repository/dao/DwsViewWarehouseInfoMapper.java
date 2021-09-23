package cn.nome.saas.sdc.bigData.repository.dao;

import cn.nome.saas.sdc.bigData.repository.entity.DwsViewWarehouseInfoDO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author hejiongyu@nome.com
 */
@Component
public interface DwsViewWarehouseInfoMapper {

    List<DwsViewWarehouseInfoDO> list();

}
