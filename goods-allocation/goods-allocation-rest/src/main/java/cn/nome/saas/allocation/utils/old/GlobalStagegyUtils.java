//package cn.nome.saas.allocation.utils.old;
//
//import cn.nome.platform.common.logger.LoggerUtil;
//import cn.nome.platform.common.utils.excel.ExcelUtil;
//import cn.nome.saas.allocation.constant.old.StrategyConsts;
//import cn.nome.saas.allocation.model.old.forbiddenRule.GlobalStrategyList;
//import cn.nome.saas.allocation.model.old.forbiddenRule.GlobalStrategyRule;
//import cn.nome.saas.allocation.model.old.forbiddenRule.RegionShop;
//import cn.nome.saas.allocation.service.old.allocation.forbiddenRule.ForbiddenRuleServiceImpl;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
///**
// * 全局策略工具类
// *
// * @author Bruce01.fan
// * @date 2019/5/29
// */
//public class GlobalStagegyUtils {
//
//    private static Logger logger = LoggerFactory.getLogger(GlobalStagegyUtils.class);
//
//    static String GLOBAL_STRATEGY_LIST_PATH  = "/Users/fanguiming/Documents/work-folders/NOME/智能配补调/全局禁配/策略作用明细-0614-保底.xlsx";
//    static String GLOBAL_STRATEGY_RULE_PATH  = "/Users/fanguiming/Documents/work-folders/NOME/智能配补调/全局禁配/策略作用区域-0614-保底.xlsx";
//    static String EXPORT_CSV_PATH = "/Users/fanguiming/Documents/work-folders/NOME/智能配补调/全局禁配/保底明细清单-0614.csv";
//
//    static String REGION_SHOP_PAHT = "/Users/fanguiming/Documents/work-folders/NOME/智能配补调/全局禁配/区域对应门店.xlsx";
//
//    //static String ALL_REGION_NAME = "华南、华北、华东、华中、西南、西北、东北、海南";
//
//    static Map<String,List<String>> regionShopMap = new HashMap<>();
//    static Map<String,List<String>> provinceShopMap = new HashMap<>();
//    static Map<String,List<String>> cityShopMap = new HashMap<>();
//
//    public static List<GlobalStrategyList> readGlobalStrategyListExcel() {
//        XSSFWorkbook wb = null;
//        try {
//            // test
//            wb =new XSSFWorkbook(GLOBAL_STRATEGY_LIST_PATH);
//        } catch (Exception e) {
//           return  null;
//        }
//
//        return ExcelUtil.readExcel(wb,GlobalStrategyList.class);
//    }
//
//    public static List<GlobalStrategyRule> readGlobalStrategyRuleExcel() {
//        XSSFWorkbook wb = null;
//        try {
//            // test
//            wb =new XSSFWorkbook(GLOBAL_STRATEGY_RULE_PATH);
//        } catch (Exception e) {
//            return  null;
//        }
//
//        return ExcelUtil.readExcel(wb,GlobalStrategyRule.class);
//    }
//
//    public static List<RegionShop> readRegionShop() {
//        XSSFWorkbook wb = null;
//        try {
//            // test
//            wb =new XSSFWorkbook(REGION_SHOP_PAHT);
//        } catch (Exception e) {
//            return  null;
//        }
//
//        return ExcelUtil.readExcel(wb,RegionShop.class);
//    }
//
//    public static void initRegionShopList() {
//        List<RegionShop> regionShopList = readRegionShop();
//
//        if (regionShopList == null) {
//            return;
//        }
//
//        regionShopList = regionShopList.stream().filter(shop->!"已撤店".equals(shop.getStatus().trim()) && !"虚拟样板店".equals(shop.getStatus().trim())).collect(Collectors.toList());
//
//        regionShopMap = regionShopList.stream().collect(Collectors.groupingBy(RegionShop::getRegion,Collectors.mapping(RegionShop::getShopCode,Collectors.toList())));
//        provinceShopMap = regionShopList.stream().collect(Collectors.groupingBy(RegionShop::getProvince,Collectors.mapping(RegionShop::getShopCode,Collectors.toList())));
//        cityShopMap = regionShopList.stream().collect(Collectors.groupingBy(RegionShop::getRegion,Collectors.mapping(RegionShop::getShopCode,Collectors.toList())));
//    }
//
//
//
//    public static void exportGlobalStrategyCsv(List<GlobalStrategyRule>  ruleList,int type,String startDate,String endDate) {
//
//        StringBuffer result = new StringBuffer();
//
//        for(GlobalStrategyRule rule : ruleList) {
//
//            List<String> shopList = rule.getShopList();
//            List<String> goodsList = rule.getGoodsCodeList();
//            List<GlobalStrategyList> securityStrategyLists = rule.getSecurityStrategyLists();
//
//            for (String shop : shopList) {
//
//                if (type == 1) {
//                    for (String goods : goodsList) {
//
//                            // 禁配
//                            result.append(shop).append(",").append("单品,").append(goods)
//                                    .append(",").append(startDate).append(",")
//                                    .append(endDate).append(",").append("")
//                                    .append(",").append(rule.getCreator() == null ? "范桂明" : rule.getCreator()).append("\n");
//
//                    }
//                } else {
//                    // 保底
//                    if (securityStrategyLists == null) {
//                        continue;
//                    }
//                    for (GlobalStrategyList globalStrategy : securityStrategyLists) {
//                        result.append(shop).append(",").append("单品,")
//                                .append(globalStrategy.getCode()).append(",")
//                                .append(globalStrategy.getNumber()).append(",")
//                                .append(startDate).append(",")
//                                .append(endDate).append(",").append("")
//                                .append(",").append(rule.getCreator() == null ? "范桂明" : rule.getCreator())
//                                .append(",").append(globalStrategy.getName()).append(",")
//                                .append("\n");
//                    }
//                }
//            }
//        }
//
//        File file = new File(EXPORT_CSV_PATH);
//        FileWriter fw = null;
//        try {
//            fw = new FileWriter(file);
//            fw.write(result.toString());
//
//        }catch (Exception e) {
//
//        } finally {
//            if (fw != null) {
//                try {
//                    fw.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//
//    }
//
//    /*public static List<String> getRegionShopListV2(String regionName) {
//
//        String source = Stream.of(ALL_REGION_NAME.split("、")).sorted().collect(Collectors.joining(","));
//        String target = Stream.of(regionName.split("、")).sorted().collect(Collectors.joining(","));
//
//        String[] regionArray = regionName.split("、");
//        StringBuffer  result = new StringBuffer();
//
//        // 完整匹配
//        if (source.equals(target)) {
//            return Stream.of(StrategyConsts.ALL_REGION_SHOPLIST.split(",")).collect(Collectors.toList());
//        }else if(regionName.equals("华东")) {
//            return Stream.of(StrategyConsts.HD_REGION_SHOPLIST.split(",")).collect(Collectors.toList());
//        }else if(regionName.equals("华中")) {
//            return Stream.of(StrategyConsts.HZ_REGION_SHOPLIST.split(",")).collect(Collectors.toList());
//        }else if(regionName.contains("西藏") && regionName.contains("港澳")) {
//            return Stream.of(StrategyConsts.ALL_REGION_SHOPLIST + "," + StrategyConsts.HK_SHOPLIST + "," +StrategyConsts.XZ_SHOPLIST.split(",")).collect(Collectors.toList());
//        }
//
//        // 逐个匹配
//        for (String region : regionArray) {
//            if (region.equals("华南")) {
//                result.append(StrategyConsts.HN_REGION_SHOPLIST).append(",");
//            } else if (region.equals("海南")) {
//                result.append(StrategyConsts.HAIN_REGION_SHOPLIST).append(",");
//            }
//        }
//
//        return Stream.of(result.toString().split(",")).collect(Collectors.toList());
//    }*/
//
//    public static List<String> getRegionShopList(String regionName) {
//
//        if(regionShopMap.isEmpty()) {
//            initRegionShopList();
//        }
//
//        List<String> shopList = getShopList(regionName,regionShopMap);
//
//
//        LoggerUtil.info(logger,"[REGION_SHOP] name:{0} , size:{1}",regionName,shopList.size());
//
//
//        return shopList;
//    }
//
//    public static List<String> getProvinceShopList(String provinceName) {
//        List<String> shopList = getShopList(provinceName,provinceShopMap);
//
//
//        LoggerUtil.info(logger,"[REGION_SHOP] name:{0} , size:{1}",provinceName,shopList.size());
//
//
//        return shopList;
//    }
//
//    private static List<String> getShopList(String name,Map<String,List<String>> map) {
//        if(regionShopMap.isEmpty()) {
//            initRegionShopList();
//        }
//
//        String[] array = name.split("、");
//        List<String> shopList = new ArrayList<>();
//        for (String region : array) {
//            if(map.containsKey(region)) {
//                shopList.addAll(map.get(region));
//            }
//        }
//
//        return shopList;
//    }
//
//    public static List<String> getLevel(String level) {
//        String[] levelNames = level.split("、");
//
//        StringBuffer shoplist = new StringBuffer();
//        for (String name : levelNames) {
//            if ("c".equalsIgnoreCase(name)) {
//                shoplist.append(StrategyConsts.LEVEL_C).append(",");
//            } else if("d".equalsIgnoreCase(name)) {
//                shoplist.append(StrategyConsts.LEVEL_D).append(",");
//            }
//        }
//
//        return Stream.of(shoplist.toString().split(",")).collect(Collectors.toList());
//    }
//
//
//    /*public static List<String> getProvinceShopListV2(String provinceName) {
//        String[] provinceNames = provinceName.split("、");
//
//        StringBuffer shoplist = new StringBuffer();
//        for(String province : provinceNames) {
//            if("湖北".equals(province)) {
//                shoplist.append(StrategyConsts.HUBEI_SHOPLIST).append(",");
//            } else if("湖南".equals(province)) {
//                shoplist.append(StrategyConsts.HUNAN_SHOPLIST).append(",");
//            }else if("河南".equals(province)) {
//                shoplist.append(StrategyConsts.HENAN_SHOPLIST).append(",");
//            }else if("江西".equals(province)) {
//                shoplist.append(StrategyConsts.JX_SHOPLIST).append(",");
//            }else if("北京".equals(province)) {
//                shoplist.append(StrategyConsts.BEIJIN_SHOPLIST).append(",");
//            }else if("广东".equals(province)) {
//                shoplist.append(StrategyConsts.GD_SHOP_LIST);
//            }
//        }
//
//        return Stream.of(shoplist.toString().split(",")).collect(Collectors.toList());
//    }*/
//
//    public static List<String> excludeProvinceShopList(String provinceName) {
//        return getProvinceShopList(provinceName);
//    }
//
//    public static List<String> excludeLevel(String level) {
//        String[] levelNames = level.split("、");
//
//        StringBuffer shoplist = new StringBuffer();
//        for (String name : levelNames) {
//            if ("a".equalsIgnoreCase(name)) {
//                shoplist.append(StrategyConsts.EXCLUDE_A).append(",");
//            } else if("b".equalsIgnoreCase(name)) {
//                shoplist.append(StrategyConsts.EXCLUDE_B).append(",");
//            }
//        }
//
//        return Stream.of(shoplist.toString().split(",")).collect(Collectors.toList());
//    }
//
//    public static void removeExcludeShop(List<String> allShopList,List<String> excludeList) {
//        Iterator<String> iterable = allShopList.iterator();
//        while(iterable.hasNext()) {
//            String shop = iterable.next();
//            if (excludeList.contains(shop)) {
//                iterable.remove();
//            }
//        }
//    }
//
//
//    public static void main(String[] args) {
//
//        ForbiddenRuleServiceImpl forbiddenRuleService = new ForbiddenRuleServiceImpl();
//
//        //forbiddenRuleService.impotGlobalStrategyExcel("全局禁配规则",1,"2019-05-10","2099-05-30");
//
//        forbiddenRuleService.impotGlobalStrategyExcel("全局保底规则",2,"2019-05-10","2099-05-30");
//
//        //System.out.println(StrategyConsts.EXCLUDE_A.split(",").length);
//        //System.out.println(StrategyConsts.EXCLUDE_B.split(",").length);
//
//    }
//}
