<template>
  <div class="manager-page-container">
    <div class="search card glass-effect">
      <el-input prefix-icon="el-icon-search" placeholder="请输入活动标题" class="apple-input" v-model="name" clearable @clear="load(1)"></el-input>
      <el-button type="primary" class="apple-btn" @click="load(1)">查 询</el-button>
      <el-button class="apple-btn plain" @click="reset">重 置</el-button>
    </div>

    <div class="operation card glass-effect">
      <el-button type="primary" class="apple-btn" @click="handleAdd" v-if="user.role === 'USER' || user.role === 'TEACHER'">
        <i class="el-icon-plus"></i> 新 增
      </el-button>
      <el-button type="danger" class="apple-btn danger-plain" @click="delBatch" :disabled="ids.length === 0">
        <i class="el-icon-delete"></i> 批量删除
      </el-button>
    </div>

    <div class="table card glass-effect">
      <el-table :data="tableData" class="apple-table" @selection-change="handleSelectionChange" :header-cell-style="{background:'#fcfcfc',color:'#86868b',fontWeight:'600'}">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column label="宣传图片" width="100" align="center">
          <template v-slot="scope">
            <div class="table-img-wrapper">
              <el-image v-if="scope.row.img" :src="scope.row.img" :preview-src-list="[scope.row.img]" class="table-img"></el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="活动名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="time" label="活动时间" show-overflow-tooltip></el-table-column>
        <el-table-column prop="description" label="活动内容" show-overflow-tooltip>
          <template v-slot="scope">
            <el-button type="text" class="apple-text-btn" @click="viewEditor(scope.row.description)">查看内容</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="审核状态">
          <template v-slot="scope">
            <el-tag v-if="scope.row.status === '待审核'" type="warning">待审核</el-tag>
            <el-tag v-if="scope.row.status === '通过'" type="success">已通过</el-tag>
            <el-tag v-if="scope.row.status === '驳回'" type="danger">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="departmentName" label="所属社团"></el-table-column>

        <el-table-column label="操作" width="180" align="center">
          <template v-slot="scope">
            <el-button type="text" class="apple-text-btn" @click="handleEdit(scope.row)" v-if="user.role === 'USER' || user.role === 'TEACHER'">编辑</el-button>
            <span class="divider" v-if="user.role === 'USER' || user.role === 'TEACHER'">|</span>
            <el-button type="text" class="apple-text-btn danger" @click="del(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
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

    <el-dialog title="活动信息" :visible.sync="fromVisible" width="600px" :close-on-click-modal="false" destroy-on-close @close="cancel" custom-class="apple-dialog">
      <el-form label-width="100px" style="padding-right: 40px" :model="form" :rules="rules" ref="formRef">
        <el-form-item label="宣传图片">
          <el-upload
              class="avatar-uploader apple-upload"
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: user.token }"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
          >
            <img v-if="form.img" :src="form.img" class="uploaded-avatar">
            <div v-else class="upload-placeholder">
              <i class="el-icon-plus"></i>
              <span>上传图片</span>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item prop="name" label="活动名称">
          <el-input v-model="form.name" autocomplete="off" class="apple-input" placeholder="请输入活动名称"></el-input>
        </el-form-item>
        <el-form-item prop="description" label="活动介绍">
          <div id="editor" class="apple-editor"></div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false" class="apple-btn plain">取 消</el-button>
        <el-button type="primary" @click="save" class="apple-btn">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="活动介绍" :visible.sync="editorVisible" width="60%" custom-class="apple-dialog">
      <div class="rich-text-content card" v-html="viewData"></div>
    </el-dialog>
  </div>
</template>

<script>
import E from 'wangeditor'

let editor
function initWangEditor(content) {	setTimeout(() => {
  if (!editor) {
    editor = new E('#editor')
    editor.config.placeholder = '请输入内容'
    editor.config.uploadFileName = 'file'
    editor.config.uploadImgServer = 'http://localhost:9090/files/wang/upload'
    editor.create()
  }
  editor.txt.html(content)
}, 0)
}

