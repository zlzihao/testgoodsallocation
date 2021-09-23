package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.utils.DateUtil;
import cn.nome.saas.allocation.cache.ShopInfoCache;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.repository.dao.allocation.IssueDayPeriodMapper;
import cn.nome.saas.allocation.repository.entity.allocation.IssueDayPeriod;
import cn.nome.saas.allocation.utils.IssueDayUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * IssueDayService
 *
 * @author Bruce01.fan
 * @date 2019/11/27
 */
@Service
public class IssueDayService {

    @Autowired
    ShopInfoCache shopInfoCache;

    @Autowired
    IssueDayPeriodMapper issueDayPeriodMapper;

    public void calcShopIssueDay(String startDate,String endDate) {

        List<ShopInfoData>  shopInfoDataList = shopInfoCache.getShopList();

        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(DateUtil.parse(startDate,DateUtil.DATE_ONLY));
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(DateUtil.parse(endDate,DateUtil.DATE_ONLY));

        List<IssueDayPeriod> issueDayPeriodList = new ArrayList<>();
        for (ShopInfoData shopInfoData : shopInfoDataList) {
            String issueTime = shopInfoData.getIssueTime();
            int roadDay = shopInfoData.getRoadDay(); // 在途天数

            List<Calendar> calendarList = IssueDayUtil.getDayOfWeekWithinDateInterval(calBegin, calEnd, new HashSet<>(IssueDayUtil.convertIssueTimeV2(issueTime)));

            for (Calendar arriveCalendar : calendarList) {
                Date arriveDate = arriveCalendar.getTime(); //  到货日期
                arriveCalendar.add(Calendar.DATE, -(roadDay+1));
                Date issueDate = arriveCalendar.getTime(); //  发货日期

                IssueDayPeriod issueDayPeriod = new IssueDayPeriod();
                issueDayPeriod.setShopCode(shopInfoData.getShopCode());
                issueDayPeriod.setShopId(shopInfoData.getShopID());
                issueDayPeriod.setIssueDate(issueDate);
                issueDayPeriod.setArriveDate(arriveDate);

                issueDayPeriodList.add(issueDayPeriod);
            }
        }

        if (CollectionUtils.isNotEmpty(issueDayPeriodList)) {
            issueDayPeriodMapper.clearData();
        }

        issueDayPeriodMapper.addIssueDay(issueDayPeriodList);

    }
}
