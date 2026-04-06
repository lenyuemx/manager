<template>
  <div class="manager-page-container">
    <div class="search card glass-effect">
      <el-input placeholder="请输入申请人姓名查询" v-model="userName" class="apple-input" clearable @clear="load(1)"></el-input>
      <el-button type="primary" class="apple-btn" @click="load(1)">查 询</el-button>
      <el-button class="apple-btn plain" @click="reset">重 置</el-button>
    </div>
    
    <div class="table card glass-effect">
      <el-table :data="tableData" class="apple-table" :header-cell-style="{background:'#fcfcfc',color:'#86868b',fontWeight:'600'}">
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="userName" label="申请人" show-overflow-tooltip></el-table-column>
        <el-table-column prop="departmentName" label="所属社团" show-overflow-tooltip></el-table-column>
        <el-table-column prop="reason" label="退团理由" show-overflow-tooltip></el-table-column>
        <el-table-column prop="applyTime" label="申请时间"></el-table-column>
        <el-table-column prop="status" label="审批状态">
          <template v-slot="scope">
            <el-tag type="info" v-if="scope.row.status === '待审批'">待审批</el-tag>
            <el-tag type="success" v-if="scope.row.status === '已批准'">已批准</el-tag>
            <el-tag type="danger" v-if="scope.row.status === '已驳回'">已驳回</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center">
          <template v-slot="scope">
            <el-button type="text" class="apple-text-btn" style="color: #67C23A" :disabled="scope.row.status !== '待审批'" @click="changeStatus(scope.row, '已批准')">批准</el-button>
            <span class="divider">|</span>
            <el-button type="text" class="apple-text-btn danger" :disabled="scope.row.status !== '待审批'" @click="changeStatus(scope.row, '已驳回')">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
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
  name: "QuitApply",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      userName: null,
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/quitApply/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          userName: this.userName
        }
      }).then(res => {
        if(res.code === '200') {
          this.tableData = res.data?.list || []
          this.total = res.data?.total || 0
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    changeStatus(row, status) {
      if(status === '已批准') {
        this.$confirm('批准后，该成员将正式从社团中移除，确定批准吗？', '确认批准', {
          type: "warning",
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          customClass: 'apple-msg-box'
        }).then(response => {
          this.updateApprove(row, status)
        }).catch(() => {})
      } else {
        this.updateApprove(row, status)
      }
    },
    updateApprove(row, status) {
      let data = JSON.parse(JSON.stringify(row));
      data.status = status;
      this.$request.put('/quitApply/update', data).then(res => {
        if (res.code === '200') {
          this.$message.success('操作成功')
          this.load()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    reset() {
      this.userName = null
      this.load(1)
    },
  }
}
</script>

<style scoped>
.manager-page-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search {
  display: flex;
  align-items: center;
  gap: 12px;
}

.apple-input {
  width: 240px;
}

.apple-input >>> .el-input__inner {
  height: 36px;
  line-height: 36px;
  border-radius: 18px;
  background-color: var(--apple-bg-color);
  border: 1px solid transparent;
  padding: 0 16px;
  transition: var(--apple-transition);
}

.apple-input >>> .el-input__inner:focus {
  background-color: #fff;
  border-color: var(--apple-blue);
  box-shadow: 0 0 0 3px var(--apple-blue-light);
}

.apple-btn {
  height: 36px;
  padding: 0 20px;
  border-radius: 18px;
  font-weight: 500;
  border: none;
  transition: var(--apple-transition);
}

.apple-btn.plain {
  background-color: var(--apple-bg-color);
  color: var(--apple-text-color);
}

.apple-btn.plain:hover {
  background-color: #e5e5ea;
}

.apple-text-btn {
  font-weight: 500;
  padding: 4px 12px;
  border-radius: 6px;
  transition: var(--apple-transition);
}

.apple-text-btn:hover:not(.is-disabled) {
  background-color: var(--apple-blue-light);
}

.apple-text-btn.danger {
  color: #ff3b30;
}

.apple-text-btn.danger:hover:not(.is-disabled) {
  background-color: rgba(255, 59, 48, 0.1);
}

.divider {
  color: #e5e5ea;
  margin: 0 4px;
}
</style>
