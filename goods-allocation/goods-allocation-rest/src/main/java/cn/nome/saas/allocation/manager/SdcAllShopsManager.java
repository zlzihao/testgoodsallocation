package cn.nome.saas.allocation.manager;

import cn.nome.saas.allocation.feign.api.SdcShopAllClient;
import cn.nome.saas.allocation.feign.model.ShopMappingPositionForm;
import cn.nome.saas.allocation.feign.model.ShopsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lizihao@nome.com
 */
@Service
public class SdcAllShopsManager {
    private final Logger logger = LoggerFactory.getLogger(SdcAllShopsManager.class);

    @Autowired
    private SdcShopAllClient shopAllClient;

    public List<ShopsVO> allShopsAttribute() {
        return (List<ShopsVO>) shopAllClient.getBaseInfo().getData();
    }


    public String changePositionByShopCode(ShopMappingPositionForm form) {
        return (String) shopAllClient.changePositionShopCode(form).getData();
    }
}
