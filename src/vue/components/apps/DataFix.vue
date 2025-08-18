<template>
  <App ref="app">
    <ButtonPanel
        :buttons="buttonConfig"
    />
    <div class="content-box">
      <div v-if="showContext === 'form'" class="form-content">
        <el-form :model="form" label-width="200px" label-position="left">
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
        </el-form>
      </div>
      <div v-if="showContext === 'log'" class="table-content">
        <div class="header-actions">
          <el-progress
              v-if="showProgress"
              :percentage="deleteProgress"
              :color="customColor"
              :status="progressStatus">
          </el-progress>
          <el-button-group>
            <el-button
                type="primary"
                @click="executeFix"
                :loading="loading"
                :disabled="loading"
            >
              {{ loading ? '执行中...' : '执行修复' }}
            </el-button>
          </el-button-group>
        </div>
        <div class="log-area">
          <el-input
              type="textarea"
              :rows="2"
              placeholder="执行日志..."
              :value="contentsStr"
              readonly>
          </el-input>
        </div>
      </div>
    </div>
  </App>
</template>

<script>
import ButtonPanel from "./base/ButtonPanel.vue";
import App from './base/App.vue'
import { AppMixin } from './base/AppMixin'
import axios from 'axios'
import { Search, Delete } from '@element-plus/icons-vue'
import { ElProgress } from 'element-plus'

export default {
  components: {App, ButtonPanel, Search, Delete, ElProgress},
  mixins: [AppMixin],
  data() {
    return {
      name: 'DataFix',
      title: '数据修复',
      showContext: 'form',
      // 进度条相关数据
      showProgress: false, // 控制进度条显示
      deleteProgress: 0,   // 删除进度百分比
      progressStatus: null, // 进度条状态
      customColor: '#409eff',
      form: {
        domain: 'https://c4.yonyoucloud.com',
        cookie: 'at=527b707d-4dae-4644-bea1-ccb00c4e20ea; JSESSIONID=...; yht_access_token=...',
        codes: 'LLJY202508010006,LLJY202508010005',
        isStorage: '1'
      },
      logs: [],
      loading: false,
      contentsStr: ''
    }
  },
  computed: {
    // 定义按钮配置
    buttonConfig() {
      return [
        { label: '配置参数', handler: function() { this.changePanel('form') }.bind(this) },
        { label: '执行修复', handler: function() { this.changePanel('log') }.bind(this) },
      ];
    }
  },
  async mounted() {
    await this.$nextTick();
    // 然后执行原来的逻辑
    if (window.api && typeof window.api.onLogMessage === 'function' && typeof window.api.onFixFinished === 'function') {
      window.api.onLogMessage((event, message) => {
        console.log('message:', message);
        this.appendLog(message);
      });

      window.api.onFixFinished((event, result) => {
        if (result.success) {
          this.appendLog('数据修复完成!');
        } else {
          this.appendLog(`数据修复失败，错误码: ${result.code}`);
        }
        this.loading = false;
      });
    }
  },
  methods: {
    changePanel(panelName) {
      this.showContext = panelName
    },
    executeFix() {
      this.loading = true;
      this.logs = [];
      this.appendLog('开始执行修复...');

      // 使用 Electron 暴露的 API 发送请求
      window.api.executeFix({
        isStorage: this.form.isStorage,
        codes: this.form.codes,
        domain: this.form.domain,
        cookie: this.form.cookie
      });
    },
    appendLog(message) {
      const logEntry = `[${new Date().toLocaleString()}] ${message}`;
      this.logs.push(logEntry);
      // 同时更新 contentsStr
      this.contentsStr = this.contentsStr ? `${this.contentsStr}\n${logEntry}` : logEntry;
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.content-box {
  width: 100%;
  height: calc(100% - 50px);
  top: 50px;
  position: absolute;

  .form-content {
    width: calc(100% - 20px);
    height: calc(100% - 20px);
    left: 10px;
    top: 10px;
    overflow: hidden;
    display: block;
    position: absolute;
  }

  .table-content {
    height: 100%;
    overflow: hidden;

    // 调整后的样式
    .header-actions {
      display: flex;
      justify-content: center; /* 整行居中对齐 */
      align-items: center;
      padding: 10px;
      border-bottom: 1px solid #ebeef5;
      position: relative; /* 为绝对定位提供参考 */

      :deep(.el-select) {
        width: 120px;
        height: 40px;
      }
      :deep(.el-select .el-select__wrapper) {
        height: 100%;

        border-radius: 20px !important;

        background-color: rgba(255, 255, 255, 0.15);
        backdrop-filter: blur(12px) saturate(150%);
        -webkit-backdrop-filter: blur(12px) saturate(150%);
        border: 1px solid rgba(255, 255, 255, 0.4);
        box-shadow: 0 20px 30px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
      }

      :deep(.el-button-group) {
        /* 居中显示 */
        margin:0px 10px 0px 10px;
        border-radius: 20px !important;

        background-color: rgba(255, 255, 255, 0.15);
        backdrop-filter: blur(12px) saturate(150%);
        -webkit-backdrop-filter: blur(12px) saturate(150%);
        border: 1px solid rgba(255, 255, 255, 0.4);
        box-shadow: 0 20px 30px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
      }

      // 直接针对按钮组内的按钮
      :deep(.el-button-group .el-button) {
        height: 40px;
        color: #5e5e5e;
        background-color: transparent !important;
        background: transparent !important;
        border: none !important;
        border-radius: 0 !important;
        box-shadow: none !important;
      }


      :deep(.el-progress) {
        position: absolute; /* 绝对定位 */
        left: 10px; /* 距离右边的距离 */
        width: 100px;
        height: 20px;
      }
    }

    .log-area {
      height: 100%;
      :deep(.el-textarea) {
        height: 100%;

        textarea {
          width: calc(100% - 20px);
          height: calc(100% - 75px);
          left: 10px;
          border-radius: 20px;
        }
      }
    }
  }
}
</style>
