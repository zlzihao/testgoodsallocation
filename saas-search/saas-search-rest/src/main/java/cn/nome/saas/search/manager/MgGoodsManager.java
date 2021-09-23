package cn.nome.saas.search.manager;

import cn.nome.platform.common.api.result.RpcResult;
import cn.nome.saas.search.feign.IMgGoodsCenter;
import cn.nome.saas.search.feign.ProductTagReq;
import cn.nome.saas.search.model.vo.ProductTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chentaikuang
 * 商品服务聚合层
 */
@Component
public class MgGoodsManager {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IMgGoodsCenter mgGoodsCenter;

    public Map<Integer, Integer> productTagList(List<Integer> ids, int corpId, int appId) {
        Map<Integer, Integer> tagMap = null;
        try {
            ProductTagReq tagReq = new ProductTagReq();
            tagReq.setProduct_ids(ids);
            RpcResult<List<ProductTag>> rst = mgGoodsCenter.productTagList(corpId, appId, tagReq);
            LOGGER.info("productTagList:{}", rst);
            tagMap = convertProductTagMap(rst);
        } catch (Exception e) {
            LOGGER.error("productTagList err msg:{},ids:{}", e.getMessage(), ids);
        }
        return tagMap;
    }

    private Map<Integer, Integer> convertProductTagMap(RpcResult<List<ProductTag>> rst) {
        Map<Integer, Integer> tagMap = new HashMap<>();
        if (rst != null && rst.getData() != null && !rst.getData().isEmpty()) {
            List<ProductTag> tags = rst.getData();
            //过滤tag>0
            tags.stream().filter(tag -> tag.getTag_id() > 0).forEach(tag -> tagMap.put(tag.getProduct_id(), tag.getTag_id()));
        }
        return tagMap;
    }

}
