<template>
  <div class="integral-rules">
    <el-card class="rules-card">
      <template #header>
        <div class="card-header">
          <span>积分规则</span>
        </div>
      </template>
      
      <div class="rules-content">
        <el-divider content-position="left">加分项</el-divider>
        <el-table :data="plusRules" style="width: 100%">
          <el-table-column prop="action" label="行为" width="180" />
          <el-table-column prop="points" label="积分" width="100" />
          <el-table-column prop="description" label="说明" />
        </el-table>
        
        <el-divider content-position="left">扣分项</el-divider>
        <el-table :data="minusRules" style="width: 100%">
          <el-table-column prop="action" label="行为" width="180" />
          <el-table-column prop="points" label="积分" width="100" />
          <el-table-column prop="description" label="说明" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 加分规则
const plusRules = ref([
  { action: '上传资料', points: '+20', description: '成功上传资料并通过审核后获得' },
  { action: '资料被下载', points: '+2', description: '您上传的资料被其他用户下载后获得' },
  { action: '提问', points: '+5', description: '成功发布问题后获得' },
  { action: '评论', points: '+3', description: '成功发表评论后获得' },
  { action: '点赞', points: '+1', description: '成功点赞评论后获得' },
  { action: '收藏', points: '+2', description: '成功收藏问题后获得' }
])

// 扣分规则
const minusRules = ref([
  { action: '下载资料', points: '-5', description: '下载其他用户上传的资料时扣除' },
  { action: '取消收藏', points: '-2', description: '取消收藏问题后扣除' },
  { action: '取消点赞', points: '-1', description: '取消点赞评论后扣除' }
])
</script>

<style scoped>
.integral-rules {
  max-width: 1000px;
  margin: 20px auto;
  padding: 0 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.rules-content {
  padding: 20px 0;
}

.el-divider {
  margin: 20px 0;
}

.el-table {
  margin-bottom: 30px;
}
</style>