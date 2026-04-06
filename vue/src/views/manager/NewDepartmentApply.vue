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
        <el-table-column label="审批说明" prop="approvalDescription"></el-table-column>
        <el-table-column label="状态" prop="status"></el-table-column>
        <el-table-column label="操作" width="220">
          <template slot-scope="scope">
            <el-button type="primary" @click="showApproveDialog(scope.row)" v-if="scope.row.status === '待审批'">批准</el-button>
            <el-button type="danger" @click="showRejectDialog(scope.row)" v-if="scope.row.status === '待审批'">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="card" v-if="total">
        <el-pagination layout="prev, pager, next" @current-change="handleCurrentChange" :page-size="pageSize" :total="total"></el-pagination>
      </div>
    </div>

    <!-- 审批对话框 -->
    <el-dialog title="审批" :visible.sync="dialogVisible" width="30%">
      <el-form :model="form" label-width="80px">
        <el-form-item label="审批说明">
          <el-input type="textarea" v-model="form.approvalDescription"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitApproval">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: 'NewDepartmentApply',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      name: '',
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      dialogVisible: false,
      form: {
        id: null,
        status: '',
        approvalDescription: ''
      },
      isApprove: true // true for approve, false for reject
    }
  },
  created() {
    this.load();
  },
  methods: {
    load() {
      let params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        name: this.name,
      };
      if (this.user.role === 'TEACHER') {
        params.teacherId = this.user.id;
      }
      this.$request.get('/newDepartmentApply/selectPage', {
        params: params
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
    },
    showApproveDialog(row) {
      this.form.id = row.id;
      this.isApprove = true;
      this.form.approvalDescription = ''; // Reset description
      this.dialogVisible = true;
    },
    showRejectDialog(row) {
      this.form.id = row.id;
      this.isApprove = false;
      this.form.approvalDescription = ''; // Reset description
      this.dialogVisible = true;
    },
    submitApproval() {
      this.form.status = this.isApprove ? '已批准' : '已驳回';
      this.$request.put('/newDepartmentApply/update', this.form).then(res => {
        if (res.code === '200') {
          this.$message.success('操作成功');
          this.dialogVisible = false;
          this.load();
        } else {
          this.$message.error(res.msg);
        }
      });
    }
  }
}
</script>
