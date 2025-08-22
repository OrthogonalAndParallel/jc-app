const { app, BrowserWindow, ipcMain, shell } = require('electron');
const path = require('path');
const { spawn } = require('child_process');
const fs = require('fs');
const net = require('net');

let backendProcess;
let backendPort = 8001; // 默认端口

function isPortAvailable(port) {
    return new Promise((resolve) => {
        const server = net.createServer();
        server.listen(port, () => {
            server.close();
            resolve(true);
        });
        server.on('error', () => {
            resolve(false);
        });
    });
}

function startBackend() {
    const javaExecutable = 'java';
    let jarPath;
    let logPath;

    if (app.isPackaged) {
        jarPath = path.join(process.resourcesPath, 'app.asar.unpacked/target/jc-app-1.0-SNAPSHOT-jar-with-dependencies.jar');
        // 在打包应用中，将日志写入用户数据目录
        logPath = path.join(app.getPath('logs'), 'backend.log');
    } else {
        jarPath = path.join(__dirname, '../target/jc-app-1.0-SNAPSHOT-jar-with-dependencies.jar');
        // 在开发环境中，将日志写入项目目录
        logPath = path.join(__dirname, '../backend.log');
    }

    // 确保日志目录存在
    const logDir = path.dirname(logPath);
    if (!fs.existsSync(logDir)) {
        fs.mkdirSync(logDir, { recursive: true });
    }

    // 检查 JAR 文件是否存在
    if (!fs.existsSync(jarPath)) {
        console.error('Backend JAR file not found:', jarPath);
        return;
    }

    // 获取用户数据目录，用于存储数据库文件
    const userDataPath = app.getPath('userData');
    const dbPath = path.join(userDataPath, 'user.db');

    // 确保用户数据目录存在
    if (!fs.existsSync(userDataPath)) {
        fs.mkdirSync(userDataPath, { recursive: true });
    }

    const javaArgs = [
        '-jar',
        jarPath,
        `-Dserver.port=${backendPort}`,
        `--logging.file.name=${logPath}`, // 配置 SpringBoot 日志文件
        `-Dapp.db.path=${dbPath}`  // 传递数据库路径给Java应用
    ];

    console.log('Starting backend with db path:', dbPath);
    console.log('Java args:', javaArgs);

    backendProcess = spawn(javaExecutable, javaArgs);

    backendProcess.stdout.on('data', (data) => {
        const output = data.toString();
        console.log(`[Backend] ${output}`);

        // 写入日志文件
        fs.appendFileSync(logPath, `[STDOUT] ${output}`);

        // 可以监听特定启动成功的日志
        if (output.includes('Started') && output.includes('application')) {
            console.log('Backend application started successfully');
        }
    });

    backendProcess.stderr.on('data', (data) => {
        const output = data.toString();
        console.error(`[Backend ERROR] ${output}`);

        // 写入日志文件
        fs.appendFileSync(logPath, `[STDERR] ${output}`);
    });

    backendProcess.on('error', (error) => {
        console.error('Failed to start backend process:', error);
        fs.appendFileSync(logPath, `[ERROR] Failed to start backend process: ${error.message}\n`);
    });

    backendProcess.on('close', (code) => {
        const message = `Backend process exited with code ${code}\n`;
        console.log(message);
        fs.appendFileSync(logPath, message);
    });
}

async function createWindow() {

    // 等待后端启动
    try {
        await startBackend();
        // 等待端口可用
        let retries = 0;
        while (!(await isPortAvailable(backendPort)) && retries < 30) {
            await new Promise(resolve => setTimeout(resolve, 1000));
            retries++;
        }
    } catch (error) {
        console.error('Failed to start backend:', error);
    }

    const win = new BrowserWindow({
        width: 800,
        height: 600,
        webPreferences: {
            preload: path.join(__dirname, 'preload.js'),
            contextIsolation: true,
            nodeIntegration: false
        }
    });

    // 加载 Vue 前端
    if (process.env.NODE_ENV === 'development') {
        win.loadURL('http://localhost:5173'); // Vite 的默认地址
    } else {
        const indexPath = path.join(__dirname, '../dist-vue/index.html');
        console.log('Trying to load:', indexPath);
        if (fs.existsSync(indexPath)) {
            win.loadFile(indexPath);
        } else {
            console.error('File not found:', indexPath);
            win.loadURL('data:text/html,<h1>错误：找不到构建文件，请先运行 npm run build:vue</h1>');
        }
    }
}

app.whenReady().then(() => {
    // startBackend();
    createWindow();

    app.on('activate', () => {
        if (BrowserWindow.getAllWindows().length === 0) {
            createWindow();
        }
    });
});

app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
        app.quit();
    }
});

// 添加 IPC 监听器，用于打开日志目录
ipcMain.on('open-logs-folder', (event) => {
    let logPath;
    if (app.isPackaged) {
        logPath = path.join(app.getPath('logs'), 'backend.log');
    } else {
        logPath = path.join(__dirname, '../backend.log');
    }

    // 确保日志文件存在
    if (fs.existsSync(logPath)) {
        shell.showItemInFolder(logPath);
    } else {
        // 如果日志文件不存在，则打开日志目录
        const logDir = path.dirname(logPath);
        if (fs.existsSync(logDir)) {
            shell.openPath(logDir);
        }
    }
});

// 或者添加一个读取日志内容的监听器
ipcMain.handle('read-backend-logs', async () => {
    let logPath;
    if (app.isPackaged) {
        logPath = path.join(app.getPath('logs'), 'backend.log');
    } else {
        logPath = path.join(__dirname, '../backend.log');
    }

    try {
        if (fs.existsSync(logPath)) {
            const logs = fs.readFileSync(logPath, 'utf8');
            return logs;
        }
        return '日志文件不存在';
    } catch (error) {
        return `读取日志失败: ${error.message}`;
    }
});