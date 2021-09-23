//package cn.nome.saas.allocation.cache.old.cache;
//
//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.ForwardingLoadingCache;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
///**
// * MinDisplayCache
// *
// * @author Bruce01.fan
// * @date 2019/6/19
// */
//@Component
//public class MinDisplayCache extends ForwardingLoadingCache.SimpleForwardingLoadingCache<String,Map<String,Integer>>{
//
//    @Autowired
//    public MinDisplayCache(MinDisplayCacheLoader delegate) {
//        super(CacheBuilder.newBuilder().maximumSize(100000)
//                .refreshAfterWrite(30, TimeUnit.MINUTES)
//                .build(delegate));
//    }
//
//    public Map<String,Integer> getMinDisplayMap() {
//        try {
//            return this.get("minDisplay");
//        } catch (Exception e) {
//        }
//        return null;
//    }
//}
