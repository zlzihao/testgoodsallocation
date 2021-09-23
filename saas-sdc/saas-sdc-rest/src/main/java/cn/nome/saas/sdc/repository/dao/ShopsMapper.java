package cn.nome.saas.sdc.repository.dao;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.ShopsReq;
import cn.nome.saas.sdc.repository.entity.ShopsDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@Repository
public interface ShopsMapper {
    /**
     * 插入
     *
     * @param record DO
     * @return 影响条数
     */
    int insertSelective(ShopsDO record);

    /**
     * 查询单条记录
     *
     * @param req 过滤参数
     * @return DO
     */
    ShopsDO queryRow(@Param("req") ShopsReq req);

    /**
     * 查询所有(不分页)
     *
     * @param req 过滤参数
     * @return DO列表
     */
    List<ShopsDO> queryAll(@Param("req") ShopsReq req);

    /**
     * 更新
     *
     * @param record DO
     * @return 影响条数
     */
    int update(ShopsDO record);

    /**
     * @param record
     * @return
     */
    int clearMarkingArea(ShopsDO record);

    int resetAreaId(ShopsDO record);

    /**
     * 总数
     *
     * @param req 过滤条件
     * @return 总条目数
     */
    Integer pageCount(@Param("req") ShopsReq req);

    /**
     * 搜索列表
     *
     * @param req  过滤参数
     * @param page 分页参数
     * @return DO列表
     */
    List<ShopsDO> search(@Param("req") ShopsReq req, @Param("page") Page page);

    int batchUpdate(@Param("list") List<ShopsDO> list);
}
