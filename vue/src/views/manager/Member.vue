<template>
  <div class="manager-page-container">
    <div class="search card glass-effect" v-if="user.role === 'ADMIN'">
      <el-select v-model="departmentId" placeholder="请选择社团" class="apple-input" clearable @clear="load(1)">
        <el-option v-for="item in departmentData" :key="item.id" :label="item.name" :value="item.id"></el-option>
      </el-select>
      <el-button type="primary" class="apple-btn" @click="load(1)">查 询</el-button>
      <el-button class="apple-btn plain" @click="reset">重 置</el-button>
    </div>
    
    <div class="table card glass-effect">
      <el-table :data="tableData" class="apple-table" :header-cell-style="{background:'#fcfcfc',color:'#86868b',fontWeight:'600'}">
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="userName" label="学生姓名" show-overflow-tooltip></el-table-column>
        <el-table-column prop="departmentName" label="所属社团" show-overflow-tooltip></el-table-column>
        <el-table-column prop="description" label="申请说明"></el-table-column>

        <el-table-column label="操作" width="120" align="center">
          <template v-slot="scope">
            <el-button type="text" class="apple-text-btn danger" @click="del(scope.row.id)">移除成员</el-button>
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
  name: "Member",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        status: [
          {required: true, message: '请选择审核状态', trigger: 'blur'},
        ],
      },
      ids: [],
      status: null,
      departmentId: null,
      departmentData: []
    }
  },
  created() {
    this.load(1)
    this.loadDepartment()
  },
  methods: {
    loadDepartment() {
      this.$request.get('/department/selectAll').then(res => {
        if (res.code === '200') {
          this.departmentData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/apply/selectPage2', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          departmentId: this.departmentId
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    del(id) {   // 单个删除
      this.$confirm('您确定移除该成员吗？', '确认移除', {
        type: "warning",
        confirmButtonText: '确定移除',
        cancelButtonText: '取消',
        customClass: 'apple-msg-box'
      }).then(response => {
        this.$request.delete('/apply/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {})
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    reset() {
      this.departmentId = null
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

/* 搜索区域样式 */
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

/* 按钮通用样式 */
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

/* 表格操作按钮 */
.apple-text-btn {
  font-weight: 500;
  padding: 4px 12px;
  border-radius: 6px;
  transition: var(--apple-transition);
  color: var(--apple-blue);
}

.apple-text-btn:hover {
  background-color: var(--apple-blue-light);
}

.apple-text-btn.danger {
  color: #ff3b30;
}

.apple-text-btn.danger:hover {
  background-color: rgba(255, 59, 48, 0.1);
}
</style>