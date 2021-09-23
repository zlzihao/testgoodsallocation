package cn.nome.saas.sdc.repository.dao;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.AttributeTypesReq;
import cn.nome.saas.sdc.repository.entity.AttributeTypesDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 13:44
 */
public interface AttributeTypesMapper {

    /**
     * 添加
     *
     * @param record DO
     */
    void insertSelective(AttributeTypesDO record);

    /**
     * 查询
     *
     * @param id 主键ID
     * @return DO
     */
    AttributeTypesDO selectByPrimaryKey(@Param("id") Integer id);

    /**
     * 更新
     *
     * @param record AttributeTypesDO
     */
    void update(AttributeTypesDO record);

    /**
     * 总数
     *
     * @param req 过滤条件
     * @return 总条目数
     */
    Integer pageCount(@Param("req") AttributeTypesReq req);

    /**
     * 搜索列表
     *
     * @param req  过滤参数
     * @param page 分页参数
     * @return DO列表
     */
    List<AttributeTypesDO> search(@Param("req") AttributeTypesReq req, @Param("page") Page page);

    /**
     * 查询列表(全部返回)
     *
     * @param req 过滤参数
     * @return DO列表
     */
    List<AttributeTypesDO> query(@Param("req") AttributeTypesReq req);

    /**
     * 查询名称是否存在
     *
     * @param req 过滤参数
     * @return DO
     */
    AttributeTypesDO nameExist(@Param("req") AttributeTypesReq req);
}
