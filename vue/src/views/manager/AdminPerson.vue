<template>
  <div class="manager-page-container">
    <div class="person-container glass-effect">
      <div class="header-action">
        <h2 class="page-title">个人信息</h2>
      </div>
      
      <el-form :model="user" label-width="80px" class="person-form">
        <div class="avatar-section">
          <el-upload
              class="avatar-uploader apple-upload circle-upload"
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
            <el-input v-model="user.username" placeholder="用户名" disabled class="apple-input"></el-input>
          </el-form-item>
          <el-form-item label="姓名" prop="name">
            <el-input v-model="user.name" placeholder="请输入姓名" class="apple-input"></el-input>
          </el-form-item>
          <el-form-item label="电话" prop="phone">
            <el-input v-model="user.phone" placeholder="请输入电话" class="apple-input"></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="user.email" placeholder="请输入邮箱" class="apple-input"></el-input>
          </el-form-item>
        </div>

        <div class="submit-action">
          <el-button type="primary" class="apple-btn save-btn" @click="update">保 存 修 改</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "AdminPerson",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}')
    }
  },
  created() {

  },
  methods: {
    update() {
      // 保存当前的用户信息到数据库
      this.$request.put('/admin/update', this.user).then(res => {
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
  }
}
</script>

<style scoped>
.manager-page-container {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.person-container {
  width: 100%;
  max-width: 600px;
  padding: 40px;
  border-radius: var(--apple-radius-lg);
  box-shadow: var(--apple-shadow-md);
  background: var(--apple-card-bg);
}

.header-action {
  display: flex;
  justify-content: center;
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

.apple-input >>> .el-input__inner:disabled {
  background-color: rgba(0, 0, 0, 0.02);
  color: var(--apple-text-secondary);
  cursor: not-allowed;
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

/* 按钮通用样式 */
.apple-btn {
  font-weight: 500;
  border: none;
  transition: var(--apple-transition);
}
</style>