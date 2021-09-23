package cn.nome.saas.sdc.repository.dao;

import cn.nome.saas.sdc.model.req.SearchBusinessAttributesReq;
import cn.nome.saas.sdc.repository.entity.SearchBusinessAttributesDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/17 14:02
 */
public interface SearchBusinessAttributesMapper {

    /**
     * 查询
     *
     * @param req 过滤参数
     * @return DO列表
     */
    List<SearchBusinessAttributesDO> search(@Param("req") SearchBusinessAttributesReq req);

    /**
     * 获取指定属性值
     *
     * @param req 过滤条件
     * @return DO列表
     */
    SearchBusinessAttributesDO getAttributeValue(@Param("req") SearchBusinessAttributesReq req);

    /**
     * 搜索属性
     *
     * @param req 过滤条件
     * @return DO列表
     */
    List<SearchBusinessAttributesDO> searchAttribute(@Param("req") SearchBusinessAttributesReq req);

    /**
     * @param req
     * @return
     */
    List<SearchBusinessAttributesDO> filterAttributes(@Param("req") SearchBusinessAttributesReq req);

    List<SearchBusinessAttributesDO> queryAttributes(@Param("req") SearchBusinessAttributesReq req);

    List<SearchBusinessAttributesDO> queryAttributeTypes(@Param("req") SearchBusinessAttributesReq req);
}
