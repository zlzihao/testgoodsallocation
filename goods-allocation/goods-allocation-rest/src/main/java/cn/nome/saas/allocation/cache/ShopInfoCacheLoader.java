package cn.nome.saas.allocation.cache;

import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.repository.dao.allocation.ShopInfoDOMapper;
import com.google.common.cache.CacheLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ShopInfoCacheLoader
 *
 * @author Bruce01.fan
 * @date 2019/8/7
 */
@Component
public class ShopInfoCacheLoader extends CacheLoader<String,List<ShopInfoData>> {

    @Autowired
    ShopInfoDOMapper shopInfoDOMapper;

    @Override
    public List<ShopInfoData> load(String key) throws Exception {
        List<ShopInfoData> l = shopInfoDOMapper.shopInfoData();
        return l;
    }
}
