package cn.nome.saas.sdc.service.impl;

import cn.nome.platform.common.mybatis.rw.annotation.Master;
import cn.nome.platform.common.mybatis.rw.annotation.Slave;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.model.req.AreasReq;
import cn.nome.saas.sdc.model.vo.AreaOptionVO;
import cn.nome.saas.sdc.model.vo.AreasVO;
import cn.nome.saas.sdc.repository.dao.AreasMapper;
import cn.nome.saas.sdc.repository.dao.ShopsMapper;
import cn.nome.saas.sdc.repository.entity.AreasDO;
import cn.nome.saas.sdc.repository.entity.ShopsDO;
import cn.nome.saas.sdc.service.AreasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@Service
public class AreasServiceImpl implements AreasService {

    private AreasMapper areasMapper;
    private ShopsMapper shopsMapper;

    @Autowired
    public AreasServiceImpl(AreasMapper areasMapper, ShopsMapper shopsMapper) {
        this.areasMapper = areasMapper;
        this.shopsMapper = shopsMapper;
    }

    @Master
    @Override
    @Transactional
    public void importMarkingArea(Integer corpId, Integer areaTypeId, HashMap<String, AreasDO> bigAreaMap, HashMap<String, List<AreasDO>> smallAreaMap) {
        AreasDO record = new AreasDO();
        record.setCorpId(corpId);
        record.setAreaTypeId(areaTypeId);
        record.setIsDeleted(Constant.IS_DELETE_TRUE);
        clear(record);

        bigAreaMap.forEach((k, v) -> {
            areasMapper.add(v);
            List<AreasDO> areasDOList = smallAreaMap.get(k);
            if (areasDOList != null) {
                areasDOList.forEach(item -> {
                    item.setParentId(v.getId());
                    areasMapper.add(item);
                });
            }
        });
    }

    @Override
    public int clear(AreasDO record) {
        return areasMapper.clear(record);
    }

    @Override
    @Transactional
    public int add(AreasDO record) {
        return areasMapper.add(record);
    }

    @Master
    @Override
    @Transactional
    public int update(AreasDO record) {
        return areasMapper.update(record);
    }

    @Master
    @Override
    @Transactional
    public int softDelete(AreasDO areasDO, ShopsDO shopsDO) {
        shopsMapper.resetAreaId(shopsDO);
        return areasMapper.update(areasDO);
    }

    @Slave
    @Override
    public List<AreasVO> search(AreasReq req, Page page) {
        if (page != null) {
            Integer count = areasMapper.pageCount(req);
            page.setTotalRecord(count);
        }
        List<AreasDO> listDO = areasMapper.search(req, page);

        return BaseConvertor.convertList(listDO, AreasVO.class);
    }

    @Override
    public List<AreaOptionVO> queryAll(AreasReq req) {
        List<AreasDO> listDO = areasMapper.queryAll(req);
        return BaseConvertor.convertList(listDO, AreaOptionVO.class);
    }

    @Override
    public List<AreaOptionVO> queryLevel(AreasReq req) {
        List<AreasDO> listDO = areasMapper.queryAll(req);
        if (listDO.size() > 0) {
            HashMap<Integer, String> areasMap = (HashMap<Integer, String>) listDO.stream().collect(Collectors.toMap(AreasDO::getId, AreasDO::getAreaName));
            for (AreasDO areasDO : listDO) {
                Integer pid = areasDO.getParentId();
                if (pid <= 0) {
                    continue;
                }
                areasDO.setAreaName(concatAreaNames(areasMap.getOrDefault(pid, ""), areasDO.getAreaName()));
            }
        }
        return BaseConvertor.convertList(listDO, AreaOptionVO.class);
    }

    @Override
    public HashMap<Integer, String> queryLevelMap(AreasReq req) {
        List<AreaOptionVO> listVO = queryLevel(req);
        return (HashMap<Integer, String>) listVO.stream().collect(Collectors.toMap(AreaOptionVO::getId, AreaOptionVO::getAreaName));
    }

    @Slave
    @Override
    public AreasVO selectByPrimaryKey(Integer id) {
        AreasDO record = areasMapper.selectByPrimaryKey(id);
        return BaseConvertor.convert(record, AreasVO.class);
    }

    @Slave
    @Override
    public AreasVO nameExist(AreasReq req) {
        AreasDO record = areasMapper.nameExist(req);

        return BaseConvertor.convert(record, AreasVO.class);
    }

    @Slave
    @Override
    public AreasVO areaCodeExist(AreasReq req) {
        AreasDO record = areasMapper.areaCodeExist(req);

        return BaseConvertor.convert(record, AreasVO.class);
    }

    @Slave
    @Override
    public String concatAreaName(Integer id) {
        if (id <= 0) {
            return "";
        }
        AreasVO areasVO = this.selectByPrimaryKey(id);
        if (areasVO == null) {
            return "";
        }
        if (areasVO.getParentId() <= 0) {
            return areasVO.getAreaName();
        }
        AreasVO parentAreasVO = this.selectByPrimaryKey(areasVO.getParentId());
        return concatAreaNames(parentAreasVO.getAreaName(), areasVO.getAreaName());
    }

    private static String concatAreaNames(String parentAreaName, String areaName) {
        return String.format("%s/%s", parentAreaName, areaName);
    }
}
