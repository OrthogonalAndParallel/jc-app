package com.jc.user.repository;

import com.jc.user.entity.User;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface UserRepository extends CrudRepository<User,Long> {

    // 自定义添加通过用户名称查找用户信息
    List<User> findByName(String name);

}
