package cn.nome.saas.sdc.service.impl;

import cn.nome.platform.common.mybatis.rw.annotation.Master;
import cn.nome.platform.common.mybatis.rw.annotation.Slave;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.model.req.ShopsReq;
import cn.nome.saas.sdc.model.vo.ShopOptionVO;
import cn.nome.saas.sdc.model.vo.ShopsVO;
import cn.nome.saas.sdc.repository.dao.ShopsMapper;
import cn.nome.saas.sdc.repository.entity.ShopsDO;
import cn.nome.saas.sdc.service.ShopsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@Service
public class ShopsServiceImpl implements ShopsService {

    private ShopsMapper shopsMapper;

    @Autowired
    public ShopsServiceImpl(ShopsMapper shopsMapper) {
        this.shopsMapper = shopsMapper;
    }

    @Override
    @Transactional
    public int insertSelective(ShopsDO record) {
        return shopsMapper.insertSelective(record);
    }

    @Master
    @Override
    @Transactional
    public int update(ShopsDO record) {
        return shopsMapper.update(record);
    }

    @Master
    @Override
    @Transactional
    public void updateShopsMarkingArea(List<ShopsDO> records, Integer corpId) {
        ShopsDO record = new ShopsDO();
        record.setMarketingAreaId(0);
        record.setCorpId(corpId);
        clearMarkingArea(record);
        records.forEach(item -> {
            shopsMapper.update(item);
        });
    }

    @Master
    @Override
    public int clearMarkingArea(ShopsDO record) {
        return shopsMapper.clearMarkingArea(record);
    }

    @Slave
    @Override
    public List<ShopsVO> search(ShopsReq req, Page page) {
        if (page != null) {
            Integer count = shopsMapper.pageCount(req);
            page.setTotalRecord(count);
        }
        List<ShopsDO> listDO = shopsMapper.search(req, page);

        return BaseConvertor.convertList(listDO, ShopsVO.class);
    }

    @Slave
    @Override
    public ShopsVO queryRow(ShopsReq req) {
        ShopsDO record = shopsMapper.queryRow(req);
        return BaseConvertor.convert(record, ShopsVO.class);
    }

    @Override
    public List<ShopOptionVO> queryAll(ShopsReq req) {
        List<ShopsDO> listDO = shopsMapper.queryAll(req);
        return BaseConvertor.convertList(listDO, ShopOptionVO.class);
    }

    @Override
    public List<ShopsVO> getAll(ShopsReq req) {
        List<ShopsDO> listDO = shopsMapper.queryAll(req);
        return BaseConvertor.convertList(listDO, ShopsVO.class);
    }

    @Override
    public int batchUpdate(List<ShopsDO> record) {
        return shopsMapper.batchUpdate(record);
    }
}
