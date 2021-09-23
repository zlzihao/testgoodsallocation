package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.DownTemplateDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ：godsfer
 * @date ：Created in 2019/8/8 13:51
 * @description：下载模板配置
 * @modified By：
 * @version: 1.0.0$
 */
public interface DownTemplateDOMapper {
    DownTemplateDO queryByCode(String code);

    DownTemplateDO queryByName(String name);

    List<DownTemplateDO> queryAllTplList(@Param("businessCode") String businessCode);

    List<DownTemplateDO> queryAllBusList();

    List<DownTemplateDO> queryDownLoadTplList(String code);
}
