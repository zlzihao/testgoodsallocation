package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.repository.dao.allocation.DwsDimGoodsDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.QdIssueCategoryStructureDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.QdIssueDepthSuggestDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.DwsDimGoodsExDO;
import cn.nome.saas.allocation.repository.entity.allocation.QdIssueCategoryStructureDO;
import cn.nome.saas.allocation.repository.entity.allocation.QdIssueDepthSuggestDO;
import cn.nome.saas.allocation.service.ProcessFactoryManager;
import cn.nome.saas.allocation.service.ProcessService;
import cn.nome.saas.allocation.utils.StackUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author ：godsfer
 * @date ：Created in 2019/8/13 16:58
 * @description：品类结构加工服务
 * @modified By：
 * @version: 1.0.0$
 */
@Service("categoryStructureProcessService")
public class CategoryStructureProcessServiceImpl extends ProcessFactoryManager implements ProcessService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private QdIssueCategoryStructureDOMapper qdIssueCategoryStructureDOMapper;

    @Autowired
    private QdIssueDepthSuggestDOMapper qdIssueDepthSuggestDOMapper;

    @Autowired
    private DwsDimGoodsDOMapper dwsDimGoodsDOMapper;

    @Override
    public int importData(Integer isClear, String fileName, MultipartFile file, String downTemplateCode) throws Exception {
        //无需做导入特殊处理
        return NO_NEED_DO;
    }

    @Override
    public void secProcess(String downTemplateCode, int limitNum) {
        // 1、查询未处理的数据
        List<QdIssueCategoryStructureDO> qdIssueCategoryStructureDOList = qdIssueCategoryStructureDOMapper.queryByIdDeal(0,limitNum);
        if(CollectionUtils.isEmpty(qdIssueCategoryStructureDOList)){
            LoggerUtil.info(logger,"没有需要处理的数据");
            return;
        }

        // 2、初始化数据
        // 深度对应关系
        int noDealNum = qdIssueDepthSuggestDOMapper.countForDealStatus(0);
        if(noDealNum > 0){
            throw new BusinessException("深度指引存在未处理完的数据");
        }
        List<QdIssueDepthSuggestDO> qdIssueDepthSuggestDOList = qdIssueDepthSuggestDOMapper.queryAvgDepth();
        LoggerUtil.info(logger,"深度对应关系信息={0}", JSON.toJSONString(qdIssueDepthSuggestDOList));
        Map<String,Double> matchTypeAndAvgDepthMap = new HashMap<>();//内外搭和平均深度对应关系
        if(CollectionUtils.isNotEmpty(qdIssueDepthSuggestDOList)){
            qdIssueDepthSuggestDOList.forEach(qdIssueDepthSuggestDO -> {
                matchTypeAndAvgDepthMap.put(qdIssueDepthSuggestDO.getMatchType(),qdIssueDepthSuggestDO.getAvgDepth());
            });
        }
        // 商品大类和中类均价
        List<DwsDimGoodsExDO> dwsDimGoodsExDOList= dwsDimGoodsDOMapper.queryAvgPriceForQd();
        LoggerUtil.info(logger,"商品大类和中类均价信息={0}", JSON.toJSONString(qdIssueCategoryStructureDOList));
        Map<String,BigDecimal> cateAndAvgPriceMap = new HashMap<>();//大类、中类和均价对应关系
        if(CollectionUtils.isNotEmpty(dwsDimGoodsExDOList)){
            dwsDimGoodsExDOList.forEach(dwsDimGoodsExDO -> {
                cateAndAvgPriceMap.put(dwsDimGoodsExDO.getCategoryName() + "_" + dwsDimGoodsExDO.getMidCategoryName(),dwsDimGoodsExDO.getAvgPrice());
            });
        }

        // 2、开始处理
        Map<String,List<QdIssueCategoryStructureDO>> areaAndBigCateTypeMap = new HashMap<>();
        qdIssueCategoryStructureDOList.forEach(qdIssueCategoryStructureDO -> {
            try{
                // 找深度
                // 优先中类，然后按照内外搭
                Double avgDepthForMidCate = matchTypeAndAvgDepthMap.get(qdIssueCategoryStructureDO.getMidCategoryName());
                if(avgDepthForMidCate == null){
                    Double avgDepth = matchTypeAndAvgDepthMap.get(qdIssueCategoryStructureDO.getMatchType());
                    if(avgDepth == null){
                        qdIssueCategoryStructureDO.setRemark("找不到内外搭对应的均价值，matchType=" +qdIssueCategoryStructureDO.getMatchType());
                        return;
                    }
                    qdIssueCategoryStructureDO.setDepth(avgDepth);
                }else{
                    qdIssueCategoryStructureDO.setDepth(avgDepthForMidCate);
                }

                // 找均价，待产品补充逻辑
                BigDecimal avgPrice = cateAndAvgPriceMap.get(qdIssueCategoryStructureDO.getCategoryName() + "_" + qdIssueCategoryStructureDO.getMidCategoryName());
                if(avgPrice == null){
                    qdIssueCategoryStructureDO.setRemark("找不到大类和中类对应的均价值，CategoryName=" +qdIssueCategoryStructureDO.getCategoryName() + ",MidCategoryName=" + qdIssueCategoryStructureDO.getMidCategoryName());
                    return;
                }
                qdIssueCategoryStructureDO.setAvgPrice(avgPrice.doubleValue());
                qdIssueCategoryStructureDO.setRemark("");
                //分组
                String areaAndBigCateKey = qdIssueCategoryStructureDO.getRegionName() + "_" + qdIssueCategoryStructureDO.getCategoryName();
                if(areaAndBigCateTypeMap.get(areaAndBigCateKey) != null){
                    areaAndBigCateTypeMap.get(areaAndBigCateKey).add(qdIssueCategoryStructureDO);
                }else{
                    List<QdIssueCategoryStructureDO> newList = new ArrayList<>();
                    newList.add(qdIssueCategoryStructureDO);
                    areaAndBigCateTypeMap.put(areaAndBigCateKey,newList);
                }

            }catch (Exception e){
                LoggerUtil.error(logger,"处理品类结构深度、均价报错，错误信息=", StackUtil.getStackTrace(e));
                qdIssueCategoryStructureDO.setRemark(e.getMessage().substring(0,999));
            }
        });

        // 计算skc占比
        // 查询所有已处理的数据
        List<QdIssueCategoryStructureDO> allHaveDealList = qdIssueCategoryStructureDOMapper.queryAllByIsDeal(1);
        Map<String,List<QdIssueCategoryStructureDO>> allHaveDealMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(allHaveDealList)){
            allHaveDealList.forEach(qdIssueCategoryStructureDO -> {
                //分组
                String areaAndBigCateKey = qdIssueCategoryStructureDO.getRegionName() + "_" + qdIssueCategoryStructureDO.getCategoryName();
                if(allHaveDealMap.get(areaAndBigCateKey) != null){
                    allHaveDealMap.get(areaAndBigCateKey).add(qdIssueCategoryStructureDO);
                }else{
                    List<QdIssueCategoryStructureDO> newList = new ArrayList<>();
                    newList.add(qdIssueCategoryStructureDO);
                    allHaveDealMap.put(areaAndBigCateKey,newList);
                }
            });
        }

        // 循环处理
        qdIssueCategoryStructureDOList.forEach(qdIssueCategoryStructureDO -> {
            try{
                if(StringUtils.isNotEmpty(qdIssueCategoryStructureDO.getRemark())){
                    return;
                }

                String key = qdIssueCategoryStructureDO.getRegionName() + "_" + qdIssueCategoryStructureDO.getCategoryName();
                List<QdIssueCategoryStructureDO> qdIssueCategoryStructureDOS = areaAndBigCateTypeMap.get(key);//获取大类下的中类列表
                List<QdIssueCategoryStructureDO> haveDealList = allHaveDealMap.get(key);//获取已处理的列表
                boolean isBreak = false;//是否中断
                BigDecimal allPro = BigDecimal.ZERO;//总占比
                for(QdIssueCategoryStructureDO qdIssueCategoryStructureDO1 : qdIssueCategoryStructureDOS){
                    if(StringUtils.isNotEmpty(qdIssueCategoryStructureDO1.getRemark())){
                        isBreak = true;
                        break;
                    }
                    allPro = allPro.add(new BigDecimal(qdIssueCategoryStructureDO1.getBusinessPercent()/(qdIssueCategoryStructureDO1.getAvgPrice()  * qdIssueCategoryStructureDO1.getDepth())));
                }
                if(CollectionUtils.isNotEmpty(haveDealList)){
                    // 已处理的总占比
                    for(QdIssueCategoryStructureDO haveDeal : haveDealList){
                        allPro = allPro.add(new BigDecimal(haveDeal.getBusinessPercent()/(haveDeal.getAvgPrice()  * haveDeal.getDepth())));
                    }
                }

                // 如果中类列表中存在错误的，都中断
                if(isBreak){
                    return;
                }

                // 如果最后一个，分配剩余占比
                Integer curId = qdIssueCategoryStructureDO.getId();//当前ID
                Integer lastId = qdIssueCategoryStructureDOS.get(qdIssueCategoryStructureDOS.size() - 1).getId();//最后一个对象ID
                if(curId == lastId){
                    double haveAlloPrecent = 0;//已分配占比
                    for(QdIssueCategoryStructureDO qdIssueCategoryStructureDO1 : qdIssueCategoryStructureDOS){
                        if(curId == qdIssueCategoryStructureDO1.getId()){
                            continue;
                        }
                        haveAlloPrecent = haveAlloPrecent + qdIssueCategoryStructureDO1.getSkcPercent();
                    }

                    if(CollectionUtils.isNotEmpty(haveDealList)){
                        // 已处理的总占比
                        for(QdIssueCategoryStructureDO haveDeal : haveDealList){
                            haveAlloPrecent = haveAlloPrecent + haveDeal.getSkcPercent();
                        }
                    }
                    // 最后一个分配剩余占比
                    qdIssueCategoryStructureDO.setSkcPercent( 1 - haveAlloPrecent);
                    qdIssueCategoryStructureDO.setIsDeal(1);
                    return;
                }

                //个人占比
                BigDecimal perPro = new BigDecimal(qdIssueCategoryStructureDO.getBusinessPercent()/(qdIssueCategoryStructureDO.getAvgPrice()  * qdIssueCategoryStructureDO.getDepth()));

                // 设置skc占比
                qdIssueCategoryStructureDO.setSkcPercent(perPro.divide(allPro, 2, RoundingMode.HALF_UP).doubleValue());
                qdIssueCategoryStructureDO.setIsDeal(1);
            }catch (Exception e){
                LoggerUtil.error(logger,"处理品类结构skc占比报错，错误信息=", StackUtil.getStackTrace(e));
                qdIssueCategoryStructureDO.setRemark(e.getMessage().substring(0,999));
            }
        });


        // 3、更新平均深度、处理状态
        qdIssueCategoryStructureDOMapper.batchUpdateInfo(qdIssueCategoryStructureDOList);
    }
}
