//package cn.nome.saas.allocation.cache.old.cache;
//
//import cn.nome.saas.allocation.cache.ShopListCacheLoader;
//import cn.nome.saas.allocation.model.old.allocation.Shop;
//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.ForwardingLoadingCache;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
///**
// * ShopListCache
// *
// * @author Bruce01.fan
// * @date 2019/6/11
// */
//@Component
//public class ShopListCache2 extends ForwardingLoadingCache.SimpleForwardingLoadingCache<String,List<Shop>> {
//
//
//    @Autowired
//    public ShopListCache2(ShopListCacheLoader2 delegate) {
//        super(CacheBuilder.newBuilder().maximumSize(10000)
//                .refreshAfterWrite(1, TimeUnit.DAYS)
//                .build(delegate));
//    }
//
//    public List<Shop> getShopList() {
//        try {
//            return this.get("shoplist");
//        } catch (Exception e) {
//        }
//        return null;
//    }
//}
