<template>
  <transition name="slide-up-down">
    <slot></slot>
  </transition>
</template>

<script>
export default {
  name: 'SlideTransition'
}
</script>

<style scoped>
.slide-up-down-enter-active,
.slide-up-down-leave-active {
  transition: all 0.5s ease-in-out;
}
.slide-up-down-enter-from {
  opacity: 0;
  transform: translateY(100%) scale(0.8);
}
.slide-up-down-leave-to {
  opacity: 0;
  transform: translateY(-100%) scale(0.8);
}
</style>