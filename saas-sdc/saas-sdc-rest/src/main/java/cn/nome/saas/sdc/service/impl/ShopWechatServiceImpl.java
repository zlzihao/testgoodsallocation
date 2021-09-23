package cn.nome.saas.sdc.service.impl;

import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.ShopWechatReq;
import cn.nome.saas.sdc.model.vo.ShopWechatVO;
import cn.nome.saas.sdc.repository.dao.ShopWechatMapper;
import cn.nome.saas.sdc.repository.entity.ShopWechatDO;
import cn.nome.saas.sdc.service.ShopWechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2020/3/4 10:48
 */
@Service
public class ShopWechatServiceImpl implements ShopWechatService {

    private ShopWechatMapper shopWechatMapper;

    @Autowired
    public ShopWechatServiceImpl(ShopWechatMapper shopWechatMapper) {
        this.shopWechatMapper = shopWechatMapper;
    }

    @Override
    public int insertSelective(ShopWechatDO record) {
        return shopWechatMapper.insertSelective(record);
    }

    @Override
    public int updateByPrimaryKeySelective(ShopWechatDO record) {
        return shopWechatMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<ShopWechatVO> search(ShopWechatReq req, Page page) {
        if (page != null) {
            Integer count = shopWechatMapper.pageCount(req);
            page.setTotalRecord(count);
        }
        List<ShopWechatDO> listDO = shopWechatMapper.search(req, page);

        return BaseConvertor.convertList(listDO, ShopWechatVO.class);
    }

    @Override
    public ShopWechatVO query(ShopWechatReq req) {
        ShopWechatDO record = shopWechatMapper.query(req);

        return BaseConvertor.convert(record, ShopWechatVO.class);
    }
}
