package cn.nome.saas.sdc.service;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.AttributesReq;
import cn.nome.saas.sdc.model.vo.AttributesVO;
import cn.nome.saas.sdc.repository.entity.AttributesDO;

import java.util.List;


/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/9 15:23
 */
public interface AttributesService {

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
     * @param record AttributesDO
     */
    void insertSelective(AttributesDO record);

    /**
     * 查询
     *
     * @param id 主键ID
     * @return AttributesVO
     */
    AttributesVO selectByPrimaryKey(Integer id);

    /**
     * 更新
     *
     * @param record AttributesDO
     */
    void update(AttributesDO record);

    /**
     * 搜索列表
     *
     * @param req  AttributesReq 过滤参数
     * @param page Page 分页参数
     * @return VO列表
     */
    List<AttributesVO> search(AttributesReq req, Page page);

    /**
     * 检查名称是否存在
     *
     * @param req 过滤参数
     * @return AttributesVO
     */
    AttributesVO nameExist(AttributesReq req);
}