package cn.nome.saas.allocation.service.rule;

import cn.nome.saas.allocation.repository.dao.allocation.GoodsInfoDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.MinDisplaySkcDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.MinDisplaySkcDO;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MinDisplayService
 *
 * @author Bruce01.fan
 * @date 2019/6/21
 */
@Service
public class MinDisplayService {

    @Autowired
    MinDisplaySkcDOMapper minDisplaySkcDOMapper;

    @Autowired
    GoodsInfoDOMapper goodsInfoDOMapper;

    public Map<String,Integer> getMinDisplayMap() {
        Map<String,Integer> minDisplayMap = new HashMap<>();

        Map<String,Integer> smallCategorySkcMinDisplayMap = getSmallCategorySkcMinDisplayMap();
        Map<String,Integer> skcMinDisplayMap = getSkcMinDisplayMap();

        if (MapUtils.isNotEmpty(smallCategorySkcMinDisplayMap)) {
            minDisplayMap.putAll(smallCategorySkcMinDisplayMap);
        }
        if (MapUtils.isNotEmpty(skcMinDisplayMap)) {
            minDisplayMap.putAll(skcMinDisplayMap);
        }

        return minDisplayMap;
    }

    /**
     * 所有skc下最小陈列数
     * @return
     */
    private Map<String,Integer> getSkcMinDisplayMap() {
        List<MinDisplaySkcDO> minDisplayList = minDisplaySkcDOMapper.getMinDisplaySkcList();

        Map<String,Integer> skcMinDisplayMap = new HashMap<>(); // skc

        skcMinDisplayMap = minDisplayList.stream()
                .filter(minDisplaySkcDO -> minDisplaySkcDO.getType() == 2)
                .collect(Collectors.toMap(MinDisplaySkcDO::getTypeValue,MinDisplaySkcDO::getQty));

        return skcMinDisplayMap;
    }

    /**
     * 所有小类下skc最小陈列数
     * @return
     */
    private Map<String,Integer> getSmallCategorySkcMinDisplayMap() {
        Map<String,Integer> smallCategorySkcMinDisplayMap = new HashMap<>(); //  小类下skc

        List<MinDisplaySkcDO>  minDisplayList = minDisplaySkcDOMapper.getMinDisplaySkcList();
        Map<String,Integer> smallCategoryMinDisplayMap = new HashMap<>(); //  小类

        smallCategoryMinDisplayMap = minDisplayList.stream()
                .filter(minDisplaySkcDO -> minDisplaySkcDO.getType() == 1)
                .collect(Collectors.toMap(MinDisplaySkcDO::getTypeValue,MinDisplaySkcDO::getQty));

        if (!smallCategoryMinDisplayMap.isEmpty()) {
            List<Map<String,String>> categoryMatCodeList = goodsInfoDOMapper.getMatCodeBySmallCategory(smallCategoryMinDisplayMap.keySet().stream().collect(Collectors.toList()));

            for(Map<String,String> categoryMatCodeMap : categoryMatCodeList) {
                for(String categoryCode : smallCategoryMinDisplayMap.keySet()) {
                    String categoryName = categoryMatCodeMap.get("SmallCategoryName");
                    String matCode = categoryMatCodeMap.get("MatCode");

                    if (categoryName.equals(categoryCode)) {
                        Integer minDiaplayQty = smallCategoryMinDisplayMap.get(categoryCode);
                        smallCategorySkcMinDisplayMap.put(matCode,minDiaplayQty);
                    }
                }
            }
        }
        return smallCategorySkcMinDisplayMap;
    }
}
