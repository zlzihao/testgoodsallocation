package cn.nome.saas.allocation.feign.api;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.saas.allocation.feign.model.CategoriesVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author lizihao@nome.com
 */
@FeignClient(value = "pdc", url = "${pdc.host}" + "/gw/pd-pdc/")
public interface PdcClient {
    @RequestMapping(value = "public/category/getBigCategory", method = RequestMethod.GET)
    Result<List<CategoriesVO>> getBigCategory();

    @RequestMapping(value = "public/category/getMidCategory", method = RequestMethod.GET)
    Result<List<CategoriesVO>> getMidCategory();

    @RequestMapping(value = "public/category/getSmallCategory", method = RequestMethod.GET)
    Result<List<CategoriesVO>> getSmallCategory();

    @RequestMapping(value = "public/reception/mapping/top", method = RequestMethod.GET)
    Result<Map<Integer,Integer>> getTopCategoryTypeByDisplayReceptionCategoryIds(@RequestParam List<Integer> receptionCategoryIds);

    @RequestMapping(value = "/public/reception/fetch",method = RequestMethod.GET)
    Result<List<CategoriesVO>> getByIds(@RequestParam List<Integer> receptionCategoryIds);
}

