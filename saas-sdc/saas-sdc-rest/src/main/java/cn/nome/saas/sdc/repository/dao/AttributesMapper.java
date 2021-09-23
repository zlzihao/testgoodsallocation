package cn.nome.saas.sdc.repository.dao;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.AttributesReq;
import cn.nome.saas.sdc.repository.entity.AttributesDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/9 15:16
 */
public interface AttributesMapper {

    /**
     * 删除
     *
     * @param id 主键ID
     * @return 影响条数
     */
    int deleteByPrimaryKey(@Param("id") Integer id);

    /**
     * 添加
     *
     * @param record DO
     */
    void insertSelective(AttributesDO record);

    /**
     * 查询
     *
     * @param id 主键ID
     * @return DO
     */
    AttributesDO selectByPrimaryKey(@Param("id") Integer id);

    /**
     * 更新
     *
     * @param record DO
     */
    void update(AttributesDO record);

    /**
     * 总数
     *
     * @param req 过滤条件
     * @return 总条目数
     */
    Integer pageCount(@Param("req") AttributesReq req);

    /**
     * 搜索列表
     *
     * @param req  过滤参数
     * @param page 分页参数
     * @return DO列表
     */
    List<AttributesDO> search(@Param("req") AttributesReq req, @Param("page") Page page);

    /**
     * 检查名称是否存在
     *
     * @param req 过滤参数
     * @return DO
     */
    AttributesDO nameExist(@Param("req") AttributesReq req);
}
