

## 工程结构

```
├── package.json            # Node.js 项目配置文件
├── pom.xml                 # Java Maven 项目配置文件
├── vite.config.js          # vite 项目配置文件
├── index.html              # 主界面文件
├── public/                 # 静态文件
└── src/
    ├── main.js             # 主进程脚本 (Electron 入口，管理窗口)
    ├── preload.js          # 预加载脚本 (安全地连接主进程和渲染进程，后端改成SpringBoot，这个文件没有用了)
    ├── vue/
        ├── renderer.js     # vue 工程配置文件
        ├── App.vue         # vue 主页面
        ├── components/     # vue 组件
        ├── router/         # vue 路由
        ├── utils/          # vue 事件
    └── main/
        └── MainFixer.java      # 新的 Java 主程序，包含修复逻辑
```

## 命令

### SpringBoot、vue 同时调试
```shell
JcApplication
npm run dev
```

### vue、electron 同时调试
```shell
npm run dev
mvn clean install
npm run dev:electron
```

### electron、vue 编译后调试
```shell
npm run build:vue
npm run start
```

### 打包
```shell
npm run dist
```