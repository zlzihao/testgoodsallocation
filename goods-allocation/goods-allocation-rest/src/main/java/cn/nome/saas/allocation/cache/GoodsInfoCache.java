package cn.nome.saas.allocation.cache;

import cn.nome.saas.allocation.repository.entity.allocation.GoodsInfoDO;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.ForwardingLoadingCache;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * GoodsInfoCache
 *
 * @author Bruce01.fan
 * @date 2019/6/26
 */
@Component
public class GoodsInfoCache extends ForwardingLoadingCache.SimpleForwardingLoadingCache<String,List<GoodsInfoDO>>{

    public GoodsInfoCache(GoodsInfoCacheLoader delegate) {
        super(CacheBuilder.newBuilder().maximumSize(100000)
        .refreshAfterWrite(5, TimeUnit.MINUTES)
        .build(delegate));
    }

    public List<GoodsInfoDO> getGoodsInfo() {
        try {
            return this.get("goodsInfo");
        } catch (Exception e) {
        }
        return null;
    }

}
