package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.rule.NewExpress;
import cn.nome.saas.allocation.model.rule.NewShopExpress;
import cn.nome.saas.allocation.repository.entity.allocation.ShopExpressDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ShopExpressDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/6/18
 */
public interface ShopExpressDOMapper {

    List<ShopExpressDO> getShopExpressList(@Param("listA")Set listA, @Param("listB")Set listB);

    List<ShopExpressDO> getShopExpressListByPage(Map<String,Object> param);

    List<NewExpress> loadAllNexExpress();

    Integer insertNewExpressData(@Param("list") List<NewShopExpress> list);

    List<NewShopExpress> selectNewExpressByProvince(@Param("list") List<String> provinceList);

    Integer deleteNextExpress();
}
