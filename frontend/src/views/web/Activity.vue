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
                    <el-button
                        type="primary"
                        size="small"
                        :loading="attendingIds.has(act.id)"
                        :disabled="attendingIds.has(act.id) || attendedIds.has(act.id)"
                        @click="handleAttend(act)">
                        {{ attendedIds.has(act.id) ? '已签到' : '现场签到' }}
                    </el-button>
                </div>
            </el-card>
        </div>
        <el-empty v-if="!loading && list.length === 0" description="近期暂无活动" />
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '../../utils/request';

const list = ref([]);
const loading = ref(false);
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');

// 正在签到中的活动 id 集合（防止重复点击）
const attendingIds = reactive(new Set());
// 已成功签到的活动 id 集合（按钮禁用并显示"已签到"）
const attendedIds = reactive(new Set());

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
    if (!userInfo || !userInfo.id) {
        ElMessage.warning('请先登录后再进行签到');
        return;
    }
    // 已签到或正在签到则直接返回，避免重复点击
    if (attendedIds.has(act.id) || attendingIds.has(act.id)) {
        return;
    }
    attendingIds.add(act.id);
    try {
        const res = await request.post('/activity/attend', { activityId: act.id, seniorId: userInfo.id });
        const reward = (res && res.data && res.data.rewardPoints) || act.rewardPoints || 0;
        ElMessage.success(reward > 0 ? `签到成功！获得 ${reward} 积分` : '签到成功');
        attendedIds.add(act.id);
    } catch (e) {
        // 后端通过 code=409 表示重复签到，全局拦截器已弹出错误信息
        // 若是重复签到，标记为已签到，避免再次点击
        const msg = (e && (e.message || e)) + '';
        if (msg.indexOf('重复签到') !== -1 || msg.indexOf('已签到') !== -1) {
            attendedIds.add(act.id);
        }
    } finally {
        attendingIds.delete(act.id);
    }
};

onMounted(() => fetchData());
</script>
