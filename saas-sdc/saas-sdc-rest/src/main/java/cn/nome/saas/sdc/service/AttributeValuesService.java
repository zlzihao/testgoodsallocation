package cn.nome.saas.sdc.service;

import cn.nome.saas.sdc.model.req.AttributeValuesReq;
import cn.nome.saas.sdc.model.vo.AttributeValuesVO;
import cn.nome.saas.sdc.repository.entity.AttributeValuesDO;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 13:49
 */
public interface AttributeValuesService {

    /**
     * 删除
     *
     * @param id 主键ID
     * @return 影响条数
     */
    int deleteByPrimaryKey(Integer id);

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
     * @return VO
     */
    AttributeValuesVO selectByPrimaryKey(Integer id);

    /**
     * 更新
     *
     * @param record DO
     * @return 影响条数
     */
    int update(AttributeValuesDO record);

    /**
     * 搜索列表
     *
     * @param req 过滤参数
     * @return VO列表
     */
    List<AttributeValuesVO> search(AttributeValuesReq req);

    /**
     * 查询名称是否存在
     *
     * @param req 过滤参数
     * @return VO
     */
    AttributeValuesVO nameExist(AttributeValuesReq req);
}