<template>
  <div class="manager-page-container">
    <div class="search card glass-effect">
      <el-select v-model="dbStatus" placeholder="请选择审核状态" class="apple-input" clearable @clear="load(1)">
        <el-option label="待审核" value="待审核"></el-option>
        <el-option label="审核通过" value="审核通过"></el-option>
        <el-option label="审核不通过" value="审核不通过"></el-option>
      </el-select>
      <el-button type="primary" class="apple-btn" @click="load(1)">查 询</el-button>
      <el-button class="apple-btn plain" @click="reset">重 置</el-button>
    </div>

    <div class="table card glass-effect">
      <el-table :data="tableData" class="apple-table" @selection-change="handleSelectionChange" :header-cell-style="{background:'#fcfcfc',color:'#86868b',fontWeight:'600'}">
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="userName" label="学生姓名" show-overflow-tooltip></el-table-column>
        <el-table-column prop="departmentName" label="所属社团" show-overflow-tooltip></el-table-column>
        <el-table-column prop="description" label="申请说明"></el-table-column>
        <el-table-column prop="process" label="当前进度"></el-table-column>
        <el-table-column prop="status" label="审核状态">
          <template v-slot="scope">
            <span v-if="scope.row.status === '审核通过'" class="status-badge success">{{scope.row.status}}</span>
            <span v-else-if="scope.row.status === '审核不通过'" class="status-badge danger">{{scope.row.status}}</span>
            <span v-else class="status-badge warning">{{scope.row.status}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="note" label="审核说明"></el-table-column>

        <el-table-column label="操作" width="120" align="center" v-if="user.role === 'USER' || user.role === 'TEACHER'">
          <template v-slot="scope">
            <el-button type="text" class="apple-text-btn" :class="{'is-disabled': scope.row.status !== '待审核'}" :disabled="scope.row.status !== '待审核'" @click="handleEdit(scope.row)">审核</el-button>
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

    <el-dialog title="审核信息" :visible.sync="fromVisible" width="500px" :close-on-click-modal="false" destroy-on-close custom-class="apple-dialog">
      <el-form label-width="80px" style="padding-right: 40px" :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="status" label="审核状态">
          <el-select v-model="status" placeholder="请选择" style="width: 100%" class="apple-input">
            <el-option label="审核通过" value="审核通过"></el-option>
            <el-option label="审核不通过" value="审核不通过"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="note" label="审核说明">
          <el-input type="textarea" :rows="3" v-model="form.note" autocomplete="off" class="apple-textarea" placeholder="请输入审核说明"></el-input>
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
  name: "Apply",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      dbStatus: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        status: [
          {required: true, message: '请选择审核状态', trigger: 'blur'},
        ],
      },
      ids: [],
      status: null
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.status = null
      this.fromVisible = true   // 打开弹窗
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      if (!this.status) {
        this.$message.warning("请选择审核状态")
        return
      }
      this.form.status = this.status
      this.form.process = '审核完成'
      this.$request({
        url: this.form.id ? '/apply/update' : '/apply/add',
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
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)   //  [1,2]
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/apply/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          status: this.dbStatus,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.dbStatus = null
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
  padding: 0 16px;
  transition: var(--apple-transition);
}

.apple-input >>> .el-input__inner:focus {
  background-color: #fff;
  border-color: var(--apple-blue);
  box-shadow: 0 0 0 3px var(--apple-blue-light);
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

/* 状态徽章样式 */
.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
}

.status-badge.success {
  background: rgba(52, 199, 89, 0.1);
  color: #34c759;
}

.status-badge.warning {
  background: rgba(255, 149, 0, 0.1);
  color: #ff9500;
}

.status-badge.danger {
  background: rgba(255, 59, 48, 0.1);
  color: #ff3b30;
}

/* 表格操作按钮 */
.apple-text-btn {
  font-weight: 500;
  padding: 4px 12px;
  border-radius: 6px;
  transition: var(--apple-transition);
  color: var(--apple-blue);
}

.apple-text-btn:hover:not(.is-disabled) {
  background-color: var(--apple-blue-light);
}

.apple-text-btn.is-disabled {
  color: var(--apple-text-secondary);
  cursor: not-allowed;
  opacity: 0.6;
}
</style>