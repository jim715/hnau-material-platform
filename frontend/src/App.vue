<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
// 路由守卫已经处理了认证逻辑，这里不需要额外检查
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
  color: #303133;
}

#app {
  min-height: 100vh;
}
</style>