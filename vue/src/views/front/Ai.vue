<template>
  <div class="main-content">
    <div style="width: 80%; margin: 20px auto">
      <div class="chat-container">
        <!-- 聊天头部 -->
        <div class="chat-header">
          <i class="el-icon-cpu" style="margin-right: 10px"></i>
          <span>社团助手 - 问问AI</span>
        </div>

        <!-- 聊天内容区域 -->
        <div class="chat-body" ref="chatBody">
          <div v-for="(item, index) in messages" :key="index" :class="item.role === 'user' ? 'message-item-user' : 'message-item-ai'">
            <div class="avatar">
              <img v-if="item.role === 'ai'" src="@/assets/imgs/logo.png" alt="">
              <img v-else :src="user.avatar" alt="">
            </div>
            <div class="content-box">
              <div class="sender-name">{{ item.role === 'user' ? user.name : 'AI助手' }}</div>
              <div class="message-content" v-html="formatContent(item.content)"></div>
            </div>
          </div>
          <div v-if="loading" class="message-item-ai">
            <div class="avatar">
              <img src="@/assets/imgs/logo.png" alt="">
            </div>
            <div class="content-box">
              <div class="sender-name">AI助手</div>
              <div class="message-content">
                <i class="el-icon-loading"></i> 正在思考中...
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chat-footer">
          <el-input
              type="textarea"
              :rows="3"
              placeholder="请输入您想咨询关于社团的问题..."
              v-model="inputMsg"
              @keyup.enter.native="sendMessage"
          ></el-input>
          <div style="text-align: right; margin-top: 10px">
            <el-button type="primary" @click="sendMessage" :disabled="loading">发送 (Enter)</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Ai",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      messages: [
        { role: 'ai', content: '您好！我是您的社团助手。您可以问我关于社团的任何问题，例如：“有哪些社团？”或“音乐社是做什么的？”' }
      ],
      inputMsg: '',
      loading: false
    }
  },
  methods: {
    sendMessage() {
      if (!this.inputMsg.trim()) {
        this.$message.warning('请输入内容');
        return;
      }
      const msg = this.inputMsg.trim();
      this.messages.push({ role: 'user', content: msg });
      this.inputMsg = '';
      this.loading = true;
      this.scrollToBottom();

      this.$request.post('/ai/chat', { message: msg }).then(res => {
        this.loading = false;
        if (res.code === '200') {
          this.messages.push({ role: 'ai', content: res.data });
        } else {
          this.$message.error(res.msg || '系统繁忙');
          this.messages.push({ role: 'ai', content: '抱歉，我现在遇到了一点问题，请稍后再试。' });
        }
        this.scrollToBottom();
      }).catch(err => {
        this.loading = false;
        this.$message.error('发送失败');
        this.scrollToBottom();
      });
    },
    formatContent(context) {
      if (!context) return '';
      // 简单的换行转换，如果需要更复杂的Markdown支持可以引入marked库
      return context.replace(/\n/g, '<br>');
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const chatBody = this.$refs.chatBody;
        chatBody.scrollTop = chatBody.scrollHeight;
      });
    }
  }
}
</script>

<style scoped>
.chat-container {
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  display: flex;
  flex-direction: column;
  height: 700px;
}

.chat-header {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
  display: flex;
  align-items: center;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f9f9f9;
}

.chat-footer {
  padding: 20px;
  border-top: 1px solid #eee;
}

.message-item-user, .message-item-ai {
  display: flex;
  margin-bottom: 20px;
}

.message-item-user {
  flex-direction: row-reverse;
}

.avatar {
  width: 40px;
  height: 40px;
  margin: 0 10px;
}

.avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.content-box {
  max-width: 70%;
}

.message-item-user .content-box {
  text-align: right;
}

.sender-name {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}

.message-content {
  padding: 10px 15px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-all;
}

.message-item-ai .message-content {
  background-color: #fff;
  color: #333;
  border: 1px solid #e4e7ed;
}

.message-item-user .message-content {
  background-color: #409EFF;
  color: #fff;
  text-align: left;
  display: inline-block;
}
</style>
