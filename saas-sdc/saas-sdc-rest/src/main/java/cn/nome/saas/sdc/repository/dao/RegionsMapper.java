package cn.nome.saas.sdc.repository.dao;

import cn.nome.saas.sdc.model.req.RegionsReq;
import cn.nome.saas.sdc.repository.entity.RegionsDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/10/12 14:02
 */
@Repository
public interface RegionsMapper {

    /**
     * 查询列表
     *
     * @param req 过滤条件
     * @return 结果列表
     */
    List<RegionsDO> getList(@Param("req") RegionsReq req);
}
