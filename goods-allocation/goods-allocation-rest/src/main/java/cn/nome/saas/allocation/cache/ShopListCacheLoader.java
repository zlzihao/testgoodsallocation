package cn.nome.saas.allocation.cache;

import cn.nome.saas.allocation.repository.dao.allocation.DwsDimShopDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO;
import com.google.common.cache.CacheLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ShopListCacheLoader
 *
 * @author Bruce01.fan
 * @date 2019/6/11
 */
@Component
public class ShopListCacheLoader extends CacheLoader<String,List<DwsDimShopDO>> {

    @Autowired
    DwsDimShopDOMapper dwsDimShopDOMapper;

    @Override
    public List<DwsDimShopDO> load(String key) throws Exception {
        List<DwsDimShopDO> list = dwsDimShopDOMapper.selectAllShopList();

        return list;
    }
}
