package cn.nome.saas.sdc.repository.dao;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.AreasReq;
import cn.nome.saas.sdc.repository.entity.AreasDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
public interface AreasMapper {

    /**
     * 添加
     *
     * @param record DO
     * @return 影响条数
     */
    int add(AreasDO record);

    /**
     * 查询
     *
     * @param id 主键ID
     * @return DO
     */
    AreasDO selectByPrimaryKey(@Param("id") Integer id);

    /**
     * 更新
     *
     * @param record DO
     * @return 影响条数
     */
    int update(AreasDO record);

    /**
     * @param record
     * @return
     */
    int clear(AreasDO record);

    /**
     * 总数
     *
     * @param req 过滤参数
     * @return 总条目数
     */
    Integer pageCount(@Param("req") AreasReq req);

    /**
     * 搜索列表
     *
     * @param req  过滤参数
     * @param page 分页参数
     * @return DO列表
     */
    List<AreasDO> search(@Param("req") AreasReq req, @Param("page") Page page);

    /**
     * 查询所有(不分页)
     *
     * @param req 过滤条件
     * @return DO列表
     */
    List<AreasDO> queryAll(@Param("req") AreasReq req);

    /**
     * 检查名称是否存在
     *
     * @param req 过滤参数
     * @return DO
     */
    AreasDO nameExist(@Param("req") AreasReq req);

    /**
     * 检查区域编号是否存在
     *
     * @param req 过滤参数
     * @return DO
     */
    AreasDO areaCodeExist(@Param("req") AreasReq req);
}
