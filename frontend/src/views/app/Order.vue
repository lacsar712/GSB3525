<template>
  <div class="space-y-4">
    <h3 class="font-bold text-gray-800 border-l-4 border-blue-500 pl-2">我的服务单</h3>
    <el-tabs v-model="activeTab" class="w-full">
       <el-tab-pane label="派给我的" name="assigned">
           <div class="space-y-3">
               <el-card v-for="item in assignedList" :key="item.id" shadow="hover">
                  <div class="flex justify-between font-bold">
                    <span>服务类型: {{item.serviceTypeId}}</span>
                    <span class="text-blue-500">已派单</span>
                  </div>
                  <div class="text-xs text-gray-500 mt-2 space-y-1">
                      <p>预约时间: {{item.appointmentTime}}</p>
                      <p>老人要求: {{item.remark || '无'}}</p>
                  </div>
                  <div class="mt-3 flex justify-end gap-2">
                       <el-button size="small" type="primary" @click="handleSignIn(item)">到达&签到</el-button>
                  </div>
               </el-card>
               <el-empty v-if="assignedList.length===0" description="暂无派单"></el-empty>
           </div>
       </el-tab-pane>
       <el-tab-pane label="提供进行中" name="inprogress">
           <div class="space-y-3">
               <el-card v-for="item in inprogressList" :key="item.id" shadow="hover" class="border-green-300">
                  <div class="flex justify-between font-bold">
                    <span>单号: {{item.id}}</span>
                    <span class="text-green-500">服务中</span>
                  </div>
                  <div class="text-xs text-gray-500 mt-2">签到时间: {{item.signInTime || '-'}}</div>
                  <div class="mt-3 flex flex-wrap justify-end gap-2">
                       <el-button size="small" type="primary" plain @click="openHealthDialog(item, 'BEFORE_SERVICE')">测前指征</el-button>
                       <el-button size="small" type="primary" plain @click="openHealthDialog(item, 'AFTER_SERVICE')">测后指征</el-button>
                       <el-button size="small" type="success" @click="handleSignOut(item)">完成&签退</el-button>
                  </div>
               </el-card>
               <el-empty v-if="inprogressList.length===0" description="暂无进行中的服务"></el-empty>
           </div>
       </el-tab-pane>
       <el-tab-pane label="已完结" name="completed">
           <div class="space-y-3">
               <el-card v-for="item in completedList" :key="item.id" shadow="hover" class="opacity-80">
                  <div class="flex justify-between font-bold">
                    <span>单号: {{item.id}}</span>
                    <span class="text-gray-500">已完结</span>
                  </div>
                  <div class="text-xs text-gray-500 mt-2">
                      <p>签退时间: {{item.signOutTime || '-'}}</p>
                      <p class="text-blue-500 font-bold mt-1">结算金额: ¥{{item.price}}</p>
                  </div>
                  <div class="mt-3 flex justify-end gap-2" v-if="!item.carerEvaluateScore">
                       <el-button size="small" type="warning" @click="openEvaluate(item)">评价老人</el-button>
                  </div>
               </el-card>
               <el-empty v-if="completedList.length===0" description="暂无完结服务"></el-empty>
           </div>
       </el-tab-pane>
    </el-tabs>

    <!-- 护理员评价老人的弹窗 -->
    <el-dialog v-model="evalDialogVisible" title="评价老人/服务对象" width="90%">
      <el-form class="space-y-4">
        <el-form-item label="沟通与配合" required>
          <el-rate v-model="evalForm.carerEvaluateScore" class="mt-1" />
        </el-form-item>
        <el-form-item label="服务反馈">
          <el-input type="textarea" v-model="evalForm.carerEvaluateText" rows="3" placeholder="老人的配合程度如何？" />
        </el-form-item>
        <el-form-item label="结单照片">
          <el-upload
            action="/api/file/upload"
            list-type="picture-card"
            v-model:file-list="fileList"
            :headers="uploadHeaders"
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

    <el-dialog v-model="healthDialogVisible" title="录入健康指标" width="90%">
       <el-form :model="healthForm" label-width="80px" class="space-y-4">
           <el-form-item label="指标类型">
              <el-select v-model="healthForm.recordType" placeholder="选择类型" class="w-full">
                  <el-option label="血压 (收缩/舒张)" value="BLOOD_PRESSURE"></el-option>
                  <el-option label="血糖" value="BLOOD_SUGAR"></el-option>
                  <el-option label="血脂" value="BLOOD_LIPID"></el-option>
              </el-select>
           </el-form-item>
           <el-form-item label="数值1">
              <el-input v-model="healthForm.value1" placeholder="数值1" />
           </el-form-item>
           <el-form-item label="数值2" v-if="healthForm.recordType === 'BLOOD_PRESSURE'">
              <el-input v-model="healthForm.value2" placeholder="数值2" />
           </el-form-item>
           <el-form-item label="异常判定">
              <el-switch v-model="healthForm.isAbnormal" active-text="异常" inactive-text="正常" />
           </el-form-item>
       </el-form>
       <template #footer>
          <el-button @click="healthDialogVisible=false">取消</el-button>
          <el-button type="primary" @click="submitHealth" :loading="submitLoading">提交</el-button>
       </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '../../utils/request';

