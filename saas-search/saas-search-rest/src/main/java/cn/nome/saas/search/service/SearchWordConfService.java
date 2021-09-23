package cn.nome.saas.search.service;

import cn.nome.saas.search.repository.dao.SearchWordConfMapper;
import cn.nome.saas.search.repository.entity.SearchWordConfDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchWordConfService {

    @Autowired
    private SearchWordConfMapper searchWordConfMapper;

    public int insertSelective(SearchWordConfDO form) {
        return searchWordConfMapper.insertSelective(form);
    }

    public int updateByPrimaryKeySelective(SearchWordConfDO form) {
        return searchWordConfMapper.updateByPrimaryKeySelective(form);
    }

    public SearchWordConfDO selectByPrimaryKey(Integer id) {
        SearchWordConfDO record = searchWordConfMapper.selectByPrimaryKey(id);
        return record;
    }

    /**
     * 根据类型获取有效单词列表
     *
     * @param wordType
     * @return
     */
    public List<SearchWordConfDO> validWords(Integer wordType) {
        return searchWordConfMapper.validWordsByType(wordType);
    }

    /**
     * 根据类型获取有效单词分页列表
     *
     * @param wordType
     * @return
     */
    public List<SearchWordConfDO> validPageWordsByType(Integer wordType, Integer offset, Integer pageSize) {
        return searchWordConfMapper.validPageWordsByType(wordType, offset, pageSize);
    }

    /**
     * 根据类型获取有效单词列表总数
     * @param wordType
     * @return
     */
    public int validCountWordsByType(Integer wordType) {
        return searchWordConfMapper.validCountWordsByType(wordType);
    }

    /**
     * 设置发布状态
     *
     * @param id
     * @return
     */
    public int setRelease(Integer id) {
        return searchWordConfMapper.setRelease(id);
    }

    /**
     * 设置停止
     *
     * @param id
     * @return
     */
    public int setStop(Integer id) {
        return searchWordConfMapper.setStop(id);
    }
}
