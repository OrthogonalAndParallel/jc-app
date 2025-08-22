package com.jc.user.service;

import com.jc.user.entity.User;
import com.jc.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Resource
    private UserRepository userRepository;

    // 增加用户
    public User save(User user) {
        // 保存数据
        userRepository.save(user);
        return user;
    }

    // 删除用户
    @Override
    public void deleteByIds(List<Long> ids) {
        userRepository.deleteAllById(ids);
    }

    // 返回所有的用户
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    public User findById(Long id) {
        User user = null;
        Optional optional = userRepository.findById(id);
        if (optional.isPresent()) {
            user = (User) optional.get();
        }
        return user;
    }

    // 根据名称查找用户
    public List<User> findByName(String name){
        return userRepository.findByName(name);
    }

}
