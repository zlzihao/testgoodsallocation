package cn.nome.saas.allocation.service.allocation;

import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.repository.dao.allocation.DwsDimShopDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ForbiddenRuleDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.SubWarehouseConfigDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO;
import cn.nome.saas.allocation.repository.entity.allocation.SubWarehouseConfigDO;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static cn.nome.saas.allocation.constant.Constant.ATTR_VALUE_SPLIT;

/**
 * 仓库库存服务
 */

@Service
public class NewIssueWarehouseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SubWarehouseConfigDOMapper subWarehouseConfigDOMapper;
    @Autowired
    private DwsDimShopDOMapper dwsDimShopDOMapper;
    @Autowired
    private ForbiddenRuleDOMapper forbiddenRuleDOMapper;

    /**
     * 根据configId返回map
     * @param id
     * @return Set<ShopId>
     */
    public Set<String> getShopMapByConfigId(Integer id) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("id", Collections.singletonList(id));
        List<SubWarehouseConfigDO>  list = subWarehouseConfigDOMapper.selectByPage(param);
        if (CollectionUtils.isEmpty(list)) {
            return new HashSet<>();
        }

        SubWarehouseConfigDO subWarehouseConfigDO = list.get(0);
        String region = subWarehouseConfigDO.getRegion();
        String province = subWarehouseConfigDO.getProvince();
        String city = subWarehouseConfigDO.getCity();
        String saleLv = subWarehouseConfigDO.getSaleLv();
        String displayLv = subWarehouseConfigDO.getDisplayLv();
        String shopStatus = subWarehouseConfigDO.getShopStatus();
        String shopIds = subWarehouseConfigDO.getShopId();

        List<DwsDimShopDO> dwsDimShopDosApply = new ArrayList<>();
        Map<String, Object> paramMapShop = new HashMap<>(2);
        if (!StringUtils.isEmpty(province)) {
            paramMapShop.put("provinceNames", province.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(city)) {
            paramMapShop.put("cityNames", city.split(ATTR_VALUE_SPLIT));
        }
        if (paramMapShop.size() > 0) {
            dwsDimShopDosApply = dwsDimShopDOMapper.selectShopListByRegioneProvinceCityIn(paramMapShop);
        }

        List<ShopInfoData> shopInfoDatasApply = new ArrayList<>();
        Map<String, Object> paramMapShopLv = new HashMap<>(3);
        if (!StringUtils.isEmpty(saleLv)) {
            paramMapShopLv.put("shopLevels", saleLv.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(displayLv)) {
            paramMapShopLv.put("displayLevels", displayLv.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(region)) {
            paramMapShopLv.put("regions", region.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(shopStatus)) {
            paramMapShopLv.put("shopStatus", shopStatus.split(ATTR_VALUE_SPLIT));
        }
        if (paramMapShopLv.size() > 0) {
            shopInfoDatasApply = forbiddenRuleDOMapper.getShopBySaleDisplayLvIn(paramMapShopLv);
        }

        Set<String> dwsDimShopDosApplySet = dwsDimShopDosApply.stream().map(DwsDimShopDO::getShopId).collect(Collectors.toSet());
        Set<String> shopInfoDatasApplySet = shopInfoDatasApply.stream().map(ShopInfoData::getShopID).collect(Collectors.toSet());
        Set<String> shopIdSet = StringUtils.isBlank(shopIds) ? new HashSet<>() : new HashSet<>(Arrays.asList(shopIds.split(",")));
        //直接取交集
        Set<String> intersection = Sets.intersection(dwsDimShopDosApplySet, shopInfoDatasApplySet);
        //数量大于0 才取交集
//        if (dwsDimShopDosApplySet.size() > 0 && shopInfoDatasApplySet.size() > 0) {
//            intersection = Sets.intersection(dwsDimShopDosApplySet, shopInfoDatasApplySet);
//        } else if (shopInfoDatasApplySet.size() > 0) {
//            intersection = shopInfoDatasApplySet;
//        }
//        else {
//            intersection = shopInfoDatasApplySet;
//        }

//        if (intersection.size() > 0 && shopIdSet.size() > 0) {
//            intersection = Sets.intersection(intersection, shopIdSet);
//        } else if (shopIdSet.size() > 0) {
//            intersection = shopIdSet;
//        }

        return  intersection;
    }
}
