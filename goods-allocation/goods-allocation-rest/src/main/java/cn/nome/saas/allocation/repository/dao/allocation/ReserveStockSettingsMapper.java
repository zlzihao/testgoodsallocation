package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.ReserveStockSettingsDO;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/12/11 09:57
 */
public interface ReserveStockSettingsMapper {

    ReserveStockSettingsDO getLatest();

    int update(ReserveStockSettingsDO reserveStockSettingsDO);

    void add(ReserveStockSettingsDO reserveStockSettingsDO);

}
