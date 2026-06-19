<template>
  <div class="space-y-10 animate-fade-in">
    <!-- Header Hero Section -->
    <div class="flex justify-between items-center bg-white p-8 rounded-2xl premium-shadow border border-slate-200/50 relative overflow-hidden">
        <div class="relative z-10">
            <h1 class="text-3xl font-bold text-slate-900 tracking-tight">平台全局监控</h1>
            <p class="text-slate-500 mt-2 text-sm font-medium">智慧养老生态系统核心数据概览与实时审计</p>
        </div>
        <div class="relative z-10 text-right">
            <div class="inline-flex items-center gap-3 bg-success/10 px-4 py-2 rounded-xl border border-success/20">
                <div class="relative flex h-3 w-3">
                  <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-success/40 opacity-75"></span>
                  <span class="relative inline-flex rounded-full h-3 w-3 bg-success"></span>
                </div>
                <span class="text-success font-bold text-xs uppercase tracking-widest">系统健康运行中</span>
            </div>
            <p class="text-[10px] text-slate-400 mt-2 font-bold uppercase tracking-tighter">实时同步时间: {{ currentTime }}</p>
        </div>
        <!-- Decorative background circle -->
        <div class="absolute -right-20 -top-20 w-64 h-64 bg-primary/5 rounded-full blur-3xl"></div>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
        <div v-for="stat in stats" :key="stat.label" class="bg-white p-6 rounded-2xl border border-slate-200/50 hover:border-primary/30 hover:shadow-xl transition-all group overflow-hidden relative">
            <div class="flex items-center gap-4 relative z-10">
                <div :class="`w-12 h-12 rounded-xl flex items-center justify-center transition-colors ${stat.bg}`">
                    <el-icon :class="`text-xl ${stat.textColor}`"><component :is="stat.icon" /></el-icon>
                </div>
                <div>
                    <p class="text-xs font-bold text-slate-400 uppercase tracking-widest mb-1">{{ stat.label }}</p>
                    <p class="text-2xl font-bold text-slate-900">{{ stat.value }}</p>
                </div>
            </div>
            <div class="absolute -right-4 -bottom-4 opacity-[0.03] group-hover:scale-110 transition-transform duration-500">
                <el-icon class="text-7xl"><component :is="stat.icon" /></el-icon>
            </div>
        </div>
    </div>

    <!-- Secondary Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <!-- Interactive Radar Chart -->
        <div class="glass-card p-8 rounded-3xl premium-shadow border border-white/40">
            <div class="flex items-center justify-between mb-8">
                <h3 class="text-lg font-bold text-slate-800 flex items-center gap-2">
                    <el-icon class="text-primary"><Compass /></el-icon>
                    健康预警雷达
                </h3>
                <el-tag size="small" type="info" effect="plain" class="rounded-lg">实时分布图</el-tag>
            </div>
            <div ref="radarChartRef" class="h-[300px] w-full rounded-2xl relative z-10">
                <!-- Echarts will render here -->
            </div>
        </div>

        <!-- Social Heatmap -->
        <div class="bg-white p-8 rounded-3xl premium-shadow border border-slate-200/50">
            <div class="flex items-center justify-between mb-8">
                <h3 class="text-lg font-bold text-slate-800 flex items-center gap-2">
                    <el-icon class="text-pink-500"><TrendCharts /></el-icon>
                    社交与心理热度
                </h3>
                <el-button link type="primary" size="small" @click="viewSocialDetail">查看详情</el-button>
            </div>
            
            <div class="space-y-6">
                <div v-for="social in socialStats" :key="social.label" class="group">
                    <div class="flex justify-between items-center mb-2">
                        <span class="text-sm font-bold text-slate-500 group-hover:text-slate-800 transition-colors">{{ social.label }}</span>
                        <span :class="`text-sm font-bold ${social.color}`">{{ social.value }}</span>
                    </div>
                    <div class="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
                        <div 
                            class="h-full rounded-full transition-all duration-1000" 
                            :class="social.bg"
                            :style="{ width: social.percent + '%' }"
                        ></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Social Detail Dialog -->
    <el-dialog v-model="socialDialogVisible" title="社交与心理互动详情" width="500px">
        <div class="space-y-6">
            <div class="p-4 bg-slate-50 rounded-2xl border border-slate-100">
                <p class="text-sm text-slate-500 mb-4">本项数据反映了平台内长者之间的互动活跃度及心理健康状态。高频的“心情树洞”发帖往往意味着长者有较强的表达欲望，而“心理互动”评论数则体现了社区的互助精神。</p>
                <div class="grid grid-cols-2 gap-4">
                    <div class="bg-white p-4 rounded-xl shadow-sm border border-slate-100">
                        <p class="text-[10px] font-bold text-slate-400 uppercase">累计总积分</p>
                        <p class="text-xl font-bold text-slate-900">{{ (socialStats[0].value || 0) }}</p>
                    </div>
                    <div class="bg-white p-4 rounded-xl shadow-sm border border-slate-100">
                        <p class="text-[10px] font-bold text-slate-400 uppercase">总发帖评论</p>
                        <p class="text-xl font-bold text-slate-900">{{ (parseInt(socialStats[2].value) + parseInt(socialStats[3].value)) || 0 }}</p>
                    </div>
                </div>
            </div>
            <div class="flex items-center gap-3 p-4 bg-primary/5 rounded-2xl border border-primary/10">
                <el-icon class="text-primary text-xl"><InfoFilled /></el-icon>
                <p class="text-xs text-primary font-medium">提示：系统每小时自动同步一次社交权重数据。</p>
            </div>
        </div>
        <template #footer>
            <el-button @click="socialDialogVisible = false">关闭</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, shallowRef } from 'vue';
