<template>
  <div class="space-y-4">
    <div class="flex justify-between items-center bg-white p-4 rounded-lg shadow-sm">
      <h2 class="text-xl font-bold text-slate-800">账号准入审核与黑名单管理</h2>
      <el-radio-group v-model="roleFilter" @change="fetchData">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="CARER">护理员</el-radio-button>
        <el-radio-button label="ORG_ADMIN">服务机构</el-radio-button>
      </el-radio-group>
    </div>

    <el-table :data="list" v-loading="loading" class="rounded-lg overflow-hidden border border-slate-100 shadow-sm">
      <el-table-column prop="realName" label="姓名/名称" />
      <el-table-column prop="username" label="账号" />
      <el-table-column prop="role" label="角色">
        <template #default="{ row }">
          <el-tag :type="roleType(row.role)">{{ translateRole(row.role) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="联系电话" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ translateStatus(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <el-button v-if="row.status === 'PENDING'" size="small" type="success" @click="handleStatus(row, 'NORMAL')">审核通过</el-button>
          <el-button v-if="row.status === 'NORMAL'" size="small" type="danger" plain @click="handleStatus(row, 'BLACKLIST')">封禁拉黑</el-button>
          <el-button v-if="row.status === 'BLACKLIST'" size="small" type="primary" plain @click="handleStatus(row, 'NORMAL')">解除封禁</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '../../utils/request';

const list = ref([]);
const loading = ref(false);
const roleFilter = ref('');

const fetchData = async () => {
    loading.value = true;
    try {
        const res = await request.get('/user/admin/list', { params: { role: roleFilter.value } });
        list.value = res.data || [];
    } finally {
        loading.value = false;
    }
};

const handleStatus = (row, status) => {
    const actionMap = { 'NORMAL': '审核通过', 'BLACKLIST': '拉黑封禁' };
    const actionText = actionMap[status] || '更新状态';
    
    ElMessageBox.confirm(`确认对用户 ${row.realName} 执行 [${actionText}] 操作吗？`, '高级管理操作确认', {
        type: 'warning',
        confirmButtonText: '立即执行',
        draggable: true
    }).then(async () => {
        await request.post('/user/admin/updateStatus', { id: row.id, status });
        ElMessage.success('操作成功执行，已审计');
        fetchData();
    }).catch(() => {});
};

const translateRole = (role) => {
    const map = {
        'PLATFORM_ADMIN': '系统超级管理',
        'ORG_ADMIN': '服务机构代表',
        'CARER': '认证护理人员',
        'SENIOR': '社区长者',
        'CHILD': '长者家属'
    };
    return map[role] || role;
};

const roleType = (role) => {
    const map = {
        'PLATFORM_ADMIN': 'danger',
        'ORG_ADMIN': 'warning',
        'CARER': 'success',
        'SENIOR': '',
        'CHILD': 'info'
    };
    return map[role] || '';
};

const translateStatus = (status) => {
    const map = {
        'NORMAL': '正常可用',
        'PENDING': '待审核验证',
        'BLACKLIST': '风控封禁'
    };
    return map[status] || status;
};

const statusType = (status) => {
    switch (status) {
        case 'NORMAL': return 'success';
        case 'PENDING': return 'warning';
        case 'BLACKLIST': return 'danger';
        default: return 'info';
    }
};

onMounted(() => fetchData());
</script>
