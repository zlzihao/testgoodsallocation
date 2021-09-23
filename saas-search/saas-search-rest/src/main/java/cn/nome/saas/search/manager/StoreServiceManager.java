package cn.nome.saas.search.manager;

import cn.nome.platform.common.utils.JsonUtils;
import cn.nome.saas.search.constant.Constant;
import cn.nome.saas.search.feign.IStoreService;
import cn.nome.saas.search.model.vo.DiscProductVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
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
public class StoreServiceManager {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IStoreService storeService;

    public Map<Integer, DiscProductVo> getDiscProductByIds(List<Integer> ids, int corpId, int appId, int uid) {
        Map<Integer, DiscProductVo> discProductMap = null;
        try {
            String rst = storeService.getDiscountsInfo(corpId, appId, uid, ids);
            LOGGER.info("getDiscountsInfo rst:{}", rst);
            discProductMap = convertDiscProductMap(rst);
        } catch (Exception e) {
            LOGGER.error("getDiscProductByIds err msg:{},uid:{}", e.getMessage(), uid);
        }
        return discProductMap;
    }

    private Map<Integer, DiscProductVo> convertDiscProductMap(String rst) {
        Map<Integer, DiscProductVo> discProductMap = new HashMap<>();
        if (StringUtils.isBlank(rst)) {
            return discProductMap;
        }
        JSONObject json = JSONObject.parseObject(rst);
        String code = json.get(Constant.SUCCESS_CODE).toString();
        Object dataObj = json.get(Constant.DATA_STR);
        if (Constant.SUCCESS_STR.equals(code) && dataObj != null) {
            List<DiscProductVo> productList = JsonUtils.jsonToList(dataObj.toString(), DiscProductVo.class);
            productList.stream().forEach(product -> discProductMap.put(product.getProductId(), product));
        }
        return discProductMap;
    }
}
