package cn.nome.saas.allocation.cache;

import cn.nome.saas.allocation.repository.dao.allocation.GoodsInfoDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.GoodsInfoDO;
import com.google.common.cache.CacheLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * GoodsInfoCacheLoader
 *
 * @author Bruce01.fan
 * @date 2019/6/26
 */
@Component
public class GoodsInfoCacheLoader extends CacheLoader<String,List<GoodsInfoDO>> {


    @Autowired
    GoodsInfoDOMapper goodsInfoDOMapper;

    @Override
    public List<GoodsInfoDO> load(String key) throws Exception {
        return goodsInfoDOMapper.selectGoodsList();
    }
}
