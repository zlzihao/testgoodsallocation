package cn.nome.saas.allocation.service.basic;

import cn.nome.platform.common.constant.Constants;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.utils.model.WechatWarnMessage;
import cn.nome.platform.common.utils.model.WechatWarnResult;
import cn.nome.saas.allocation.feign.api.WarnClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.Date;

/**
 * WxWarnService
 *
 * @author Bruce01.fan
 * @date 2020/1/3
 */
@Service
public class WarningService {

    private static Logger LOGGER = LoggerFactory.getLogger(WarningService.class);

    public static final String tpl = ""
            + ">"
            + "项目名称: %s"+"\n"
            + "错误消息: **%s**"+"\n"
            + "来源机器: %s"+"\n"
            + "错误详情: %s"+"\n"
            + "发送时间: %s"+"\n"
            + ">";

    @Autowired
    @Lazy
    WarnClient warnClient;


    /**
     * 发送微信告警消息
     * @param applicationName
     * @param msgType
     * @param content
     * @param errorMsg
     * @return
     */
    public WechatWarnResult sendWechatWarnMsg(String applicationName, String msgType, String content, String errorMsg) {
        return sendWechatWarnMsg(applicationName, msgType, content, errorMsg, null);
    }

    public WechatWarnResult sendWechatWarnMsg(String applicationName,String msgType, String content,String errorMsg, String channel) {
        LoggerUtil.info(LOGGER, "[sendwechatWarnMsg]|applicationName={0},msgType={1},content={2},errorMsg={3}", applicationName,msgType,content, errorMsg);
        WechatWarnResult rpcResult = null;
        if(StringUtils.isNotBlank(errorMsg)&&StringUtils.isNotBlank(msgType)) {
            try {
                String sendContent = buildMarkDownTemplate(applicationName,content,errorMsg);
                WechatWarnMessage req = new WechatWarnMessage();
                req.setContent(sendContent);
                req.setMsgType(msgType);
                req.setChannel(channel);
                rpcResult = warnClient.sendMsg(req);
                LoggerUtil.debug(LOGGER, "[sendwechatWarnMsg]|content={0},rpcResult={1}", sendContent, rpcResult);
                if (rpcResult == null || !Constants.RESULT_SUCCESS.equals(rpcResult.getCode())) {
                    LoggerUtil.error(LOGGER, "发送微信告警失败   content={0},rpcResult={1}", sendContent, rpcResult);
                }
            } catch (Exception e) {
                LoggerUtil.error(LOGGER, "发送微信告警失败    content={0},exception={1}",content,e);
            }
        }
        return rpcResult;
    }

    /**
     * 构建消息模板
     * @param applicationName
     * @param content
     * @param errorMsg
     * @return
     */
    private String buildMarkDownTemplate(String applicationName,String content, String errorMsg) {
        JSONObject json = new JSONObject();
        json.put("content", content);
        json.put("errorMsg", errorMsg);
        String sendContent = JSON.toJSONString(json);
        String  contents = String.format(tpl, applicationName,sendContent,getLocalHost(),"pbd", DateUtil.format(new Date(), DateUtil.DATE_AND_TIME));

        return contents;
    }

    /**
     * 获取本机名
     * @return
     */
    private String getLocalHost() {
        InetAddress addr = null;
        String address = "";
        try {
            addr = InetAddress.getLocalHost();//新建一个InetAddress类
            address = addr.getHostName().toString();// 获得本机名称
        } catch (Exception e) {
        }
        return address;
    }

}
