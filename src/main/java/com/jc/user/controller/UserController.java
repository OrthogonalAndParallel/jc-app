package com.jc.user.controller;

import com.jc.user.entity.User;
import com.jc.user.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashMap;
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
    public ResponseEntity<Map<String, Object>> del(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");

        try {
            userService.deleteByIds(ids);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "删除成功");
            successResponse.put("deletedCount", ids != null ? ids.size() : 0);
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/search")
    public List<User> search(@ModelAttribute User user, Model model) {
        return userService.findByName(user.getName());
    }

}
