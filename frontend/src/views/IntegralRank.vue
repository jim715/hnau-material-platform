<template>
  <div class="integral-rank">
    <el-card class="rank-card">
      <template #header>
        <div class="card-header">
          <span>积分排行榜</span>
          <div class="date-selector">
            <el-button-group>
              <el-button :type="timeRange === 'today' ? 'primary' : 'default'" @click="selectTimeRange('today')">当天</el-button>
              <el-button :type="timeRange === 'month' ? 'primary' : 'default'" @click="selectTimeRange('month')">本月</el-button>
            </el-button-group>
            <el-date-picker
              v-model="selectedMonth"
              type="month"
              format="YYYY年MM月"
              value-format="YYYY-MM"
              placeholder="选择月份"
              @change="handleMonthChange"
              style="margin-left: 10px;"
            />
          </div>
        </div>
      </template>
      
      <div class="rank-content">
        <!-- 筛选条件 -->
        <div class="filter-container">
          <el-form :inline="true" class="filter-form">
            <el-form-item label="筛选范围">
              <el-select v-model="filterScope" @change="loadRankList">
                <el-option label="全校" value="all" />
                <el-option label="学院" value="college" />
                <el-option label="专业" value="major" />
              </el-select>
            </el-form-item>
            
            <el-form-item v-if="filterScope === 'college'" label="学院">
              <el-select v-model="filterCollege" @change="loadRankList">
                <el-option label="农学院" value="农学院" />
                <el-option label="林学院" value="林学院" />
                <el-option label="风景园林学院" value="风景园林学院" />
                <el-option label="艺术与设计学院" value="艺术与设计学院" />
                <el-option label="动物医学院" value="动物医学院" />
                <el-option label="动物科技学院" value="动物科技学院" />
                <el-option label="机电工程学院" value="机电工程学院" />
                <el-option label="经济与管理学院" value="经济与管理学院" />
                <el-option label="烟草学院" value="烟草学院" />
                <el-option label="植物保护学院" value="植物保护学院" />
                <el-option label="园艺学院" value="园艺学院" />
                <el-option label="信息与管理科学学院" value="信息与管理科学学院" />
                <el-option label="软件学院" value="软件学院" />
                <el-option label="生命科学学院" value="生命科学学院" />
                <el-option label="食品科学技术学院" value="食品科学技术学院" />
                <el-option label="资源与环境学院" value="资源与环境学院" />
                <el-option label="文法学院" value="文法学院" />
                <el-option label="马克思主义学院" value="马克思主义学院" />
                <el-option label="理学院" value="理学院" />
                <el-option label="外国语学院" value="外国语学院" />
                <el-option label="体育学院" value="体育学院" />
                <el-option label="国际教育学院" value="国际教育学院" />
              </el-select>
            </el-form-item>
            
            <el-form-item v-if="filterScope === 'major'" label="专业">
              <el-select v-model="filterMajor" @change="loadRankList">
                <el-option label="生物育种科学" value="生物育种科学" />
                <el-option label="动物医学" value="动物医学" />
                <el-option label="植物保护" value="植物保护" />
                <el-option label="人工智能" value="人工智能" />
                <el-option label="生物技术" value="生物技术" />
                <el-option label="农学" value="农学" />
                <el-option label="种子科学与工程" value="种子科学与工程" />
                <el-option label="智慧农业" value="智慧农业" />
                <el-option label="中药学" value="中药学" />
                <el-option label="林学" value="林学" />
                <el-option label="经济林" value="经济林" />
                <el-option label="生态学" value="生态学" />
                <el-option label="旅游管理" value="旅游管理" />
                <el-option label="智慧林业" value="智慧林业" />
                <el-option label="环境工程" value="环境工程" />
                <el-option label="园林" value="园林" />
                <el-option label="城乡规划" value="城乡规划" />
                <el-option label="风景园林" value="风景园林" />
                <el-option label="环境设计" value="环境设计" />
                <el-option label="产品设计" value="产品设计" />
                <el-option label="舞蹈表演" value="舞蹈表演" />
                <el-option label="兽医公共卫生" value="兽医公共卫生" />
                <el-option label="中兽医学" value="中兽医学" />
                <el-option label="药物制剂" value="药物制剂" />
                <el-option label="动物药学" value="动物药学" />
                <el-option label="动植物检疫" value="动植物检疫" />
                <el-option label="动物科学" value="动物科学" />
                <el-option label="智慧牧业科学与工程" value="智慧牧业科学与工程" />
                <el-option label="水产养殖学" value="水产养殖学" />
                <el-option label="饲料工程" value="饲料工程" />
                <el-option label="机械设计制造及其自动化" value="机械设计制造及其自动化" />
                <el-option label="能源与动力工程" value="能源与动力工程" />
                <el-option label="交通运输" value="交通运输" />
                <el-option label="汽车服务工程" value="汽车服务工程" />
                <el-option label="农业机械化及其自动化" value="农业机械化及其自动化" />
                <el-option label="农业建筑环境与能源工程" value="农业建筑环境与能源工程" />
                <el-option label="农业智能装备工程" value="农业智能装备工程" />
                <el-option label="电子信息工程" value="电子信息工程" />
                <el-option label="经济学" value="经济学" />
                <el-option label="经济与金融" value="经济与金融" />
                <el-option label="数字经济" value="数字经济" />
                <el-option label="农林经济管理" value="农林经济管理" />
                <el-option label="工商管理" value="工商管理" />
                <el-option label="财务管理" value="财务管理" />
                <el-option label="食品科学与工程" value="食品科学与工程" />
                <el-option label="香料香精技术与工程" value="香料香精技术与工程" />
                <el-option label="烟草" value="烟草" />
                <el-option label="制药工程" value="制药工程" />
                <el-option label="园艺" value="园艺" />
                <el-option label="设施农业科学与工程" value="设施农业科学与工程" />
                <el-option label="茶学" value="茶学" />
                <el-option label="信息与计算科学" value="信息与计算科学" />
                <el-option label="管理科学" value="管理科学" />
                <el-option label="计算机科学与技术" value="计算机科学与技术" />
                <el-option label="生物工程" value="生物工程" />
                <el-option label="生物科学" value="生物科学" />
                <el-option label="食品质量与安全" value="食品质量与安全" />
                <el-option label="食品营养与健康" value="食品营养与健康" />
                <el-option label="农业资源与环境" value="农业资源与环境" />
                <el-option label="土地整治工程" value="土地整治工程" />
                <el-option label="环境生态工程" value="环境生态工程" />
                <el-option label="土地资源管理" value="土地资源管理" />
                <el-option label="人文地理与城乡规划" value="人文地理与城乡规划" />
                <el-option label="法学" value="法学" />
                <el-option label="社会工作" value="社会工作" />
                <el-option label="汉语国际教育" value="汉语国际教育" />
                <el-option label="乡村治理" value="乡村治理" />
                <el-option label="应用化学" value="应用化学" />
                <el-option label="化学生物学" value="化学生物学" />
                <el-option label="电子信息科学与技术" value="电子信息科学与技术" />
                <el-option label="物联网工程" value="物联网工程" />
                <el-option label="英语" value="英语" />
                <el-option label="翻译" value="翻译" />
                <el-option label="日语" value="日语" />
                <el-option label="商务英语" value="商务英语" />
                <el-option label="社会体育指导与管理" value="社会体育指导与管理" />
                <el-option label="环境科学" value="环境科学" />
                <el-option label="数据科学与大数据技术" value="数据科学与大数据技术" />
                <el-option label="软件工程" value="软件工程" />
              </el-select>
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 排行榜列表 -->
        <div class="rank-list">
          <el-table :data="rankData" style="width: 100%">
            <el-table-column prop="rank" label="排名" width="80">
              <template #default="scope">
                <span :class="getRankClass(scope.row.rank)">{{ scope.row.rank }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="nickname" label="昵称" width="150" />
            <el-table-column prop="college" label="学院" />
            <el-table-column prop="major" label="专业" width="120" />
            <el-table-column prop="totalPoints" label="总积分" width="100" />
            <el-table-column prop="uploadCount" label="上传资料数" width="120" />
          </el-table>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '../api/index.js'

// 筛选条件
const filterScope = ref('all')
const filterCollege = ref('')
const filterMajor = ref('')

// 时间范围选择
const timeRange = ref('today') // today 或 month
const selectedMonth = ref('')

// 排行榜数据
const rankData = ref([])

// 计算当前日期
const getCurrentDate = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  return { year, month, day }
}

