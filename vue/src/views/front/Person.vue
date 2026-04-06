<template>
  <div class="main-content">
    <div class="person-container glass-effect">
      <div class="header-action">
        <h2 class="page-title">个人中心</h2>
        <el-button type="primary" class="change-pwd-btn" @click="updatePassword">修改密码</el-button>
      </div>
      
      <el-form :model="user" label-width="80px" class="person-form">
        <div class="avatar-section">
          <el-upload
              class="avatar-uploader"
              :action="$baseUrl + '/files/upload'"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
          >
            <div class="avatar-wrapper">
              <img v-if="user.avatar" :src="user.avatar" class="avatar" />
              <div v-else class="avatar-placeholder">
                <i class="el-icon-camera avatar-uploader-icon"></i>
              </div>
              <div class="avatar-hover-mask">
                <i class="el-icon-edit"></i>
              </div>
            </div>
          </el-upload>
          <div class="avatar-tip">点击修改头像</div>
        </div>

        <div class="form-fields">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="user.username" placeholder="用户名" disabled class="custom-input"></el-input>
          </el-form-item>
          <el-form-item label="姓名" prop="name">
            <el-input v-model="user.name" placeholder="姓名" class="custom-input"></el-input>
          </el-form-item>
          <el-form-item label="电话" prop="phone">
            <el-input v-model="user.phone" placeholder="电话" class="custom-input"></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="user.email" placeholder="邮箱" class="custom-input"></el-input>
          </el-form-item>
        </div>

        <div class="submit-action">
          <el-button type="primary" class="save-btn" @click="update">保 存 修 改</el-button>
        </div>
      </el-form>
    </div>

    <div class="person-container glass-effect" style="margin-top: 20px;">
      <div class="header-action">
        <h2 class="page-title">我所在的社团</h2>
      </div>
      <el-table :data="myClubs" class="apple-table" :header-cell-style="{background:'#fcfcfc',color:'#86868b',fontWeight:'600'}">
        <el-table-column prop="departmentName" label="社团名称"></el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template v-slot="scope">
            <el-button type="danger" size="mini" class="apple-btn danger-plain" @click="handleQuit(scope.row)">申请退团</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog title="修改密码" :visible.sync="dialogVisible" width="400px" :close-on-click-modal="false" destroy-on-close custom-class="apple-dialog">
      <div class="dialog-content">
        <el-form :model="user" label-width="80px" :rules="rules" ref="formRef">
          <el-form-item label="原始密码" prop="password">
            <el-input show-password v-model="user.password" placeholder="请输入原始密码" class="custom-input"></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input show-password v-model="user.newPassword" placeholder="请输入新密码" class="custom-input"></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input show-password v-model="user.confirmPassword" placeholder="请再次输入新密码" class="custom-input"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" class="cancel-btn">取 消</el-button>
        <el-button type="primary" @click="save" class="confirm-btn">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 申请退团弹窗 -->
    <el-dialog title="申请退团" :visible.sync="quitDialogVisible" width="400px" custom-class="apple-dialog">
      <el-form label-width="80px">
        <el-form-item label="退团理由">
          <el-input type="textarea" v-model="quitReason" placeholder="请输入退出该社团的原因"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="quitDialogVisible = false" class="cancel-btn">取 消</el-button>
        <el-button type="danger" @click="submitQuitApply" class="confirm-btn" style="background-color: #ff3b30; color: white;">提 交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    const validatePassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请确认密码'))
      } else if (value !== this.user.newPassword) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      dialogVisible: false,
      myClubs: [],
      quitDialogVisible: false,
      quitReason: '',
      currentQuitClubId: null,

      rules: {
        password: [
          { required: true, message: '请输入原始密码', trigger: 'blur' },
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
        ],
        confirmPassword: [
          { validator: validatePassword, required: true, trigger: 'blur' },
        ],
      }
    }
  },
  created() {
    this.loadMyClubs();
  },
  methods: {
    update() {
      // 保存当前的用户信息到数据库
      this.$request.put('/user/update', this.user).then(res => {
        if (res.code === '200') {
          // 成功更新
          this.$message.success('保存成功')
          // 更新浏览器缓存里的用户信息
          localStorage.setItem('xm-user', JSON.stringify(this.user))

          // 触发父级的数据更新
          this.$emit('update:user')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAvatarSuccess(response, file, fileList) {
      // 把user的头像属性换成上传的图片的链接
      this.$set(this.user, 'avatar', response.data)
    },
    // 修改密码
    updatePassword() {
      this.dialogVisible = true
    },
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request.put('/updatePassword', this.user).then(res => {
            if (res.code === '200') {
              // 成功更新
              this.$message.success('修改密码成功')
              this.$router.push('/login')
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    },
    loadMyClubs() {
      this.$request.get('/apply/selectMyApply').then(res => {
        if(res.code === '200') {
          this.myClubs = res.data.filter(item => item.status === '审核通过');
        }
      })
    },
    handleQuit(row) {
      if(!row.departmentId) {
        this.$message.error('社团数据异常');
        return;
      }
      this.currentQuitClubId = row.departmentId;
      this.quitReason = '';
      this.quitDialogVisible = true;
    },
    submitQuitApply() {
      if(!this.quitReason) {
        this.$message.warning('请输入退团理由');
        return;
      }
      this.$request.post('/quitApply/add', {
        userId: this.user.id,
        departmentId: this.currentQuitClubId,
        reason: this.quitReason
      }).then(res => {
        if(res.code === '200') {
          this.$message.success('退团申请已递交，请等待审批');
          this.quitDialogVisible = false;
        } else {
          this.$message.error(res.msg);
        }
      })
    }
  }
}
</script>

<style scoped>
.person-container {
  max-width: 600px;
  margin: 40px auto;
  padding: 40px;
  border-radius: var(--apple-radius-lg);
  box-shadow: var(--apple-shadow-md);
  background: var(--apple-card-bg);
}

.header-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--apple-text-color);
  margin: 0;
  letter-spacing: -0.5px;
}

.change-pwd-btn {
  height: 36px;
  padding: 0 20px;
}

/* 头像区域样式 */
.avatar-section {
  text-align: center;
  margin-bottom: 40px;
}

.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: var(--apple-shadow-sm);
  cursor: pointer;
  transition: var(--apple-transition);
}

.avatar-wrapper:hover {
  box-shadow: var(--apple-shadow-md);
  transform: scale(1.02);
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: var(--apple-bg-color);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader-icon {
  font-size: 32px;
  color: var(--apple-text-secondary);
}

.avatar-hover-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: var(--apple-transition);
}

.avatar-wrapper:hover .avatar-hover-mask {
  opacity: 1;
}

.avatar-hover-mask i {
  color: white;
  font-size: 24px;
}

.avatar-tip {
  margin-top: 12px;
  font-size: 13px;
  color: var(--apple-text-secondary);
}

/* 表单区域样式 */
.form-fields {
  max-width: 400px;
  margin: 0 auto 40px;
}

.person-form >>> .el-form-item {
  margin-bottom: 24px;
}

.person-form >>> .el-form-item__label {
  font-weight: 500;
  color: var(--apple-text-color);
}

.submit-action {
  text-align: center;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  padding-top: 30px;
}

.save-btn {
  width: 200px;
  height: 44px;
  font-size: 16px;
  border-radius: 22px;
}

/* 弹窗底部按钮样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn, .confirm-btn {
  height: 40px;
  padding: 0 24px;
}

.cancel-btn {
  background: rgba(0, 0, 0, 0.05);
  border: none;
  color: var(--apple-text-color);
}

.cancel-btn:hover {
  background: rgba(0, 0, 0, 0.08);
}
</style>