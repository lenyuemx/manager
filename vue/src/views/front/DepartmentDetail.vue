<template>
  <div class="main-content">
    <div class="detail-container">
      <div class="detail-header glass-effect">
        <div class="header-content">
          <div class="title-area">
            <h1 class="main-title">{{ departmentData.name }}</h1>
            <div class="meta-info">
              <span class="publish-time">
                <i class="el-icon-time"></i> {{ departmentData.time }}
              </span>
            </div>
          </div>
          <div class="action-area">
            <el-tooltip content="你已经在一个社团里了" placement="top" :disabled="!isInClub">
              <el-button type="primary" class="apply-btn" @click="init" :disabled="isInClub">申请加入</el-button>
            </el-tooltip>
          </div>
        </div>
      </div>

      <div v-if="isInClub" style="margin: 20px 0">
        <el-alert title="你已经在一个社团里了，无法申请加入其他社团" type="warning" :closable="false" show-icon></el-alert>
      </div>
      
      <div class="detail-content card">
        <div class="content-title">社团介绍</div>
        <div class="html-content" v-html="departmentData.description"></div>
      </div>
    </div>

    <!-- 申请弹窗 -->
    <el-dialog title="填写申请信息" :visible.sync="fromVisible" width="400px" :close-on-click-modal="false" destroy-on-close custom-class="apple-dialog">
      <div class="dialog-content">
        <el-form label-width="0">
          <el-form-item>
            <div class="form-label">申请说明</div>
            <el-input 
              type="textarea" 
              :rows="4" 
              v-model="description" 
              placeholder="请输入您加入社团的理由或自我介绍..."
              class="custom-textarea"
            ></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false" class="cancel-btn">取消</el-button>
        <el-button type="primary" @click="save" class="confirm-btn">提交申请</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

export default {
  data() {
    let departmentId = this.$route.query.id
    return {
      user:JSON.parse(localStorage.getItem('xm-user') || '{}'),
      departmentData: {},
      departmentId: departmentId,
      fromVisible:false,
      description: null,
      isInClub: false,
    }
  },
  mounted() {
    this.loadDepartment()
    this.checkIsInClub()
  },
  methods: {
    loadDepartment() {
      this.$request.get('/department/selectById/' + this.departmentId).then(res => {
        if (res.code === '200') {
          this.departmentData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    init() {
      this.fromVisible = true
      this.description = null
    },
    save() {
      if (!this.description) {
        this.$message.warning('请填写申请说明');
        return;
      }
      let data = {
        userId: this.user.id,
        departmentId: this.departmentId,
        description: this.description
      }
      this.$request.post('/apply/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('申请成功，等待社长审核，您可以在申请的社团查看审核进度')
          this.fromVisible = false
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    checkIsInClub() {
      if (this.user.id) {
        this.$request.get('/apply/checkIsInClub/' + this.user.id).then(res => {
          if (res.code === '200') {
            this.isInClub = res.data
          }
        })
      }
    }
  }
}
</script>

<style scoped>
.detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
}

.detail-header {
  padding: 30px 40px;
  border-radius: var(--apple-radius-lg);
  margin-bottom: 24px;
  box-shadow: var(--apple-shadow-sm);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.main-title {
  font-size: 32px;
  font-weight: 600;
  color: var(--apple-text-color);
  margin: 0 0 12px 0;
  letter-spacing: -0.5px;
}

.meta-info {
  color: var(--apple-text-secondary);
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.apply-btn {
  height: 44px;
  padding: 0 32px;
  font-size: 16px;
}

.detail-content {
  padding: 40px;
  min-height: 400px;
}

.content-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--apple-text-color);
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.html-content {
  line-height: 1.8;
  color: #333;
  font-size: 16px;
}

.html-content >>> img {
  max-width: 100%;
  border-radius: var(--apple-radius-md);
  margin: 16px 0;
}

.html-content >>> p {
  margin-bottom: 16px;
}

/* 弹窗自定义样式 */
.form-label {
  font-size: 15px;
  font-weight: 500;
  color: var(--apple-text-color);
  margin-bottom: 8px;
}

.custom-textarea >>> .el-textarea__inner {
  border-radius: var(--apple-radius-md);
  padding: 12px;
  font-family: inherit;
  resize: none;
}

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