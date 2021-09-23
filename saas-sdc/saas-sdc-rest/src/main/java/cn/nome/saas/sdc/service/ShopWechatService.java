package cn.nome.saas.sdc.service;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.ShopWechatReq;
import cn.nome.saas.sdc.model.vo.ShopWechatVO;
import cn.nome.saas.sdc.repository.entity.ShopWechatDO;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2020/3/4 10:46
 */
public interface ShopWechatService {

    int insertSelective(ShopWechatDO record);

    int updateByPrimaryKeySelective(ShopWechatDO record);

    List<ShopWechatVO> search(ShopWechatReq req, Page page);

    ShopWechatVO query(ShopWechatReq req);

}
