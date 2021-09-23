package cn.nome.saas.sdc.repository.dao;

import cn.nome.saas.sdc.model.req.BusinessAttributeValuesReq;
import cn.nome.saas.sdc.repository.entity.BusinessAttributeValuesDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
public interface BusinessAttributeValuesMapper {

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
     * @return 影响条数
     */
    int insertSelective(BusinessAttributeValuesDO record);

    /**
     * 查询
     *
     * @param id 主键ID
     * @return DO
     */
    BusinessAttributeValuesDO selectByPrimaryKey(@Param("id") Integer id);

    /**
     * 更新
     *
     * @param record DO
     * @return 影响条数
     */
    int updateByPrimaryKeySelective(BusinessAttributeValuesDO record);

    /**
     * 搜索列表
     *
     * @param req 过滤参数
     * @return DO列表
     */
    List<BusinessAttributeValuesDO> query(@Param("req") BusinessAttributeValuesReq req);
}
