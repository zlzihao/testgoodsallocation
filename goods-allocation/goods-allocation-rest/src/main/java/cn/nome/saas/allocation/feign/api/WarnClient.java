package cn.nome.saas.allocation.feign.api;

import cn.nome.platform.common.utils.model.WechatWarnMessage;
import cn.nome.platform.common.utils.model.WechatWarnResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * WXWarnClient
 *
 * @author Bruce01.fan
 * @date 2020/1/3
 */
@FeignClient(value = "warnClient", url = "http://172.16.0.40:7004")
public interface WarnClient {

    @RequestMapping(value = "/sys/message/send", method = RequestMethod.POST)
    WechatWarnResult sendMsg(@RequestBody WechatWarnMessage msg);
}
