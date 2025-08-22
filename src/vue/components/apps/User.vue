<template>
  <App ref="app">
    <ButtonPanel
        :buttons="buttonConfig"
    />
    <div class="content-box">
      <SlideTransition>
        <div v-if="showContext === 'form'" class="form-content">
          <div class="header-actions">
            <el-button-group>
              <el-button @click="saveData()" title="保存">
                <el-icon><Check /></el-icon>
              </el-button>
            </el-button-group>
          </div>
          <el-form :model="form" label-width="200px" label-position="left">
            <el-form-item label="名称">
              <el-input v-model="form.name"></el-input>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email"></el-input>
            </el-form-item>
          </el-form>
        </div>
        <div v-if="showContext === 'table'" class="table-content">
          <div class="header-actions">
            <el-progress
                v-if="showProgress"
                :percentage="deleteProgress"
                :color="customColor"
                :status="progressStatus">
            </el-progress>
            <!-- 添加项目选择下拉框 -->
            <el-button-group>
              <el-button @click="queryData()" title="查询">
                <el-icon><Search /></el-icon>
              </el-button>
              <el-button @click="deleteData()" :disabled="selectedRows.length === 0" title="删除">
                <el-icon><Delete /></el-icon>
              </el-button>
            </el-button-group>
            <input class="search-box" v-model="searchText" placeholder="请输入内容进行搜索" id="searchText" />
          </div>
          <el-table
              :data="filteredTableData"
              @selection-change="handleSelectionChange"
              @row-click="handleRowClick"
              ref="table"
              select-on-click-row>
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column prop="name" label="名称" width="220" show-overflow-tooltip></el-table-column>
            <el-table-column prop="email" label="邮箱" width="120"></el-table-column>
            <el-table-column prop="createTime" label="创建时间"></el-table-column>
            <el-table-column prop="id" label="ID"></el-table-column>
          </el-table>
          <div class="pagination-container">
            <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="currentPage"
                :page-sizes="[10, 20, 50, 100]"
                :page-size="pageSize"
                :total="filteredTableData.length"
                layout="total, sizes, prev, pager, next, jumper"
                background
            >
            </el-pagination>
          </div>
        </div>
      </SlideTransition>
    </div>
  </App>
</template>

<script>
import ButtonPanel from "./base/ButtonPanel.vue";
import App from './base/App.vue'
import { AppMixin } from './base/AppMixin'
import { apiClient } from './base/api'
import { Search, Delete } from '@element-plus/icons-vue'
import { ElProgress } from 'element-plus'
import SlideTransition from "./base/SlideTransition.vue";

export default {
  components: {App, ButtonPanel, Search, Delete, ElProgress, SlideTransition},
  mixins: [AppMixin],
  data() {
    return {
      name: 'User',
      title: '用户管理',
      showContext: 'form',

      // 表单数据
      form: {
        name: '王木',
        email: 'wang@test.com'
      },

      // 列表数据
      tableData: [],
      searchText: '',
      selectedRows: [],

      // 分页相关数据
      currentPage: 1,
      pageSize: 20,
      selectedProjectId: '',

      // 进度条相关数据
      showProgress: false, // 控制进度条显示
      deleteProgress: 0,   // 删除进度百分比
      progressStatus: null, // 进度条状态
      customColor: '#409eff'
    }
  },
  computed: {
    // 定义按钮配置
    buttonConfig() {
      return [
        { label: '新增用户', handler: function() { this.changePanel('form') }.bind(this) },
        { label: '管理用户', handler: function() { this.changePanel('table') }.bind(this) },
      ];
    },
    // 添加过滤后的表格数据计算属性
    filteredTableData() {
      if (!this.searchText) {
        return this.tableData;
      }
      const searchText = this.searchText.toLowerCase();
      return this.tableData.filter(item =>
          item.name.toLowerCase().includes(searchText)
      );
    },
    // 添加分页后的数据计算属性
    paginatedTableData() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.filteredTableData.slice(start, end);
    }
  },
  methods: {
    /**
     * 改变面板
     * @param panelName
     */
    changePanel(panelName) {
      this.showContext = panelName
    },
    // 选择数据
    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },
    // 添加处理行点击的方法
    handleRowClick(row, column, event) {
      // 检查是否按下了 Shift 键
      if (event.shiftKey && this.lastSelectedIndex !== -1) {
        const currentIndex = this.filteredTableData.indexOf(row);
        const [start, end] = [this.lastSelectedIndex, currentIndex].sort((a, b) => a - b);

        // 清除当前选择
        this.$refs.table.clearSelection();

        // 选择范围内的所有行
        for (let i = start; i <= end; i++) {
          this.$refs.table.toggleRowSelection(this.filteredTableData[i], true);
        }
      } else {
        // 更新最后选中的索引
        this.lastSelectedIndex = this.filteredTableData.indexOf(row);
      }
    },
    // 分页大小改变处理
    handleSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1; // 重置到第一页
    },
    // 当前页改变处理
    handleCurrentChange(val) {
      this.currentPage = val;
    },
    // 查询数据
    async queryData() {
      try {
        const data = await apiClient.get('/user/list');
        this.tableData = data;
      } catch (error) {
        console.error('查询失败:', error);
      }
    },
    // 保存数据
    async saveData() {
      try {
        const result = await apiClient.post('/user/save', this.form);
        console.log('保存成功:', result);
      } catch (error) {
        console.error('保存失败:', error);
      }
    },
    // 删除数据
    async deleteData() {
      if (this.selectedRows.length === 0) return;

      try {
        const ids = this.selectedRows.map(row => row.id);
        await apiClient.delete('/user/del', { ids });

        // 更新本地数据
        this.tableData = this.tableData.filter(
            item => !ids.includes(item.id)
        );
        this.selectedRows = [];
      } catch (error) {
        console.error('删除失败:', error);
      }
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
    height: 100%;
    width: 100%;
    overflow: hidden;
    display: block;
    position: absolute;
    .el-form {
      width: calc(100% - 100px);
      margin: 0 auto;
    }
  }

  .table-content {
    height: 100%;
    overflow: hidden;

    .el-table {
      width: 100%;
      // 调整高度以适应新的布局
      height: calc(100% - 120px);
    }

    .pagination-container {
      height: 35px;
      display: flex;
      justify-content: center;
      padding: 10px 0;
    }
  }

  .form-content, .table-content {
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

      .search-box {
        position: absolute; /* 绝对定位 */
        right: 10px; /* 距离右边的距离 */

        height: 40px;
        width: 200px;
        border-radius: 20px;

        /* 添加内边距以美化输入框内容 */
        padding: 0 15px;
        /* 确保输入框文本垂直居中 */
        line-height: 40px;

        background-color: rgba(255, 255, 255, 0.15);
        backdrop-filter: blur(12px) saturate(150%);
        -webkit-backdrop-filter: blur(12px) saturate(150%);
        border: 1px solid rgba(255, 255, 255, 0.4);
        box-shadow: 0 20px 30px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
      }
    }
  }

}
</style>
