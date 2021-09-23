//package cn.nome.saas.allocation.cache.old.cache;
//
//import cn.nome.saas.allocation.model.old.allocation.Shop;
//import cn.nome.saas.allocation.repository.old.allocation.dao.StockDOMapper2;
//import com.google.common.cache.CacheLoader;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * ShopListCacheLoader
// *
// * @author Bruce01.fan
// * @date 2019/6/11
// */
//@Component
//public class ShopListCacheLoader2 extends CacheLoader<String,List<Shop>> {
//
//    @Autowired
//    StockDOMapper2 stockDOMapper2;
//
//    @Override
//    public List<Shop> load(String key) throws Exception {
//        List<Shop> list = stockDOMapper2.getShopList();
//
//        return list;
//    }
//}
