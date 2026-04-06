<template>
  <div class="manager-page-container">
    <div class="search card glass-effect">
      <el-input prefix-icon="el-icon-search" placeholder="请输入资讯标题" class="apple-input" v-model="name" clearable @clear="load(1)"></el-input>
      <el-button type="primary" class="apple-btn" @click="load(1)">查 询</el-button>
      <el-button class="apple-btn plain" @click="reset">重 置</el-button>
    </div>

    <div class="table card glass-effect">
      <el-table :data="tableData" class="apple-table" :header-cell-style="{background:'#fcfcfc',color:'#86868b',fontWeight:'600'}">
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="name" label="标题" show-overflow-tooltip></el-table-column>
        <el-table-column prop="time" label="发布时间" show-overflow-tooltip></el-table-column>
        <el-table-column prop="status" label="审核状态">
          <template v-slot="scope">
            <el-tag v-if="scope.row.status === '待审核'" type="warning">待审核</el-tag>
            <el-tag v-if="scope.row.status === '通过'" type="success">已通过</el-tag>
            <el-tag v-if="scope.row.status === '驳回'" type="danger">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="departmentName" label="所属社团"></el-table-column>

        <el-table-column label="操作" width="180" align="center">
          <template v-slot="scope">
            <el-button type="text" class="apple-text-btn" @click="changeStatus(scope.row, '通过')" v-if="scope.row.status === '待审核'">通过</el-button>
            <span class="divider" v-if="scope.row.status === '待审核'">|</span>
            <el-button type="text" class="apple-text-btn danger" @click="changeStatus(scope.row, '驳回')" v-if="scope.row.status === '待审核'">驳回</el-button>
            <span v-else>-</span>
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
  name: "TeacherInformationApply",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      name: null,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    changeStatus(row, status) {
      let data = JSON.parse(JSON.stringify(row))
      data.status = status
      this.$request.put('/information/update', data).then(res => {
        if (res.code === '200') {
          this.$message.success('操作成功')
          this.load(1)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/information/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
          status: '待审核'
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data?.list
          this.total = res.data?.total
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    reset() {
      this.name = null
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
.search { display: flex; align-items: center; gap: 12px; }
.apple-input { width: 240px; }
.apple-input >>> .el-input__inner { height: 36px; line-height: 36px; border-radius: 18px; background-color: var(--apple-bg-color); border: 1px solid transparent; padding-left: 36px; transition: var(--apple-transition); }
.apple-btn { height: 36px; padding: 0 20px; border-radius: 18px; font-weight: 500; border: none; transition: var(--apple-transition); }
.apple-text-btn { font-weight: 500; padding: 4px 8px; border-radius: 6px; }
.divider { color: #e5e5ea; margin: 0 4px; }
</style>
