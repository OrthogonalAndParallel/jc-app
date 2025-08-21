const { app, BrowserWindow, ipcMain } = require('electron');
const path = require('path');
const { spawn } = require('child_process');
const fs = require('fs');

let backendProcess;
function startBackend() {
    const javaExecutable = 'java';
    let jarPath;

    if (app.isPackaged) {
        jarPath = path.join(process.resourcesPath, 'app.asar.unpacked/target/jc-app-1.0-SNAPSHOT-jar-with-dependencies.jar');
    } else {
        jarPath = path.join(__dirname, '../target/jc-app-1.0-SNAPSHOT-jar-with-dependencies.jar');
    }

    const javaArgs = ['-jar', jarPath]; // 可以添加 -Dserver.port=... 来指定端口

    backendProcess = spawn(javaExecutable, javaArgs);

    backendProcess.stdout.on('data', (data) => {
        console.log(`[Backend] ${data}`);
    });

    backendProcess.stderr.on('data', (data) => {
        console.error(`[Backend ERROR] ${data}`);
    });

    backendProcess.on('close', (code) => {
        console.log(`Backend process exited with code ${code}`);
    });
}

function createWindow() {
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
    startBackend();
    createWindow();

    app.on('activate', () => {
        if (BrowserWindow.getAllWindows().length === 0) {
            if (backendProcess) {
                backendProcess.kill();
            }
            createWindow();
        }
    });
});

app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
        app.quit();
    }
});

/* 监听来自渲染进程的 fix-request
ipcMain.on('execute-fix', (event, { isStorage, codes, domain, cookie }) => {
    const javaExecutable = 'java';

    // const jarPath = path.join(__dirname, '../target/jc-app-1.0-SNAPSHOT-jar-with-dependencies.jar');

    let jarPath;
    if (app.isPackaged) {
        // 打包模式下，JAR 包被 asarUnpack 解压到 app.asar.unpacked 目录
        jarPath = path.join(process.resourcesPath, 'app.asar.unpacked/target/jc-app-1.0-SNAPSHOT-jar-with-dependencies.jar');
    } else {
        // 开发模式下，路径保持不变
        jarPath = path.join(__dirname, '../target/jc-app-1.0-SNAPSHOT-jar-with-dependencies.jar');
    }

    const javaArgs = ['-jar', jarPath, isStorage, codes, domain, cookie];

    const child = spawn(javaExecutable, javaArgs);

    child.stdout.on('data', (data) => {
        event.sender.send('log-message', data.toString());
    });

    child.stderr.on('data', (data) => {
        event.sender.send('log-message', `[ERROR] ${data.toString()}`);
    });

    child.on('close', (code) => {
        if (code === 0) {
            event.sender.send('log-message', 'Java进程执行完成，成功退出。');
            event.sender.send('fix-finished', { success: true });
        } else {
            event.sender.send('log-message', `Java进程异常退出，错误码: ${code}`);
            event.sender.send('fix-finished', { success: false, code: code });
        }
    });
});
*/