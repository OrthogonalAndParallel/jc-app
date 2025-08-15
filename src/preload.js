const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('api', {
    executeFix: (params) => ipcRenderer.send('execute-fix', params),
    onLogMessage: (callback) => ipcRenderer.on('log-message', (event, data) => callback(data)),
    onFixFinished: (callback) => ipcRenderer.on('fix-finished', (event, result) => callback(result))
});