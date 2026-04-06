<template>
  <div class="main-content">
    <div class="card department-overview">
      <div class="header">
        <div class="title">社团总览</div>
        <div class="subtitle">发现你感兴趣的社团，加入充满活力的集体</div>
      </div>

      <div class="search-section">
        <el-input
          v-model="name"
          placeholder="搜索社团名称"
          class="search-input"
          @keyup.enter.native="load(1)"
        >
          <el-button slot="append" icon="el-icon-search" @click="load(1)"></el-button>
        </el-input>
      </div>

      <div class="department-grid" v-if="tableData.length">
        <div v-for="item in tableData" :key="item.id" class="department-card" @click="navToDetail(item.id)">
          <div class="img-wrapper shadow-effect">
            <img :src="item.img" alt="">
          </div>
          <div class="info">
            <div class="dept-name">{{ item.name }}</div>
            <div class="dept-desc" v-if="item.description">{{ item.description }}</div>
            <div class="dept-footer">
              <el-button type="text" class="detail-link">查看详情 <i class="el-icon-right"></i></el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <img src="@/assets/imgs/logo.png" style="width: 80px; opacity: 0.3; margin-bottom: 20px" alt="">
        <div class="empty-text">暂无相关社团信息</div>
      </div>

      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          background
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-size="pageSize"
          layout="total, prev, pager, next"
          :total="total">
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 9,
      total: 0,
      name: ''
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/department/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data?.list || []
          this.total = res.data?.total || 0
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    navToDetail(id) {
      this.$router.push('/front/departmentDetail?id=' + id)
    }
  }
}
</script>

<style scoped>
.department-overview {
  min-height: 600px;
  padding: 40px;
  background: var(--apple-card-bg);
  border-radius: var(--apple-radius-lg);
  box-shadow: var(--apple-shadow-sm);
}

.header {
  text-align: center;
  margin-bottom: 40px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: var(--apple-text-color);
  margin-bottom: 12px;
  letter-spacing: -1px;
}

.subtitle {
  font-size: 16px;
  color: var(--apple-text-secondary);
}

.search-section {
  max-width: 500px;
  margin: 0 auto 40px;
}

.search-input /deep/ .el-input__inner {
  border-radius: 20px 0 0 20px;
  padding-left: 20px;
}

.search-input /deep/ .el-input-group__append {
  border-radius: 0 20px 20px 0;
  background-color: var(--apple-blue);
  color: white;
  border: none;
}

.department-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.department-card {
  background: white;
  border-radius: var(--apple-radius-lg);
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

.department-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
  border-color: var(--apple-blue-light);
}

.img-wrapper {
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: #f5f5f7;
}

.img-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}

.department-card:hover img {
  transform: scale(1.05);
}

.info {
  padding: 24px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.dept-name {
  font-size: 20px;
  font-weight: 600;
  color: var(--apple-text-color);
  margin-bottom: 12px;
}

.dept-desc {
  font-size: 14px;
  color: var(--apple-text-secondary);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 16px;
  flex-grow: 1;
}

.dept-footer {
  margin-top: auto;
  border-top: 1px solid rgba(0,0,0,0.05);
  padding-top: 12px;
}

.detail-link {
  font-weight: 500;
  color: var(--apple-blue);
  padding: 0;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
}

.empty-text {
  font-size: 16px;
  color: var(--apple-text-secondary);
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.shadow-effect {
  box-shadow: inset 0 0 20px rgba(0,0,0,0.02);
}
</style>
