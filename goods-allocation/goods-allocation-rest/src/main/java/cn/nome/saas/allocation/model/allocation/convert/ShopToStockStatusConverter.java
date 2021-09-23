package cn.nome.saas.allocation.model.allocation.convert;

import cn.nome.platform.common.utils.excel.converter.Converter;

public class ShopToStockStatusConverter implements Converter<Integer> {

    @Override
    public Integer toObjField(String str) {
        return null;
    }

    @Override
    public String toExcelColumn(Integer num) {
        if(num == null) {
            return null;
        } else if(num.intValue() == 0) {
            return "未审核";
        } else if(num.intValue() == 1) {
            return "已审核";
        }
        return null;
    }
}
