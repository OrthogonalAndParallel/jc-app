package com.jc.user.controller;

import com.jc.user.entity.User;
import com.jc.user.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @GetMapping(path = "/list")
    public List<User> list(Model model) {
        return userService.findAll();
    }

    @PostMapping(path = "/save")
    public User save(@RequestBody User user) {
        return userService.save(user);
    }

    @DeleteMapping(path = "/del")
    public ResponseEntity<?> del(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        userService.deleteByIds(ids);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/search")
    public List<User> search(@ModelAttribute User user, Model model) {
        return userService.findByName(user.getName());
    }

}
