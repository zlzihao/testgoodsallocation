package cn.nome.saas.sdc.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.nome.saas.sdc.repository.dao.SeasonChangeMapper;
import cn.nome.saas.sdc.repository.entity.SeasonChangeDO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lizihao@nome.com
 */
@Service
public class SeasonChangeServiceManager {
    private final Logger logger = LoggerFactory.getLogger(SeasonChangeServiceManager.class);

    private final SeasonChangeMapper mapper;

    @Autowired
    public SeasonChangeServiceManager(SeasonChangeMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional
    @Async
    public void save(List<SeasonChangeDO> doList) {
        List<SeasonChangeDO> allSeasonList = mapper.getByCondition(new SeasonChangeDO());
        List<SeasonChangeDO> updateList = new ArrayList<SeasonChangeDO>();
        List<SeasonChangeDO> insertList = new ArrayList<SeasonChangeDO>();
        if (!CollectionUtil.isEmpty(allSeasonList)) {
            doList.stream().forEach(vos -> {
                List<SeasonChangeDO> checkList = allSeasonList.stream().filter(dos -> dos.getShopCode().equals(vos.getShopCode()) && dos.getYear().equals(vos.getYear()) && dos.getSeasonsAlternate().equals(vos.getSeasonsAlternate())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(checkList)) {
                    updateList.add(vos);
                    return;
                }
                insertList.add(vos);
            });

        } else {
            mapper.batchInsert(doList);
            return;
        }
        //更新
        if (!CollectionUtils.isEmpty(updateList)) {
            mapper.batchUpdate(updateList);
        }
        //插入
        if (!CollectionUtils.isEmpty(insertList)) {
            mapper.batchInsert(insertList);
        }
        return;
    }

}
