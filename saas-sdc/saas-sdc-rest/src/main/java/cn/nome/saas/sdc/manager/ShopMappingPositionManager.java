package cn.nome.saas.sdc.manager;

import cn.nome.saas.sdc.model.vo.ShopMappingPositionVO;
import cn.nome.saas.sdc.service.ShopMappingPositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lizihao@nome.com
 */
@Component
public class ShopMappingPositionManager {
    private final Logger logger = LoggerFactory.getLogger(ShopMappingPositionManager.class);
    @Autowired
    private ShopMappingPositionService shopMappingPositionService;

    public String changePositionCoefficient(List<ShopMappingPositionVO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "入参为空";
        }
        List<ShopMappingPositionVO> mappingList = shopMappingPositionService.selectByCondition(list);
        Map<String, Map<Integer, ShopMappingPositionVO>> shopMap = mappingList.stream().collect(Collectors.groupingBy(ShopMappingPositionVO::getShopCode,
                Collectors.toMap(ShopMappingPositionVO::getCategoryId, Function.identity(), (v1, v2) -> v1)));
        //区别新增还是更新
        List<ShopMappingPositionVO> insertList = new ArrayList<>();
        List<ShopMappingPositionVO> updateList = new ArrayList<>();
        for (ShopMappingPositionVO vo : list) {
            if (shopMap.get(vo.getShopCode()) != null && shopMap.get(vo.getShopCode()).get(vo.getCategoryId()) != null) {
                vo.setUpdateTime(new Date());
                updateList.add(vo);
                continue;
            }
            vo.setCreateTime(new Date());
            insertList.add(vo);
        }
        String response = null;
        if (!CollectionUtils.isEmpty(insertList)) {
            response = shopMappingPositionService.batchInsert(insertList);
        }

        if (!CollectionUtils.isEmpty(updateList)) {
            response = response + shopMappingPositionService.batchUpdate(updateList);
        }
        return response;
    }
}
