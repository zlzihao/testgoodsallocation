package cn.nome.saas.allocation.cache;

import cn.nome.saas.allocation.model.issue.ShopInfoData;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.ForwardingLoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ShopInfoCache
 *
 * @author Bruce01.fan
 * @date 2019/8/7
 */
@Component
public class ShopInfoCache extends ForwardingLoadingCache.SimpleForwardingLoadingCache<String,List<ShopInfoData>> {

    @Autowired
    public ShopInfoCache(ShopInfoCacheLoader delegate) {
        super(CacheBuilder.newBuilder().maximumSize(10000)
                .refreshAfterWrite(2, TimeUnit.MINUTES)
                .build(delegate));
    }

    public List<ShopInfoData> getShopList() {
        try {
            return this.get("shopinfo");
        } catch (Exception e) {
        }
        return null;
    }
}
