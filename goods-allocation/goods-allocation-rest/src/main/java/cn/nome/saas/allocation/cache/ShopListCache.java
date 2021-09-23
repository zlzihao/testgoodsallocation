package cn.nome.saas.allocation.cache;

import cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.ForwardingLoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ShopListCache
 *
 * @author Bruce01.fan
 * @date 2019/6/11
 */
@Component
public class ShopListCache extends ForwardingLoadingCache.SimpleForwardingLoadingCache<String,List<DwsDimShopDO>> {


    @Autowired
    public ShopListCache(ShopListCacheLoader delegate) {
        super(CacheBuilder.newBuilder().maximumSize(10000)
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .build(delegate));
    }

    public List<DwsDimShopDO> getShopList() {
        try {
            return this.get("shoplist");
        } catch (Exception e) {
        }
        return null;
    }
}
