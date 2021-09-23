package cn.nome.saas.cart.feign;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.saas.cart.model.CampaignSkuCodeReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 外部接口调用
 */
@FeignClient(value = "pd-promo")
public interface IPromoService {
    @RequestMapping(value = "/sys/campaign/getCampaignByProductSetIds", method = RequestMethod.POST)
    public Result<Map<Integer, List<CampaignModel>>> getCampaignByProductSetIds(@RequestParam("corpId") Integer corpId,
                                                                                @RequestParam("appId") Integer appId, @RequestParam("uid") Integer uid,
                                                                                @RequestParam("productSetIds") List<Integer> productSetIds);

    //http://wiki.nome.com/pages/viewpage.action?pageId=8495084
    @RequestMapping(value = "/sys/campaign/getCampaignListBySkuCodes", method = RequestMethod.POST)
    public Result<List<CampaignInfoResult>> campaignListBySkuCodes(@RequestBody CampaignSkuCodeReq req);

    //http://wiki.nome.com/pages/viewpage.action?pageId=8495976
    @RequestMapping(value = "/sys/campaign/getCalcCampaignPromotionAmount", method = RequestMethod.POST)
    public Result<List<PromoSkuCalcResult>> calcCampaignPromoAmount(@RequestBody CampaignSkuCodeReq req);


}
