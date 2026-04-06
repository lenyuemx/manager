<template>
  <div class="manager-page-container">
    <div class="search card glass-effect">
      <el-input prefix-icon="el-icon-search" placeholder="请输入标题查询" class="apple-input" v-model="title" clearable @clear="load(1)"></el-input>
      <el-button type="primary" class="apple-btn" @click="load(1)">查 询</el-button>
      <el-button class="apple-btn plain" @click="reset">重 置</el-button>
    </div>

    <div class="operation card glass-effect">
      <el-button type="primary" class="apple-btn" @click="handleAdd">
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
        <el-table-column prop="title" label="标题" show-overflow-tooltip></el-table-column>
        <el-table-column prop="content" label="内容" show-overflow-tooltip></el-table-column>
        <el-table-column prop="time" label="发布时间" width="180"></el-table-column>
        <el-table-column prop="user" label="发布人" width="120"></el-table-column>

        <el-table-column label="操作" width="180" align="center">
          <template v-slot="scope">
            <el-button type="text" class="apple-text-btn" @click="handleEdit(scope.row)">编辑</el-button>
            <span class="divider">|</span>
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

    <el-dialog title="公告信息" :visible.sync="fromVisible" width="500px" :close-on-click-modal="false" destroy-on-close custom-class="apple-dialog">
      <el-form label-width="80px" style="padding-right: 40px" :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="title" label="标题">
          <el-input v-model="form.title" autocomplete="off" class="apple-input" placeholder="请输入公告标题"></el-input>
        </el-form-item>
        <el-form-item prop="content" label="内容">
          <el-input type="textarea" :rows="5" v-model="form.content" autocomplete="off" class="apple-textarea" placeholder="请输入公告内容"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false" class="apple-btn plain">取 消</el-button>
        <el-button type="primary" @click="save" class="apple-btn">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Notice",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      title: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        title: [
          {required: true, message: '请输入标题', trigger: 'blur'},
        ],
        content: [
          {required: true, message: '请输入内容', trigger: 'blur'},
        ]
      },
      ids: []
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    handleAdd() {   // 新增数据
      this.form = {}  // 新增数据的时候清空数据
      this.fromVisible = true   // 打开弹窗
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.fromVisible = true   // 打开弹窗
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/notice/update' : '/notice/add',
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
        this.$request.delete('/notice/delete/' + id).then(res => {
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
        this.$request.delete('/notice/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/notice/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          title: this.title,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.title = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
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

.apple-textarea >>> .el-textarea__inner {
  border-radius: 12px;
  background-color: var(--apple-bg-color);
  border: 1px solid transparent;
  padding: 12px;
  transition: var(--apple-transition);
  font-family: inherit;
  resize: none;
}

.apple-textarea >>> .el-textarea__inner:focus {
  background-color: #fff;
  border-color: var(--apple-blue);
  box-shadow: 0 0 0 3px var(--apple-blue-light);
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
</style>
