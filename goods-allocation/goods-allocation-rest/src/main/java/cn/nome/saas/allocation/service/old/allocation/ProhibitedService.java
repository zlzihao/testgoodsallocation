package cn.nome.saas.allocation.service.old.allocation;

import cn.nome.saas.allocation.model.old.allocation.ProhibitedGoods;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface ProhibitedService {

	/**
	 * 判断是否为禁品
	 * 
	 * @param map
	 * @param shopId
	 * @param matCode
	 * @return
	 */
	public boolean checkIfIsProhibited(Map<String, Map<String, ProhibitedGoods>> map, String shopId, String matCode);

	/**
	 * 将禁品列表放到map中，方便读取
	 * 
	 * @return
	 */
	public Map<String, Map<String, ProhibitedGoods>> getProhibitedGoods();

	/**
	 * 将禁品列表放到map中，方便读取
	 *
	 * @return
	 */
	public Map<String, Map<String, ProhibitedGoods>> getProhibitedGoods(Set<String> shopIds);

	/**
	 * 将禁品列表放到map中，方便读取
	 *
	 * @return
	 */
	public Map<String, Map<String, ProhibitedGoods>> getProhibitedGoodsByDate(Set<String> shopIds, Date date);

	/**
	 * 货盘处理
	 */
	public void processGoodsArea();

	/**
	 * 调拨易碎品
	 */
	Map<String, String> getAllocationProhibitedGoods();

}
