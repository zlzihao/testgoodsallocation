package cn.nome.saas.allocation.service.rule;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.cache.ShopListCache;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.rule.NewExpress;
import cn.nome.saas.allocation.model.rule.NewShopExpress;
import cn.nome.saas.allocation.repository.dao.allocation.ShopExpressDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO;
import cn.nome.saas.allocation.repository.entity.allocation.ShopExpressDO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ShopExpressService
 *
 * @author Bruce01.fan
 * @date 2019/6/18
 */
@Service
public class ShopExpressService {

    private static Logger logger = LoggerFactory.getLogger(ShopExpressService.class);

    @Autowired
    ShopExpressDOMapper shopExpressDOMapper;

    @Autowired
    ShopListCache shopListCache;

    @Autowired
    ExecutorService commonPool;

    /**
     * 获取快递信息
     * @return
     */
    public List<ShopExpressDO> getShippingFree(Set<String> demandShopIdList,Set<String> supplyShopIdList) {
        return  shopExpressDOMapper.getShopExpressList(demandShopIdList,supplyShopIdList);
    }

    public List<ShopExpressDO> getShopExpressListByPage(int offset,int pageSize) {
        Map<String,Object> param = new HashMap<>();
        param.put("offset",offset);
        param.put("pageSize",pageSize);

        return shopExpressDOMapper.getShopExpressListByPage(param);
    }

    public List<NewShopExpress> selectNewExpressByProvince(List<String> provinceList) {
        return shopExpressDOMapper.selectNewExpressByProvince(provinceList);
    }

    /**
     * 计算运费
     * @param expressDO
     * @param type
     * @param qty
     * @return
     */
    public Double calcShippingFree(NewShopExpress expressDO,int type,int qty) {

        Double fee = expressDO.getShippingFee();

        long qtyKg = 0;

        if (type == Constant.CLOTHING_TYPE) {

           qtyKg =  Math.round (0.3D * qty);

        } else if (type == Constant.MARKET_TYPE) {

            qtyKg =  Math.round (0.25D * qty);
        }

        if (qtyKg > 1 && qtyKg < 20) {
            return expressDO.getShippingFee() + (qtyKg - 1) * expressDO.getAddShippingFee();
        } else if (qtyKg >= 20) {
            return expressDO.getShippingFee20kg() + (qtyKg - 20) * expressDO.getBigAddShippingFee();
        }

        return fee;
    }

    public void loaddingAllNewShopExpress() {

        List<NewExpress> newExpressList = shopExpressDOMapper.loadAllNexExpress();

        if (CollectionUtils.isEmpty(newExpressList)) {
            return;
        }

        shopExpressDOMapper.deleteNextExpress();

        List<NewShopExpress> newShopExpressList = new ArrayList<>();
        for (NewExpress newExpress : newExpressList) {

            String from = newExpress.getFromCity();
            String to = newExpress.getToCity();

            NewShopExpress newShopExpress = parseAddress(from);
            NewShopExpress toNewShopExpress = parseAddress(to);

            newShopExpress.setProvince2(toNewShopExpress.getProvince1());
            newShopExpress.setCity2(toNewShopExpress.getCity1());
            newShopExpress.setShippingFee(newExpress.getShipping());
            newShopExpress.setAddShippingFee(newExpress.getAddShipping());
            newShopExpress.setShippingFee20kg(newExpress.getShipping20kg());
            newShopExpress.setBigAddShippingFee(newExpress.getAddShipping20kg());

            int spendDays = 0;
            String spendTime = newExpress.getSpendTime();
            if (spendTime.contains("工作日")) {
                spendTime = spendTime.replace("工作日","");
                spendDays = Integer.parseInt(spendTime.split("-")[1]);
            } else {
                spendDays = Integer.parseInt(spendTime);
            }

            newShopExpress.setSpendDays(spendDays);
            newShopExpressList.add(newShopExpress);
        }

        shopExpressDOMapper.insertNewExpressData(newShopExpressList);

    }

    public void matchShopFee() throws ExecutionException, InterruptedException {

        List<DwsDimShopDO> shopList = shopListCache.getShopList();
        List<String> provinceList = shopList.stream().map(DwsDimShopDO::getProvinceName).distinct().collect(Collectors.toList());

        List<NewShopExpress> shopExpressList = shopExpressDOMapper.selectNewExpressByProvince(provinceList);

        int size = 30;
        int offsize = 0;

        List<Future> futures = new ArrayList<>();
        while(true) {
            List<DwsDimShopDO> subShopList = shopList.stream().skip(offsize * size).limit(size).collect(Collectors.toList());

            if (subShopList.isEmpty()) {
                break;
            }

            Future<List<String>> future = commonPool.submit(()->{
                List<String> infoList = new ArrayList<>();
                Set<String> missShopSet = new HashSet<>();

                for (DwsDimShopDO shop1 : subShopList) {
                    for (DwsDimShopDO shop2 : subShopList) {
                        boolean flag = false;

                        String p1 = shop1.getProvinceName();
                        String p2 = shop2.getProvinceName();
                        String c1 = shop1.getCityName();
                        String c2 = shop2.getCityName();

                        for (NewShopExpress newShopExpress : shopExpressList) {

                            if ((newShopExpress.getProvince1().equals(p1) && newShopExpress.getProvince2().equals(p2) &&
                                    newShopExpress.getCity1().contains(c1) && newShopExpress.getCity2().contains(c2)
                            )
                                    ||
                                    (newShopExpress.getProvince1().equals(p2) && newShopExpress.getProvince2().equals(p1) &&
                                            newShopExpress.getCity1().contains(c2) && newShopExpress.getCity2().contains(c1))) {

                                flag = true;
                            }
                        }

                        if (!flag) {
                            String s = Stream.of(shop1.getCityName(),shop2.getCityName()).sorted().collect(Collectors.joining(":"));

                            if (missShopSet.contains(s)) {
                                continue;
                            } else {
                                missShopSet.add(s);
                            }


                            String info = buildMissInfo(shop1,shop2);
                            infoList.add(info);
                        }

                    }
                }
                return infoList;
            });

            futures.add(future);


            //LoggerUtil.info(logger,"[HIT] total:{0},match:{1}",total,match);

            offsize++;
        }

        List<String> infoList = new ArrayList<>();
        for(Future<List<String>> future : futures) {
            infoList.addAll(future.get());
        }

        LoggerUtil.info(logger,"[MISS_HIT] set:{0}",infoList);

    }

    private String buildMissInfo(DwsDimShopDO shop1,DwsDimShopDO shop2) {
        String info = "[\n";

        info += "\"" + shop1.getProvinceName()+"/" +shop1.getCityName()+"\",\n";
        info += "\"" + shop2.getProvinceName()+"/" +shop2.getCityName()+"\",\n";
        info += "\"" + shop1.getShopId()+"\",\n";
        info += "\"" + shop2.getShopId()+"\"\n";

        info += "],\n";

        return info;
    }

    private NewShopExpress parseAddress(String address) {
        NewShopExpress  newShopExpress = new NewShopExpress();

        String[] addressArray = address.split("/");

        if(addressArray[0].contains("市")) {
            newShopExpress.setProvince1(addressArray[0].replace("市",""));
            newShopExpress.setCity1(addressArray[0]);
        } else {
            newShopExpress.setProvince1(addressArray[0]);
            newShopExpress.setCity1(addressArray[1]);
        }

        return newShopExpress;
    }

}
