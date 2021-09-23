package cn.nome.saas.allocation.cache;

import cn.nome.saas.allocation.model.rule.GoodsCategroyTree;
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
public class GoodsCategoryTreeCache extends ForwardingLoadingCache.SimpleForwardingLoadingCache<String,List<GoodsCategroyTree>>{

    public GoodsCategoryTreeCache(GoodsCategoryTreeCacheLoader loader) {

        super(CacheBuilder.newBuilder()
                .maximumSize(100000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(loader));
    }

    public List getCategoryTree(Integer type) {
        try {
            String key =  "categoryTree";
            if (type != null) {
                key += "_"+ type;
            }
            List list =  this.get(key);
            return  list;
        } catch (ExecutionException e) {
        }
        return null;
    }
}
