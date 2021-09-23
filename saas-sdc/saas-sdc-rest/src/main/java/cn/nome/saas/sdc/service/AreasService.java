package cn.nome.saas.sdc.service;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.AreasReq;
import cn.nome.saas.sdc.model.vo.AreaOptionVO;
import cn.nome.saas.sdc.model.vo.AreasVO;
import cn.nome.saas.sdc.repository.entity.AreasDO;
import cn.nome.saas.sdc.repository.entity.ShopsDO;

import java.util.HashMap;
import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
public interface AreasService {

    /**
     * 添加区域
     *
     * @param record AreasDO
     * @return 影响条数
     */
    int add(AreasDO record);

    /**
     * 查询
     *
     * @param id 主键ID
     * @return AreasVO
     */
    AreasVO selectByPrimaryKey(Integer id);

    /**
     * 更新
     *
     * @param record AreasDO
     * @return 影响条数
     */
    int update(AreasDO record);

    /**
     * @param corpId
     * @param areaTypeId
     * @param bigAreaMap
     * @param smallAreaMap
     */
    void importMarkingArea(Integer corpId, Integer areaTypeId, HashMap<String, AreasDO> bigAreaMap, HashMap<String, List<AreasDO>> smallAreaMap);

    /**
     * @param record
     * @return
     */
    int clear(AreasDO record);

    int softDelete(AreasDO areasDO, ShopsDO shopsDO);

    /**
     * 搜索列表
     *
     * @param req  请求参数
     * @param page 分页信息
     * @return VO列表
     */
    List<AreasVO> search(AreasReq req, Page page);

    /**
     * 查询列表(不分页)
     *
     * @param req 请求参数
     * @return VO列表
     */
    List<AreaOptionVO> queryAll(AreasReq req);

    /**
     * 查询列表(不分页)，组合父级名称
     *
     * @param req 请求参数
     * @return VO列表
     */
    List<AreaOptionVO> queryLevel(AreasReq req);

    /**
     * 区域列表 map
     *
     * @param req 请求参数
     * @return areaId => areaName
     */
    HashMap<Integer, String> queryLevelMap(AreasReq req);

    /**
     * 检查名称是否已存在
     *
     * @param req 过滤参数
     * @return VO
     */
    AreasVO nameExist(AreasReq req);

    /**
     * 检查区域编号是否存在
     *
     * @param req 过滤参数
     * @return VO
     */
    AreasVO areaCodeExist(AreasReq req);

    /**
     * 拼接层级关系的区域名称
     *
     * @param id 区域ID
     * @return 拼接后的区域名称串
     */
    String concatAreaName(Integer id);
}