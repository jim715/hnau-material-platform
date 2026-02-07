<template>
  <div class="integral-center">
    <el-card class="center-card">
      <template #header>
        <div class="card-header">
          <span>个人积分中心</span>
        </div>
      </template>
      
      <div class="center-content">
        <!-- 积分概览 -->
        <div class="integral-overview">
          <el-card class="overview-card">
            <div class="total-integral">
              <div class="label">当前总积分</div>
              <div class="value">{{ totalIntegral }}</div>
            </div>
          </el-card>
        </div>
        
        <!-- 积分变动趋势 -->
        <div class="integral-trend">
          <el-card class="trend-card">
            <template #header>
              <span>近7天积分变动趋势</span>
            </template>
            <div class="chart-container">
              <div ref="chartRef" style="width: 100%; height: 300px;"></div>
            </div>
          </el-card>
        </div>
        
        <!-- 积分变动记录 -->
        <div class="integral-records">
          <el-card class="records-card">
            <template #header>
              <span>积分变动记录</span>
            </template>
            <el-table :data="recordsData" style="width: 100%">
              <el-table-column prop="points" label="积分变动" width="120" />
              <el-table-column prop="reason" label="变动原因" />
              <el-table-column prop="createTime" label="时间" width="180" />
              <el-table-column prop="type" label="类型" width="80">
          <template #default="scope">
            {{ getRecordType(scope.row) }}
          </template>
        </el-table-column>
            </el-table>
            <div class="pagination">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </el-card>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import api from '../api/index.js'
import { ElMessage } from 'element-plus'

// 获取积分记录类型的显示文本
const getRecordType = (record) => {
  if (record.reason.includes('提问')) {
    return '提问'
  } else if (record.reason.includes('评论')) {
    return '评论'
  } else if (record.reason.includes('点赞')) {
    return '点赞'
  } else if (record.reason.includes('收藏')) {
    return '收藏'
  } else if (record.reason.includes('下载')) {
    return '下载'
  } else if (record.reason.includes('上传')) {
    return '上传'
  } else {
    return '其他'
  }
}

// 总积分
const totalIntegral = ref(0)

// 积分记录
const recordsData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 图表引用
const chartRef = ref(null)
let chart = null

// 近7天积分数据
const weekData = ref([])

// 定时器
let updateTimer = null

// 从后端获取用户积分信息
const getUserIntegral = async () => {
  try {
    // 添加时间戳防止缓存
    const timestamp = new Date().getTime()
    const response = await api.get(`/integral/user?_t=${timestamp}`)
    if (response.code === 200) {
      totalIntegral.value = response.data.totalPoints
      console.log('获取到的积分数据:', response.data)
    }
  } catch (error) {
    console.error('获取用户积分失败:', error)
    ElMessage.error('获取用户积分失败')
  }
}

// 过滤短时间内频繁操作的积分记录
const filterFrequentRecords = (records) => {
  // 按操作类型和相关ID分组
  const groupedRecords = {};
  
  // 分组逻辑
  records.forEach(record => {
    // 提取操作类型（如点赞、收藏、下载等）
    const type = getRecordType(record);
    // 提取相关ID（如果有的话）
    const relatedId = record.relatedId || 'unknown';
    // 组合分组键
    const groupKey = `${type}_${relatedId}`;
    
    if (!groupedRecords[groupKey]) {
      groupedRecords[groupKey] = [];
    }
    groupedRecords[groupKey].push(record);
  });
  
  // 对每组记录进行过滤
  const filteredRecords = [];
  
  Object.values(groupedRecords).forEach(group => {
    // 按时间降序排序
    group.sort((a, b) => {
      return new Date(b.createTime) - new Date(a.createTime);
    });
    
    // 过滤5秒内的重复操作
    let lastRecordTime = 0;
    group.forEach(record => {
      const recordTime = new Date(record.createTime).getTime();
      // 如果是组内第一条记录，或者与上一条记录的时间差大于5秒，则保留
      if (lastRecordTime === 0 || (recordTime - lastRecordTime) > 5000) {
        filteredRecords.push(record);
        lastRecordTime = recordTime;
      }
    });
  });
  
  // 所有过滤后的记录按时间降序排序
  filteredRecords.sort((a, b) => {
    return new Date(b.createTime) - new Date(a.createTime);
  });
  
  return filteredRecords;
};

