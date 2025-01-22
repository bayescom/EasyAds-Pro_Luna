package com.easyads.component.mapper;

import com.easyads.management.user.model.filter.UserFilterParams;
import com.easyads.management.user.model.user.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserMapper {
    // 用户登录及修改等操作
    User getUserByName(String name);

    // 用户列表
    int getUserCount(UserFilterParams filterParams);
    List<User> getUserList(UserFilterParams filterParams);

    // 用户管理
    User getOneUser(long userId);
    int createOneUser(User user);
    int updateOneUser(long userId, User user);
    int updateOneUserStatus(long userId, int status);
    int updateOneUserPassword(long userId, String passwordHash);
    int deleteOneUser(long userId);
}
