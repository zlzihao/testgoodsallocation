package cn.nome.saas.allocation.repository.dao.aiPortal;


import cn.nome.saas.allocation.model.protal.LocalUser;
import cn.nome.saas.allocation.model.protal.UserApplication;
import cn.nome.saas.allocation.repository.entity.portal.ApplicationDO;

import java.util.List;

public interface AiUserMapper {

    List<LocalUser> getUserList();

    List<UserApplication> getLocationUserApplication();
}