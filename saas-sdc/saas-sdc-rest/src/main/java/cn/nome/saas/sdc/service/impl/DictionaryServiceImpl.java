package cn.nome.saas.sdc.service.impl;

import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.saas.sdc.model.req.QueryDictionaryReq;
import cn.nome.saas.sdc.model.vo.DictionaryVO;
import cn.nome.saas.sdc.repository.dao.DictionaryMapper;
import cn.nome.saas.sdc.repository.entity.DictionaryDO;
import cn.nome.saas.sdc.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/4 13:39
 */
@Service
public class DictionaryServiceImpl implements DictionaryService {

    private DictionaryMapper dictionaryMapper;

    @Autowired
    public DictionaryServiceImpl(DictionaryMapper dictionaryMapper) {
        this.dictionaryMapper = dictionaryMapper;
    }

    @Override
    public List<DictionaryVO> query(QueryDictionaryReq req) {
        List<DictionaryDO> listDO = dictionaryMapper.query(req.getDictionaryCode());
        return BaseConvertor.convertList(listDO, DictionaryVO.class);
    }

    @Override
    public HashMap<Integer, String> queryMap(QueryDictionaryReq req) {
        List<DictionaryVO> listVO = query(req);
        return (HashMap<Integer, String>) listVO.stream().collect(Collectors.toMap(DictionaryVO::getId, DictionaryVO::getName));
    }
}
