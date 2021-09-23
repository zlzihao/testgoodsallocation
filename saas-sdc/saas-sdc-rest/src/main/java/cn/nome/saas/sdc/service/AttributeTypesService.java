package cn.nome.saas.sdc.service;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.AttributeTypesReq;
import cn.nome.saas.sdc.model.vo.AttributeTypesVO;
import cn.nome.saas.sdc.repository.entity.AttributeTypesDO;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 13:49
 */
public interface AttributeTypesService {

    /**
     * 添加
     *
     * @param record AttributeTypesDO
     */
    void insertSelective(AttributeTypesDO record);

    /**
     * 查询
     *
     * @param id 主键ID
     * @return AttributeTypesVO
     */
    AttributeTypesVO selectByPrimaryKey(Integer id);

    /**
     * 更新
     *
     * @param record AttributeTypesDO
     */
    void update(AttributeTypesDO record);

    /**
     * 搜索列表
     *
     * @param req  过滤参数
     * @param page 分页参数
     * @return VO列表
     */
    List<AttributeTypesVO> search(AttributeTypesReq req, Page page);

    /**
     * 查询列表(全部返回)
     *
     * @param req 过滤参数
     * @return VO列表
     */
    List<AttributeTypesVO> query(AttributeTypesReq req);

    /**
     * 查询名称是否存在
     *
     * @param req 过滤参数
     * @return VO
     */
    AttributeTypesVO nameExist(AttributeTypesReq req);
}
