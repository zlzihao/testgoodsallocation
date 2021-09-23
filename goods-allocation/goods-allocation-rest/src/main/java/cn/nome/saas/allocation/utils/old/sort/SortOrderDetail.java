package cn.nome.saas.allocation.utils.old.sort;


import cn.nome.saas.allocation.model.old.issue.OrderDetailVo;

import java.util.Comparator;

public class SortOrderDetail implements Comparator<OrderDetailVo> {
    @Override
    public int compare(OrderDetailVo o1, OrderDetailVo o2) {

        int rst = o1.getIsIssue() - o2.getIsIssue();
        if (rst == 0) {
            rst = o1.getCategoryName().compareTo(o2.getCategoryName());
            if (rst == 0) {
                rst = o1.getMidCategoryName().compareTo(o2.getMidCategoryName());
                if (rst == 0) {
                    rst = o1.getSmallCategoryName().compareTo(o2.getSmallCategoryName());
                    if (rst == 0) {
                        rst = o1.getPackageQty().compareTo(o2.getPackageQty());
                    }
                }
            }
        }
        return rst;
    }
}
