package cn.nome.saas.search.rest.pub;

import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.web.controller.BaseController;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.search.constant.Constant;
import cn.nome.saas.search.manager.NmSearchManager;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chentaikuang
 */
@Api(description = "远程词典热加载控制器")
@RestController
@RequestMapping("/public/remoteDict")
public class RemoteDictPubController extends BaseController {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static AtomicInteger reqCount = new AtomicInteger(0);
    private static int RELOAD_REQ_COUNT_LIMIT = 10;

    @Value("${remote_ext_dict_path}")
    private String remoteExtDictPath;

    @Autowired
    private NmSearchManager nmSearchManager;

    @ApiOperation("新增远程词典接口")
    @RequestMapping(value = "/add", method = {RequestMethod.GET})
    public Result add(@RequestParam String keyword) {
        //String rtn = addDictKeyword(keyword);
//        String rtn = nmSearchManager.addRemoteDict(keyword);
        String rtn = nmSearchManager.addRemoteDictStr(keyword);
        return ResultUtil.handleSuccessReturn(rtn);
    }

    @ApiOperation("加载远程词典接口")
    @RequestMapping(value = "/reload/{doer}", method = {RequestMethod.GET})
    public void reload(@PathVariable("doer") String doer, HttpServletRequest request, HttpServletResponse response) {

        response.setContentType(Constant.CONTENTTYPE_TEXT_PLAIN_UTF8);

        if (nmSearchManager.openReqCountLimitSwitch()){

            if (reqCount.incrementAndGet() % RELOAD_REQ_COUNT_LIMIT != 0) {
                LOGGER.info("[RELOAD] limit req count:{}", reqCount.intValue());
                return;
            }

            if (reqCount.intValue() >= Constant.REQ_COUNT_10000) {
                LOGGER.info("[RELOAD] reqCount reset");
                reqCount.set(0);
            }
        }

        if (StringUtils.isNotBlank(doer) && Constant.I_AM_SVR.equals(doer)) {

            int bETag = request.getIntHeader(Constant.HEADER_IF_NONE_MATCH);
            Long bModified = request.getDateHeader(Constant.HEADER_IF_MODIFIED_SINCE);
            LOGGER.info("[RELOAD] bETag:{},bModified:{}", bETag, bModified);
            reloadRemoteDict(response, bETag);
        } else {
            LOGGER.error("[RELOAD] noAuth:{}", doer);
        }
    }

    @ApiOperation("删除远程词典接口")
    @RequestMapping(value = "/del", method = {RequestMethod.GET})
    public Result del(@RequestParam String keyword) {
        //String rtn = nmSearchManager.delRemoteDict(keyword);
        String rtn = nmSearchManager.delRemoteDictStr(keyword);
        return ResultUtil.handleSuccessReturn(rtn);
    }

    @ApiOperation("加载远程同义词接口")
    @RequestMapping(value = "/synonyms/{doer}", method = {RequestMethod.GET})
    public void synonyms(@PathVariable("doer") String doer, HttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("synonyms:{}", doer);
    }

    private String addDictKeyword(@RequestParam String keyword) {

        String rtn = "ok";
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            fos = new FileOutputStream(remoteExtDictPath, true);
            bw = new BufferedWriter(new OutputStreamWriter(fos, Constant.CHART_SET_UTF8));
            bw.write(keyword);
            bw.newLine();
        } catch (Exception e) {
            //e.printStackTrace();
            LOGGER.error("[ADD] errMsg:{}", e.getMessage());
            rtn = e.getMessage();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return rtn;
    }

    private void reloadRemoteDict(HttpServletResponse response, int bETag) {

        //从文件读取词典更新
//        String dictStr = readDictFile();
//        int dictLen = dictStr.length();

        //从缓存读取词典更新
        String dictStr = readDictCache();
        int dictLen = dictStr.length();

        boolean reload = (dictLen != bETag);
        LOGGER.info("[RELOAD] reload:{}", reload);
        if (reload) {
            try {
                OutputStream out = response.getOutputStream();
                response.setIntHeader(Constant.HEADER_ETAG, dictLen);
                response.setDateHeader(Constant.HEADER_LAST_MODIFIED, DateUtil.getCurTimeMillis());
                out.write(dictStr.getBytes(Constant.CHART_SET_UTF8));
                out.flush();
            } catch (IOException e) {
                //e.printStackTrace();
                LOGGER.error("[RELOAD] errMsg:{}", e.getMessage());
            }
        }
    }

    /**
     * 读取字典字符串
     *
     * @return
     */
    private String readDictCache() {
        //return nmSearchManager.readRemoteDict();
        return nmSearchManager.loadRemoteDictStr();
    }

    /**
     * 调试：ik加载远程词库
     */
    @ApiOperation("测试ik加载远程词库")
    @RequestMapping(value = "/testIk", method = {RequestMethod.GET})
    private void testIk() {

        List<String> buffer = new ArrayList<String>();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        BufferedReader in;
        String location = "http://192.168.70.71:8330/public/remoteDict/reload/xiaochen";
        location = "http://localhost:8330/public/remoteDict/reload/xiaochen";
        location = "http://134.175.222.68:8330/public/remoteDict/reload/iamsvr";
        HttpGet get = new HttpGet(location);
        try {
            response = httpclient.execute(get);
            LOGGER.info("[TEST_IK] response:{}", JSONObject.toJSONString(response));

            HttpEntity entity = response.getEntity();

            Header contentTypeHeader = entity.getContentType();

            String charset = "UTF-8";
            // 获取编码，默认为utf-8
            if (contentTypeHeader.getValue().contains("charset=")) {
                String contentType = contentTypeHeader.getValue();
                charset = contentType.substring(contentType.lastIndexOf("=") + 1);
            }
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
            String line = null;
            while ((line = in.readLine()) != null) {
                buffer.add(line);
            }
            in.close();
            response.close();
            LOGGER.info("[TEST_IK] buffer:{}", buffer);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("[TEST_IK] errMsg:{}", e.getMessage());
        }

    }

    /**
     * TODO:读取字典文件,后续可读取缓存形式
     *
     * @return
     */
    private String readDictFile() {
        String path = remoteExtDictPath;
        InputStream is = null;
        BufferedReader in = null;
        String readTxt = "";
        try {
            is = new FileInputStream(path);
            if (is == null) {
                LOGGER.warn("[READ_DICT_FILE] return null");
                return readTxt;
            }

            in = new BufferedReader(new InputStreamReader(is));
            StringBuffer buffer = new StringBuffer();
            String line = null;
            while ((line = in.readLine()) != null) {
                buffer.append(line).append("\r\n");
            }
            readTxt = buffer.toString();
        } catch (Exception e) {
            //e.printStackTrace();
            LOGGER.error("[READ_DICT_FILE] errMsg:{}", e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                    LOGGER.error("[READ_DICT_FILE] errMsg:{}", e.getMessage());
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                    LOGGER.error("[READ_DICT_FILE] errMsg:{}", e.getMessage());
                }
            }
        }
        return readTxt;
    }
}
