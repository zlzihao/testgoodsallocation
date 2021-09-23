package cn.nome.saas.allocation.cache;

import cn.nome.saas.allocation.model.rule.RuleTree;
import cn.nome.saas.allocation.repository.dao.allocation.GoodsInfoDOMapper;
import cn.nome.saas.allocation.repository.dao.vertical.DwsShopDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.GoodsInfoDO;
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
public class GoodsLatitudeCacheLoader extends CacheLoader<String,List<RuleTree>> {

    private static final String HK_PROVINCE_CODE = "388";

    @Autowired
    DwsShopDOMapper dwsShopDOMapper;

    @Autowired
    GoodsInfoDOMapper goodsInfoDOMapper;

    @Override
    public List<RuleTree> load(String key) throws Exception {
        return getGoodsLatitudeTree();
    }

    public List<RuleTree> getGoodsLatitudeTree() {

        List<GoodsInfoDO> goodsDOList = goodsInfoDOMapper.selectGoodsList();

        // 大类
        Set<RuleTree> categorySet =  goodsDOList.stream().map(goods->{
            RuleTree ruleTree = RuleTree.buildGoodsRuleTree();
            ruleTree.setCode(goods.getCategoryName());
            ruleTree.setName(goods.getCategoryName());
            ruleTree.setLevel(RuleTree.BIG_LEVEL);
            return ruleTree;
        }).collect(Collectors.toSet());

        // 大类-中类
        Map<String,Set<RuleTree>> middleCategoryMap = goodsDOList.stream()
                .collect(Collectors.groupingBy(GoodsInfoDO::getCategoryName,Collectors.mapping(goods->{
                    RuleTree ruleTree = RuleTree.buildGoodsRuleTree();
                    ruleTree.setCode(goods.getMidCategoryName());
                    ruleTree.setName(goods.getMidCategoryName());
                    ruleTree.setLevel(RuleTree.MIDDLE_LEVEL);
                    return ruleTree;
                },Collectors.toSet())));

        // 中类-小类
        Map<String,Set<RuleTree>> smallCategoryMap = goodsDOList.stream()
                .collect(Collectors.groupingBy(GoodsInfoDO::getMidCategoryName,Collectors.mapping(goods->{
                    RuleTree ruleTree = RuleTree.buildGoodsRuleTree();
                    ruleTree.setCode(goods.getSmallCategoryName());
                    ruleTree.setName(goods.getSmallCategoryName());
                    ruleTree.setLevel(RuleTree.SMALL_LEVEL);
                    return ruleTree;
                },Collectors.toSet())));

        // 小类-skc
        Map<String,Set<RuleTree>> skcMap = goodsDOList.stream()
                .collect(Collectors.groupingBy(GoodsInfoDO::getSmallCategoryName,Collectors.mapping(goods->{
                    RuleTree ruleTree = RuleTree.buildGoodsRuleTree();
                    ruleTree.setCode(goods.getMatCode());
                    ruleTree.setName(goods.getMatCode());
                    ruleTree.setLevel(RuleTree.SKC_LEVEL);
                    return ruleTree;
                },Collectors.toSet())));

        return this.fillingGoodsChildRules(categorySet,middleCategoryMap,smallCategoryMap,skcMap);
    }

    private List<RuleTree> fillingGoodsChildRules(Set<RuleTree> categorySet,Map<String,Set<RuleTree>> middleCategoryMap,
                                                  Map<String,Set<RuleTree>> smallCategoryMap,Map<String,Set<RuleTree>> skcMap) {
        // skc 放入小类
        for (String middleCategorCode : smallCategoryMap.keySet()) {
            Set<RuleTree> smallRuleSet = smallCategoryMap.get(middleCategorCode);
            for (RuleTree smallRule : smallRuleSet) {
                if (skcMap.containsKey(smallRule.getCode())) {
                    smallRule.addAllChilds(skcMap.get(smallRule.getCode()).stream().collect(Collectors.toList()));
                }
            }
        }

        // 小类放入中类
        for (String bigCategoryCode : middleCategoryMap.keySet()) {
            Set<RuleTree> middleRuleSet = middleCategoryMap.get(bigCategoryCode);
            for (RuleTree middleRule : middleRuleSet) {
                if (smallCategoryMap.containsKey(middleRule.getCode())) {
                    middleRule.addAllChilds(smallCategoryMap.get(middleRule.getCode()).stream().collect(Collectors.toList()));
                }
            }
        }

        // 中类放入大类
        for (RuleTree bigCategoryRule : categorySet) {
            if (middleCategoryMap.containsKey(bigCategoryRule.getCode())) {
                bigCategoryRule.addAllChilds(middleCategoryMap.get(bigCategoryRule.getCode()).stream().collect(Collectors.toList()));
            }
        }

        return categorySet.stream().collect(Collectors.toList());
    }
}
