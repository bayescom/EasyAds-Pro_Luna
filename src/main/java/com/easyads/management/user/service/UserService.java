package com.easyads.management.user.service;


import com.easyads.management.common.SystemCode;
import com.easyads.component.enums.SystemCodeEnum;
import com.easyads.component.exception.BadRequestException;
import com.easyads.component.exception.ForbiddenException;
import com.easyads.component.exception.NoResourceException;
import com.easyads.component.mapper.SystemMapper;
import com.easyads.component.mapper.UserMapper;
import com.easyads.management.user.model.filter.UserFilterParams;
import com.easyads.management.user.model.user.UserPassword;
import com.easyads.management.user.model.user.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {
    private static final int TOKEN_EXPIRE_TIME = 3600;

    private static final String BLINK_DOMAIN = "blink.bayescom.cn";

    private static final String SUBJECT = "倍联系统密码重置";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SystemMapper systemMapper;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> userLogin(UserPassword userPassword) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        User user = userMapper.getUserByName(userPassword.getName());
        if (null == user) {
            throw new NoResourceException("用户无效");
        }

        boolean passwordMatch = passwordEncoder.matches(userPassword.getPassword(), user.getPasswordHash());
        if (!passwordMatch) {
            throw new ForbiddenException("密码错误");
        }

        resultMap.put("user", user);

        return resultMap;
    }


    public Map<String, Object> getUserList(Map<String, Object> queryParams) throws Exception {
        Map<String, Object> userResult = new HashMap() {{
            put("meta", new HashMap() {{
                put("total", 0);
            }});
            put("users", new ArrayList<>());
        }};

        UserFilterParams filterParams = new UserFilterParams(queryParams);
        int userCount = userMapper.getUserCount(filterParams);
        List<User> userList = userMapper.getUserList(filterParams);

        ((Map) userResult.get("meta")).put("total", userCount);
        userResult.put("users", userList);

        return userResult;
    }

    public Map<String, Object> getOneUser(long userId) throws BadRequestException {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("user", userMapper.getOneUser(userId));
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> createOneUser(User user) throws BadRequestException {
        Map<String, Object> resultMap = new HashMap<>();

        User userExist = userMapper.getUserByName(user.getUserName());
        if(null != userExist) {
            throw new BadRequestException("登录名已存在，请换一个新的吧");
        }

        // 更新设置用户密码Hash值
        String passwordHash = passwordEncoder.encode(user.getPassword());
        user.setPasswordHash(passwordHash);

        // 用户基本信息创建
        userMapper.createOneUser(user);

        User createUser = userMapper.getOneUser(user.getId());
        resultMap.put("user", createUser);

        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateOneUser(Long userId, User user) throws BadRequestException {
        Map<String, Object> resultMap = new HashMap<>();

        User userExist = userMapper.getUserByName(user.getUserName());
        if(null != userExist && userExist.getId() != userId) {
            throw new BadRequestException("登录名已存在，请换一个新的吧");
        }

        userMapper.updateOneUser(userId, user);

        resultMap.put("user", userMapper.getOneUser(userId));
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateOneUserStatus(Long id, int status) throws BadRequestException {
        userMapper.updateOneUserStatus(id, status);

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("user", userMapper.getOneUser(id));

        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateOneUserPassword(Long id, String password) throws BadRequestException {
        if (StringUtils.isBlank(password)) {
            throw new BadRequestException("Password is Empty");
        }

        // 更新设置用户密码Hash值
        String passwordHash = passwordEncoder.encode(password);
        userMapper.updateOneUserPassword(id, passwordHash);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("user", userMapper.getOneUser(id));
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> deleteOneUser(long id) throws BadRequestException {
        Map<String, Object> resultMap = new HashMap<>();
        userMapper.deleteOneUser(id);
        resultMap.put("user", null);
        return resultMap;
    }

    public Map<String, Object> getUserType(int userRoleType) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        List<SystemCode> allUserTypeList = systemMapper.getSystemCodeList(SystemCodeEnum.USER_ROLE.getValue());

        // 根据当前用户的userType，获取当前用户的下级用户类型
        List<SystemCode> userTypeList = allUserTypeList.stream().filter(systemCode -> Integer.valueOf(systemCode.getValue()) >= userRoleType).toList();

        resultMap.put("code-list", userTypeList);
        return resultMap;
    }
}
