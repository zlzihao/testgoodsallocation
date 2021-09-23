package cn.nome.saas.allocation.feign.api;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.saas.allocation.feign.model.ShopMappingPositionForm;
import cn.nome.saas.allocation.feign.model.ShopsVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author lizihao@nome.com
 */
@FeignClient(value = "sdc", url = "${sdc.host}" + "/gw/pd-sdc/")
public interface SdcShopAllClient {
    @RequestMapping(value = "public/shops/base", method = RequestMethod.GET)
    Result<List<ShopsVO>> getBaseInfo();

    @RequestMapping(value = "sys/shops/changePosition", method = RequestMethod.POST)
    Result<?> changePositionShopCode(ShopMappingPositionForm forms);
}