export default {
  name: "Department",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      name: null,
      fromVisible: false,
      editorVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        name: [
          {required: true, message: '请输入活动名称', trigger: 'blur'},
        ],
      },
      ids: [],
      headerData: [],
      viewData: null,
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    viewEditor(description) {
      this.viewData = description
      this.editorVisible = true
    },
    handleAdd() {   // 新增数据
      this.form = {}  // 新增数据的时候清空数据
      initWangEditor('')
      this.fromVisible = true   // 打开弹窗
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      initWangEditor(this.form.description || '')
      this.fromVisible = true   // 打开弹窗
    },
    cancel() {
      this.fromVisible = false
      location.href = '/activity'
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.form.description = editor.txt.html()
          this.$request({
            url: this.form.id ? '/activity/update' : '/activity/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // 表示成功保存
              this.$message.success('保存成功')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg)  // 弹出错误的信息
            }
          })
        }
      })
    },
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {
        type: "warning",
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        customClass: 'apple-msg-box'
      }).then(response => {
        this.$request.delete('/activity/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {})
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)   //  [1,2]
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {
        type: "warning",
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        customClass: 'apple-msg-box'
      }).then(response => {
        this.$request.delete('/activity/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {})
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/activity/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data?.list
          this.total = res.data?.total
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    reset() {
      this.name = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    handleAvatarSuccess(response, file, fileList) {
      // 把图片url保存到img里
      this.$set(this.form, 'img', response.data)
    },
  }
}
</script>

<style scoped>
.manager-page-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 搜索区域样式 */
.search {
  display: flex;
  align-items: center;
  gap: 12px;
}

.apple-input {
  width: 240px;
}

.apple-input >>> .el-input__inner {
  height: 36px;
  line-height: 36px;
  border-radius: 18px;
  background-color: var(--apple-bg-color);
  border: 1px solid transparent;
  padding-left: 36px;
  transition: var(--apple-transition);
}

.apple-input >>> .el-input__inner:focus {
  background-color: #fff;
  border-color: var(--apple-blue);
  box-shadow: 0 0 0 3px var(--apple-blue-light);
}

.apple-input >>> .el-input__prefix {
  left: 12px;
  color: var(--apple-text-secondary);
}

/* 按钮通用样式 */
.apple-btn {
  height: 36px;
  padding: 0 20px;
  border-radius: 18px;
  font-weight: 500;
  border: none;
  transition: var(--apple-transition);
}

.apple-btn.plain {
  background-color: var(--apple-bg-color);
  color: var(--apple-text-color);
}

.apple-btn.plain:hover {
  background-color: #e5e5ea;
}

.apple-btn.danger-plain {
  background-color: rgba(255, 59, 48, 0.1);
  color: #ff3b30;
}

.apple-btn.danger-plain:hover:not(.is-disabled) {
  background-color: rgba(255, 59, 48, 0.2);
}

.apple-btn.is-disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 操作区域样式 */
.operation {
  display: flex;
  gap: 12px;
}

/* 表格和图片样式 */
.table-img-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
}

.table-img {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  box-shadow: var(--apple-shadow-sm);
  object-fit: cover;
  background-color: #f5f5f7;
}

/* 表格操作按钮 */
.apple-text-btn {
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 6px;
  transition: var(--apple-transition);
}

.apple-text-btn:hover {
  background-color: var(--apple-blue-light);
}

.apple-text-btn.danger {
  color: #ff3b30;
}

.apple-text-btn.danger:hover {
  background-color: rgba(255, 59, 48, 0.1);
}

.divider {
  color: #e5e5ea;
  margin: 0 4px;
}

/* 弹窗上传样式 */
.apple-upload >>> .el-upload {
  border: 2px dashed #e5e5ea;
  border-radius: 16px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 120px;
  height: 120px;
  transition: var(--apple-transition);
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--apple-bg-color);
}

.apple-upload >>> .el-upload:hover {
  border-color: var(--apple-blue);
  background-color: #fff;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: var(--apple-text-secondary);
}

.upload-placeholder i {
  font-size: 28px;
  margin-bottom: 8px;
}

.uploaded-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 富文本内容展示 */
.rich-text-content {
  padding: 24px;
  line-height: 1.8;
  color: var(--apple-text-color);
}

.rich-text-content >>> img {
  max-width: 100%;
  border-radius: 12px;
  margin: 16px 0;
}
</style>