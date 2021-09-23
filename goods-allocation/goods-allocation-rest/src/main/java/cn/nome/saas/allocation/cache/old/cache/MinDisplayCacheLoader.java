//package cn.nome.saas.allocation.cache.old.cache;
//
//import cn.nome.saas.allocation.repository.old.allocation.dao.StockDOMapper2;
//import cn.nome.saas.allocation.repository.old.allocation.entity.MinDisplaySkcDO;
//import com.google.common.cache.CacheLoader;
//import org.apache.commons.collections.MapUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * MinDisplayCacheLoader
// *
// * @author Bruce01.fan
// * @date 2019/6/19
// */
//@Component
//public class MinDisplayCacheLoader extends CacheLoader<String,Map<String,Integer>> {
//
//    @Autowired
//    StockDOMapper2 stockDOMapper2;
//
//    @Override
//    public Map<String, Integer> load(String key) throws Exception {
//        return getMinDisplayMap();
//    }
//
//    private Map<String,Integer> getMinDisplayMap() {
//        Map<String,Integer> minDisplayMap = new HashMap<>();
//
//        Map<String,Integer> smallCategorySkcMinDisplayMap = getSmallCategorySkcMinDisplayMap();
//        Map<String,Integer> skcMinDisplayMap = getSkcMinDisplayMap();
//
//        if (MapUtils.isNotEmpty(smallCategorySkcMinDisplayMap)) {
//            minDisplayMap.putAll(smallCategorySkcMinDisplayMap);
//        }
//        if (MapUtils.isNotEmpty(skcMinDisplayMap)) {
//            minDisplayMap.putAll(skcMinDisplayMap);
//        }
//
//        return minDisplayMap;
//    }
//
//    private Map<String,Integer> getSkcMinDisplayMap() {
//        List<MinDisplaySkcDO> minDisplayList = stockDOMapper2.getMinDisplaySkcList();
//
//        Map<String,Integer> skcMinDisplayMap = new HashMap<>(); // skc
//
//        skcMinDisplayMap = minDisplayList.stream()
//                .filter(minDisplaySkcDO -> minDisplaySkcDO.getType() == 2)
//                .collect(Collectors.toMap(MinDisplaySkcDO::getTypeValue, MinDisplaySkcDO::getQty));
//
//        return skcMinDisplayMap;
//    }
//
//
//
//    private Map<String,Integer> getSmallCategorySkcMinDisplayMap() {
//        Map<String,Integer> smallCategorySkcMinDisplayMap = new HashMap<>(); //  小类下skc
//
//        List<MinDisplaySkcDO>  minDisplayList = stockDOMapper2.getMinDisplaySkcList();
//        Map<String,Integer> smallCategoryMinDisplayMap = new HashMap<>(); //  小类
//
//        smallCategoryMinDisplayMap = minDisplayList.stream()
//                .filter(minDisplaySkcDO -> minDisplaySkcDO.getType() == 1)
//                .collect(Collectors.toMap(MinDisplaySkcDO::getTypeValue, MinDisplaySkcDO::getQty));
//
//        if (!smallCategoryMinDisplayMap.isEmpty()) {
//            List<Map<String,String>> categoryMatCodeList = stockDOMapper2.getMatCodeBySmallCategory(smallCategoryMinDisplayMap.keySet().stream().collect(Collectors.toList()));
//
//            for(Map<String,String> categoryMatCodeMap : categoryMatCodeList) {
//                for(String categoryCode : smallCategoryMinDisplayMap.keySet()) {
//                    String categoryName = categoryMatCodeMap.get("SmallCategoryName");
//                    String matCode = categoryMatCodeMap.get("MatCode");
//
//                    if (categoryName.equals(categoryCode)) {
//                        Integer minDiaplayQty = smallCategoryMinDisplayMap.get(categoryCode);
//                        smallCategorySkcMinDisplayMap.put(matCode,minDiaplayQty);
//                    }
//                }
//            }
//        }
//        return smallCategorySkcMinDisplayMap;
//    }
//}
