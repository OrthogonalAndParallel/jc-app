package com.jc.user.service;

import com.jc.user.entity.User;

import java.util.List;

public interface IUserService {

    User save(User user);

    // 删除用户
    void deleteByIds(List<Long> ids); // 添加批量删除方法

    // 返回所有的用户
    List<User> findAll();

    User findById(Long id);

    // 根据名称查找用户
    List<User> findByName(String name);

}