import { 
  User, OfficeBuilding, Service, List, TrendCharts, InfoFilled
} from '@element-plus/icons-vue';
import * as echarts from 'echarts';
import request from '../../utils/request';

const currentTime = ref(new Date().toLocaleTimeString());
let timer = null;

const radarChartRef = ref(null);
const chartInstance = shallowRef(null);
const socialDialogVisible = ref(false);

const viewSocialDetail = () => {
    socialDialogVisible.value = true;
};

onMounted(() => {
    timer = setInterval(() => {
        currentTime.value = new Date().toLocaleTimeString();
    }, 1000);
    
    fetchStats();
    initRadarChart();
    window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
    if (timer) clearInterval(timer);
    if (chartInstance.value) chartInstance.value.dispose();
    window.removeEventListener('resize', handleResize);
});

const handleResize = () => {
    if (chartInstance.value) chartInstance.value.resize();
};

const initRadarChart = () => {
    if (!radarChartRef.value) return;
    
    chartInstance.value = echarts.init(radarChartRef.value);
    
    const option = {
        color: ['#4f46e5', '#38bdf8'],
        tooltip: {
            trigger: 'item',
            backgroundColor: 'rgba(255, 255, 255, 0.9)',
            borderColor: '#e2e8f0',
            textStyle: { color: '#1e293b' },
            padding: [10, 15],
            borderWidth: 1,
            borderRadius: 8,
            shadowColor: 'rgba(0, 0, 0, 0.1)',
            shadowBlur: 10
        },
        legend: {
            data: ['长者健康体征', '区域响应能力'],
            bottom: 0,
            icon: 'circle',
            itemWidth: 8,
            itemHeight: 8,
            textStyle: {
                color: '#64748b',
                fontSize: 12
            }
        },
        radar: {
            center: ['50%', '45%'],
            radius: '60%',
            indicator: [
                { name: '心率监测', max: 100 },
                { name: '急救呼叫', max: 100 },
                { name: '血压异常', max: 100 },
                { name: '跌倒预警', max: 100 },
                { name: '睡眠质量', max: 100 },
                { name: '运动活力', max: 100 }
            ],
            splitNumber: 4,
            axisName: {
                color: '#475569',
                backgroundColor: '#f8fafc',
                borderRadius: 3,
                padding: [3, 5]
            },
            splitArea: {
                areaStyle: {
                    color: ['rgba(250,250,250,0.3)', 'rgba(200,200,200,0.1)']
                }
            },
            axisLine: { lineStyle: { color: '#e2e8f0' } },
            splitLine: { lineStyle: { color: '#e2e8f0' } }
        },
        series: [
            {
                name: '平台综合指标',
                type: 'radar',
                symbolSize: 6,
                itemStyle: {
                    borderWidth: 2
                },
                lineStyle: {
                    width: 2
                },
                areaStyle: {
                    opacity: 0.15
                },
                data: [
                    {
                        value: [85, 90, 78, 95, 82, 70],
                        name: '长者健康体征',
                        areaStyle: { color: 'rgba(79, 70, 229, 0.2)' }
                    },
                    {
                        value: [95, 85, 92, 88, 75, 85],
                        name: '区域响应能力',
                        areaStyle: { color: 'rgba(56, 189, 248, 0.2)' }
                    }
                ]
            }
        ]
    };
    
    chartInstance.value.setOption(option);
};

