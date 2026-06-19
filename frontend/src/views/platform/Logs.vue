<template>
  <div class="space-y-4">
    <div class="flex justify-between items-center bg-white p-4 rounded-lg shadow-sm">
      <h2 class="text-xl font-bold text-slate-800">系统审计日志</h2>
      <el-button type="primary" link icon="Refresh" @click="fetchData">刷新日志</el-button>
    </div>

    <el-table :data="list" v-loading="loading" class="rounded-lg overflow-hidden border border-slate-100 shadow-sm" stripe>
      <el-table-column prop="createTime" label="操作时间" width="180" />
      <el-table-column prop="username" label="操作人员" width="150" />
      <el-table-column prop="action" label="动作描述" width="150" />
      <el-table-column prop="ip" label="IP地址" width="150" />
      <el-table-column prop="method" label="调用方法" show-overflow-tooltip />
      <el-table-column prop="params" label="操作参数" show-overflow-tooltip />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '../../utils/request';

const list = ref([]);
const loading = ref(false);

const fetchData = async () => {
    loading.value = true;
    try {
        const res = await request.get('/admin/log/list');
        list.value = res.data || [];
    } finally {
        loading.value = false;
    }
};

onMounted(() => fetchData());
</script>
