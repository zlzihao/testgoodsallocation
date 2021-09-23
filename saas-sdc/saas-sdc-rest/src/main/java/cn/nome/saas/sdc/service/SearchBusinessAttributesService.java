package cn.nome.saas.sdc.service;

import cn.nome.saas.sdc.model.req.SearchBusinessAttributesReq;
import cn.nome.saas.sdc.model.vo.SearchBusinessAttributesVO;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/17 14:15
 */
public interface SearchBusinessAttributesService {

    /**
     * 查询
     *
     * @param req 过滤参数
     * @return VO列表
     */
    List<SearchBusinessAttributesVO> search(SearchBusinessAttributesReq req);


    /**
     * 查询属性值
     *
     * @param req 过滤条件
     * @return VO列表
     */
    SearchBusinessAttributesVO getAttributeValue(SearchBusinessAttributesReq req);

    /**
     * 搜索属性
     *
     * @param req 过滤条件
     * @return VO列表
     */
    List<SearchBusinessAttributesVO> searchAttribute(SearchBusinessAttributesReq req);

    /**
     * @param req
     * @return
     */
    List<SearchBusinessAttributesVO> filterAttributes(SearchBusinessAttributesReq req);

    List<SearchBusinessAttributesVO> queryAttributes(SearchBusinessAttributesReq req);

    List<SearchBusinessAttributesVO> queryAttributeTypes(SearchBusinessAttributesReq req);

    /**
     * 查询属性值
     *
     * @param req 过滤条件
     * @return 字符串属性值
     */
    String getAttributeValueString(SearchBusinessAttributesReq req);

    /**
     * 查询属性值
     *
     * @param req 过滤条件
     * @return 整型属性值
     */
    Integer getAttributeValueInteger(SearchBusinessAttributesReq req);

    /**
     * 查询属性值ID
     *
     * @param req 过滤条件
     * @return 属性值ID
     */
    Integer getAttributeValueId(SearchBusinessAttributesReq req);
}
