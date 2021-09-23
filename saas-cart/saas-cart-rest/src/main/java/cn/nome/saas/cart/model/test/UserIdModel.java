package cn.nome.saas.cart.model.test;

import cn.nome.saas.cart.feign.SkuModel;
import cn.nome.saas.cart.utils.SortSkuModel;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.RandomStringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class UserIdModel extends TabIndexModel {

    public static void main(String[] args) {
        List<SkuModel> skus = initData();
//        SkuModel sku = getBestSku(skus);
//        System.out.printf("sku:"+ JSONObject.toJSONString(sku));
        System.out.printf("skus:" + skus.toString());

        //testPrintList();

    }

    private static void testPrintList() {
        List<String> strings = new ArrayList<>();
        for (int n = 0; n < 5; n++) {
            strings.add(RandomStringUtils.randomAlphanumeric(5));
        }
        System.out.printf("strings:" + strings.toString());
    }

    private static List<SkuModel> initData() {
        List<SkuModel> skuModels = new ArrayList<>();
        for (int n = 3; n > 0; n--) {
            SkuModel s = new SkuModel();
            s.setSkuCode("xxx");
            s.setSkuId(new Random().nextInt(10));
            skuModels.add(s);
        }
        for (int n = 0; n < 3; n++) {
            SkuModel s = new SkuModel();
            s.setSkuCode(RandomStringUtils.randomAlphanumeric(5));
            s.setSkuId(new Random().nextInt(10));
            skuModels.add(s);
        }
        return skuModels;
    }

    private static SkuModel getBestSku(List<SkuModel> vals) {

        Map<String, List<SkuModel>> skuDataGroup = vals.stream()
                .collect(Collectors.groupingBy(SkuModel::getSkuCode));
        Iterator<Map.Entry<String, List<SkuModel>>> itr = skuDataGroup.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, List<SkuModel>> entry = itr.next();
            List<SkuModel> skuModels = entry.getValue();
            if (skuModels.size() > 1) {
                Collections.sort(skuModels, new SortSkuModel());
                return skuModels.get(0);
            }
        }
        return vals.get(0);
    }

}
