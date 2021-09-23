package cn.nome.saas.search.feign;

import cn.nome.platform.common.api.result.RpcResult;
import cn.nome.saas.search.constant.CommonHeader;
import cn.nome.saas.search.model.vo.ProductTag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "mg-goodscenter")
public interface IMgGoodsCenter {

    @RequestMapping(value = "/sys/product-tag/list", method = RequestMethod.POST)
    public RpcResult<List<ProductTag>> productTagList(@RequestHeader(CommonHeader.corpId) Integer corpId,
                                                      @RequestHeader(CommonHeader.appId) Integer appId,
                                                      @RequestBody ProductTagReq tagReq);
}
