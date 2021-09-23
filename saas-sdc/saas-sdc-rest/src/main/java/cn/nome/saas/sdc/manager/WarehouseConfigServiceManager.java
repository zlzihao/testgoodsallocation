package cn.nome.saas.sdc.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.saas.sdc.model.req.WarehouseReq;
import cn.nome.saas.sdc.model.vo.WarehouseVO;
import cn.nome.saas.sdc.repository.dao.WareHouseConfigMapper;
import cn.nome.saas.sdc.repository.entity.WarehouseDO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lizihao@nome.com
 */
@Service
public class WarehouseConfigServiceManager {

    private final WareHouseConfigMapper warehouseConfigMapper;

    public WarehouseConfigServiceManager(WareHouseConfigMapper warehouseConfigMapper) {
        this.warehouseConfigMapper = warehouseConfigMapper;
    }

    public Map<String, String> getProvinceStockCodeMap() {
        WarehouseReq req = new WarehouseReq();
        List<WarehouseDO> warehouses = warehouseConfigMapper.getPageList(BaseConvertor.convert(req, WarehouseDO.class), null);
        return warehouses.stream().collect(Collectors.toMap(WarehouseDO::getProvince, WarehouseDO::getWarehouseCode));
    }

    public void save(WarehouseVO vo) {
        WarehouseDO convertDO = new WarehouseDO();
        convertDO.setProvince(vo.getProvince());
        List<WarehouseDO> list = warehouseConfigMapper.getByProvinceAndWareHouse(convertDO);
        if (CollectionUtil.isEmpty(list)) {
            //插入
            warehouseConfigMapper.insert(BaseConvertor.convert(vo, WarehouseDO.class));
            return;
        }
        //更新
        warehouseConfigMapper.updateWareHouse((BaseConvertor.convert(vo, WarehouseDO.class)));
    }

}
