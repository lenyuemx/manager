<template>
  <div class="main-content">
    <div class="home-container">
      <!-- 轮播图区域 -->
      <div class="carousel-container glass-effect">
        <el-carousel height="400px" class="apple-carousel">
          <el-carousel-item v-for="item in carousel" :key="item.id">
            <div class="carousel-img-wrapper">
              <img :src="item.img" alt="" class="carousel-img">
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>

      <div class="content-section">
        <el-row :gutter="24" type="flex">
          <!-- 社团活动 -->
          <el-col :span="12">
            <div class="section-card flex-card">
              <div class="section-header">
                <h2 class="section-title">社团活动</h2>
                <div class="title-underline"></div>
              </div>
              <div class="list-container activity-list">
                <div v-for="item in activityData" :key="item.id" class="list-item activity-item" @click="navTo('/front/activityDetail?id=' + item.id)">
                  <img :src="item.img" alt="" class="item-img">
                  <div class="item-content">
                    <div class="item-title">{{ item.name }}</div>
                    <div class="item-desc">{{ item.description }}</div>
                    <div class="item-meta">
                      <span class="time">{{ item.time }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="pagination-wrapper mt-auto">
                <el-pagination
                    background
                    @current-change="handleActivityCurrentChange"
                    :current-page="activityPageNum"
                    :page-size="activityPageSize"
                    layout="prev, pager, next"
                    :total="activityTotal">
                </el-pagination>
              </div>
            </div>
          </el-col>

          <!-- 社团资讯 -->
          <el-col :span="12">
            <div class="section-card flex-card">
              <div class="section-header">
                <h2 class="section-title">社团资讯</h2>
                <div class="title-underline"></div>
              </div>
              <div class="list-container info-list">
                <div v-for="item in informationData" :key="item.id" class="list-item info-item" @click="navTo('/front/informationDetail?id=' + item.id)">
                  <div class="info-content">
                    <div class="item-title line1">{{item.name}}</div>
                  </div>
                  <div class="info-meta">
                    <span class="time">{{item.time}}</span>
                    <i class="el-icon-arrow-right"></i>
                  </div>
                </div>
              </div>
              <div class="pagination-wrapper mt-auto">
                <el-pagination
                    background
                    @current-change="handleInformationCurrentChange"
                    :current-page="informationPageNum"
                    :page-size="informationPageSize"
                    layout="prev, pager, next"
                    :total="informationTotal">
                </el-pagination>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 热门社团展示 -->
      <div class="section-card department-section">
        <div class="section-header center">
          <h2 class="section-title">热门社团</h2>
          <div class="title-underline center"></div>
        </div>
        <div class="department-grid">
          <div v-for="item in departmentData" :key="item.id" class="department-card" @click="navTo('/front/departmentDetail?id=' + item.id)">
            <div class="img-wrapper">
              <img :src="item.img" alt="">
            </div>
            <div class="dept-name">{{ item.name }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  data() {
    return {
      carousel: [],
      departmentData: [],
      activityData: [],
      activityPageNum: 1,
      activityPageSize: 3,
      activityTotal: 0,

      informationData:[],
      informationPageNum: 1,
      informationPageSize: 6,
      informationTotal: 0,
    }
  },
  mounted() {
    this.loadCarousel()
    this.loadDepartment()
    this.loadActivity()
    this.loadInformation()
  },
  methods: {
    loadCarousel() {
      this.$request.get('/carousel/selectAll').then(res => {
        if (res.code === '200') {
          this.carousel = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadInformation(pageNum) {
      if (pageNum) this.informationPageNum = pageNum
      this.$request.get('/information/selectPage', {
        params: {
          pageNum: this.informationPageNum,
          pageSize: this.informationPageSize,
          status: '通过'
        }
      }).then(res => {
        if (res.code === '200') {
          const list = res.data?.list || [];
          list.forEach(item => {
            if (item.description) {
              item.description = item.description.replace(/<[^>]+>/g, '');
            }
          });
          this.informationData = list;
          this.informationTotal = res.data?.total
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadActivity(pageNum) {
      if (pageNum) this.activityPageNum = pageNum
      this.$request.get('/activity/selectPage', {
        params: {
          pageNum: this.activityPageNum,
          pageSize: this.activityPageSize,
          status: '通过'
        }
      }).then(res => {
        if (res.code === '200') {
          const list = res.data?.list || [];
          list.forEach(item => {
            if (item.description) {
              item.description = item.description.replace(/<[^>]+>/g, '');
            }
          });
          this.activityData = list;
          this.activityTotal = res.data?.total
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadDepartment() {
      this.$request.get('/department/selectHot').then(res => {
        if (res.code === '200') {
          this.departmentData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    navTo(url) {
      location.href = url
    },
    handleActivityCurrentChange(pageNum) {
      this.loadActivity(pageNum)
    },
    handleInformationCurrentChange(pageNum) {
      this.loadInformation(pageNum)
    }
  }
}
</script>

<style scoped>
.home-container {
  padding-bottom: 40px;
}

/* 轮播图样式 */
.carousel-container {
  border-radius: var(--apple-radius-lg);
  overflow: hidden;
  margin-bottom: 40px;
  box-shadow: var(--apple-shadow-md);
  background: white;
}

.apple-carousel {
  border-radius: var(--apple-radius-lg);
}

.carousel-img-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f7;
}

.carousel-img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain; /* 保证图片完整显示，不被裁剪 */
}

/* 区块通用样式 */
.section-card {
  background: var(--apple-card-bg);
  border-radius: var(--apple-radius-lg);
  padding: 30px;
  box-shadow: var(--apple-shadow-sm);
  margin-bottom: 30px;
  height: 100%;
  transition: var(--apple-transition);
}

.flex-card {
  display: flex;
  flex-direction: column;
}

.mt-auto {
  margin-top: auto;
}

.section-card:hover {
  box-shadow: var(--apple-shadow-md);
}

.section-header {
  margin-bottom: 24px;
}

.section-header.center {
  text-align: center;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--apple-text-color);
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.title-underline {
  width: 40px;
  height: 4px;
  background: var(--apple-blue);
  border-radius: 2px;
}

.title-underline.center {
  margin: 0 auto;
}

/* 列表项通用样式 */
.list-container {
  min-height: 420px; /* 统一高度，让内容有足够的空间排布 */
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.list-item {
  padding: 16px 20px;
  border-radius: var(--apple-radius-md);
  cursor: pointer;
  transition: var(--apple-transition);
  background: rgba(0, 0, 0, 0.02);
  margin-bottom: 0;
}

.list-item:hover {
  background: var(--apple-bg-color);
  transform: translateX(4px);
}

/* 活动列表特定样式 */
.activity-item {
  display: flex;
  gap: 20px;
  align-items: center;
  flex: 1; /* 充满容器 */
}

.item-img {
  width: 90px;
  height: 90px;
  border-radius: var(--apple-radius-md);
  object-fit: cover;
  box-shadow: var(--apple-shadow-sm);
}

.item-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.item-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--apple-text-color);
  margin-bottom: 8px;
}

.item-desc {
  font-size: 13px;
  color: var(--apple-text-secondary);
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  line-height: 1.5;
  margin-bottom: 8px;
}

/* 资讯列表特定样式 */
.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  flex: 1; /* 充满容器 */
}

.info-content {
  flex: 1;
  padding-right: 20px;
}

.info-item .item-title {
  margin: 0;
  font-weight: 500;
}

.info-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--apple-text-secondary);
}

.time {
  font-size: 13px;
  color: var(--apple-text-secondary);
}

/* 优秀社团网格 */
.department-section {
  margin-top: 40px;
}

.department-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px;
  margin-top: 30px;
}

.department-card {
  text-align: center;
  cursor: pointer;
  padding: 20px;
  border-radius: var(--apple-radius-lg);
  transition: var(--apple-transition);
}

.department-card:hover {
  background: var(--apple-bg-color);
  transform: translateY(-4px);
}

.img-wrapper {
  width: 140px;
  height: 140px;
  margin: 0 auto 16px;
  border-radius: 50%;
  padding: 4px;
  background: white;
  box-shadow: var(--apple-shadow-md);
  transition: var(--apple-transition);
}

.department-card:hover .img-wrapper {
  box-shadow: var(--apple-shadow-lg);
  transform: scale(1.05);
}

.img-wrapper img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.dept-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--apple-text-color);
}

/* 分页器对齐 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}
</style>