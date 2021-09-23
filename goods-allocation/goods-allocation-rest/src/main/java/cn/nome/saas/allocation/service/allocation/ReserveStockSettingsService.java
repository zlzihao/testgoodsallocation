package cn.nome.saas.allocation.service.allocation;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/12/11 10:22
 */

import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.saas.allocation.model.allocation.ReserveStockSettings;
import cn.nome.saas.allocation.repository.dao.allocation.ReserveStockSettingsMapper;
import cn.nome.saas.allocation.repository.entity.allocation.ReserveStockSettingsDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReserveStockSettingsService {

    private ReserveStockSettingsMapper reserveStockSettingsMapper;

    @Autowired
    public ReserveStockSettingsService(ReserveStockSettingsMapper reserveStockSettingsMapper) {
        this.reserveStockSettingsMapper = reserveStockSettingsMapper;
    }

    public ReserveStockSettings getLatest() {
        ReserveStockSettingsDO reserveStockSettingsDO = reserveStockSettingsMapper.getLatest();
        return BaseConvertor.convert(reserveStockSettingsDO, ReserveStockSettings.class);
    }

    @Transactional(value = "allocationTransactionManager")
    public int update(ReserveStockSettingsDO reserveStockSettingsDO) {
        return reserveStockSettingsMapper.update(reserveStockSettingsDO);
    }

    @Transactional(value = "allocationTransactionManager")
    public void add(ReserveStockSettingsDO reserveStockSettingsDO) {
        reserveStockSettingsMapper.add(reserveStockSettingsDO);
    }

    public ReserveStockSettings getLatestWithInit() {
        ReserveStockSettingsDO reserveStockSettingsDO = reserveStockSettingsMapper.getLatest();
        if (reserveStockSettingsDO == null) {
            init();
        }
        return getLatest();
    }

    private void init() {
        ReserveStockSettingsDO record = new ReserveStockSettingsDO();
        record.setIsEnable(ReserveStockSettingsDO.IS_ENABLE_FALSE);
        record.setUseSalePredict(1);
        record.setReserveDate("");
//        record.setReserveDate(LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth()).toString());
        add(record);
    }
}
