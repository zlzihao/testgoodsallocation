package cn.nome.saas.sdc.service;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.form.SeasonChangeForm;
import cn.nome.saas.sdc.model.req.SeasonChangeReq;
import cn.nome.saas.sdc.model.vo.SeasonChangeVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author lizihao@nome.com
 */
public interface SeasonChangeService {
    List<SeasonChangeVO> getPageList(SeasonChangeReq req, Page page);

    List<SeasonChangeVO> selectByCondition(SeasonChangeReq req);

    void exportExcel(HttpServletResponse response, Integer userCode);

    int importExcel(HttpServletResponse response, MultipartFile file);

    int update(SeasonChangeForm form, Integer userCode);

    int deleted(long id, Integer userCode);
}
