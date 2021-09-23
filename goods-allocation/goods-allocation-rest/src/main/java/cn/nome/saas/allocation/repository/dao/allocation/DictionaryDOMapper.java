package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.DictionaryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface DictionaryDOMapper {

    List<DictionaryDO> getDictionaryList();

    List<DictionaryDO> getDictionaryByType(@Param("type") String type);

}
