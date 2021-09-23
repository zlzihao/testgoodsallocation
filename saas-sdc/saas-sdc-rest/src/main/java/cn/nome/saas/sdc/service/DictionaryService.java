package cn.nome.saas.sdc.service;

import cn.nome.saas.sdc.model.req.QueryDictionaryReq;
import cn.nome.saas.sdc.model.vo.DictionaryVO;

import java.util.HashMap;
import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/4 13:39
 */
public interface DictionaryService {

    /**
     * 查询
     *
     * @param req 过滤参数
     * @return VO列表
     */
    List<DictionaryVO> query(QueryDictionaryReq req);

    /**
     * 查询
     *
     * @param req 过滤参数
     * @return Map  id=>name
     */
    HashMap<Integer, String> queryMap(QueryDictionaryReq req);
}
