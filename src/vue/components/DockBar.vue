<template>
  <div class="dock-bar">
    <div
        class="dock-container"
        @mousemove="onDockMouseMove"
        @mouseleave="onDockMouseLeave"
    >
      <div
          v-for="(app, index) in apps"
          :key="app.name"
          class="dock-item"
          @click="changeShowApp(app.name)"
          @mouseenter="showTooltip(app.name, $event)"
          @mouseleave="hideTooltip"
          :data-tooltip="app.name"
          :style="getDockItemStyle(index)"
      >
        <div class="icon" :style="{ backgroundImage: 'url(' + app.icon + ')' }"></div>
      </div>
      <div v-if="tooltipVisible" class="tooltip" :style="dynamicTooltipStyle">{{ tooltipText }}</div>
    </div>
  </div>
</template>

<script>
import { EventBus } from '../utils/event-bus'
import {AppRegistry} from './apps/data/AppRegistry.js'

export default {
  name: 'DockBar',
  data () {
    return {
      apps: Object.values(AppRegistry),
      tooltipVisible: false,
      tooltipText: '',
      // tooltipStyle 不再作为 data 属性，改为计算属性
      hoveredApp: null, // 新增：记录当前悬停的应用名称

      // ====== Dock 特效相关数据 ======
      mousePos: { x: 0, y: 0 }, // 鼠标在 dock-container 内部的相对位置
      itemScales: {}, // 存储每个图标的当前放大倍数
      itemWidth: 50, // 每个 dock-item 的宽度
      itemMargin: 5, // 每个 dock-item 的左右外边距
      maxScale: 1.5, // 最大放大倍数
      minScale: 1.0, // 最小缩放倍数
      magnificationRadius: 100 // 放大影响范围（鼠标中心向外扩散的距离）
    }
  },
  mounted() {
    // 初始化时设置所有图标的缩放为最小
    this.apps.forEach(app => {
      this.itemScales[app.name] = this.minScale;
    });
  },
  computed: {
    // 动态计算 tooltip 的样式
    dynamicTooltipStyle() {
      // 如果 tooltip 不可见或没有悬停的应用，则隐藏 tooltip
      if (!this.tooltipVisible || !this.hoveredApp) {
        return {
          opacity: 0,
          pointerEvents: 'none' // 确保隐藏时不可交互
        };
      }

      // 找到当前悬停应用的索引
      const hoveredIndex = this.apps.findIndex(app => app.name === this.hoveredApp);
      if (hoveredIndex === -1) {
        return { opacity: 0 };
      }

      // 获取当前悬停图标的 DOM 元素
      const hoveredItem = this.$el.querySelectorAll('.dock-item')[hoveredIndex];
      if (!hoveredItem) {
        return { opacity: 0 };
      }

      // 获取图标和容器的相对位置和尺寸
      const rect = hoveredItem.getBoundingClientRect();
      const containerRect = this.$el.querySelector('.dock-container').getBoundingClientRect();

      // 获取当前图标的缩放比例（从响应式数据中获取，而非 DOM 计算样式）
      const currentScale = this.itemScales[this.hoveredApp] || this.minScale;

      // 计算原始高度和放大后的高度
      const originalHeight = this.itemWidth;
      const magnifiedHeight = originalHeight * currentScale;

      // 计算由于放大而引起的向上偏移量（与 getDockItemStyle 中的 translateY 对应）
      const translateYOffset = (magnifiedHeight - originalHeight) / 2;

      // 计算 tooltip 的水平位置：图标中心 - tooltip 宽度的一半 (假设 tooltip 宽度为 60px)
      const left = rect.left - containerRect.left + rect.width / 2 - 30;

      // 计算 tooltip 的垂直位置：图标顶部 - 额外间距 - 向上偏移量
      const top = rect.top - containerRect.top - 40 - translateYOffset; // 40px 是 tooltip 垂直偏移和上下间距

      return {
        left: `${left}px`,
        top: `${top}px`,
        opacity: 1, // 确保 tooltip 可见
        pointerEvents: 'none' // 确保 tooltip 不会阻挡鼠标事件
      };
    }
  },
  methods: {
    changeShowApp (appName) {
      EventBus.emit('show-app', appName)
    },
    // showTooltip 仅设置状态和记录悬停应用
    showTooltip (text, event) {
      this.tooltipText = text;
      this.hoveredApp = text; // 记录当前悬停的应用
      this.tooltipVisible = true;
    },
    // hideTooltip 仅清除状态
    hideTooltip () {
      this.tooltipVisible = false;
      this.tooltipText = '';
      this.hoveredApp = null; // 清除悬停的应用
    },
    // ====== Dock 特效相关方法 ======
    onDockMouseMove(event) {
      const container = event.currentTarget;
      const rect = container.getBoundingClientRect();
      this.mousePos.x = event.clientX - rect.left;
      this.mousePos.y = event.clientY - rect.top;

      this.updateItemScales();
    },
    onDockMouseLeave() {
      this.mousePos.x = 0; // 重置鼠标位置，使其不再影响缩放
      this.apps.forEach(app => {
        this.itemScales[app.name] = this.minScale;
      });
      // 鼠标离开时隐藏 tooltip
      this.hideTooltip();
    },
    // 确保 'this' 上下文正确指向 Vue 实例
    updateItemScales() {
      const centerOfDockY = this.itemWidth / 2 + this.itemMargin;

      this.apps.forEach((app, index) => {
        const itemCenterX = (index * (this.itemWidth + 2 * this.itemMargin)) + this.itemWidth / 2 + this.itemMargin;
        const distanceX = Math.abs(this.mousePos.x - itemCenterX);
        const distanceY = Math.abs(this.mousePos.y - centerOfDockY);
        const distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        let scale = this.minScale;
        if (distance < this.magnificationRadius) {
          scale = this.minScale + (this.maxScale - this.minScale) * (1 - distance / this.magnificationRadius);
        }
        scale = Math.max(this.minScale, Math.min(this.maxScale, scale));

        this.itemScales[app.name] = scale;
      });
    },
    getDockItemStyle(index) {
      const appName = this.apps[index].name;
      const scale = this.itemScales[appName] || this.minScale;

      const originalHeight = this.itemWidth;
      const magnifiedHeight = originalHeight * scale;
      const translateY = (magnifiedHeight - originalHeight) / 2;

      return {
        transform: `scale(${scale}) translateY(-${translateY}px)`,
        transition: 'transform 0.15s ease-out'
      };
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
.dock-bar {
  position: fixed;
  bottom: 10px;
  width: 100%;
  z-index: 2;
  display: flex;
  justify-content: center;

  @media (max-height: 600px) {
    bottom: 5px;
  }
}

.dock-container {
  display: flex;
  padding: 10px 0;
  border-radius: 20px;
  position: relative;

  /* Liquid Glass 核心样式 */
  background-color: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px) saturate(150%);
  -webkit-backdrop-filter: blur(12px) saturate(150%);
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s ease;

  .dock-item {
    position: relative;
    z-index: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 50px;
    height: 50px;
    margin: 0 5px;
    cursor: pointer;
    color: #333;

    .icon {
      width: 40px;
      height: 40px;
      background-size: cover;
      background-position: center;
      border-radius: 8px;
      transition: all 0.2s ease;
    }
  }
  .tooltip {
    position: absolute;
    background: #d9ecff;
    color: #1f2d3d;
    padding: 3px 9px;
    border-radius: 16px;
    font-size: 12px;
    white-space: nowrap;
    // pointer-events: none; // 保持不可交互，由 JS 动态控制
    z-index: 10;
    // opacity: 0; // opacity 由 JS 控制
    transition: opacity 0.2s ease, left 0.15s ease-out, top 0.15s ease-out; // 增加 left 和 top 的过渡
    // transform: translateY(-10px); // 移除：由 JS 动态计算 top
    // animation: tooltipFadeIn 0.2s ease forwards; // 移除：由 JS 控制 opacity 和 top

    /* 添加箭头 */
    &::after {
      content: '';
      position: absolute;
      bottom: -8px;
      left: 50%;
      margin-left: -8px;
      width: 0;
      height: 0;
      border-left: 8px solid transparent;
      border-right: 8px solid transparent;
      border-top: 8px solid #d9ecff;
    }
  }
}

// 移除 @keyframes tooltipFadeIn，因为 opacity 和 top 由 JS 动态设置和过渡
/*
@keyframes tooltipFadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
*/
</style>