package cn.nome.saas.allocation.repository.dao.allocation;

import java.util.List;

import cn.nome.saas.allocation.repository.entity.allocation.NewGoodsIssueRangeDO;
import cn.nome.saas.allocation.repository.entity.allocation.NewGoodsIssueRangeDetailDO;
import org.apache.ibatis.annotations.Param;

import cn.nome.platform.common.utils.Page;

/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2019-11-15 14:35:14
 */
public interface NewGoodsIssueRangeDetailMapper {

	int deleteByPrimaryKey(@Param("id") Integer id);


	int insertSelective(NewGoodsIssueRangeDetailDO record);

	NewGoodsIssueRangeDetailDO selectByPrimaryKey(@Param("id") Integer id);

	List<NewGoodsIssueRangeDetailDO> selectByRangeId(@Param("rangeId") Integer id, @Param("planFlag") Integer planFlag);

	List<NewGoodsIssueRangeDetailDO> selectByIssueFin(@Param("rangeId") Integer id, @Param("issueFin") Integer issueFin);

	List<NewGoodsIssueRangeDetailDO> selectByShopCode(@Param("rangeId") Integer id, @Param("shopCode") String shopCode);

	int shopCount(@Param("list") List<NewGoodsIssueRangeDO> list);


	/**
	 * delByRangeId
	 * @param rangeId
	 * @param planFlag
	 * @return
	 */
	int delByRangeId(@Param("rangeId") Integer rangeId, @Param("planFlag") Integer planFlag);

	/**
	 * 删除此商品下所有明细(除已配发)
	 * @param rangeId
	 * @param issueFin
	 * @return
	 */
	int delByMatCodeWithFin(@Param("matCode") String matCode, @Param("issueFin") Integer issueFin);

	int updateByPrimaryKeySelective(NewGoodsIssueRangeDetailDO record);

	/**
	 * batchInsert
	 * @param list
	 * @return
	 */
	int batchInsert(@Param("list") List<NewGoodsIssueRangeDetailDO> list);

	/**
	 * getNotSaleTime 获取还未到上市时间的新品
	 * @return
	 */
	List<NewGoodsIssueRangeDetailDO> getNotSaleTime();

	/**
	 * getNotSaleTime 根据门店最近一个到货日期获取门店的配发量
	 * @return
	 */
	List<NewGoodsIssueRangeDetailDO> getIssueNumBySaleTime();

	/**
	 * 获取新品铺货范围 新品白名单
	 * @return
	 */
	List<NewGoodsIssueRangeDetailDO> selectNewGoodsWhiteList();

	/**
	 * 获取新品铺货范围 新品白名单
	 * @return
	 */
	List<NewGoodsIssueRangeDetailDO> selectNewGoodsList(@Param("planFlag") Integer planFlag, @Param("rangeId") Integer rangeId);


}
