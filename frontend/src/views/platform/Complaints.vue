<template>
  <div class="space-y-4">
    <div class="bg-white p-4 rounded-lg shadow-sm border-l-4 border-red-500">
      <h2 class="text-xl font-bold text-slate-800">服务投诉仲裁中心</h2>
      <p class="text-sm text-slate-500 mt-1">处理老人及子女发起的针对服务质量的申诉请求</p>
    </div>

    <el-table :data="list" v-loading="loading" class="rounded-lg overflow-hidden border border-slate-100 shadow-sm">
      <el-table-column prop="createTime" label="时间" width="180" />
      <el-table-column prop="title" label="投诉主题" />
      <el-table-column prop="targetType" label="投诉类别" width="100" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 'PENDING' ? 'danger' : 'info'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="详情/证据">
        <template #default="{ row }">
          <el-button link type="primary" @click="viewDetail(row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="投诉仲裁处理" width="600px">
      <div class="space-y-4" v-if="current">
        <div class="bg-gray-50 p-4 rounded text-sm space-y-2">
            <p><strong>投诉详情：</strong>{{ current.content }}</p>
        </div>
        <el-form label-position="top">
          <el-form-item label="仲裁裁决/处理结果">
            <el-input type="textarea" v-model="resolveForm.result" :rows="4" placeholder="请输入投诉核实结果及最终处理方案..." />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="danger" @click="submitResolve">确认结案</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import request from '../../utils/request';

const list = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const current = ref(null);
const resolveForm = ref({ result: '' });

const fetchData = async () => {
    loading.value = true;
    try {
        const res = await request.get('/admin/complaint/list');
        list.value = res.data || [];
    } finally {
        loading.value = false;
    }
};

const viewDetail = (row) => {
    current.value = row;
    resolveForm.value.result = row.result || '';
    dialogVisible.value = true;
};

const submitResolve = async () => {
    if (!resolveForm.value.result) return ElMessage.warning('请输入处理结果');
    await request.post('/admin/complaint/resolve', { id: current.value.id, result: resolveForm.value.result });
    ElMessage.success('已结案并存证');
    dialogVisible.value = false;
    fetchData();
};

onMounted(() => fetchData());
</script>
