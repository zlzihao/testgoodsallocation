package cn.nome.saas.sdc.repository.dao;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.FranchiseesReq;
import cn.nome.saas.sdc.repository.entity.FranchiseesDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/25 13:54
 */
public interface FranchiseesMapper {

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
     * @return DO
     */
    FranchiseesDO selectByPrimaryKey(@Param("id") Integer id);

    /**
     * 更新
     *
     * @param record DO
     * @return 影响条数
     */
    int updateByPrimaryKeySelective(FranchiseesDO record);

    /**
     * 统计总数
     *
     * @param req 过滤参数
     * @return 总数
     */
    Integer pageCount(@Param("req") FranchiseesReq req);

    /**
     * 搜索列表
     *
     * @param req  过滤参数
     * @param page 分页参数
     * @return DO列表
     */
    List<FranchiseesDO> search(@Param("req") FranchiseesReq req, @Param("page") Page page);
}
