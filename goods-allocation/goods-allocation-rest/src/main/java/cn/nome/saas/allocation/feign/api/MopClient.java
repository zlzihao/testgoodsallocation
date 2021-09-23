package cn.nome.saas.allocation.feign.api;

import cn.nome.platform.common.api.result.RpcResult;
import cn.nome.saas.allocation.feign.model.DisplayPlan;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * MopClient
 *
 * @author author
 * @date date
 */
@FeignClient(value = "mop", url = "${mop.host}" + "/gw/pd-pms/")
public interface MopClient {

    /**
     * getRangePlan
     *
     * @return
     */
    @RequestMapping(value = "public/displayPlan/selectByParam", method = RequestMethod.GET)
    RpcResult<List<DisplayPlan>> getRangePlan();

}
