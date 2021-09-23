package cn.nome.saas.sdc.manager;

import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.model.req.ShopWechatReq;
import cn.nome.saas.sdc.model.req.ShopsReq;
import cn.nome.saas.sdc.model.vo.ShopWechatVO;
import cn.nome.saas.sdc.model.vo.ShopsVO;
import cn.nome.saas.sdc.repository.entity.ShopWechatDO;
import cn.nome.saas.sdc.service.ShopWechatService;
import cn.nome.saas.sdc.service.ShopsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2020/3/4 11:07
 */
@Component
public class ShopWechatServiceManager {

    private ShopWechatService shopWechatService;
    private ShopsService shopsService;

    @Autowired
    public ShopWechatServiceManager(ShopWechatService shopWechatService, ShopsService shopsService) {
        this.shopWechatService = shopWechatService;
        this.shopsService = shopsService;
    }

    public void jobNumberChange(Integer corpId, Integer shopId, String jobNumber) {
        //若店长工号为空，则暂不改变saas_wechat原有数据
        if (jobNumber.isEmpty()) {
            return;
        }
        //生成config_id, qr_code
        String configId = "config_id";
        String qrCode = "ar_code";
        ShopWechatReq req = new ShopWechatReq();
        req.setShopId(shopId);
        ShopWechatVO shopWechatVO = shopWechatService.query(req);
        ShopWechatDO record = new ShopWechatDO();
        record.setJobNumber(jobNumber);
        record.setConfigId(configId);
        record.setQrCode(qrCode);
        if (shopWechatVO == null) {
            record.setShopId(shopId);
            record.setShopCode(getShopCodeById(corpId, shopId));
            shopWechatService.insertSelective(record);
        } else {
            record.setId(shopWechatVO.getId());
            shopWechatService.updateByPrimaryKeySelective(record);
        }
    }

    private String getShopCodeById(Integer corpId, Integer shopId) {
        ShopsReq req = new ShopsReq();
        req.setId(shopId);
        req.setCorpId(corpId);
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        ShopsVO shopsVO = shopsService.queryRow(req);
        String shopCode = "";
        if (shopsVO != null) {
            shopCode = shopsVO.getShopCode();
        }
        return shopCode;
    }
}
