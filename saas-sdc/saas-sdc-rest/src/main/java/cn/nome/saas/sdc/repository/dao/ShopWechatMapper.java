package cn.nome.saas.sdc.repository.dao;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.ShopWechatReq;
import cn.nome.saas.sdc.repository.entity.ShopWechatDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2020/3/4 10:26
 */
public interface ShopWechatMapper {


    int insertSelective(ShopWechatDO record);

    int updateByPrimaryKeySelective(ShopWechatDO record);

    Integer pageCount(@Param("req") ShopWechatReq req);

    List<ShopWechatDO> search(@Param("req") ShopWechatReq req, @Param("page") Page page);

    ShopWechatDO query(@Param("req") ShopWechatReq req);

}
