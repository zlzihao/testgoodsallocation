package cn.nome.saas.allocation.mapper;

import cn.nome.saas.allocation.model.allocation.DemandStock;
import cn.nome.saas.allocation.model.allocation.Stock;
import cn.nome.saas.allocation.repository.entity.allocation.OutOfStockGoodsDO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * StockMapper
 *
 * @author Bruce01.fan
 * @date 2019/6/21
 */
public class StockMapper {



    public static List<Stock> mapperToDemand(List<OutOfStockGoodsDO> outOfStockGoodsDOList) {

        return outOfStockGoodsDOList.stream().map(outOfStockGoodsDO -> {
            Stock stock = new Stock();
            BeanUtils.copyProperties(outOfStockGoodsDO,stock);
            return stock;
        }).collect(Collectors.toList());


    }
}
