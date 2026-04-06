<template>
  <div class="main-content">
    <div class="detail-container">
      <div class="detail-header glass-effect">
        <h1 class="main-title">{{ informationData.name }}</h1>
        <div class="meta-info-list">
          <div class="meta-item department-link" @click="navTo('/front/DepartmentDetail?id=' + informationData.departmentId)">
            <i class="el-icon-school"></i>
            {{informationData.departmentName}}
          </div>
          <div class="meta-item">
            <i class="el-icon-time"></i>
            发布时间：{{ informationData.time }}
          </div>
          <div class="meta-item">
            <i class="el-icon-view"></i>
            阅读次数：{{ informationData.views }}
          </div>
          <div class="meta-item">
            <i class="el-icon-star-on" style="color: #ff9900;"></i>
            点赞数：{{ likeCount }}
          </div>
        </div>
      </div>

      <div class="detail-content card">
        <div class="html-content" v-html="informationData.description"></div>
        <div class="like-container">
          <el-button 
            :type="hasLiked ? 'primary' : 'default'" 
            round 
            @click="toggleLike">
            <i :class="hasLiked ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
            点赞 {{ likeCount }}
          </el-button>
        </div>
      </div>

      <div class="comment-section card">
        <div class="content-title">讨论区</div>
        <comment :foreign-id="informationId" module="information"></comment>
      </div>
    </div>
  </div>
</template>

<script>
import Comment from "./Comment.vue";

export default {
  components: {
    Comment,
  },
  data() {
    let informationId = this.$route.query.id
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      informationData: {},
      informationId: informationId,
      hasLiked: false,
      likeCount: 0,
    }
  },
  mounted() {
    this.loadInformation()
    this.loadLikes()
  },
  methods: {
    loadInformation() {
      this.$request.get('/information/selectById/' + this.informationId).then(res => {
        if (res.code === '200') {
          this.informationData = res.data
          if (this.informationData.status !== '通过') {
            this.$message.warning('该资讯正在审核中或已被驳回')
            setTimeout(() => {
              location.href = '/front/home'
            }, 1500)
          }
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadLikes() {
      this.$request.get('/likes/selectCount', {
        params: {
          foreignId: this.informationId,
          module: 'INFORMATION',
          userId: this.user.id || null
        }
      }).then(res => {
        if (res.code === '200') {
          this.likeCount = res.data.count;
          this.hasLiked = res.data.userLike;
        }
      })
    },
    toggleLike() {
      if (!this.user.id) {
        this.$message.warning('请先登录');
        return;
      }
      this.$request.post('/likes/set', {
        foreignId: this.informationId,
        module: 'INFORMATION',
        userId: this.user.id
      }).then(res => {
        if (res.code === '200') {
          this.loadLikes();
        } else {
          this.$message.error(res.msg);
        }
      })
    },
    navTo(url) {
      location.href = url
    },
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
  padding: 40px 40px 30px;
  border-radius: var(--apple-radius-lg);
  margin-bottom: 24px;
  box-shadow: var(--apple-shadow-sm);
  text-align: center;
}

.main-title {
  font-size: 32px;
  font-weight: 600;
  color: var(--apple-text-color);
  margin: 0 0 24px 0;
  line-height: 1.4;
  letter-spacing: -0.5px;
}

.meta-info-list {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 30px;
  color: var(--apple-text-secondary);
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-item i {
  font-size: 16px;
}

.department-link {
  color: var(--apple-blue);
  cursor: pointer;
  transition: var(--apple-transition);
  font-weight: 500;
  padding: 6px 12px;
  border-radius: var(--apple-radius-sm);
  background: var(--apple-blue-light);
}

.department-link:hover {
  background: rgba(0, 102, 204, 0.15);
  transform: translateY(-1px);
}

.detail-content {
  padding: 40px;
  min-height: 300px;
  margin-bottom: 24px;
}

.comment-section {
  padding: 40px;
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
  margin: 20px 0;
  box-shadow: var(--apple-shadow-sm);
  display: block;
}

.html-content >>> p {
  margin-bottom: 16px;
}

.html-content >>> h1, 
.html-content >>> h2, 
.html-content >>> h3 {
  color: var(--apple-text-color);
  margin-top: 32px;
  margin-bottom: 16px;
  font-weight: 600;
}
.like-container {
  margin-top: 30px;
  text-align: center;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  padding-top: 20px;
}
.like-container .el-button {
  font-size: 16px;
  padding: 12px 24px;
}
</style>