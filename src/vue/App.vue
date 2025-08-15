<template>
  <el-container class="app-container">
    <el-header>
      <h1 class="title">质检数据修复工具</h1>
    </el-header>
    <el-main>
      <el-card class="box-card">
        <template #header>
          <div class="card-header">
            <span>参数设置</span>
          </div>
        </template>
        <el-form :model="form" label-width="120px" label-position="left">
          <el-form-item label="域名(domain)">
            <el-input v-model="form.domain" placeholder="https://c4.yonyoucloud.com"></el-input>
          </el-form-item>
          <el-form-item label="Cookie">
            <el-input
                v-model="form.cookie"
                type="textarea"
                :rows="5"
                placeholder="请输入Cookie"
            ></el-input>
          </el-form-item>
          <el-form-item label="检验单号(codes)">
            <el-input v-model="form.codes" placeholder="LLJY202508010006,LLJY202508010005"></el-input>
          </el-form-item>
          <el-form-item label="是否可入库(is_storage)">
            <el-input v-model="form.isStorage" placeholder="1"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button
                type="primary"
                @click="executeFix"
                :loading="loading"
                :disabled="loading"
            >
              {{ loading ? '执行中...' : '执行修复' }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="log-card">
        <template #header>
          <div class="card-header">
            <span>执行日志</span>
          </div>
        </template>
        <div class="log-area">
          <p v-for="(log, index) in logs" :key="index" :class="{ 'error-log': log.startsWith('[ERROR]') }">
            {{ log }}
          </p>
        </div>
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const loading = ref(false);
const logs = ref([]);
const form = ref({
  domain: 'https://c4.yonyoucloud.com',
  cookie: 'at=527b707d-4dae-4644-bea1-ccb00c4e20ea; JSESSIONID=...; yht_access_token=...',
  codes: 'LLJY202508010006,LLJY202508010005',
  isStorage: '1'
});

const appendLog = (message) => {
  logs.value.push(`[${new Date().toLocaleString()}] ${message}`);
};

const executeFix = () => {
  loading.value = true;
  logs.value = [];
  appendLog('开始执行修复...');

  // 使用 Electron 暴露的 API 发送请求
  window.api.executeFix({
    isStorage: form.value.isStorage,
    codes: form.value.codes,
    domain: form.value.domain,
    cookie: form.value.cookie
  });
};

onMounted(() => {
  // 监听来自主进程的日志消息
  window.api.onLogMessage((message) => {
    appendLog(message);
  });

  // 监听修复完成事件
  window.api.onFixFinished((result) => {
    if (result.success) {
      appendLog('数据修复完成!');
    } else {
      appendLog(`数据修复失败，错误码: ${result.code}`);
    }
    loading.value = false;
  });
});
</script>

<style scoped>
.app-container {
  height: 100vh;
}
.title {
  color: #005a9c;
  margin: 0;
  text-align: center;
}
.box-card {
  margin-bottom: 20px;
}
.log-card {
  min-height: 250px;
}
.log-area {
  background-color: #f5f7fa;
  border: 1px solid #dcdfe6;
  padding: 10px;
  max-height: 250px;
  overflow-y: auto;
  font-family: monospace;
  font-size: 14px;
  line-height: 1.5;
}
.error-log {
  color: red;
}
</style>