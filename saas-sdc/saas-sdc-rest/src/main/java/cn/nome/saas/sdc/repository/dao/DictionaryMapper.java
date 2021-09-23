package cn.nome.saas.sdc.repository.dao;

import cn.nome.saas.sdc.repository.entity.DictionaryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
public interface DictionaryMapper {

    /**
     * 查询
     *
     * @param dictionaryCode 字典编码
     * @return DO列表
     */
    List<DictionaryDO> query(@Param("dictionaryCode") String dictionaryCode);

}
