<template>
  <div class="space-y-4">
    <div class="flex justify-between items-center bg-white p-4 rounded-lg shadow-sm">
      <h2 class="text-xl font-bold text-slate-800">健康科普文章管理</h2>
      <el-button type="primary" @click="handleAdd">发布新文章</el-button>
    </div>

    <el-table :data="list" v-loading="loading" class="rounded-lg overflow-hidden border border-slate-100 shadow-sm">
      <el-table-column prop="title" label="标题" show-overflow-tooltip />
      <el-table-column prop="type" label="类型" width="120">
        <template #default="{ row }">
          <el-tag>{{ row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="targetRole" label="定向角色" width="120" />
      <el-table-column prop="scheduleTime" label="定时发布" width="180" />
      <el-table-column prop="isPushed" label="已推送" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isPushed ? 'success' : 'info'">{{ row.isPushed ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑文章' : '发布文章'" width="70%">
      <el-form :model="form" label-width="100px">
        <el-form-item label="文章标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="文章类型">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option label="通用科普" value="GENERAL" />
            <el-option label="老人专属" value="SENIOR" />
            <el-option label="子女专属" value="CHILD" />
          </el-select>
        </el-form-item>
        <el-form-item label="定向角色">
          <el-select v-model="form.targetRole" placeholder="留空则所有人可见" clearable>
            <el-option label="所有老人" value="SENIOR" />
            <el-option label="所有子女" value="CHILD" />
          </el-select>
        </el-form-item>
        <el-form-item label="定时发布">
          <el-date-picker
            v-model="form.scheduleTime"
            type="datetime"
            placeholder="选择日期时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="封面图片">
          <el-input v-model="form.coverImage" placeholder="图片 URL" />
        </el-form-item>
        <el-form-item label="文章内容">
          <el-input 
            v-model="form.content" 
            type="textarea" 
            :rows="10" 
            placeholder="文章正文（支持 HTML）" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '../../utils/request';

const list = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const form = ref({});

const fetchData = async () => {
    loading.value = true;
    try {
        const res = await request.get('/article/list');
        list.value = res.data || [];
    } finally {
        loading.value = false;
    }
};

const handleAdd = () => {
    form.value = { type: 'GENERAL' };
    dialogVisible.value = true;
};

const handleEdit = (row) => {
    form.value = { ...row };
    dialogVisible.value = true;
};

const handleDelete = (row) => {
    ElMessageBox.confirm('确认删除该文章吗？', '提示', { type: 'warning' }).then(async () => {
        await request.delete(`/article/${row.id}`);
        ElMessage.success('删除成功');
        fetchData();
    });
};

const submitForm = async () => {
    if (form.value.id) {
        await request.post('/article/update', form.value);
    } else {
        await request.post('/article/add', form.value);
    }
    ElMessage.success('操作成功');
    dialogVisible.value = false;
    fetchData();
};

onMounted(() => fetchData());
</script>
