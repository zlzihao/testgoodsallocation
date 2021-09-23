package cn.nome.saas.sdc.service.impl;

import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.saas.sdc.model.req.SearchBusinessAttributesReq;
import cn.nome.saas.sdc.model.vo.SearchBusinessAttributesVO;
import cn.nome.saas.sdc.repository.dao.SearchBusinessAttributesMapper;
import cn.nome.saas.sdc.repository.entity.SearchBusinessAttributesDO;
import cn.nome.saas.sdc.service.SearchBusinessAttributesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/17 14:15
 */
@Service
public class SearchBusinessAttributesServiceImpl implements SearchBusinessAttributesService {

    private SearchBusinessAttributesMapper searchBusinessAttributesMapper;

    @Autowired
    public SearchBusinessAttributesServiceImpl(SearchBusinessAttributesMapper searchBusinessAttributesMapper) {
        this.searchBusinessAttributesMapper = searchBusinessAttributesMapper;
    }

    @Override
    public List<SearchBusinessAttributesVO> search(SearchBusinessAttributesReq req) {
        List<SearchBusinessAttributesDO> listDO = searchBusinessAttributesMapper.search(req);
        return BaseConvertor.convertList(listDO, SearchBusinessAttributesVO.class);
    }

    @Override
    public SearchBusinessAttributesVO getAttributeValue(SearchBusinessAttributesReq req) {
        SearchBusinessAttributesDO record = searchBusinessAttributesMapper.getAttributeValue(req);
        return BaseConvertor.convert(record, SearchBusinessAttributesVO.class);
    }

    @Override
    public List<SearchBusinessAttributesVO> searchAttribute(SearchBusinessAttributesReq req) {
        List<SearchBusinessAttributesDO> listDO = searchBusinessAttributesMapper.searchAttribute(req);
        return BaseConvertor.convertList(listDO, SearchBusinessAttributesVO.class);
    }

    @Override
    public List<SearchBusinessAttributesVO> filterAttributes(SearchBusinessAttributesReq req) {
        List<SearchBusinessAttributesDO> listDO = searchBusinessAttributesMapper.filterAttributes(req);
        return BaseConvertor.convertList(listDO, SearchBusinessAttributesVO.class);
    }

    @Override
    public List<SearchBusinessAttributesVO> queryAttributes(SearchBusinessAttributesReq req) {
        List<SearchBusinessAttributesDO> listDO = searchBusinessAttributesMapper.queryAttributes(req);
        return BaseConvertor.convertList(listDO, SearchBusinessAttributesVO.class);
    }

    @Override
    public List<SearchBusinessAttributesVO> queryAttributeTypes(SearchBusinessAttributesReq req) {
        List<SearchBusinessAttributesDO> listDO = searchBusinessAttributesMapper.queryAttributeTypes(req);
        return BaseConvertor.convertList(listDO, SearchBusinessAttributesVO.class);
    }

    @Override
    public String getAttributeValueString(SearchBusinessAttributesReq req) {
        SearchBusinessAttributesVO vo = getAttributeValue(req);
        return vo == null ? "" : vo.getAttributeValue();
    }

    @Override
    public Integer getAttributeValueInteger(SearchBusinessAttributesReq req) {
        SearchBusinessAttributesVO vo = getAttributeValue(req);
        if (vo == null) {
            return 0;
        }
        return Integer.parseInt(vo.getAttributeValue());
    }

    @Override
    public Integer getAttributeValueId(SearchBusinessAttributesReq req) {
        SearchBusinessAttributesVO vo = getAttributeValue(req);
        return vo == null ? 0 : vo.getAttributeValueId();
    }
}
