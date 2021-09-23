package cn.nome.saas.allocation.repository.dao.allocation;


import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.model.old.allocation.NewGoodsIssueRangeReq;
import cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO;
import cn.nome.saas.allocation.repository.entity.allocation.ForbiddenRuleDO;
import cn.nome.saas.allocation.repository.entity.allocation.NewGoodsIssueRangeDO;
import cn.nome.saas.allocation.repository.entity.allocation.UserAdminDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 禁配相关
 *
 * @author Bruce01.fan
 * @date 2019/5/24
 */
public interface NewGoodsIssueRangeMapper {


    /**
     * pageCount
     * @param req req
     * @return return
     */
    int pageCount(@Param("req") NewGoodsIssueRangeReq req);

    List<NewGoodsIssueRangeDO> pageList(@Param("req") NewGoodsIssueRangeReq req);

    List<NewGoodsIssueRangeDO> selectByIssueFin(@Param("issueFin") Integer issueFin);

    NewGoodsIssueRangeDO selectByMatCodeSizeId(@Param("matCode") String matCode, @Param("sizeId") String sizeId);



    NewGoodsIssueRangeDO selectByMatCodeSizeName(@Param("matCode") String matCode, @Param("sizeName") String sizeName);

    Integer updateByPrimaryKeySelective(@Param("req") NewGoodsIssueRangeReq req);

    Integer updateByMatCodeSizeId(@Param("req") NewGoodsIssueRangeReq req);
    Integer updateByMatCodeSizeName(@Param("req") NewGoodsIssueRangeReq req);

    Integer insertSelective(NewGoodsIssueRangeDO newGoodsIssueRangeDO);
}
