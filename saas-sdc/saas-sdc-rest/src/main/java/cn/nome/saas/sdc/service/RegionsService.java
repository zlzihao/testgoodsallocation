package cn.nome.saas.sdc.service;

import cn.nome.saas.sdc.model.req.RegionsReq;
import cn.nome.saas.sdc.model.vo.RegionsVO;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/10/12 14:02
 */
public interface RegionsService {

    /**
     * 查询列表
     *
     * @param req 过滤条件
     * @return 结果列表
     */
    List<RegionsVO> getList(RegionsReq req);
}