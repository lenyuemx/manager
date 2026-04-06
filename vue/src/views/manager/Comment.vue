<template>
  <div class="manager-page-container">
    <div class="search card glass-effect">
      <el-input prefix-icon="el-icon-search" placeholder="请输入评论内容查询" class="apple-input" v-model="content" clearable @clear="load(1)"></el-input>
      <el-button type="primary" class="apple-btn" @click="load(1)">查 询</el-button>
      <el-button class="apple-btn plain" @click="reset">重 置</el-button>
    </div>

    <div class="operation card glass-effect">
      <el-button type="danger" class="apple-btn danger-plain" @click="delBatch" :disabled="ids.length === 0">
        <i class="el-icon-delete"></i> 批量删除
      </el-button>
    </div>

    <div class="table card glass-effect">
      <el-table :data="tableData" class="apple-table" @selection-change="handleSelectionChange" :header-cell-style="{background:'#fcfcfc',color:'#86868b',fontWeight:'600'}">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="module" label="模块" width="100">
          <template v-slot="scope">
            <el-tag v-if="scope.row.module === 'activity'" type="primary">社团活动</el-tag>
            <el-tag v-else-if="scope.row.module === 'information'" type="success">社团资讯</el-tag>
            <span v-else>{{ scope.row.module }}</span>
          </template>
        </el-table-column>
        <el-table-column label="关联标题" show-overflow-tooltip width="200">
          <template v-slot="scope">
            <span>{{ scope.row.activityName || scope.row.informationName || '无' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" show-overflow-tooltip></el-table-column>
        <el-table-column prop="userName" label="评论人" width="120"></el-table-column>
        <el-table-column prop="time" label="评论时间" width="180"></el-table-column>

        <el-table-column label="操作" width="120" align="center">
          <template v-slot="scope">
            <el-button type="text" class="apple-text-btn danger" @click="del(scope.row.id)">删除</el-button>
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
  name: "Comment",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      content: null,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      ids: []
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {
        type: "warning",
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        customClass: 'apple-msg-box'
      }).then(response => {
        this.$request.delete('/comment/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {})
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {
        type: "warning",
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        customClass: 'apple-msg-box'
      }).then(response => {
        this.$request.delete('/comment/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {})
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/comment/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          content: this.content,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.content = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
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
  padding-left: 36px;
  transition: var(--apple-transition);
}

.apple-input >>> .el-input__inner:focus {
  background-color: #fff;
  border-color: var(--apple-blue);
  box-shadow: 0 0 0 3px var(--apple-blue-light);
}

.apple-input >>> .el-input__prefix {
  left: 12px;
  color: var(--apple-text-secondary);
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

.apple-btn.danger-plain {
  background-color: rgba(255, 59, 48, 0.1);
  color: #ff3b30;
}

.apple-btn.danger-plain:hover:not(.is-disabled) {
  background-color: rgba(255, 59, 48, 0.2);
}

.apple-btn.is-disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 操作区域样式 */
.operation {
  display: flex;
  gap: 12px;
}

/* 表格操作按钮 */
.apple-text-btn {
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 6px;
  transition: var(--apple-transition);
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

.divider {
  color: #e5e5ea;
  margin: 0 4px;
}
</style>
