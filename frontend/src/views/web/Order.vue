<template>
  <div class="space-y-4">
    <div class="flex justify-between items-center">
      <h2 class="text-xl font-bold">服务预约记录</h2>
      <el-button type="primary" @click="dialogVisible = true" icon="Plus">新建预约</el-button>
    </div>

    <el-card>
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="单号" width="80" />
        <el-table-column prop="appointmentTime" label="预约时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
           <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
              </el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="carerId" label="护理员ID" width="100" />
        <el-table-column prop="price" label="预估价格" width="100" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
             <el-button v-if="scope.row.status === 'PENDING'" size="small" type="danger" @click="cancelOrder(scope.row)">取消预约</el-button>
             <el-button v-if="scope.row.status === 'COMPLETED' && !scope.row.evaluateScore" size="small" type="success" @click="openEvaluate(scope.row)">评价</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新建预约服务" width="500px">
      <el-form :model="form" label-width="100px" space-y-4>
        <el-form-item label="服务类型" required>
          <el-select v-model="form.serviceTypeId" placeholder="请选择服务">
             <el-option label="日常保洁 (100元)" :value="1"></el-option>
             <el-option label="助浴服务 (150元)" :value="2"></el-option>
             <el-option label="健康体检 (50元)" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="预约时间" required>
          <el-date-picker v-model="form.appointmentTime" type="datetime" placeholder="选择日期时间" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="备注需求">
          <el-input type="textarea" v-model="form.remark" rows="3" placeholder="请填写您的特殊需求" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitOrder" :loading="submitLoading">确认预约</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 评价弹窗 -->
    <el-dialog v-model="evalDialogVisible" title="服务评价" width="500px">
      <el-form label-width="80px" class="space-y-4">
        <el-form-item label="服务打分" required>
          <el-rate v-model="evalForm.evaluateScore" class="mt-1" />
        </el-form-item>
        <el-form-item label="评价体验">
          <el-input type="textarea" v-model="evalForm.evaluateText" rows="3" placeholder="请分享您的服务体验" />
        </el-form-item>
        <el-form-item label="现场照片">
          <el-upload
            action="/api/file/upload"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="evalDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEval" :loading="submitLoading">提交评价</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '../../utils/request';

const tableData = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const submitLoading = ref(false);
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');

const form = ref({
    serviceTypeId: null,
    appointmentTime: '',
    remark: ''
});

const evalDialogVisible = ref(false);
const currentOrderId = ref(null);
const evalForm = ref({
    evaluateScore: 5,
    evaluateText: '',
    evaluateImages: []
});

const handleUploadSuccess = (res, file, fileList) => {
    if (res.code === 200) {
        evalForm.value.evaluateImages.push(res.data);
    }
};

const handleRemove = (file, fileList) => {
    const url = file.response ? file.response.data : file.url;
    evalForm.value.evaluateImages = evalForm.value.evaluateImages.filter(u => u !== url);
};

const getStatusType = (status) => {
    const map = { PENDING: 'warning', ASSIGNED: 'info', IN_PROGRESS: 'primary', COMPLETED: 'success', CANCELLED: 'danger' };
    return map[status] || 'info';
};

const getStatusText = (status) => {
    const map = { PENDING: '待派单', ASSIGNED: '已派单', IN_PROGRESS: '服务中', COMPLETED: '已完成', CANCELLED: '已取消' };
    return map[status] || status;
};

const fetchData = async () => {
    loading.value = true;
    try {
        const idParam = userInfo.role === 'SENIOR' ? `?seniorId=${userInfo.id}` : '';
        const res = await request.get(`/order/list${idParam}`);
        tableData.value = res.data;
    } finally {
        loading.value = false;
    }
};

const submitOrder = async () => {
    if (!form.value.serviceTypeId || !form.value.appointmentTime) {
        ElMessage.warning('请填写必填项'); return;
    }
    submitLoading.value = true;
    try {
        const payload = { ...form.value, seniorId: userInfo.id, status: 'PENDING' };
        if (payload.serviceTypeId === 1) payload.price = 100;
        if (payload.serviceTypeId === 2) payload.price = 150;
        if (payload.serviceTypeId === 3) payload.price = 50;

        await request.post('/order/add', payload);
        ElMessage.success('预约成功');
        dialogVisible.value = false;
        fetchData();
    } finally {
        submitLoading.value = false;
    }
};

const cancelOrder = (row) => {
    ElMessageBox.confirm('确定要取消该预约吗?', '二次确认', {
        confirmButtonText: '确定取消',
        cancelButtonText: '暂不取消',
        type: 'warning'
    }).then(async () => {
        await request.put('/order/update', { id: row.id, status: 'CANCELLED' });
        ElMessage.success('已取消预约');
        fetchData();
    }).catch(() => {});
};

const openEvaluate = (row) => {
    currentOrderId.value = row.id;
    evalForm.value = { evaluateScore: 5, evaluateText: '', evaluateImages: [] };
    evalDialogVisible.value = true;
};

const submitEval = async () => {
    if (!evalForm.value.evaluateScore) {
        ElMessage.warning('请进行打分'); return;
    }
    submitLoading.value = true;
    try {
        await request.put('/order/update', { 
            id: currentOrderId.value, 
            evaluateScore: evalForm.value.evaluateScore,
            evaluateText: evalForm.value.evaluateText,
            evaluateImages: evalForm.value.evaluateImages.join(',')
        });
        ElMessage.success('评价成功，感谢您的反馈！');
        evalDialogVisible.value = false;
        fetchData();
    } finally {
        submitLoading.value = false;
    }
};

onMounted(() => { fetchData(); });
</script>
