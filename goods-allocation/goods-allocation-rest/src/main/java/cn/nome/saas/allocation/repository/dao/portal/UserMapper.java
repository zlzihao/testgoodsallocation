package cn.nome.saas.allocation.repository.dao.portal;


import cn.nome.saas.allocation.model.protal.LocalUser;
import cn.nome.saas.allocation.model.protal.UserApplication;
import cn.nome.saas.allocation.repository.entity.portal.ApplicationDO;

import java.util.List;

public interface UserMapper {

    int insertUser(String userId, String userName);
    List<LocalUser> getUser(String userId);
    List<LocalUser> getUserList();
    List<ApplicationDO> getApplicationList();
    List<Integer> getUserApplicationIdList(String userId);
    void DeleteUserApplicationIdList(String userId);
    int setUserApplicationId(String userId, Integer applicationId);

    List<LocalUser> getAllocationOperatorList();

    Integer clearUser();
    Integer clearUserApplication();

    void insertUserData(LocalUser user);

    void insertUserApplicationDate(UserApplication userApplication);
}