<template>
  <div class="main-content">
    <div class="card" style="width: 60%; margin: 30px auto">
      <div style="font-weight: bold; font-size: 24px; margin-bottom: 30px">新建社团申请</div>
      <el-form :model="form" label-width="100px">
        <el-form-item label="社团图标">
          <el-upload
            class="avatar-uploader"
            :action="$baseUrl + '/files/upload'"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
          >
            <img v-if="form.cover" :src="form.cover" class="avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="社团名称">
          <el-input v-model="form.name" placeholder="请输入社团名称"></el-input>
        </el-form-item>
        <el-form-item label="社团描述">
          <el-input type="textarea" v-model="form.description" placeholder="请输入社团描述"></el-input>
        </el-form-item>
        <el-form-item label="指导老师">
          <el-select v-model="form.teacherId" placeholder="请选择指导老师">
            <el-option v-for="item in teachers" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <div v-if="isInClub" style="margin-bottom: 20px">
          <el-alert title="你已经在一个社团里了，无法申请新建社团" type="warning" :closable="false" show-icon></el-alert>
        </div>
        <div style="text-align: center">
          <el-button type="primary" @click="submit" :disabled="isInClub">提交申请</el-button>
        </div>
      </el-form>
    </div>

    <div class="card" style="width: 60%; margin: 30px auto">
      <div style="font-weight: bold; font-size: 24px; margin-bottom: 30px">我的申请列表</div>
      <el-table :data="tableData" stripe border>
        <el-table-column label="社团图标">
          <template v-slot="scope">
            <el-image style="width: 50px; height: 50px; border-radius: 5px" v-if="scope.row.cover" :src="scope.row.cover" :preview-src-list="[scope.row.cover]"></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="社团名称"></el-table-column>
        <el-table-column prop="description" label="社团描述" show-overflow-tooltip></el-table-column>
        <el-table-column prop="applyTime" label="申请时间"></el-table-column>
        <el-table-column prop="status" label="审批状态">
          <template v-slot="scope">
            <el-tag type="info" v-if="scope.row.status === '待审批'">待审批</el-tag>
            <el-tag type="success" v-if="scope.row.status === '已批准'">已批准</el-tag>
            <el-tag type="danger" v-if="scope.row.status === '已驳回'">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approvalDescription" label="审批说明" show-overflow-tooltip></el-table-column>
      </el-table>
      <div style="margin-top: 20px" v-if="total > 0">
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
  name: 'NewDepartment',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      teachers: [],
      tableData: [],
      pageNum: 1,
      pageSize: 5,
      total: 0,
      form: {
        name: '',
        description: '',
        applicantId: null,
        applyTime: '',
        status: '待审批',
        teacherId: null,
        cover: ''
      },
      isInClub: false
    }
  },
  created() {
    this.form.applicantId = this.user.id;
    this.loadTeachers();
    this.loadApplications(1);
    this.checkClubMembership();
  },
  methods: {
    submit() {
      if (!this.form.name || !this.form.description || !this.form.teacherId) {
        this.$message.error('请填写所有必填项');
        return;
      }
      const date = new Date();
      this.form.applyTime = date.getFullYear() + '-' + (date.getMonth() + 1).toString().padStart(2, '0') + '-' + date.getDate().toString().padStart(2, '0');

      this.$request.post('/newDepartmentApply/add', this.form).then(res => {
        if (res.code === '200') {
          this.$message.success('申请已提交，请等待审批');
          this.form.name = '';
          this.form.description = '';
          this.form.cover = '';
          this.loadApplications(1);
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    handleCoverSuccess(response, file, fileList) {
      this.form.cover = response.data;
    },
    loadApplications(pageNum) {
      if (pageNum) this.pageNum = pageNum;
      this.$request.get('/newDepartmentApply/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          applicantId: this.user.id
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
    handleCurrentChange(pageNum) {
      this.loadApplications(pageNum);
    },
    loadTeachers() {
      this.$request.get('/user/selectUnassignedTeachers').then(res => {
        if (res.code === '200') {
          this.teachers = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    checkClubMembership() {
      this.$request.get('/newDepartmentApply/checkIsInClub/' + this.user.id).then(res => {
        if (res.code === '200') {
          this.isInClub = res.data;
        }
      })
    }
  }
}
</script>

<style scoped>
.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 120px;
  height: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.avatar-uploader:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}
.avatar {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}
</style>
