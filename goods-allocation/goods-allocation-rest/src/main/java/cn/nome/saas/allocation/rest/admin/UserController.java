package cn.nome.saas.allocation.rest.admin;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.feign.model.User;
import cn.nome.saas.allocation.model.protal.Application;
import cn.nome.saas.allocation.model.protal.ApplicationMangled;
import cn.nome.saas.allocation.model.protal.LocalUser;
import cn.nome.saas.allocation.model.protal.UserData;
import cn.nome.saas.allocation.service.portal.UserService;
import cn.nome.saas.allocation.utils.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author bare
 * @create 2017/12/26.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${nome.wx.corpId}")
    private String corpId;
    @Value("${nome.wx.secret}")
    private String secret;
    @Value("${nome.wx.user_secret}") // user_secret
    private String userSecret;
    @Autowired
    UserService userService;

    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    @ResponseBody
    public Result createUser(HttpServletRequest request, @RequestParam(value = "user_id") String userId, @RequestParam(value = "user_name") String userName) throws Exception {

        String cached_userid = AuthUtil.getUserid(request);

        LocalUser cachedUser = userService.getUser(cached_userid);
        if (cachedUser == null) {
            return ResultUtil.handleFailtureReturn("no privilege");
        }

        if (cachedUser.getRole() == 0) {
            return ResultUtil.handleFailtureReturn("no privilege");
        }

        int result = userService.createUser(userId, userName);
        if (result == 1) {
            return ResultUtil.handleSuccessReturn();
        } else {
            return ResultUtil.handleFailtureReturn("create user error");
        }
    }

    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public Result getUserInfo(HttpServletRequest request) throws Exception {

        String userid = AuthUtil.getUserid(request);

        User user = userService.getUserInfo(userid);

        LocalUser cachedUser = userService.getUser(userid);
        if (cachedUser != null) {
            user.setRole(cachedUser.getRole());
        } else {
            user.setRole(0);
        }

        return ResultUtil.handleSuccessReturn(user);
    }

    @RequestMapping(value = "/getUserDataList", method = RequestMethod.GET)
    @ResponseBody
    public Result getUserList() throws Exception {

        List<UserData> userDataList = userService.getUserDataList();
        return ResultUtil.handleSuccessReturn(userDataList);
    }

    @RequestMapping(value = "/postUserDataList", method = RequestMethod.POST)
    @ResponseBody
    public Result postUserDataList(@RequestBody(required = false) String userDataList) throws Exception {

        int result = 0;

        try {
            com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
            com.google.gson.JsonArray array = (com.google.gson.JsonArray) parser.parse(userDataList);

            for (int i = 0; i < array.size(); i++) {
                String user_data_string = array.get(i).toString();
                UserData user_data = new com.google.gson.Gson().fromJson(user_data_string, UserData.class);
                String user_id = user_data.getUserId();
                List<Integer> appid_list = user_data.getAppIdList();
                result = userService.setUserApplicationIdList(user_id, appid_list);
                if (result < 0) {
                    break;
                }
            }

        } catch (Exception e) {
            return ResultUtil.handleFailtureReturn("response parse exception in postUserDataList");
        }

        if (result >= 0) {
            return ResultUtil.handleSuccessReturn();
        } else {
            return ResultUtil.handleFailtureReturn("update database error");
        }
    }

    @RequestMapping(value = "/getUserApplicationList", method = RequestMethod.GET)
    @ResponseBody
    public Result getUserApplicationList(HttpServletRequest request) throws Exception {
        String userid = AuthUtil.getUserid(request);

        List<ApplicationMangled> list = userService.getUserApplicationList(userid);
        return ResultUtil.handleSuccessReturn(list);
    }

    @RequestMapping(value = "/getApplicationList", method = RequestMethod.GET)
    @ResponseBody
    public Result getApplicationList(HttpServletRequest request) throws Exception {

        List<Application> list = userService.getApplicationList();
        return ResultUtil.handleSuccessReturn(list);
    }

}
