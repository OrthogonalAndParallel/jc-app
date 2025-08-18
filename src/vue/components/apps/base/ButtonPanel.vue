<template>
  <div class="btn-box-wrapper">
    <div class="btn-box" :class="{ 'collapsed': isCollapsed }">
      <el-button
          v-for="(button, index) in buttons"
          :key="index"
          @click="button.handler"
      >
        <div>{{ button.label }}</div>
      </el-button>
    </div>
    <div class="toggle-icon-container" @click="toggle">
      <el-icon>
        <component :is="isCollapsed ? 'ArrowRight' : 'ArrowLeft'" />
      </el-icon>
    </div>
  </div>
</template>

<script>export default {
  name: 'ButtonPanel',
  props: {
    buttons: {
      type: Array,
      required: true,
      default: () => []
    }
  },
  data() {
    return {
      isCollapsed: false
    }
  },
  methods: {
    toggle() {
      this.isCollapsed = !this.isCollapsed;
    }
  }
}
</script>

<style scoped>
.btn-box-wrapper {
  position: absolute;
  left: 10px;
  top: calc(50% + 25px);
  transform: translateY(-50%);
  z-index: 2;
  display: flex;
  align-items: center;
}

.btn-box {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  height: calc(100% - 70px);
  width: auto;
  padding: 10px;
  border-radius: 20px;
  background-color: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px) saturate(150%);
  -webkit-backdrop-filter: blur(12px) saturate(150%);
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 20px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  .el-button {
    width: 120px;
    height: auto;
    margin: 0px 0px 10px 0px;
    border-radius: 20px;
    border: none;
  }

  .el-button:last-child {
    margin-bottom: 0px;
  }

  &.collapsed {
    width: 0 !important;
    padding: 0 !important;
    overflow: hidden;
    border: none;
    background-color: transparent;
    box-shadow: none;

    .el-button {
      width: 0;
      opacity: 0;
      margin: 0;
    }
  }
}

.toggle-icon-container {
  cursor: pointer;
  font-size: 24px;
  color: #fff;
  background-color: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px) saturate(150%);
  -webkit-backdrop-filter: blur(12px) saturate(150%);
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 20px 30px rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 10px;
  transition: all 0.3s ease;

  &:hover {
    background-color: rgba(255, 255, 255, 0.25);
  }
  .el-icon {
    color: black;
  }
}
</style>