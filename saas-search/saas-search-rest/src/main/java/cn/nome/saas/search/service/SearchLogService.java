package cn.nome.saas.search.service;

import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.saas.search.model.vo.SearchLogVO;
import cn.nome.saas.search.model.vo.SearchLogWordsVO;
import cn.nome.saas.search.repository.dao.SearchLogMapper;
import cn.nome.saas.search.repository.entity.SearchLogDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SearchLogService {

    @Autowired
    private SearchLogMapper searchLogMapper;

    @Transactional
    public int deleteByPrimaryKey(Integer id) {
        return searchLogMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public int insertSelective(SearchLogDO record) {
        return searchLogMapper.insertSelective(record);
    }

    @Transactional
    public int updateByPrimaryKeySelective(SearchLogDO record) {
        return searchLogMapper.updateByPrimaryKeySelective(record);
    }

    public SearchLogVO selectByPrimaryKey(Integer id) {
        SearchLogDO record = searchLogMapper.selectByPrimaryKey(id);
        return BaseConvertor.convert(record, SearchLogVO.class);
    }

    /**
     * 获取几天前的搜索记录
     *
     * @param beforeDays
     * @return
     */
    public List<SearchLogWordsVO> getLogBeforeDays(int beforeDays) {
        List<SearchLogDO> logDOS = searchLogMapper.getLogBeforeDays(beforeDays);
        if (logDOS == null || logDOS.isEmpty()) {
            return Collections.emptyList();
        }
        List<SearchLogWordsVO> wordsVOS = new ArrayList<>(logDOS.size());
        logDOS.stream().forEach(logDO -> wordsVOS.add(new SearchLogWordsVO(logDO.getUserId(), logDO.getInputWord(), logDO.getSplitWord(), logDO.getIp())));
        return wordsVOS;
    }
}
