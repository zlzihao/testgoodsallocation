package cn.nome.saas.search.rest.pub;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author bare
 * @create 2017/12/26.
 */
@RestController
@RequestMapping("/public/test")
public class PublicTestController {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/backStr", method = RequestMethod.GET)
    public String backStr(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        LOGGER.info("params:" + JSONObject.toJSONString(params));
        return "back->" + RandomStringUtils.randomAlphanumeric(10);
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

}
