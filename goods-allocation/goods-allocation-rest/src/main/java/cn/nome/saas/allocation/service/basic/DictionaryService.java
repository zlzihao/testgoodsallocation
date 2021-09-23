package cn.nome.saas.allocation.service.basic;

import cn.nome.saas.allocation.repository.dao.allocation.DictionaryDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.DictionaryDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * DictionaryService
 *
 * @author Bruce01.fan
 * @date 2019/7/28
 */
@Service
public class DictionaryService {

    @Autowired
    DictionaryDOMapper dictionaryDOMapper;

    public List<DictionaryDO> getDictionaryList() {
        return dictionaryDOMapper.getDictionaryList();
    }
}
