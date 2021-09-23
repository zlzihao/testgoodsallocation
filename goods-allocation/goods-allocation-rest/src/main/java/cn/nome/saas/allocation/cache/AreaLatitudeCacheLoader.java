package cn.nome.saas.allocation.cache;

import cn.nome.saas.allocation.model.rule.RuleTree;
import cn.nome.saas.allocation.repository.dao.vertical.DwsShopDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.ForbiddenSingleItemDO;
import cn.nome.saas.allocation.repository.entity.vertical.DwsDimShopDO;
import com.google.common.cache.CacheLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AreaLatitudeCacheLoader
 *
 * @author Bruce01.fan
 * @date 2019/6/27
 */
@Component
public class AreaLatitudeCacheLoader extends CacheLoader<String,List<RuleTree>> {

    private static final String HK_PROVINCE_CODE = "388";

    @Autowired
    DwsShopDOMapper dwsShopDOMapper;

    @Override
    public List<RuleTree> load(String key) throws Exception {
        return getAreaLatitudeTree();
    }

    public List<RuleTree> getAreaLatitudeTree() {
        // 所有门店原数据
        List<DwsDimShopDO> shopList = dwsShopDOMapper.selectAllShopList();

        shopList.forEach(shop->{
            // 处理香港门店没有regionNo的情况
            if (shop.getProvinceCode().equals(HK_PROVINCE_CODE)) {
                shop.setRegionNo("0011");
            }
        });

        // 大区
        Set<RuleTree> regionRuleList = shopList.stream().map(shop->{
            RuleTree ruleTree = RuleTree.buildAreaRuleTree();
            ruleTree.setCode(shop.getRegionNo().trim());
            ruleTree.setName(shop.getRegionBusName().trim());
            ruleTree.setLevel(RuleTree.REGION_LEVEL);
            return ruleTree;
        }).distinct().sorted(Comparator.comparing(RuleTree::getCode)).collect(Collectors.toSet());

        // 大区-省份
        Map<String,Set<RuleTree>> provinceRuleMap = shopList.stream()
                .collect(Collectors.groupingBy(DwsDimShopDO::getRegionNo,Collectors.mapping(shop->{
                    RuleTree ruleTree = RuleTree.buildAreaRuleTree();
                    ruleTree.setCode(shop.getProvinceCode());
                    ruleTree.setName(shop.getProvinceName());
                    ruleTree.setLevel(RuleTree.PROVINCE_LEVEL);
                    return ruleTree;
                },Collectors.toSet())));

        // 省-城市
        Map<String,Set<RuleTree>> cityRuleMap = shopList.stream()
                .collect(Collectors.groupingBy(DwsDimShopDO::getProvinceCode,Collectors.mapping(shop->{
                    RuleTree ruleTree = RuleTree.buildAreaRuleTree();
                    ruleTree.setCode(shop.getCityCode());
                    ruleTree.setName(shop.getCityName());
                    ruleTree.setLevel(RuleTree.CITY_LEVEL);
                    return ruleTree;
                },Collectors.toSet())));

        // 城市-门店
        Map<String,Set<RuleTree>> shopRuleMap =shopList.stream()
                .collect(Collectors.groupingBy(DwsDimShopDO::getCityCode,Collectors.mapping(shop->{
                    RuleTree ruleTree = RuleTree.buildAreaRuleTree();
                    ruleTree.setCode(shop.getShopCode());
                    ruleTree.setName(shop.getShopName());
                    ruleTree.setLevel(RuleTree.SHOP_LEVEL);
                    ruleTree.setLeaf(true); // 叶子节点
                    return ruleTree;
                },Collectors.toSet())));

        return this.fillingAreaChildRules(regionRuleList,provinceRuleMap,cityRuleMap,shopRuleMap);
    }

    private List<RuleTree> fillingAreaChildRules(Set<RuleTree> regionRuleList, Map<String,Set<RuleTree>> provinceRuleMap,
                                                 Map<String,Set<RuleTree>> cityRuleMap, Map<String,Set<RuleTree>> shopRuleMap) {
        // 将门店填充到城市
        for (String provinceCode :cityRuleMap.keySet()) {
            Set<RuleTree> cityRules = cityRuleMap.get(provinceCode);
            for (RuleTree cityRule : cityRules) {
                if (shopRuleMap.containsKey(cityRule.getCode())) {
                    cityRule.addAllChilds(shopRuleMap.get(cityRule.getCode()).stream().collect(Collectors.toList()));
                }
            }
        }

        // 将城市填充到省份
        for (String regionCode : provinceRuleMap.keySet()) {

            Set<RuleTree> provinceSet = provinceRuleMap.get(regionCode);
            for (RuleTree provinceRule : provinceSet) {
                if (cityRuleMap.containsKey(provinceRule.getCode())) {
                    provinceRule.addAllChilds(cityRuleMap.get(provinceRule.getCode()).stream().collect(Collectors.toList()));
                }
            }
        }

        // 将省份填充到大区中
        regionRuleList.forEach(regionRule->{
            if (provinceRuleMap.containsKey(regionRule.getCode())) {
                regionRule.addAllChilds(provinceRuleMap.get(regionRule.getCode()).stream().collect(Collectors.toList()));
            }
        });

        return regionRuleList.stream().collect(Collectors.toList());
    }
}
