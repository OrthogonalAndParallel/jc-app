import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

export default defineConfig({
    plugins: [vue()],
    root: path.resolve(__dirname, './'),
    build: {
        outDir: 'dist-vue',
    },
    server: {
        // 确保 Vite 监听所有可用的网络接口
        host: '0.0.0.0',
        port: 5173
    },
    base: './' // 使用相对路径，这对 Electron 很重要
});