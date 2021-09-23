package cn.nome.saas.sdc.manager;

import cn.nome.saas.sdc.model.req.QueryDictionaryReq;
import cn.nome.saas.sdc.model.vo.DictionaryVO;
import cn.nome.saas.sdc.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/4 13:39
 */
@Component
public class DictionaryServiceManager {

    private DictionaryService dictionaryService;

    @Autowired
    public DictionaryServiceManager(DictionaryService dictionaryService) {

        this.dictionaryService = dictionaryService;
    }

    public List<DictionaryVO> query(QueryDictionaryReq req) {
        return dictionaryService.query(req);
    }

}
