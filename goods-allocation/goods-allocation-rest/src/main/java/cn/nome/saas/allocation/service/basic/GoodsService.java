package cn.nome.saas.allocation.service.basic;

import cn.nome.platform.common.utils.DateUtil;
import cn.nome.saas.allocation.cache.GoodsCategoryTreeCache;
import cn.nome.saas.allocation.cache.GoodsInfoCache;
import cn.nome.saas.allocation.model.allocation.Category;
import cn.nome.saas.allocation.model.allocation.Paramater;
import cn.nome.saas.allocation.model.common.SelectByPageResult;
import cn.nome.saas.allocation.repository.dao.allocation.DwsDimGoodsDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.GoodsInfoDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.TaskDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.GoodsInfoDO;
import cn.nome.saas.allocation.repository.entity.allocation.TaskDO;
import cn.nome.saas.allocation.repository.entity.vertical.DwsDimGoodsDO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.nome.saas.allocation.constant.Constant.ATTR_VALUE_SPLIT;

/**
 * GoodsService
 *
 * @author Bruce01.fan
 * @date 2019/6/18
 */
@Service
public class GoodsService {

    private static Logger logger = LoggerFactory.getLogger(GoodsService.class);

    @Autowired
    GoodsInfoDOMapper goodsInfoDOMapper;
    @Autowired
    GoodsInfoCache goodsInfoCache;
    @Autowired
    cn.nome.saas.allocation.repository.dao.vertical.DwsGoodsDOMapper dwsGoodsDOVMapper;
    @Autowired
    DwsDimGoodsDOMapper dwsDimGoodsDOMapper;
    @Autowired
    TaskDOMapper taskDOMapper;

    @Autowired
    GoodsCategoryTreeCache goodsCategoryTreeCache;


    public Map<String,GoodsInfoDO> getGoodsInfo() {
        List<GoodsInfoDO> goodsInfoDOList = goodsInfoCache.getGoodsInfo();
        return goodsInfoDOList.stream().collect(Collectors.toMap(GoodsInfoDO::getMatCode, Function.identity()));
    }

    public List<String> selectMatCodeList(int offset,int pageSize) {

        Map<String,Object> param = new HashMap<>();
        param.put("offset",offset * pageSize);
        param.put("pageSize",pageSize);

        return goodsInfoDOMapper.selectMatCodeList(param);
    }

    public List<String> getForbiddenAllocationGoods() {
        List<GoodsInfoDO> goodsInfoDOList = goodsInfoCache.getGoodsInfo();

        if (goodsInfoDOList == null) {
            return null;
        }

        return goodsInfoDOList.stream().filter(goods->goods.getIsAllocationProhibited() == 1)
                .map(GoodsInfoDO::getMatCode)
                .collect(Collectors.toList());

    }

    public SelectByPageResult selectByParam(String largeCategory, String midCategory, String smallCategory, String matCode, String matName, int page, int pageSize) {
        Map<String,Object> param = new HashMap<>();
        if (!StringUtils.isEmpty(largeCategory)) {
            param.put("largeCategory", largeCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(midCategory)) {
            param.put("midCategory", midCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(smallCategory)) {
            param.put("smallCategory", smallCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(matCode)) {
            param.put("matCode", matCode);
        }
        if (!StringUtils.isEmpty(matName)) {
            param.put("matName", matName);
        }
        param.put("offset",(page - 1) * pageSize);
        param.put("pageSize",pageSize);

        int total = goodsInfoDOMapper.getCount(param);
        List<GoodsInfoDO> list = goodsInfoDOMapper.selectByPage(param);

        SelectByPageResult<GoodsInfoDO> result = new SelectByPageResult<>();
        result.setTotal(total);
        result.setList(list);
        int totalPage;
        if (total % pageSize == 0) {
            totalPage = total / pageSize;
        } else {
            totalPage = (total / pageSize) + 1;
        }
        result.setTotalPage(totalPage);
        return result;
    }



    public List<Paramater> getYearNoList() {
        return dwsDimGoodsDOMapper.getYearNoList();
    }

    public List<Paramater> getSeasonList() {
        return dwsDimGoodsDOMapper.getSeasonList();
    }

    public List<String> getSeasonNameList() {
        return dwsDimGoodsDOMapper.getSeasonNameList();
    }

    public List<Category> getMidCategory(int taskId) {

        TaskDO taskDO = taskDOMapper.getTask(taskId);

        if (taskDO == null) {
            return  null;
        }

        return dwsDimGoodsDOMapper.getMidCategory(this.getTaskTableName(taskId,taskDO.getRunTime()),taskDO.getTaskType());
    }

    public List<Category> getSmallCategory(int taskId,String midCategoryCode) {

        TaskDO taskDO = taskDOMapper.getTask(taskId);

        if (taskDO == null) {
            return  null;
        }

        return dwsDimGoodsDOMapper.getSmallCategory(midCategoryCode,this.getTaskTableName(taskId,taskDO.getRunTime()),taskDO.getTaskType());
    }

    public List<String> getMatCodeListBy(String year, String season) {
        return dwsDimGoodsDOMapper.getMatCodeListBy(year,season);
    }

    public List<String> getMatCodeLisyByCategoryCode(String categoryCode, String midCategoryCode, String smallCategoryCode) {
        return dwsDimGoodsDOMapper.getMatCodeLisyByCategoryCode(categoryCode,midCategoryCode,smallCategoryCode);
    }

    private String getTaskTableName(int taskId) {
        Date date = DateUtil.getCurrentDate();
        String currentDate = DateUtil.format(DateUtil.addDate(date,-1,DateUtil.DAY),DateUtil.DATE_ONLY);
        currentDate = currentDate.replaceAll("-","").substring(2);

        //currentDate = "190625";
        return "out_of_stock_goods"+"_"+taskId+"_"+ currentDate;
    }

    private String getTaskTableName(int taskId,Date runtime) {
        Date date = DateUtil.getCurrentDate();
        String currentDate = DateUtil.format(DateUtil.addDate(runtime,-1,DateUtil.DAY),DateUtil.DATE_ONLY);
        currentDate = currentDate.replaceAll("-","").substring(2);

        return "out_of_stock_goods"+"_"+taskId+"_"+ currentDate;
    }

    public int syncDataCenterData() {
        int count = dwsGoodsDOVMapper.getCount();

        logger.info("[SYNC_DATA_CENTER_DATA]|get data center data size:{0}", count);
        if (count > 0) {
            dwsDimGoodsDOMapper.clearAll();
            int index = 0;
            int size = 50000;

            //while(index < size) {
                List<DwsDimGoodsDO> dwsDimGoodsDOS = dwsGoodsDOVMapper.selectGoodsListByPage(index, size);
                dwsDimGoodsDOMapper.insertBatchDataCenterData(dwsDimGoodsDOS);
                index += size;
            //}

        } else {
            logger.error("[SYNC_DATA_CENTER_DATA]|get data center data is empty");
        }
        return 1;
    }

    public List<GoodsCategoryTreeCache> getGoodsCategoryTree(Integer type) {
        return goodsCategoryTreeCache.getCategoryTree(type);
    }
}
