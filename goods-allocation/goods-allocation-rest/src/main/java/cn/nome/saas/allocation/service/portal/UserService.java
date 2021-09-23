package cn.nome.saas.allocation.service.portal;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.feign.api.WeixinClient;
import cn.nome.saas.allocation.feign.model.Department;
import cn.nome.saas.allocation.feign.model.DepartmentList;
import cn.nome.saas.allocation.feign.model.Token;
import cn.nome.saas.allocation.model.protal.*;
import cn.nome.saas.allocation.feign.model.User;
import cn.nome.saas.allocation.repository.dao.aiPortal.AiUserMapper;
import cn.nome.saas.allocation.repository.dao.portal.UserMapper;
import cn.nome.saas.allocation.repository.entity.portal.ApplicationDO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * UserService
 *
 * @author Bruce01.fan
 * @date 2019/6/18
 */
@Service
public class UserService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${nome.wx.corpId}")
    private String corpId;
    @Value("${nome.wx.secret}")
    private String secret;
    @Value("${nome.wx.user_secret}") // user_secret
    private String userSecret;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    @Lazy
    private WeixinClient weixinClient;
    @Autowired
    private AiUserMapper aiUserMapper;

    public LocalUser getUser(String userId) {

        List<LocalUser> userList = userMapper.getUser(userId);

        if (CollectionUtils.isNotEmpty(userList)) {
            return userList.get(0);
        }
        return null;
    }

    public int createUser(String userId, String userName) {
        int result = -1;
        if(getUser(userId) != null) {
            return 0;
        }

        try {
            result = userMapper.insertUser(userId, userName);
        } catch(Exception ex) {
        }

        return result;
    }

    public User getUserInfo(String userId) {
        Token token = weixinClient.gettoken(corpId, userSecret);

        LoggerUtil.info(logger, "get token：{0}", token);

        User user = weixinClient.getUser(token.getAccess_token(), userId);

        LoggerUtil.info(logger, "get User：{0}", user);

        List<Integer> department_id_list = user.getDepartment();

        if (department_id_list.size() > 0) {
            int department_id = department_id_list.get(0);
            DepartmentList department_list = weixinClient.getDepartmentList(token.getAccess_token(), department_id);

            LoggerUtil.info(logger, "getDepartmentList：{0}", department_list);

            if (department_list.getDepartment().size() > 0) {
                Department department = department_list.getDepartment().get(0);
                String department_name = department.getName();
                List<String> departmentname_list = new ArrayList<String>();
                departmentname_list.add(department_name);
                user.setDepartmentName(departmentname_list);
            }
        }
        return user;
    }

    public List<UserData> getUserDataList() {

        List<LocalUser> user_list = userMapper.getUserList();
        List<UserData> user_data_list = new ArrayList<UserData>();

        for(LocalUser user : user_list) {

            String userId = user.getUserId();

            UserData userData = new UserData();
            userData.setUserId(userId);
            userData.setUserName(user.getUserName());

            List<Integer> appid_list = userMapper.getUserApplicationIdList(userId);
            userData.setAppIdList(appid_list);

            user_data_list.add(userData);
        }

        return user_data_list;
    }

    public int setUserApplicationIdList(String userId, List<Integer> appidList) {
        LocalUser user = getUser(userId);
        if(user == null) {
            return -1;
        }
        int result = 0;

        userMapper.DeleteUserApplicationIdList(userId);

        for(Integer applicationId : appidList) {
            result += userMapper.setUserApplicationId(userId, applicationId);
        }
        return result;
    }

    public List<ApplicationMangled> getUserApplicationList(String user_id) {

        List<ApplicationDO> appList = userMapper.getApplicationList();
        List<Integer> appid_list = userMapper.getUserApplicationIdList(user_id);

        List<ApplicationMangled> list = new ArrayList<ApplicationMangled>();

        for (ApplicationDO app : appList) {
            Integer app_id = app.getId();
            ApplicationMangled item = new ApplicationMangled();
            item.setName(app.getName());
            if (appid_list.contains(app_id)) {
                item.setOpen(true);
            }
            else {
                item.setOpen(false);
            }
            item.setIcon(app.getIconPath());
            item.setApplyLink(app.getApplyLink());
            item.setProjectLink(app.getUrl());

            String introduction = app.getIntroduction();

            String[] intro_array = introduction.split(";;");

            List<String> intros = new ArrayList<String>();
            for (String intro : intro_array) {
                intros.add(intro);
            }
            item.setIntros(intros);

            list.add(item);
        }

        return list;
    }

    public List<Application> getApplicationList() {

        List<ApplicationDO> appList = userMapper.getApplicationList();
        List<Application> applicationList = new ArrayList<>(appList.size());

        for (ApplicationDO applicationDO : appList) {
            Application application = new Application();
            // copy properties
            BeanUtils.copyProperties(applicationDO,application);

            applicationList.add(application);
        }

        return applicationList;
    }

    public List<String> getUserApplicationCodeList(String user_id) {

        List<ApplicationDO> app_list = userMapper.getApplicationList();
        List<Integer> appid_list = userMapper.getUserApplicationIdList(user_id);

        List<String> list = new ArrayList<String>();

        for (ApplicationDO app : app_list) {
            Integer app_id = app.getId();
            if (appid_list.contains(app_id)) {
                list.add(app.getCode());
            }
        }

        return list;
    }

    public void syncUserData() {

        List<LocalUser> userList = aiUserMapper.getUserList();

        List<UserApplication> applicationList = aiUserMapper.getLocationUserApplication();

        if (userList.size() > 0) {
            userMapper.clearUser();
            for (LocalUser user : userList) {
                userMapper.insertUserData(user);
            }
        }

        if (applicationList.size() > 0) {
            userMapper.clearUserApplication();
            for (UserApplication userApplication : applicationList) {
                userMapper.insertUserApplicationDate(userApplication);
            }
        }
    }
}
