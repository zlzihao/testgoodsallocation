package cn.nome.saas.sdc.rest;

import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.util.HttpHeaderUtil;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/16 14:35
 */
public class BaseController {

    public Integer getCorpId() {
        return NumberUtils.toInt(HttpHeaderUtil.getParameter(Constant.HEADER_CORP_ID, Constant.PARAM_CORP_ID), 0);
    }

    public Integer getUid() {
        return NumberUtils.toInt(HttpHeaderUtil.getParameter(Constant.HEADER_UID, Constant.PARAM_UID), 0);
    }
}
