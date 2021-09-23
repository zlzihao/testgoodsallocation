package cn.nome.saas.allocation.manager;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.saas.allocation.feign.api.PdcClient;
import cn.nome.saas.allocation.feign.model.CategoriesVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lizihao@nome.com
 */
@Component
public class PdcManager {

    private static final Logger logger = LoggerFactory.getLogger(PdcManager.class);

    private final PdcClient client;

    @Autowired
    public PdcManager(PdcClient client) {
        this.client = client;
    }

    public List<CategoriesVO> getBigCategory() {
        return client.getBigCategory().getData();
    }

    public List<CategoriesVO> getMidCategory() {
        return client.getMidCategory().getData();
    }

    public List<CategoriesVO> getSmallCategory() {
        return client.getSmallCategory().getData();
    }

    public Map<Integer,Integer> getTopCategoryTypeByDisplayReceptionCategoryIds (List<Integer> receptionCategoryIds) {
        Result<Map<Integer,Integer>> result = client.getTopCategoryTypeByDisplayReceptionCategoryIds(receptionCategoryIds);
        if (result == null || !result.getCode().equals("SUCCESS")) {
            logger.error("[PdcManager] [getTopCategoryTypeByDisplayReceptionCategoryIds] unexpected result = {}", result);
            throw new BusinessException("查询[商品中台前台类目]数据异常，请稍后再试");
        }
        if (result.getData() == null) {
            return new HashMap<>();
        }
        return result.getData();
    }

    private List<CategoriesVO> getByIds (List<Integer> receptionCategoryIds) {
        Result<List<CategoriesVO>> result = client.getByIds(receptionCategoryIds);
        if (result == null || !result.getCode().equals("SUCCESS")) {
            logger.error("[PdcManager] [getTopCategoryTypeByDisplayReceptionCategoryIds] unexpected result = {}", result);
            throw new BusinessException("查询[商品中台前台类目]数据异常，请稍后再试");
        }
        if (result.getData() == null) {
            return new ArrayList<>();
        }
        return result.getData();
    }

    public Map<Integer,String> getIdNameMap (List<Integer> receptionCategoryIds) {
        if(CollectionUtils.isEmpty(receptionCategoryIds)){
            return new HashMap<>();
        }
        List<CategoriesVO> categoriesVOS = getByIds(receptionCategoryIds);
        return categoriesVOS.stream().collect(Collectors.toMap(CategoriesVO::getId,CategoriesVO::getCnName,(v1,v2) -> v2));
    }
}
