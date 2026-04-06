<template>
  <div class="manager-page-container">
    <div class="password-container glass-effect">
      <div class="header-action">
        <h2 class="page-title">修改密码</h2>
        <p class="page-subtitle">定期修改密码有助于保护账号安全</p>
      </div>
      
      <el-form ref="formRef" :model="user" :rules="rules" label-width="100px" class="password-form">
        <div class="form-fields">
          <el-form-item label="原始密码" prop="password">
            <el-input show-password v-model="user.password" placeholder="请输入原始密码" class="apple-input"></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input show-password v-model="user.newPassword" placeholder="请输入新密码" class="apple-input"></el-input>
          </el-form-item>
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input show-password v-model="user.confirmPassword" placeholder="请再次输入新密码" class="apple-input"></el-input>
          </el-form-item>
        </div>

        <div class="submit-action">
          <el-button type="primary" class="apple-btn save-btn" @click="update">确 认 修 改</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "Password",
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
      rules: {
        password: [
          { required: true, message: '请输入原始密码', trigger: 'blur' },
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度应在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { validator: validatePassword, required: true, trigger: 'blur' },
        ],
      }
    }
  },
  created() {

  },
  methods: {
    update() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request.put('/updatePassword', this.user).then(res => {
            if (res.code === '200') {
              // 成功更新
              localStorage.removeItem('xm-user')   // 清除缓存的用户信息
              this.$message.success('修改密码成功，请重新登录')
              this.$router.push('/login')
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    },
  }
}
</script>

<style scoped>
.manager-page-container {
  display: flex;
  justify-content: center;
  padding: 40px 20px;
}

.password-container {
  width: 100%;
  max-width: 500px;
  padding: 40px;
  border-radius: var(--apple-radius-lg);
  box-shadow: var(--apple-shadow-md);
  background: var(--apple-card-bg);
}

.header-action {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--apple-text-color);
  margin: 0 0 12px 0;
  letter-spacing: -0.5px;
}

.page-subtitle {
  font-size: 14px;
  color: var(--apple-text-secondary);
  margin: 0;
}

/* 表单区域样式 */
.form-fields {
  margin-bottom: 40px;
}

.password-form >>> .el-form-item {
  margin-bottom: 24px;
}

.password-form >>> .el-form-item__label {
  font-weight: 500;
  color: var(--apple-text-color);
}

.apple-input >>> .el-input__inner {
  height: 40px;
  line-height: 40px;
  border-radius: 8px;
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

.submit-action {
  text-align: center;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  padding-top: 30px;
}

.save-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
}

/* 按钮通用样式 */
.apple-btn {
  font-weight: 500;
  border: none;
  transition: var(--apple-transition);
}
</style>