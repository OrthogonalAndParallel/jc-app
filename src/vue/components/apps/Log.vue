<template>
  <App ref="app">
    <div>
      <h2>后端日志</h2>
      <div>
        <button @click="loadLogs">刷新日志</button>
        <button @click="openLogsFolder">打开日志文件夹</button>
      </div>
      <div class="log-container">
        <pre>{{ logs }}</pre>
      </div>
    </div>
  </App>
</template>

<script>
import ButtonPanel from "./base/ButtonPanel.vue";
import App from './base/App.vue'
import { AppMixin } from './base/AppMixin'

export default {
  components: {App, ButtonPanel},
  mixins: [AppMixin],
  data() {
    return {
      name: 'Log',
      title: '日志管理',
      logs: ''
    }
  },
  methods: {
    async loadLogs() {
      try {
        this.logs = await window.electronAPI.readBackendLogs();
      } catch (error) {
        this.logs = `获取日志失败: ${error.message}`;
      }
    },
    openLogsFolder() {
      window.electronAPI.openLogsFolder();
    }
  },
  mounted() {
    this.loadLogs();
  }
}
</script>

<style scoped>
.log-container {
  border: 1px solid #ccc;
  height: 400px;
  overflow-y: auto;
  padding: 10px;
  background-color: #f5f5f5;
}
</style>
