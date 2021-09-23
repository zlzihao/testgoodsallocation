package cn.nome.saas.allocation.repository.dao.allocation;


import cn.nome.saas.allocation.model.issue.IssueDetailDo;
import cn.nome.saas.allocation.model.issue.OrderDetailDo;
import cn.nome.saas.allocation.model.issue.OrderDetailParam;
import cn.nome.saas.allocation.model.issue.OrderSkuModifyParam;
import cn.nome.saas.allocation.repository.entity.allocation.IssueUndoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IssueUndoDOMapper {

    int batchInsertTab(@Param("insertData") List<IssueUndoDO> insertData);

    List<IssueUndoDO> getUndoData(@Param("taskId") int taskId, @Param("shopId") String shopId);

    List<OrderDetailDo> issueUndoDetail(@Param("detailParam") OrderDetailParam detailParam);

    IssueDetailDo getDetail(@Param("shopId") String shopId, @Param("matCode") String matCode, @Param("sizeId") String sizeId, @Param("taskId") int taskId);

    int modifySkuPackageQty(@Param("param") OrderSkuModifyParam param);

     /*非配发分类 start */

    List<String> categoryList(@Param("taskId") int taskId, @Param("shopId") String shopId);

    List<String> allMidCategorys(@Param("taskId") int taskId, @Param("shopId") String shopId);

    List<String> midCategorys(@Param("categoryName") String categoryName, @Param("taskId") int taskId, @Param("shopId") String shopId);

    List<String> allSmallCategorys(@Param("taskId") int taskId, @Param("shopId") String shopId);

    List<String> smallCategorys(@Param("midCategoryName") String midCategoryName, @Param("taskId") int taskId, @Param("shopId") String shopId);
    /*非配发分类 end */
}

