<template>
  <div class="app-box" :class="{nofull:!fullScreen, full:fullScreen}" :style="positionStyle" v-if="isShow">
    <div
        class="title-box"
        id="title-box"
        draggable="true"
        @dragstart="onDragStart"
        @drag="onDrag"
        @dragend="onDragEnd"
    >
      <div class="title">{{ title }}</div>
      <div class="option-box">
        <el-icon @click="changeShow()" class="close-icon">
          <CircleCloseFilled />
        </el-icon>
        <el-icon @click="changeFull()" v-if="!fullScreen" class="maximize-icon">
          <CirclePlusFilled />
        </el-icon>
        <el-icon @click="changeFull()" v-if="fullScreen" class="restore-icon">
          <RemoveFilled />
        </el-icon>
      </div>
    </div>
    <slot></slot>
  </div>
</template>

<script>
import { gsap } from 'gsap'
export default {
  data() {
    return {
      isShow: false,
      fullScreen: false,
      // 存储用于 transform 的当前 X 和 Y 偏移量
      translateX: 0, // 初始 X 位置设置为 0
      translateY: 0, // 初始 Y 位置设置为 0
      offsetX: 0,
      offsetY: 0,
      title: '标题',
      isAnimating: false, // 新增：控制动画状态
      initialPosition: { // 新增：用于 Dock 栏动画的起始位置和大小
        left: '50%', // 示例：从屏幕底部中心开始
        bottom: '20px', // 示例：从屏幕底部中心开始
        width: '0px', // 初始宽度为 0
        height: '0px', // 初始高度为 0
        transform: 'translate(-50%, 0) scale(0)' // 初始缩放为 0，并水平居中
      }
    }
  },
  watch: {
    // 监听 fullScreen 变化，以便在退出全屏时重新调整位置
    fullScreen(newVal) {
      if (!newVal) {
        // 当退出全屏时，将窗口重置到初始或上次的非全屏位置
        // 这里可以根据您的需求保存和恢复上次的 translateX/Y
        // 例如，可以保存退出全屏前的 translate 值
        // 为了简单起见，我们假设退出全屏后保持在当前拖动结束的位置或初始位置
        // 如果没有进行拖动，它会回到初始的 300, 100
      }
    }
  },
  computed: {
    positionStyle() {
      if (this.isAnimating) { // 如果正在动画中
        // 动画期间，使用 initialPosition 作为起始状态
        return {
          left: this.initialPosition.left, // 使用 initialPosition 的 left
          bottom: this.initialPosition.bottom, // 使用 initialPosition 的 bottom
          width: this.initialPosition.width, // 使用 initialPosition 的 width
          height: this.initialPosition.height, // 使用 initialPosition 的 height
          transform: this.initialPosition.transform, // 使用 initialPosition 的 transform
          transition: 'all 0.5s cubic-bezier(0.68, -0.55, 0.27, 1.55)' // 平滑的动画曲线
        };
      } else if (!this.fullScreen) { // 如果不是全屏模式且不在动画中
        // 动画结束后或非动画状态且非全屏时
        return {
          transform: `translate(${this.translateX}px, ${this.translateY}px)`, // 应用当前的拖动位置
          transition: 'none' // 拖动时禁用过渡效果，确保拖动流畅
        };
      }
      // 全屏或默认状态
      return {
        transition: 'none' // 全屏或其他状态下禁用过渡效果
      };
    }
  },
  methods: {
    init(title) {
      this.title = title
    },
    changeShow() {
      if (!this.isShow) {
        this.isShow = true;
        this.$nextTick(() => {
          const el = this.$el;
          const windowWidth = window.innerWidth;
          const windowHeight = window.innerHeight;
          const elWidth = el.offsetWidth;
          const elHeight = el.offsetHeight;

          // 計算置中位置
          this.translateX = 0;
          this.translateY = 0;

          // 使用 GSAP 從中心向外執行動畫
          gsap.fromTo(el,
              {
                x: this.translateX,
                y: this.translateY,
                scale: 0.5,
                opacity: 0,
              },
              {
                duration: 0.5,
                scale: 1,
                opacity: 1,
                ease: 'back.out(1.7)',
                onComplete: () => {
                  this.isAnimating = false;
                  this.translateX = 0;
                  this.translateY = 0;
                }
              }
          );
        });
      } else {
        // 使用 GSAP 执行关闭动画
        const el = this.$el;
        gsap.to(el, {
          duration: 0.3,
          scale: 0.8,
          opacity: 0,
          x: this.translateX,
          ease: 'power2.in',
          onComplete: () => {
            this.isShow = false;
            this.$emit('close');
          }
        });
      }
    },
    initAndShow(title) {
      this.init(title);
      this.changeShow();
    },
    changeFull() {
      // 切换全屏状态
      this.fullScreen = !this.fullScreen;
      // 当切换到非全屏时，如果希望窗口回到上次拖动的位置，
      // 可以在此处重新计算 translateX/Y 或保存其值。
    },
    onDragStart(event) {
      const el = this.$el;
      const style = window.getComputedStyle(el);
      // 获取当前计算出的 transform 属性
      const transform = style.transform;

      let currentX = 0;
      let currentY = 0;

      // 解析 transform 矩阵以获取当前的 translateX 和 translateY
      if (transform && transform !== 'none') {
        const matrix = transform.match(/matrix.*\((.+)\)/)[1].split(', ');
        // 2D 转换矩阵的 m41 是 translateX，m42 是 translateY
        // 如果是 3D 转换矩阵，则为 matrix3d(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
        // 其中 m 是 translateX (index 12), n 是 translateY (index 13)
        // 考虑到这里主要使用 2D transform，使用 matrix(a,b,c,d,e,f) 形式
        // e 是 translateX (index 4), f 是 translateY (index 5)
        // 实际上在 DOMMatrixReadOnly 中，m41 和 m42 更通用
        currentX = parseFloat(matrix[4] || 0); // translateX
        currentY = parseFloat(matrix[5] || 0); // translateY
      }

      // 将当前元素的 transform 值同步到 data 中的 translateX 和 translateY
      this.translateX = currentX;
      this.translateY = currentY;

      // 计算鼠标点击位置相对于当前元素 transform 后的左上角的偏移量
      this.offsetX = event.clientX - this.translateX;
      this.offsetY = event.clientY - this.translateY;

      // 阻止默认拖动图像
      event.dataTransfer.setDragImage(new Image(), 0, 0);
    },
    onDrag(event) {
      // 当拖动突然结束时，event.clientX/Y 可能为 0，因此进行检查
      if (event.clientX === 0 && event.clientY === 0) return;

      // 更新 translateX 和 translateY
      // 新的 translate 值 = 当前鼠标位置 - 鼠标点击点相对于元素左上角的偏移量
      this.translateX = event.clientX - this.offsetX;
      this.translateY = event.clientY - this.offsetY;
    },
    onDragEnd(event) {
      // 拖动结束时，`onDrag` 已经更新了 `translateX` 和 `translateY` 到最终位置，
      // 所以这里通常不需要额外的赋值。
      // 如果有吸附到网格或最终位置调整的逻辑，可以在这里添加。
    }
  },
  props: {
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
.app-box {
  overflow: hidden;
  background: #f2f2f2c2 !important;
  backdrop-filter: blur(15px) saturate(1.5);
  border-radius: 20px;
  box-shadow: 0 1px 1px #fff inset, 0 15px 20px #1f2d4059;
  position: absolute; // 保持此属性
  // 添加一个默认过渡，用于非动画状态下可能变化的属性，但避免在此处使用 `transform`
  transition: width 0.3s ease, height 0.3s ease, top 0.3s ease, left 0.3s ease;

  &.nofull {
    width: 800px;
    height: 600px;
  }

  &.full {
    top: 10px;
    left: 10px; // 确保全屏时正确定位
    width: calc(100% - 20px);
    height: calc(100% - 85px);
    transform: none !important; // 全屏时覆盖 transform 非常重要
    transition: width 0.3s ease, height 0.3s ease, top 0.3s ease, left 0.3s ease; // 全屏时的平滑过渡
  }

  .title-box {
    width: 100%;
    height: 50px;
    line-height: 50px;
    position: relative;
    background: #f4f4f4b3;
    cursor: move;

    .title {
      font-size: 16px;
      color: #333;
      text-align: center;
      font-weight: bolder;
    }

    .option-box {
      width: 44px;
      height: 24px;
      position: absolute;
      z-index: 1;
      left: 10px;
      top: 50%;
      transform: translateY(-50%);
      display: flex;
      align-items: center;
      justify-content: center;

      .el-icon {
        margin: 3px;
        cursor: pointer;
        width: 18px;
        height: 18px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.2s ease;

        &:hover {
          background-color: rgba(0,0,0,0.1);
        }
      }

      .close-icon:hover {
        background-color: #ff5f56 !important;
        transform: rotate(90deg);
      }

      .maximize-icon:hover {
        background-color: #27c93f !important;
      }

      .restore-icon:hover {
        background-color: #27c93f !important;
      }
    }
  }
}
</style>
