package cn.nome.saas.sdc.manager;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.utils.JsonUtils;
import cn.nome.saas.sdc.enums.ReturnType;
import cn.nome.saas.sdc.model.vo.GuanYuanVO;
import cn.nome.saas.sdc.repository.entity.GuanYuanDO;
import cn.nome.saas.sdc.util.RSA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2020/2/11 11:21
 */

@Component
public class GuanYuanServiceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuanYuanServiceManager.class);

    @Value("${nome.guan-yuan.url}")
    private String guanYuanUrl;

    @Value("${nome.guan-yuan.domain-id}")
    private String domainId;

    @Value("${nome.guan-yuan.private-key}")
    private String rsaPrivateKey;

    public GuanYuanVO genSsoUrl(String userId) {
        GuanYuanDO gy = new GuanYuanDO();
        gy.setDomainId(domainId);
        gy.setExternalUserId(userId);
        gy.setTimestamp((new Date()).getTime());
        String gyStr = JsonUtils.toJson(gy);
        LOGGER.debug("sso data: " + gyStr);
        try {
            String encryptedData = RSA.privateEncrypt(gyStr, RSA.getPrivateKey(rsaPrivateKey));
            String hexEncryptedData = RSA.toHexString(encryptedData);
            String ssoUrl = String.format("%s?provider=%s&ssoToken=%s", guanYuanUrl, domainId, hexEncryptedData);
            GuanYuanVO guanYuanVO = new GuanYuanVO();
            guanYuanVO.setSsoUrl(ssoUrl);
            return guanYuanVO;
        } catch (Exception e) {
            LOGGER.error("观远单点登录链接生成异常：" + e.getMessage());
            throw new BusinessException(ReturnType.GEN_SSO_URL_FAIL.getType(), ReturnType.GEN_SSO_URL_FAIL.getMsg());
        }
    }
}
