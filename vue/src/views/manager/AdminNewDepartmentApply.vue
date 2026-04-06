<template>
  <div>
    <div class="card" style="margin-bottom: 10px">
      <el-input style="width: 260px; margin-right: 10px" v-model="name" placeholder="请输入社团名称查询"></el-input>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>

    <div class="card">
      <el-table :data="tableData" stripe>
        <el-table-column label="社团名称" prop="name"></el-table-column>
        <el-table-column label="社团图标">
          <template v-slot="scope">
            <el-image style="width: 50px; height: 50px; border-radius: 5px" v-if="scope.row.cover" :src="scope.row.cover" :preview-src-list="[scope.row.cover]"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="申请人" prop="applicantName"></el-table-column>
        <el-table-column label="申请时间" prop="applyTime"></el-table-column>
        <el-table-column label="审批人" prop="teacherName"></el-table-column>
        <el-table-column label="状态">
          <template v-slot="scope">
            <el-tag type="success">已批准</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审批说明" prop="approvalDescription"></el-table-column>
      </el-table>

      <div class="card" v-if="total">
        <el-pagination layout="prev, pager, next" @current-change="handleCurrentChange" :page-size="pageSize" :total="total"></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminNewDepartmentApply',
  data() {
    return {
      name: '',
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10
    }
  },
  created() {
    this.load();
  },
  methods: {
    load() {
      this.$request.get('/newDepartmentApply/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
          status: '已批准'
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data?.list || [];
          this.total = res.data?.total || 0;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    reset() {
      this.name = '';
      this.load();
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum;
      this.load();
    }
  }
}
</script>