// 从后端获取积分记录
const getPointsRecords = async () => {
  try {
    // 添加时间戳防止缓存
    const timestamp = new Date().getTime()
    const response = await api.get(`/integral/records?page=${currentPage.value}&size=${pageSize.value}&_t=${timestamp}`)
    console.log('获取积分记录响应:', response)
    if (response.code === 200) {
      // 确保使用正确的响应数据结构
      const records = Array.isArray(response.data) ? response.data : []
      console.log('获取到的积分记录:', records)
      console.log('积分记录总数:', records.length)
      
      // 过滤短时间内频繁操作的记录
      const filteredRecords = filterFrequentRecords(records);
      console.log('过滤后的积分记录:', filteredRecords)
      console.log('过滤后记录总数:', filteredRecords.length)
      
      recordsData.value = filteredRecords
      total.value = filteredRecords.length
    }
  } catch (error) {
    console.error('获取积分记录失败:', error)
    ElMessage.error('获取积分记录失败')
  }
}

// 从后端获取近7天积分数据
const getWeeklyIntegralData = async () => {
  try {
    // 添加时间戳防止缓存
    const timestamp = new Date().getTime()
    const response = await api.get(`/integral/weekly?_t=${timestamp}`)
    if (response.code === 200) {
      weekData.value = response.data
      console.log('获取到的近7天积分数据:', response.data)
    }
  } catch (error) {
    console.error('获取近7天积分数据失败:', error)
    ElMessage.error('获取近7天积分数据失败')
    // 生成默认数据
    const today = new Date()
    const data = []
    for (let i = -3; i <= 3; i++) {
      const date = new Date(today)
      date.setDate(date.getDate() + i)
      const month = date.getMonth() + 1
      const day = date.getDate()
      const dateStr = `${month}月${day}日`
      data.push({ date: dateStr, points: 0 })
    }
    weekData.value = data
  }
}

// 初始化图表
const initChart = () => {
  if (chartRef.value) {
    chart = echarts.init(chartRef.value)
    
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['积分变动']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: weekData.value.map(item => item.date)
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '积分变动',
          type: 'line',
          stack: 'Total',
          data: weekData.value.map(item => item.points),
          itemStyle: {
            color: '#409EFF'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: 'rgba(64, 158, 255, 0.5)'
              },
              {
                offset: 1,
                color: 'rgba(64, 158, 255, 0.1)'
              }
            ])
          }
        }
      ]
    }
    
    chart.setOption(option)
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  getPointsRecords()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getPointsRecords()
}

// 加载积分记录
const loadRecords = () => {
  getPointsRecords()
}

// 监听窗口大小变化
const handleResize = () => {
  if (chart) {
    chart.resize()
  }
}

// 更新积分数据和图表
const updateIntegralData = async () => {
  await getUserIntegral()
  await getPointsRecords()
  await getWeeklyIntegralData()
  initChart()
}

// 设置定时器，每天00:00自动更新
const setupAutoUpdate = () => {
  // 清除之前的定时器
  if (updateTimer) {
    clearInterval(updateTimer)
  }
  
  // 计算距离当天00:00的时间差
  const now = new Date()
  const tomorrow = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1, 0, 0, 0, 0)
  const timeUntilMidnight = tomorrow - now
  
  // 设置定时器，到00:00时更新数据
  setTimeout(() => {
    // 更新数据
    updateIntegralData()
    
    // 之后每天00:00更新
    updateTimer = setInterval(() => {
      updateIntegralData()
    }, 24 * 60 * 60 * 1000) // 24小时
  }, timeUntilMidnight)
}

onMounted(async () => {
  await getUserIntegral()
  await getPointsRecords()
  await getWeeklyIntegralData()
  initChart()
  window.addEventListener('resize', handleResize)
  
  // 设置自动更新定时器
  setupAutoUpdate()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  // 清除定时器
  if (updateTimer) {
    clearInterval(updateTimer)
  }
})
</script>

<style scoped>
.integral-center {
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

.center-content {
  padding: 20px 0;
}

.integral-overview {
  margin-bottom: 30px;
}

.overview-card {
  text-align: center;
  padding: 30px 0;
}

.total-integral {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.total-integral .label {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
}

.total-integral .value {
  font-size: 48px;
  font-weight: bold;
  color: #409EFF;
}

.integral-trend {
  margin-bottom: 30px;
}

.trend-card {
  padding: 20px;
}

.chart-container {
  margin-top: 20px;
}

.integral-records {
  margin-bottom: 30px;
}

.records-card {
  padding: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>