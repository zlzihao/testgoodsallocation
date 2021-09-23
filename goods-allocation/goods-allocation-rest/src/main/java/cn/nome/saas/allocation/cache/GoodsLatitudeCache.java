package cn.nome.saas.allocation.cache;

import cn.nome.saas.allocation.model.rule.RuleTree;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.ForwardingLoadingCache;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * AreaLatitudeCache
 *
 * @author Bruce01.fan
 * @date 2019/6/27
 */
@Component
public class GoodsLatitudeCache extends ForwardingLoadingCache.SimpleForwardingLoadingCache<String,List<RuleTree>>{

    public GoodsLatitudeCache(GoodsLatitudeCacheLoader loader) {

        super(CacheBuilder.newBuilder()
                .maximumSize(100000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build(loader));
    }

    public List getAreaLatitude() {
        try {
            return this.get("goodsTree");
        } catch (ExecutionException e) {
        }
        return null;
    }
}
