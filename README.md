

## 工程结构

```
├── package.json            # Node.js 项目配置文件
├── pom.xml                 # Java Maven 项目配置文件
├── public/                 # 静态资源，如 HTML, CSS, 图片
│   └── index.html          # 主界面文件
│   └── styles.css          # 样式文件
│   └── renderer.js         # 渲染进程脚本 (前端逻辑)
└── src/
    ├── main.js             # 主进程脚本 (Electron 入口，管理窗口)
    ├── preload.js          # 预加载脚本 (安全地连接主进程和渲染进程)
    └── main/
        ├── pom.xml             # Maven 项目配置文件
        ├── HttpRequestUtil.java
        └── MainFixer.java      # 新的 Java 主程序，包含修复逻辑
```