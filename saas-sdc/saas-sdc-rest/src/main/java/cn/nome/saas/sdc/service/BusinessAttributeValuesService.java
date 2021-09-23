package cn.nome.saas.sdc.service;

import cn.nome.saas.sdc.model.req.BusinessAttributeValuesReq;
import cn.nome.saas.sdc.model.vo.BusinessAttributeValuesVO;
import cn.nome.saas.sdc.repository.entity.BusinessAttributeValuesDO;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 13:49
 */
public interface BusinessAttributeValuesService {

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
     * @return 影响条数
     */
    int insertSelective(BusinessAttributeValuesDO record);

    /**
     * 查询
     *
     * @param id 主键ID
     * @return VO
     */
    BusinessAttributeValuesVO selectByPrimaryKey(Integer id);

    /**
     * 更新
     *
     * @param record DO
     */
    void updateByPrimaryKeySelective(BusinessAttributeValuesDO record);

    /**
     * 查询
     *
     * @param req 过滤参数
     * @return VO列表
     */
    List<BusinessAttributeValuesVO> query(BusinessAttributeValuesReq req);
}