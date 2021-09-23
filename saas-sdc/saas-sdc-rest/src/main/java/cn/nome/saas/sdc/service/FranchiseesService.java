package cn.nome.saas.sdc.service;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.FranchiseesReq;
import cn.nome.saas.sdc.model.vo.FranchiseesVO;
import cn.nome.saas.sdc.repository.entity.FranchiseesDO;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/25 13:54
 */
public interface FranchiseesService {

    /**
     * 插入
     *
     * @param record DO
     * @return 影响条数
     */
    int insertSelective(FranchiseesDO record);

    /**
     * 查询
     *
     * @param id 主键ID
     * @return VO
     */
    FranchiseesVO selectByPrimaryKey(Integer id);

    /**
     * 更新
     *
     * @param record DO
     * @return 影响条数
     */
    int updateByPrimaryKeySelective(FranchiseesDO record);

    /**
     * 搜索列表
     *
     * @param req  过滤参数
     * @param page 分页参数
     * @return VO列表
     */
    List<FranchiseesVO> search(FranchiseesReq req, Page page);
}