// 获取今天的日期范围
const getTodayDateRange = () => {
  const { year, month, day } = getCurrentDate()
  const startDate = `${year}-${month}-${day} 00:00:00`
  const endDate = `${year}-${month}-${day} 23:59:59`
  return { startDate, endDate }
}

// 获取本月的日期范围
const getMonthDateRange = (yearMonth) => {
  let year, month
  if (yearMonth) {
    [year, month] = yearMonth.split('-')
  } else {
    const current = getCurrentDate()
    year = current.year
    month = current.month
  }
  
  // 计算本月的第一天和最后一天
  const startDate = `${year}-${month}-01 00:00:00`
  
  // 计算本月的最后一天
  const lastDay = new Date(parseInt(year), parseInt(month), 0).getDate()
  const endDate = `${year}-${month}-${lastDay} 23:59:59`
  
  return { startDate, endDate }
}

// 选择时间范围
const selectTimeRange = (range) => {
  timeRange.value = range
  if (range === 'today') {
    loadRankListByDateRange('today')
  } else if (range === 'month') {
    loadRankListByDateRange('month')
  }
}

// 处理月份变化
const handleMonthChange = (value) => {
  if (value) {
    timeRange.value = 'month'
    loadRankListByDateRange('month', value)
  }
}

// 加载排行榜数据（按日期范围）
const loadRankListByDateRange = async (range, yearMonth = null) => {
  try {
    // 确定筛选参数
    let college = null
    let major = null
    
    if (filterScope.value === 'college' && filterCollege.value) {
      college = filterCollege.value
    } else if (filterScope.value === 'major' && filterMajor.value) {
      major = filterMajor.value
    }
    
    // 确定日期范围
    let startDate, endDate
    if (range === 'today') {
      const dateRange = getTodayDateRange()
      startDate = dateRange.startDate
      endDate = dateRange.endDate
    } else if (range === 'month') {
      const dateRange = getMonthDateRange(yearMonth || selectedMonth.value)
      startDate = dateRange.startDate
      endDate = dateRange.endDate
    }
    
    // 调用后端API获取排行榜数据
    const response = await api.get('/integral/rank/date', {
      params: {
        limit: 100,
        college: college,
        major: major,
        startDate: startDate,
        endDate: endDate
      }
    })
    
    if (response.code === 200) {
      // 打印后端返回的数据量
      console.log('后端返回的用户数量:', response.data.length);
      
      // 处理后端返回的数据
      const data = response.data.map(item => {
        // 获取学号并提取后两位
        const studentId = item.studentId || item.student_id || '';
        // 确保学号是字符串，并提取最后两位
        const studentIdStr = String(studentId);
        const lastTwoDigits = studentIdStr.length >= 2 ? studentIdStr.slice(-2) : studentIdStr;
        
        return {
          userId: item.userId || item.user_id,
          nickname: `用户${lastTwoDigits}`,
          college: item.college || '',
          major: item.major || '',
          totalPoints: item.totalPoints || item.total_points || 0,
          uploadCount: item.uploadCount || item.upload_count || 0
        };
      })
      
      // 打印处理后的数据量
      console.log('处理后的用户数量:', data.length);
      
      // 只显示前100名用户，如果不足100名则显示实际数量
      const displayData = data.slice(0, 100);
      
      // 打印最终显示的数据量
      console.log('最终显示的用户数量:', displayData.length);
      
      // 重新计算排名
      rankData.value = displayData.map((item, index) => ({
        ...item,
        rank: index + 1
      }))
    }
  } catch (error) {
    console.error('加载排行榜数据失败:', error)
    rankData.value = []
  }
}

// 加载排行榜数据（兼容旧API）
const loadRankList = async () => {
  // 默认加载当天的数据
  loadRankListByDateRange(timeRange.value)
}

// 获取排名样式
const getRankClass = (rank) => {
  if (rank <= 3) {
    return 'rank-top3'
  } else if (rank <= 10) {
    return 'rank-top10'
  }
  return ''
}

onMounted(() => {
  // 初始化时设置当前月份
  const current = getCurrentDate()
  selectedMonth.value = `${current.year}-${current.month}`
  // 默认加载当天的数据
  loadRankListByDateRange('today')
})
</script>

<style scoped>
.integral-rank {
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

.date-selector {
  display: flex;
  align-items: center;
}

.rank-content {
  padding: 20px 0;
}

.filter-container {
  margin-bottom: 30px;
}

.filter-form {
  display: flex;
  align-items: center;
}

.rank-list {
  margin-top: 20px;
}

.rank-top3 {
  color: red;
  font-weight: bold;
  font-size: 16px;
}

.rank-top10 {
  color: orange;
  font-weight: bold;
  font-size: 14px;
}
</style>