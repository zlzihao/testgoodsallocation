package cn.nome.saas.sdc.service;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.bigData.model.DwsViewWarehouseInfoVO;
import cn.nome.saas.sdc.model.form.WarehouseForm;
import cn.nome.saas.sdc.model.req.WarehouseReq;
import cn.nome.saas.sdc.model.vo.WarehouseVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author lizihao@nome.com
 */
public interface WarehouseConfigService {
    List<WarehouseVO> getPageList(WarehouseReq req, Page page);

    void exportExcel(HttpServletResponse response, Integer userCode);

    int importExcel(HttpServletResponse response, MultipartFile file);

    int update(WarehouseForm form, Integer userCode);

    void delete(Long id, Integer userCode);

    List<DwsViewWarehouseInfoVO> getAllWareHouse();

}
