package cn.nome.saas.sdc.repository.dao;

import cn.nome.saas.sdc.model.req.AttributeValuesReq;
import cn.nome.saas.sdc.repository.entity.AttributeValuesDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
public interface AttributeValuesMapper {

    /**
     * 删除
     *
     * @param id 主键ID
     * @return 影响条数
     */
    int deleteByPrimaryKey(@Param("id") Integer id);

    /**
     * 插入
     *
     * @param record DO
     */
    void insertSelective(AttributeValuesDO record);

    /**
     * 查询
     *
     * @param id 主键ID
     * @return DO
     */
    AttributeValuesDO selectByPrimaryKey(@Param("id") Integer id);

    /**
     * 更新
     *
     * @param record DO
     * @return 影响条数
     */
    int update(AttributeValuesDO record);

    /**
     * 总数
     *
     * @param req 过滤条件
     * @return 总条目数
     */
    Integer pageCount(@Param("req") AttributeValuesReq req);

    /**
     * 搜索列表
     *
     * @param req 过滤参数
     * @return DO列表
     */
    List<AttributeValuesDO> search(@Param("req") AttributeValuesReq req);

    /**
     * 查询名称是否存在
     *
     * @param req 过滤参数
     * @return DO
     */
    AttributeValuesDO nameExist(@Param("req") AttributeValuesReq req);
}
