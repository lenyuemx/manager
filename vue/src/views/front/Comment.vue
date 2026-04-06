<template>
  <div class="main-content">
    <div class="comment-container">
      <div class="comment-header">
        <div class="comment-count">{{ comments.length }} 条评论</div>
        <div class="sort-options">
          <span @click="sortComments('time')" :class="{ active: sortBy === 'time' }">按时间</span>
          <span @click="sortComments('likes')" :class="{ active: sortBy === 'likes' }">按热度</span>
        </div>
      </div>

      <div class="comment-editor">
        <div id="editor"></div>
        <el-button type="primary" @click="postComment">发表评论</el-button>
      </div>

      <div class="comment-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-main">
            <el-avatar :src="comment.userAvatar"></el-avatar>
            <div class="comment-content">
              <div class="comment-user">{{ comment.userName }}</div>
              <div v-html="comment.content" class="comment-text"></div>
              <div class="comment-footer">
                <span class="comment-time">{{ comment.time }}</span>
                <span class="comment-actions">
                  <span @click="showReplyEditor(comment)"><i class="el-icon-chat-dot-round"></i> 回复</span>
                </span>
              </div>
            </div>
          </div>

          <div v-if="comment.children && comment.children.length" class="comment-children">
            <div v-for="child in comment.children" :key="child.id" class="comment-item">
              <el-avatar :src="child.userAvatar"></el-avatar>
              <div class="comment-content">
                <div class="comment-user">{{ child.userName }}</div>
                <div v-html="child.content" class="comment-text"></div>
                <div class="comment-footer">
                  <span class="comment-time">{{ child.time }}</span>
                  <span class="comment-actions">
                    <span @click="showReplyEditor(child)"><i class="el-icon-chat-dot-round"></i> 回复</span>
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div v-if="comment.replying" class="reply-editor">
            <div :id="'reply-editor-' + comment.id"></div>
            <el-button type="primary" size="mini" @click="postReply(comment)">回复</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import E from 'wangeditor';

export default {
  name: 'Comment',
  props: {
    foreignId: {
      type: Number,
      required: true,
    },
    module: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      comments: [],
      sortBy: 'time',
      editor: null,
      replyEditor: null,
    };
  },
  mounted() {
    this.loadComments();
    this.initEditor();
  },
  methods: {
    loadComments() {
      this.$request.get('/comment/select/tree', {
        params: {
          foreignId: this.foreignId,
          module: this.module,
        },
      }).then(res => {
        if (res.code === '200') {
          this.comments = res.data;
          this.sortComments(this.sortBy);
        }
      });
    },
    initEditor() {
      this.editor = new E('#editor');
      this.editor.create();
    },
    postComment() {
      const content = this.editor.txt.html();
      if (!content) {
        this.$message.warning('请输入评论内容');
        return;
      }
      const data = {
        content: content,
        foreignId: this.foreignId,
        module: this.module,
      };
      this.$request.post('/comment/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('评论成功');
          this.editor.txt.clear();
          this.loadComments();
        }
      });
    },
    likeComment(comment) {
        this.$request.post('/comment/like/' + comment.id).then(res => {
            if (res.code === '200') {
                this.loadComments();
            }
        });
    },
    showReplyEditor(comment) {
      this.$set(comment, 'replying', !comment.replying);
      if (comment.replying) {
        this.$nextTick(() => {
          this.replyEditor = new E(`#reply-editor-${comment.id}`);
          this.replyEditor.create();
        });
      }
    },
    postReply(comment) {
      const content = this.replyEditor.txt.html();
      if (!content) {
        this.$message.warning('请输入回复内容');
        return;
      }
      const data = {
        content: content,
        foreignId: this.foreignId,
        module: this.module,
        parentId: comment.id,
      };
      this.$request.post('/comment/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('回复成功');
          this.replyEditor.destroy();
          this.$set(comment, 'replying', false);
          this.loadComments();
        }
      });
    },
    sortComments(type) {
      this.sortBy = type;
      if (type === 'time') {
        this.comments.sort((a, b) => new Date(b.time) - new Date(a.time));
      } else if (type === 'likes') {
        this.comments.sort((a, b) => b.likes - a.likes);
      }
    },
  },
};
</script>

<style scoped>
.comment-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 5px;
}
.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.comment-count {
  font-size: 18px;
  font-weight: bold;
}
.sort-options span {
  margin-left: 10px;
  cursor: pointer;
}
.sort-options span.active {
  color: #409EFF;
}
.comment-editor {
  margin-bottom: 20px;
}
.comment-item {
  margin-bottom: 20px;
}
.comment-main {
    display: flex;
}
.comment-content {
  margin-left: 10px;
  flex: 1;
}
.comment-user {
  font-weight: bold;
}
.comment-text {
  margin: 10px 0;
}
.comment-footer {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 12px;
}
.comment-actions span {
  margin-left: 10px;
  cursor: pointer;
}
.comment-children {
  margin-left: 50px;
  margin-top: 10px;
}
.reply-editor {
  margin-left: 50px;
  margin-top: 10px;
}
</style>
