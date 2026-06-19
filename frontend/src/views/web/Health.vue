<template>
  <div class="space-y-4">
    <div class="flex justify-between items-center">
      <h2 class="text-xl font-bold">健康监测数据</h2>
      <el-button type="success" @click="dialogVisible = true" icon="Upload">自助上传指标</el-button>
    </div>

    <!-- 预警卡片 -->
    <el-alert v-if="hasAbnormal" title="发现异常健康指标，请及时关注就依！" type="error" show-icon class="mb-4" />

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
       <el-card v-for="item in tableData" :key="item.id" 
                :class="{'border-red-300 bg-red-50': item.isAbnormal, 'bg-white': !item.isAbnormal}"
                shadow="hover">
           <template #header>
             <div class="flex justify-between items-center font-bold">
                 <span>{{ getTypeName(item.recordType) }}</span>
                 <div>
                     <el-tag size="small" type="info" class="mr-2" v-if="item.measureStage">
                         {{ getStageName(item.measureStage) }}
                     </el-tag>
                     <el-tag size="small" :type="item.isAbnormal ? 'danger' : 'success'">
                         {{ item.isAbnormal ? '异常' : '正常' }}
                     </el-tag>
                 </div>
             </div>
          </template>
          <div class="space-y-2 text-sm text-gray-600">
             <div class="flex justify-between">
                 <span>测量时间:</span> <span>{{ item.measureTime || item.createTime }}</span>
             </div>
             <div class="flex justify-between text-lg text-gray-900 font-mono font-bold mt-2 border-t pt-2">
                 <span>指标数值:</span> <span>{{ item.value1 }} <span v-if="item.value2">/ {{item.value2}}</span></span>
             </div>
             <p v-if="item.relatedOrderId" class="text-xs text-blue-500 mt-2">关联工单号: #{{ item.relatedOrderId }}</p>
             <p v-if="item.remark" class="text-xs text-gray-400 mt-1">备注: {{ item.remark }}</p>
          </div>
       </el-card>
    </div>
    
    <el-empty v-if="tableData.length===0 && !loading" description="暂无健康数据记录"></el-empty>

    <!-- 上传弹窗 -->
    <el-dialog v-model="dialogVisible" title="录入健康指标" width="400px">
       <el-form :model="form" label-width="80px">
           <el-form-item label="指标类型">
              <el-select v-model="form.recordType" placeholder="选择类型" class="w-full">
                  <el-option label="血压 (收缩压/舒张压)" value="BLOOD_PRESSURE"></el-option>
                  <el-option label="血糖 (空腹)" value="BLOOD_SUGAR"></el-option>
                  <el-option label="血脂" value="BLOOD_LIPID"></el-option>
              </el-select>
           </el-form-item>
           <el-form-item label="数值1">
              <el-input v-model="form.value1" placeholder="如 收缩压 120" />
           </el-form-item>
           <el-form-item label="数值2" v-if="form.recordType === 'BLOOD_PRESSURE'">
              <el-input v-model="form.value2" placeholder="如 舒张压 80" />
           </el-form-item>
           <el-form-item label="是否异常">
              <el-switch v-model="form.isAbnormal" active-text="异常" inactive-text="正常" />
           </el-form-item>
           <el-form-item label="备注">
              <el-input type="textarea" v-model="form.remark" placeholder="如 饭后测试" />
           </el-form-item>
       </el-form>
       <template #footer>
          <el-button @click="dialogVisible=false">取消</el-button>
          <el-button type="primary" @click="submit" :loading="submitLoading">提交保存</el-button>
       </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import request from '../../utils/request';

const tableData = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const submitLoading = ref(false);
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');

const form = ref({ recordType: 'BLOOD_PRESSURE', value1: '', value2: '', isAbnormal: false, remark: '' });

const hasAbnormal = computed(() => tableData.value.some(r => r.isAbnormal));

const getTypeName = (type) => {
    const map = { BLOOD_PRESSURE: '血压', BLOOD_SUGAR: '血糖', BLOOD_LIPID: '血脂' };
    return map[type] || type;
};

const getStageName = (stage) => {
    const map = { BEFORE_SERVICE: '服务前测', AFTER_SERVICE: '服务后测', SELF_UPLOAD: '自助记录' };
    return map[stage] || '自助记录';
};

const fetchData = async () => {
    loading.value = true;
    try {
        const idParam = userInfo.role === 'SENIOR' ? `?seniorId=${userInfo.id}` : '';
        const res = await request.get(`/health/list${idParam}`);
        tableData.value = res.data;
    } finally {
        loading.value = false;
    }
};

const submit = async () => {
    if (!form.value.value1) { ElMessage.warning('请输入数值'); return; }
    submitLoading.value = true;
    try {
        await request.post('/health/add', { 
            ...form.value, 
            seniorId: userInfo.id, 
            measuredBy: userInfo.id,
            measureStage: 'SELF_UPLOAD'
        });
        ElMessage.success('指标上传成功');
        dialogVisible.value = false;
        fetchData();
        form.value = { recordType: 'BLOOD_PRESSURE', value1: '', value2: '', isAbnormal: false, remark: '' };
    } finally {
        submitLoading.value = false;
    }
};

onMounted(() => fetchData());
</script>
