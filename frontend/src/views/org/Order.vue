<template>
  <div class="space-y-4">
    <div class="flex justify-between items-center">
      <h2 class="text-xl font-bold">待派发单列表</h2>
    </div>

    <el-card>
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="单号" width="80" />
        <el-table-column prop="appointmentTime" label="预约时间" width="180" />
        <el-table-column prop="serviceTypeId" label="服务类型" width="120" />
        <el-table-column prop="remark" label="需求备注" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
             <el-button type="primary" size="small" @click="openAssignForm(scope.row)">指派护理员</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="指派护理员" width="400px">
        <el-form label-width="100px">
            <el-form-item label="选择护理员">
                <el-select v-model="selectedCarer" placeholder="请选择">
                    <el-option label="王阿姨 (ID: 3)" :value="3" />
                </el-select>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="dialogVisible=false">取消</el-button>
            <el-button type="primary" @click="submitAssign">确定指派</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import request from '../../utils/request';

const tableData = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const currentOrder = ref(null);
const selectedCarer = ref(null);

const fetchData = async () => {
    loading.value = true;
    try {
        const res = await request.get('/order/list');
        // 只看待派发的
        tableData.value = res.data.filter(i => i.status === 'PENDING') || [];
    } finally {
        loading.value = false;
    }
};

const openAssignForm = (row) => {
    currentOrder.value = row;
    selectedCarer.value = null;
    dialogVisible.value = true;
};

const submitAssign = async () => {
    if (!selectedCarer.value) { ElMessage.warning('请选择护理员'); return; }
    await request.put('/order/update', { id: currentOrder.value.id, carerId: selectedCarer.value, status: 'ASSIGNED' });
    ElMessage.success('派单成功');
    dialogVisible.value = false;
    fetchData();
};

onMounted(() => fetchData());
</script>
