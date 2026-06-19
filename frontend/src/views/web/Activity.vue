<template>
    <div class="space-y-4">
        <h2 class="text-xl font-bold">社区社交与心理互动活动</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <el-card v-for="act in list" :key="act.id" shadow="hover" class="relative overflow-hidden">
                <div class="absolute top-0 right-0 bg-blue-500 text-white px-3 py-1 rounded-bl-lg text-xs font-bold">
                    积分奖励: +{{act.rewardPoints}}
                </div>
                <h3 class="text-lg font-bold text-gray-800 mt-2">{{ act.title }}</h3>
                <p class="text-gray-500 text-sm mt-2 line-clamp-2 my-2">{{ act.content }}</p>
                <div class="text-xs text-gray-400 space-y-1">
                    <p><el-icon><Location /></el-icon> {{ act.address }}</p>
                    <p><el-icon><Calendar /></el-icon> {{ act.startTime }} ~ {{ act.endTime }}</p>
                </div>
                <div class="mt-4 flex flex-wrap justify-end gap-2">
                    <el-button type="info" plain size="small" @click="handleSign(act)">我要报名</el-button>
                    <el-button type="primary" size="small" @click="handleAttend(act)">现场签到</el-button>
                </div>
            </el-card>
        </div>
        <el-empty v-if="!loading && list.length === 0" description="近期暂无活动" />
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '../../utils/request';

const list = ref([]);
const loading = ref(false);
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');

const fetchData = async () => {
    loading.value = true;
    try {
        const res = await request.get('/activity/list');
        list.value = res.data || [];
    } finally {
        loading.value = false;
    }
};

const handleSign = (act) => {
    ElMessageBox.confirm(`确认报名活动：${act.title} 吗？`, '报名确认', {
        confirmButtonText: '确认报名',
        cancelButtonText: '取消',
        type: 'info'
    }).then(() => {
        ElMessage.success('报名成功！请准时参加');
    }).catch(() => {});
};

const handleAttend = async (act) => {
    try {
        await request.post('/activity/attend', { activityId: act.id, seniorId: userInfo.id });
        ElMessage.success(`签到成功！获得 ${act.rewardPoints} 积分`);
    } catch (e) {
        // Error is handled by global interceptor usually
    }
};

onMounted(() => fetchData());
</script>
