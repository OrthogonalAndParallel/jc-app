const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('electronAPI', {
    // executeFix: (params) => ipcRenderer.send('execute-fix', params),
    onLogMessage: (callback) => ipcRenderer.on('log-message', (event, data) => {
        callback(event, data);
    }),
    onFixFinished: (callback) => ipcRenderer.on('fix-finished', (event, result) => callback(event, result)),
    readBackendLogs: () => ipcRenderer.invoke('read-backend-logs'),
    openLogsFolder: () => ipcRenderer.send('open-logs-folder')
});