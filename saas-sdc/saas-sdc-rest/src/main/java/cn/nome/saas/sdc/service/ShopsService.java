package cn.nome.saas.sdc.service;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.ShopsReq;
import cn.nome.saas.sdc.model.vo.ShopOptionVO;
import cn.nome.saas.sdc.model.vo.ShopsVO;
import cn.nome.saas.sdc.repository.entity.ShopsDO;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 13:49
 */
public interface ShopsService {
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
     * @return VO
     */
    ShopsVO queryRow(ShopsReq req);

    /**
     * 查询所有(不分页)
     *
     * @param req 过滤参数
     * @return VO列表
     */
    List<ShopOptionVO> queryAll(ShopsReq req);

    /**
     * 更新
     *
     * @param record DO
     * @return 影响条数
     */
    int update(ShopsDO record);

    /**
     * @param records
     * @param corpId
     */
    void updateShopsMarkingArea(List<ShopsDO> records, Integer corpId);

    /**
     * @param record
     * @return
     */
    int clearMarkingArea(ShopsDO record);

    /**
     * 搜索列表
     *
     * @param req  过滤参数
     * @param page 分页参数
     * @return VO列表
     */
    List<ShopsVO> search(ShopsReq req, Page page);


    List<ShopsVO> getAll(ShopsReq req);

    int batchUpdate(List<ShopsDO> record);
}