const list = ref([]);
const activeTab = ref('assigned');
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');

const assignedList = computed(() => list.value.filter(i => i.status === 'ASSIGNED'));
const inprogressList = computed(() => list.value.filter(i => i.status === 'IN_PROGRESS'));
const completedList = computed(() => list.value.filter(i => i.status === 'COMPLETED'));

const evalDialogVisible = ref(false);
const healthDialogVisible = ref(false);
const currentOrderId = ref(null);
const currentSeniorId = ref(null);
const submitLoading = ref(false);
const evalForm = ref({
    carerEvaluateScore: 5,
    carerEvaluateText: '',
    carerEvaluateImages: []
});
const fileList = ref([]);

const healthForm = ref({
    recordType: 'BLOOD_PRESSURE', value1: '', value2: '', isAbnormal: false, remark: '', measureStage: '', relatedOrderId: null
});

const uploadHeaders = computed(() => {
    return { Authorization: localStorage.getItem('token') || '' }
});

const handleUploadSuccess = (res, file) => {
    if (res.code === 200) {
        evalForm.value.carerEvaluateImages.push(res.data);
    }
};

const handleRemove = (file) => {
    const url = file.response ? file.response.data : file.url;
    evalForm.value.carerEvaluateImages = evalForm.value.carerEvaluateImages.filter(u => u !== url);
};

const fetchData = async () => {
    const res = await request.get(`/order/list?carerId=${userInfo.id}`);
    list.value = res.data || [];
};

const formatDate = (date) => {
    const pad = (n) => n < 10 ? '0' + n : n;
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
};

const handleSignIn = (item) => {
    ElMessageBox.confirm('确认已经到达老人居所并开始服务吗？', '签到', {
        type: 'info'
    }).then(async () => {
        await request.put('/order/update', { id: item.id, status: 'IN_PROGRESS', signInTime: formatDate(new Date()) });
        ElMessage.success('签到成功');
        fetchData();
        activeTab.value = 'inprogress';
    }).catch(()=>{});
};

const handleSignOut = (item) => {
    ElMessageBox.confirm('确认服务已完成并签退吗？', '签退', {
        type: 'success'
    }).then(async () => {
        await request.put('/order/update', { id: item.id, status: 'COMPLETED', signOutTime: formatDate(new Date()) });
        ElMessage.success('签退成功，服务流程结束');
        fetchData();
        activeTab.value = 'completed';
    }).catch(()=>{});
};

const openEvaluate = (row) => {
    currentOrderId.value = row.id;
    evalForm.value = { carerEvaluateScore: 5, carerEvaluateText: '', carerEvaluateImages: [] };
    fileList.value = [];
    evalDialogVisible.value = true;
};

const submitEval = async () => {
    if (!evalForm.value.carerEvaluateScore) {
        ElMessage.warning('请进行打分'); return;
    }
    submitLoading.value = true;
    try {
        await request.put('/order/update', { 
            id: currentOrderId.value, 
            carerEvaluateScore: evalForm.value.carerEvaluateScore,
            carerEvaluateText: evalForm.value.carerEvaluateText,
            carerEvaluateImages: evalForm.value.carerEvaluateImages.join(',')
        });
        ElMessage.success('评价成功！');
        evalDialogVisible.value = false;
        fetchData();
    } finally {
        submitLoading.value = false;
    }
};

const openHealthDialog = (row, stage) => {
    currentOrderId.value = row.id;
    currentSeniorId.value = row.seniorId;
    healthForm.value = { recordType: 'BLOOD_PRESSURE', value1: '', value2: '', isAbnormal: false, remark: '护理员记录', measureStage: stage, relatedOrderId: row.id };
    healthDialogVisible.value = true;
};

const submitHealth = async () => {
    if (!healthForm.value.value1) {
        ElMessage.warning('请输入数值'); return;
    }
    submitLoading.value = true;
    try {
        await request.post('/health/add', { 
            ...healthForm.value, 
            seniorId: currentSeniorId.value, 
            measuredBy: userInfo.id 
        });
        ElMessage.success('健康指标记录成功');
        healthDialogVisible.value = false;
    } finally {
        submitLoading.value = false;
    }
};

onMounted(() => fetchData());
</script>
