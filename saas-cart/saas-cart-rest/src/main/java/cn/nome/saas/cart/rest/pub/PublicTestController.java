package cn.nome.saas.cart.rest.pub;

import cn.nome.platform.common.kafka.NomeKafkaService;
import cn.nome.platform.common.kafka.model.rece.MqReceReqVo;
import cn.nome.platform.common.kafka.model.rece.MqReceRespVo;
import cn.nome.platform.common.kafka.model.send.MqSendReqVo;
import cn.nome.platform.common.kafka.model.send.MqSendRespVo;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * @author bare
 * @create 2017/12/26.
 */
@RestController
@RequestMapping("/public/test")
public class PublicTestController {

    private String BIZ_TYPE = "cart";
    private String TOPIC = "pollTopic";

    @Autowired
    private NomeKafkaService nomeKafkaService;

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/backStr")
    public String backStr(HttpServletRequest request) {

        testSendMsg();
        testReceMsg();

        Map<String, String[]> params = request.getParameterMap();
        LOGGER.info("params:" + JSONObject.toJSONString(params));
        return "back";
    }

    private void testReceMsg() {
        MqReceReqVo receReqVo = new MqReceReqVo();
        receReqVo.setNum(1);
        receReqVo.setBizType(BIZ_TYPE);
        receReqVo.setTopicName(TOPIC);
        Result<MqReceRespVo> ret = nomeKafkaService.rece(receReqVo);
        LOGGER.info("receVo:{}", JSONObject.toJSONString(ret));
    }

    private void testSendMsg() {
        LOGGER.info(nomeKafkaService.toString());
        MqSendReqVo sendReqVo = new MqSendReqVo();
        sendReqVo.setDatas(Arrays.asList(RandomStringUtils.randomNumeric(2) + "," + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")));
        sendReqVo.setBizType(BIZ_TYPE);
        sendReqVo.setTopicName(TOPIC);
        Result<MqSendRespVo> ret = nomeKafkaService.send(sendReqVo);
        LOGGER.info("sendVo:{}", JSONObject.toJSONString(ret));
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    public Result login(@RequestHeader("HEADER-NOME-AppID") String appId, @RequestHeader("HEADER-NOME-UID") String uid)
            throws Exception {
        Map map = Maps.newHashMap();
        map.put("appId", appId);
        map.put("uid", uid);
        return ResultUtil.handleSuccessReturn(map);
    }

    @RequestMapping(value = "/testUpload", method = RequestMethod.GET)
    public void testUpload(MultipartHttpServletRequest request, HttpServletResponse response,
                           @RequestParam(value = "file", required = false) MultipartFile file) {
        System.out.printf("params:" + JSONObject.toJSONString(request.getParameterMap()));
        long len = file.getSize();
        System.out.printf("len:" + len);
    }

    @RequestMapping(value = "/upload")
    public void upload(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam(value = "file", required = false) MultipartFile file) {
        System.out.printf("params:" + JSONObject.toJSONString(request.getParameterMap()));
        long len = file.getSize();
        System.out.printf("len:" + len);
    }

}
