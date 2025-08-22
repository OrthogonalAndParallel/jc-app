<template>
  <transition :name="transitionName">
    <slot></slot>
  </transition>
</template>

<script>
export default {
  props: {
    direction: {
      type: String,
      default: 'forward' // 默认向前
    }
  },
  computed: {
    transitionName() {
      return `slide-${this.direction}`;
    }
  }
};
</script>

<style>
/* 向前滑动动画 */
.slide-forward-enter-active, .slide-forward-leave-active {
  transition: all 0.5s ease-in-out;
}
.slide-forward-enter-from {
  opacity: 0;
  transform: translateY(100%) scale(0.8);
}
.slide-forward-leave-to {
  opacity: 0;
  transform: translateY(-100%) scale(0.8);
}

/* 向后滑动动画 */
.slide-backward-enter-active, .slide-backward-leave-active {
  transition: all 0.5s ease-in-out;
}
.slide-backward-enter-from {
  opacity: 0;
  transform: translateY(-100%) scale(0.8);
}
.slide-backward-leave-to {
  opacity: 0;
  transform: translateY(100%) scale(0.8);
}
</style>