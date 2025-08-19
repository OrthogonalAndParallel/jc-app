

## 工程结构

```
├── package.json            # Node.js 项目配置文件
├── pom.xml                 # Java Maven 项目配置文件
├── index.html              # 主界面文件
└── src/
    ├── main.js             # 主进程脚本 (Electron 入口，管理窗口)
    ├── renderer.js         # 渲染进程脚本 (前端逻辑)
    ├── vue/
        ├── renderer.js     # vue 工程配置文件
        ├── App.vue         # vue 主页面
        ├── assets/         # vue 静态资源文件
        ├── components/     # vue 组件
        ├── router/         # vue 路由
        ├── utils/          # vue 事件
    ├── preload.js          # 预加载脚本 (安全地连接主进程和渲染进程)
    └── main/
        ├── pom.xml             # Maven 项目配置文件
        ├── HttpRequestUtil.java
        └── MainFixer.java      # 新的 Java 主程序，包含修复逻辑
```

## 命令

### vue、electron 同时调试
```shell
npm run dev
npm run dev:electron
```

### electron 调试
```shell
npm run build:vue
npm run start
```

### 打包
```shell
npm run dist
```