const stats = ref([
    { label: '注册老人', value: '0', icon: User, bg: 'bg-blue-50', textColor: 'text-blue-500' },
    { label: '入驻机构', value: '0', icon: OfficeBuilding, bg: 'bg-indigo-50', textColor: 'text-indigo-500' },
    { label: '认证护理员', value: '0', icon: Service, bg: 'bg-success/10', textColor: 'text-success' },
    { label: '累计订单', value: '0', icon: List, bg: 'bg-amber-50', textColor: 'text-amber-500' }
]);

const socialStats = ref([
    { label: '累计签到获积分', value: '0', percent: 0, color: 'text-blue-500', bg: 'bg-blue-500' },
    { label: '商城兑换消耗积分', value: '0', percent: 0, color: 'text-orange-500', bg: 'bg-orange-500' },
    { label: '心情树洞发帖量', value: '0', percent: 0, color: 'text-pink-500', bg: 'bg-pink-500' },
    { label: '心理互动评论数', value: '0', percent: 0, color: 'text-indigo-500', bg: 'bg-indigo-500' }
]);

const fetchStats = async () => {
    try {
        const res = await request.get('/dashboard/stats');
        const data = res.data || {};
        
        stats.value[0].value = data.seniorCount?.toLocaleString() || '0';
        stats.value[1].value = data.orgCount?.toLocaleString() || '0';
        stats.value[2].value = data.carerCount?.toLocaleString() || '0';
        stats.value[3].value = data.orderCount?.toLocaleString() || '0';
        
        const inPoints = data.pointsIn || 0;
        const outPoints = data.pointsOut || 0;
        const totalP = Math.max(1, inPoints + outPoints);
        
        socialStats.value[0].value = inPoints.toLocaleString();
        socialStats.value[0].percent = Math.min(100, Math.round((inPoints / totalP) * 100));
        
        socialStats.value[1].value = outPoints.toLocaleString();
        socialStats.value[1].percent = Math.min(100, Math.round((outPoints / totalP) * 100));
        
        const tree = data.treeHoleCount || 0;
        const comment = data.treeHoleCommentCount || 0;
        const totalSocial = Math.max(1, tree + comment);
        
        socialStats.value[2].value = tree.toLocaleString();
        socialStats.value[2].percent = Math.min(100, Math.round((tree / totalSocial) * 100));
        
        socialStats.value[3].value = comment.toLocaleString();
        socialStats.value[3].percent = Math.min(100, Math.round((comment / totalSocial) * 100));
        
    } catch (e) {
        console.error(e);
    }
};
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.8s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
