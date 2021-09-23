package cn.nome.saas.allocation.cache;

import cn.nome.saas.allocation.model.rule.GoodsCategroyTree;
import cn.nome.saas.allocation.model.rule.RuleTree;
import cn.nome.saas.allocation.repository.dao.allocation.DwsDimGoodsDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.GoodsInfoDOMapper;
import cn.nome.saas.allocation.repository.dao.vertical.DwsShopDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.GoodsInfoDO;
import com.google.common.cache.CacheLoader;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AreaLatitudeCacheLoader
 *
 * @author Bruce01.fan
 * @date 2019/6/27
 */
@Component
public class GoodsCategoryTreeCacheLoader extends CacheLoader<String,List<GoodsCategroyTree>> {


    @Autowired
    DwsDimGoodsDOMapper dwsDimGoodsDOMapper;

    @Autowired
    GoodsInfoDOMapper goodsInfoDOMapper;

    @Override
    public List<GoodsCategroyTree> load(String key) throws Exception {

        String[] keys = key.split("_");
        Integer type= null;
        if (keys.length>1) {
            type = Integer.parseInt(keys[1]);
        }

        return getGoodsCategoryTree(type);
    }

    public List<GoodsCategroyTree> getGoodsCategoryTree(Integer type) {

        List<String> matCodeList = null;
        if (type != null) {
            Map<String, Object> param = new HashMap<>();
            param.put("type", type);

            matCodeList = dwsDimGoodsDOMapper.getMatCodeList(param);

            if (CollectionUtils.isEmpty(matCodeList)) {
                return new ArrayList<>();
            }
        }
        List<GoodsInfoDO> goodsDOList = goodsInfoDOMapper.selectGoodsListByMatCode(matCodeList);

        // 大类
        Set<GoodsCategroyTree> categorySet =  goodsDOList.stream().map(goods->{
            GoodsCategroyTree goodsCategroyTree = new GoodsCategroyTree();
            goodsCategroyTree.setName(goods.getCategoryName());
            goodsCategroyTree.setLevel(RuleTree.BIG_LEVEL);
            return goodsCategroyTree;
        }).collect(Collectors.toSet());

        // 大类-中类
        Map<String,Set<GoodsCategroyTree>> middleCategoryMap = goodsDOList.stream()
                .collect(Collectors.groupingBy(GoodsInfoDO::getCategoryName,Collectors.mapping(goods->{
                    GoodsCategroyTree goodsCategroyTree = new GoodsCategroyTree();
                    goodsCategroyTree.setName(goods.getMidCategoryName());
                    goodsCategroyTree.setFullName(goods.getCategoryName()+"_"+goods.getMidCategoryName());
                    goodsCategroyTree.setLevel(RuleTree.MIDDLE_LEVEL);
                    return goodsCategroyTree;
                },Collectors.toSet())));

        // 中类-小类
        Map<String,Set<GoodsCategroyTree>> smallCategoryMap = goodsDOList.stream()
                .collect(Collectors.groupingBy(GoodsInfoDO::getMidCategoryName,Collectors.mapping(goods->{
                    GoodsCategroyTree goodsCategroyTree = new GoodsCategroyTree();

                    goodsCategroyTree.setName(goods.getSmallCategoryName());
                    goodsCategroyTree.setFullName(goods.getCategoryName()+"_"+goods.getMidCategoryName()+"_"+goods.getSmallCategoryName());
                    goodsCategroyTree.setLevel(RuleTree.SMALL_LEVEL);
                    return goodsCategroyTree;
                },Collectors.toSet())));


        List<GoodsCategroyTree> list =  this.fillingGoodsChild(categorySet,middleCategoryMap,smallCategoryMap);

        return list;
    }

    private List<GoodsCategroyTree> fillingGoodsChild(Set<GoodsCategroyTree> categorySet,Map<String,Set<GoodsCategroyTree>> middleCategoryMap,
                                                  Map<String,Set<GoodsCategroyTree>> smallCategoryMap) {


        // 小类放入中类
        for (String bigCategoryCode : middleCategoryMap.keySet()) {
            Set<GoodsCategroyTree> middleRuleSet = middleCategoryMap.get(bigCategoryCode);
            for (GoodsCategroyTree middleRule : middleRuleSet) {
                if (smallCategoryMap.containsKey(middleRule.getName())) {
                    middleRule.addAllChilds(smallCategoryMap.get(middleRule.getName()).stream().collect(Collectors.toList()));
                }
            }
        }

        // 中类放入大类
        for (GoodsCategroyTree bigCategoryRule : categorySet) {
            if (middleCategoryMap.containsKey(bigCategoryRule.getName())) {
                bigCategoryRule.addAllChilds(middleCategoryMap.get(bigCategoryRule.getName()).stream().collect(Collectors.toList()));
            }
        }

        return categorySet.stream().collect(Collectors.toList());
    }
}
