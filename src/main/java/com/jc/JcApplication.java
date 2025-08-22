package com.jc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JcApplication {

    public static void main(String[] args) {
        // 处理数据库路径参数
        processArgs(args);
        SpringApplication.run(JcApplication.class, args);
    }

    private static void processArgs(String[] args) {
        // 从系统属性中获取数据库路径
        String appDbPath = System.getProperty("app.db.path");
        if (appDbPath != null && !appDbPath.isEmpty()) {
            System.setProperty("app.db.path", appDbPath);
        } else {
            // 默认使用用户主目录
            String userHome = System.getProperty("user.home");
            String defaultDbPath = userHome + "/user.db";
            System.setProperty("app.db.path", defaultDbPath);
        }
    }

}
