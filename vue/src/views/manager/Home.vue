<template>
  <div class="manager-home">
    <div class="welcome-card card">
      <div class="welcome-content">
        <h1 class="welcome-title">欢迎回来，{{ user?.name }}</h1>
        <p class="welcome-subtitle">在这里管理和查看系统的各项数据</p>
      </div>
      <div class="date-display">
        <div class="date-month">{{ currentMonth }}</div>
        <div class="date-day">{{ currentDay }}</div>
      </div>
    </div>

    <div class="content-grid">
      <div class="notice-card card">
        <div class="card-header">
          <i class="el-icon-bell header-icon"></i>
          <span class="header-title">系统公告</span>
        </div>
        <div class="notice-list">
          <el-timeline reverse>
            <el-timeline-item 
              v-for="item in notices" 
              :key="item.id" 
              :timestamp="item.time"
              placement="top"
              class="apple-timeline-item">
              <el-popover
                  placement="right"
                  width="280"
                  trigger="hover"
                  popper-class="apple-popover"
                  :content="item.content">
                <div slot="reference" class="notice-item-title">{{ item.title }}</div>
              </el-popover>
            </el-timeline-item>
          </el-timeline>
          <div v-if="notices.length === 0" class="empty-state">
            暂无公告
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      notices: [],
      currentMonth: '',
      currentDay: ''
    }
  },
  created() {
    this.initDate()
    this.$request.get('/notice/selectAll').then(res => {
      this.notices = res.data || []
    })
  },
  methods: {
    initDate() {
      const date = new Date()
      const months = ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
      this.currentMonth = months[date.getMonth()]
      this.currentDay = date.getDate()
    }
  }
}
</script>

<style scoped>
.manager-home {
  padding-bottom: 24px;
}

.welcome-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: linear-gradient(135deg, var(--apple-card-bg) 0%, rgba(245, 245, 247, 0.5) 100%);
}

.welcome-title {
  font-size: 28px;
  font-weight: 600;
  color: var(--apple-text-color);
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.welcome-subtitle {
  font-size: 15px;
  color: var(--apple-text-secondary);
  margin: 0;
}

.date-display {
  text-align: center;
  background: white;
  padding: 12px 24px;
  border-radius: var(--apple-radius-md);
  box-shadow: var(--apple-shadow-sm);
  min-width: 100px;
}

.date-month {
  font-size: 13px;
  color: #ff3b30;
  font-weight: 600;
  text-transform: uppercase;
}

.date-day {
  font-size: 32px;
  font-weight: 300;
  color: var(--apple-text-color);
  line-height: 1;
  margin-top: 4px;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}

@media (min-width: 1200px) {
  .content-grid {
    grid-template-columns: 1fr 1fr;
  }
}

.notice-card {
  height: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.header-icon {
  font-size: 20px;
  color: var(--apple-blue);
  background: var(--apple-blue-light);
  padding: 8px;
  border-radius: 8px;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--apple-text-color);
}

.notice-list {
  padding: 10px 20px 10px 10px;
}

.notice-item-title {
  font-size: 15px;
  color: var(--apple-text-color);
  cursor: pointer;
  transition: var(--apple-transition);
  padding: 8px 12px;
  border-radius: var(--apple-radius-sm);
  display: inline-block;
}

.notice-item-title:hover {
  background: var(--apple-bg-color);
  color: var(--apple-blue);
}

.empty-state {
  text-align: center;
  padding: 40px 0;
  color: var(--apple-text-secondary);
  font-size: 15px;
}

/* 覆盖 Element UI 时间线样式 */
.apple-timeline-item >>> .el-timeline-item__timestamp {
  color: var(--apple-text-secondary);
  font-size: 13px;
  margin-bottom: 8px;
}

.apple-timeline-item >>> .el-timeline-item__node {
  background-color: var(--apple-blue);
}

.apple-timeline-item >>> .el-timeline-item__tail {
  border-left-color: rgba(0, 0, 0, 0.1);
}
</style>
