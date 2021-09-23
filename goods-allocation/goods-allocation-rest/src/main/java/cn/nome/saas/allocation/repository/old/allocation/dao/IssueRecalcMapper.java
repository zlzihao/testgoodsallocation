package cn.nome.saas.allocation.repository.old.allocation.dao;

import org.apache.ibatis.annotations.Param;

public interface IssueRecalcMapper {

    void del_issue_undo(@Param("shopId") String shopId, @Param("taskId") int taskId);

    void del_issue_in_stock(@Param("shopId") String shopId, @Param("taskId") int taskId);

    void del_issue_goods_data(@Param("shopId") String shopId, @Param("taskId") int taskId);

    void del_issue_detail(@Param("shopId") String shopId, @Param("taskId") int taskId);

    void del_issue_category_data(@Param("shopId") String shopId, @Param("taskId") int taskId);

    void del_issue_mid_category_qty(@Param("shopId") String shopId, @Param("taskId") int taskId);

    void del_issue_midcategory_data(@Param("shopId") String shopId, @Param("taskId") int taskId);

    void del_issue_need_stock(@Param("shopId") String shopId, @Param("taskId") int taskId);

    void del_issue_out_stock_remain(@Param("taskId") int taskId);

    int del_issue_out_stock_remain_by_taskId(@Param("taskId") int taskId);
}

