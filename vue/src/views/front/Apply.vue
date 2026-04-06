<template>
  <div class="main-content">
    <div class="apply-container glass-effect">
      <div class="header-action">
        <h2 class="page-title">我的申请记录 
          <span class="count-badge">{{ tableData.length }}</span>
        </h2>
      </div>
      
      <div class="table-wrapper">
        <el-table :data="tableData" class="apple-table">
          <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
          <el-table-column prop="departmentName" label="申请的社团"></el-table-column>
          <el-table-column prop="status" label="审核状态">
            <template v-slot="scope">
              <span v-if="scope.row.status === '审核通过'" class="status-badge success">{{scope.row.status}}</span>
              <span v-else-if="scope.row.status === '审核不通过'" class="status-badge danger">{{scope.row.status}}</span>
              <span v-else class="status-badge warning">{{scope.row.status}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="申请说明" show-overflow-tooltip></el-table-column>
          <el-table-column prop="process" label="当前进度"></el-table-column>
          <el-table-column prop="note" label="审核说明" show-overflow-tooltip></el-table-column>

          <el-table-column label="操作" width="180" align="center" v-if="user.role === 'USER'">
            <template v-slot="scope">
              <el-button 
                class="action-btn" 
                type="text" 
                :disabled="scope.row.status !== '待审核'" 
                @click="del(scope.row.id)">
                撤销申请
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      tableData: []
    }
  },
  mounted() {
    this.loadApply()
  },
  methods: {
    loadApply() {
      this.$request.get('/apply/selectMyApply').then(res => {
        if (res.code === '200') {
          this.tableData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    del(id) {
      this.$confirm('您确定撤销申请吗？', '提示', {
        type: "warning",
        confirmButtonText: '确定撤销',
        cancelButtonText: '暂不',
        customClass: 'apple-msg-box'
      }).then(response => {
        this.$request.delete('/apply/delete/' + id).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功')
            this.loadApply()
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {
      })
    },
  }
}
</script>

<style scoped>
.apply-container {
  max-width: 1000px;
  margin: 40px auto;
  padding: 30px 40px;
  border-radius: var(--apple-radius-lg);
  box-shadow: var(--apple-shadow-md);
  background: var(--apple-card-bg);
}

.header-action {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--apple-text-color);
  margin: 0;
  letter-spacing: -0.5px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.count-badge {
  background: var(--apple-blue-light);
  color: var(--apple-blue);
  font-size: 14px;
  font-weight: 600;
  padding: 2px 10px;
  border-radius: 12px;
}

.table-wrapper {
  background: white;
  border-radius: var(--apple-radius-md);
  overflow: hidden;
  box-shadow: var(--apple-shadow-sm);
  border: 1px solid rgba(0, 0, 0, 0.02);
}

/* 状态徽章样式 */
.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
}

.status-badge.success {
  background: rgba(52, 199, 89, 0.1);
  color: #34c759;
}

.status-badge.warning {
  background: rgba(255, 149, 0, 0.1);
  color: #ff9500;
}

.status-badge.danger {
  background: rgba(255, 59, 48, 0.1);
  color: #ff3b30;
}

/* 按钮样式 */
.action-btn {
  color: var(--apple-blue);
  font-weight: 500;
  transition: var(--apple-transition);
}

.action-btn:hover:not(.is-disabled) {
  color: var(--apple-blue-hover);
  background: var(--apple-blue-light);
  border-radius: var(--apple-radius-sm);
  padding: 4px 12px;
}

.action-btn.is-disabled {
  color: var(--apple-text-secondary);
}

/* Element UI 表格样式覆盖 */
.apple-table >>> .el-table__header th {
  background-color: #fcfcfc !important;
  color: var(--apple-text-secondary);
  font-weight: 600;
  padding: 16px 0;
}

.apple-table >>> .el-table__body td {
  padding: 16px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.02);
}

.apple-table >>> .el-table__row:hover > td {
  background-color: var(--apple-bg-color) !important;
}
</style>