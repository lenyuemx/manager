<template>
  <div class="container">
    <div class="login-box">
      <div class="logo-area">
        <!-- 如果有logo可以在这里放 <img src="@/assets/imgs/logo.png" /> -->
        <div class="title">欢迎使用</div>
        <div class="subtitle">大学社团管理系统</div>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="username">
          <el-input prefix-icon="el-icon-user" placeholder="请输入账号" v-model="form.username" class="custom-input"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input prefix-icon="el-icon-lock" placeholder="请输入密码" show-password  v-model="form.password" class="custom-input"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="form.role" placeholder="请选择身份登录" style="width: 100%" class="custom-input">
            <el-option label="管理员" value="ADMIN"></el-option>
            <el-option label="学生" value="USER"></el-option>
            <el-option label="教师" value="TEACHER"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" @click="login">登 录</el-button>
        </el-form-item>

        <div class="register-link">
          还没有账号？请 <a href="/register">注册</a>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      form: { },
      rules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
        ]
      }
    }
  },
  methods: {
    login() {
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          // 验证通过
          this.$request.post('/login', this.form).then(res => {
            if (res.code === '200') {
              localStorage.setItem("xm-user", JSON.stringify(res.data))  // 存储用户数据
              if(res.data.role === 'USER' && res.data.level === '学生'){
                location.href = '/front/home'
              }else {
                this.$router.push('/')  // 跳转主页
              }
              this.$message.success('登录成功')
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.container {
  height: 100vh;
  overflow: hidden;
  background-image: url("@/assets/imgs/bg.jpg");
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-box {
  width: 400px;
  padding: 40px;
  border-radius: var(--apple-radius-lg);
  box-shadow: var(--apple-shadow-lg);
  background-color: white;
}

.logo-area {
  text-align: center;
  margin-bottom: 30px;
}

.title {
  font-size: 28px;
  font-weight: 600;
  color: var(--apple-text-color);
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 16px;
  color: var(--apple-text-secondary);
  margin-top: 8px;
}

.login-form .el-form-item {
  margin-bottom: 24px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  margin-top: 10px;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: var(--apple-text-secondary);
}

a {
  font-weight: 500;
}
</style>