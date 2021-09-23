package cn.nome.saas.sdc.manager;

import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.model.form.FranchiseesForm;
import cn.nome.saas.sdc.model.req.FranchiseesReq;
import cn.nome.saas.sdc.model.req.SearchBusinessAttributesReq;
import cn.nome.saas.sdc.model.vo.FranchiseesVO;
import cn.nome.saas.sdc.repository.entity.FranchiseesDO;
import cn.nome.saas.sdc.service.FranchiseesService;
import cn.nome.saas.sdc.service.SearchBusinessAttributesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/25 13:54
 */
@Component
public class FranchiseesServiceManager {

    private FranchiseesService franchiseesService;

    private SearchBusinessAttributesService searchBusinessAttributesService;

    @Autowired
    public FranchiseesServiceManager(FranchiseesService franchiseesService, SearchBusinessAttributesService searchBusinessAttributesService) {
        this.franchiseesService = franchiseesService;
        this.searchBusinessAttributesService = searchBusinessAttributesService;
    }

    public List<FranchiseesVO> search(FranchiseesReq req, Page page) {
        List<FranchiseesVO> listVO = franchiseesService.search(req, page);
        SearchBusinessAttributesReq businessAttributesReq = new SearchBusinessAttributesReq();
        businessAttributesReq.setIsDeleted(Constant.IS_DELETE_FALSE);
        businessAttributesReq.setBusinessType(Constant.BUSINESS_TYPE_FRANCHISEES);
        //todo opt performance
        for (FranchiseesVO vo : listVO) {
            businessAttributesReq.setBusinessId(vo.getId());
            businessAttributesReq.setAttributeName(Constant.FIXED_ATTRIBUTE_FRANCHISEE_NAME);
            vo.setFranchiseeName(searchBusinessAttributesService.getAttributeValueString(businessAttributesReq));

            businessAttributesReq.setAttributeName(Constant.FIXED_ATTRIBUTE_CONTRACT_SUBJECT);
            vo.setContractSubject(searchBusinessAttributesService.getAttributeValueString(businessAttributesReq));

            businessAttributesReq.setAttributeName(Constant.FIXED_ATTRIBUTE_CUSTOMER_LEVEL);
            vo.setCustomerLevel(searchBusinessAttributesService.getAttributeValueString(businessAttributesReq));

            businessAttributesReq.setAttributeName(Constant.FIXED_ATTRIBUTE_VALID_SHOPS);
            vo.setValidShops(searchBusinessAttributesService.getAttributeValueInteger(businessAttributesReq));
        }
        return listVO;
    }

    public FranchiseesForm getDetail(Integer id) {
        FranchiseesVO franchiseesVO = franchiseesService.selectByPrimaryKey(id);
        return BaseConvertor.convert(franchiseesVO, FranchiseesForm.class);
    }

    public Integer insert(FranchiseesForm form) {
        FranchiseesDO record = BaseConvertor.convert(form, FranchiseesDO.class);
        return franchiseesService.insertSelective(record);
    }

    public Integer update(FranchiseesForm form) {
        FranchiseesDO record = BaseConvertor.convert(form, FranchiseesDO.class);
        return franchiseesService.updateByPrimaryKeySelective(record);
    }
}
