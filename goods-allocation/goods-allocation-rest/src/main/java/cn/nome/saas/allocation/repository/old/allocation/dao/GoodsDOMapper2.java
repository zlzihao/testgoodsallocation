package cn.nome.saas.allocation.repository.old.allocation.dao;

import cn.nome.saas.allocation.model.old.allocation.GoodsAreaLevel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsDOMapper2 {
	List<GoodsAreaLevel> getGoodsAreaLevelList();
	void addGoodsAreaLevel(@Param("list") List<GoodsAreaLevel> list);
	void addGoodsAreaLevelDetail();
	
	public void deleteGoodsAreaLevel();
	public void deleteGoodsAreaLevelDetail();
	
	List<String> getAllocationProhibitedGoods();
}