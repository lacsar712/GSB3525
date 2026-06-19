<template>
  <div class="space-y-10 animate-fade-in relative z-10">
    <!-- Professional Header -->
    <div class="flex justify-between items-end pb-4 border-b border-slate-200/60">
        <div>
            <h2 class="text-3xl font-bold text-slate-800 tracking-tight">机构运营看板</h2>
            <p class="text-slate-500 mt-2 font-medium">实时监控机构运营指标与服务效能</p>
        </div>
        <div class="flex items-center gap-4">
            <el-button plain rounded class="shadow-sm" @click="exportReport" :loading="exporting">导出报表</el-button>
            <el-button type="primary" rounded class="shadow-lg shadow-indigo-200" @click="fetchData" :loading="refreshing">刷新数据</el-button>
        </div>
    </div>

    <!-- Metrics Grid -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
        <div v-for="metric in metrics" :key="metric.label" class="bg-white p-8 rounded-3xl border border-slate-100 hover:border-indigo-200 hover:shadow-2xl transition-all group relative overflow-hidden">
            <div class="relative z-10 flex flex-col items-center">
                <div :class="['w-14', 'h-14', metric.bg, metric.color, 'rounded-2xl flex items-center justify-center mb-6 transition-transform group-hover:scale-110 duration-500 shadow-sm text-2xl']">
                    <el-icon><component :is="metric.icon" /></el-icon>
                </div>
                <div class="text-slate-400 text-[10px] font-bold uppercase tracking-widest mb-1">{{ metric.label }}</div>
                <div :class="`text-3xl font-bold ${metric.color}`">{{ metric.value }}</div>
            </div>
            <!-- Decorative bg pattern -->
            <div class="absolute -right-2 -bottom-2 opacity-[0.03] group-hover:opacity-[0.05] transition-opacity">
                <el-icon class="text-8xl"><component :is="metric.icon" /></el-icon>
            </div>
        </div>
    </div>

    <!-- Secondary Insights Section (Placeholder) -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div class="lg:col-span-2 glass-card p-10 rounded-[40px] border border-white/40 min-h-[400px] flex flex-col items-center justify-center group bg-indigo-50/10">
            <div class="w-20 h-20 bg-indigo-100 rounded-full flex items-center justify-center mb-6 group-hover:bg-indigo-200 transition-colors">
                <el-icon class="text-4xl text-indigo-600"><TrendCharts /></el-icon>
            </div>
            <h3 class="text-xl font-bold text-slate-800">服务流量趋势分析</h3>
            <p class="text-slate-400 text-sm mt-2 font-medium">近 7 日订单与接单趋势概览</p>
            <div class="w-full h-full mt-8 min-h-[250px] relative z-20" ref="chartRef"></div>
        </div>

        <div class="bg-white p-10 rounded-[40px] border border-slate-100 shadow-xl shadow-slate-200/50">
            <h3 class="text-lg font-bold text-slate-800 mb-8 flex items-center gap-3">
                <el-icon class="text-amber-500"><Warning /></el-icon>
                待办事项
            </h3>
            <div class="space-y-6">
                <div v-for="todo in todos" :key="todo.title" class="flex items-center gap-4 group cursor-pointer">
                    <div :class="`w-2 h-2 rounded-full ${todo.color}`"></div>
                    <div class="flex-1 border-b border-slate-50 pb-4 group-hover:border-indigo-100 transition-colors">
                        <p class="text-sm font-bold text-slate-700">{{ todo.title }}</p>
                        <p class="text-[10px] text-slate-400 font-medium uppercase mt-1">{{ todo.time }}</p>
                    </div>
                    <el-icon class="text-slate-300 group-hover:text-indigo-600"><ArrowRight /></el-icon>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import request from '../../utils/request';
import * as echarts from 'echarts';
import { 
  Collection, CircleCheck, UserFilled, ChatLineRound, TrendCharts, Warning, ArrowRight 
} from '@element-plus/icons-vue';

const refreshing = ref(false);
const exporting = ref(false);

const metrics = ref([
    { label: '今日待派单', value: '0', icon: Collection, bg: 'bg-indigo-50', color: 'text-indigo-600' },
    { label: '正在服务中', value: '0', icon: CircleCheck, bg: 'bg-emerald-50', color: 'text-emerald-600' },
    { label: '在岗护理员', value: '0', icon: UserFilled, bg: 'bg-sky-50', color: 'text-sky-600' },
    { label: '客户满意度', value: '100%', icon: ChatLineRound, bg: 'bg-amber-50', color: 'text-amber-600' }
]);

const todos = [
    { title: '紧急派单申请 (2条)', time: '10分钟前', color: 'bg-red-500' },
    { title: '护理员入驻审核 (3人)', time: '20分钟前', color: 'bg-amber-500' },
    { title: '本周财务报表待审', time: '1小时前', color: 'bg-indigo-500' },
    { title: '客户回访计划', time: '2小时前', color: 'bg-slate-300' }
];

const fetchData = async () => {
    refreshing.value = true;
    try {
        const res = await request.get('/dashboard/org');
        const data = res.data || {};
        metrics.value[0].value = (data.todayPendingOrders || 0).toString();
        metrics.value[1].value = (data.inProgressOrders || 0).toString();
        metrics.value[2].value = (data.activeCarers || 0).toString();
        metrics.value[3].value = (data.customerSatisfaction || 100) + '%';
        if (event) {
            ElMessage.success('数据已刷新');
        }
    } catch(e) {
        console.error(e);
    } finally {
        refreshing.value = false;
    }
};

const exportReport = () => {
    exporting.value = true;
    
    // Prepare CSV data
    let lines = ["指标名称,数值"];
    metrics.value.forEach(m => {
        lines.push(`${m.label},${m.value}`);
    });
    
    lines.push("");
    lines.push("待办事项,产生时间");
    todos.forEach(t => {
        lines.push(`${t.title},${t.time}`);
    });
    
    // Use BOM for Excel UTF-8 compatibility
    const csvString = "\uFEFF" + lines.join("\n");
    const blob = new Blob([csvString], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    
    const link = document.createElement("a");
    link.setAttribute("href", url);
    link.setAttribute("download", `机构运营报表_${new Date().toLocaleDateString()}.csv`);
    document.body.appendChild(link);
    
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
    
    exporting.value = false;
    ElMessage.success('报表导出成功，请检查浏览器下载列表');
};

const chartRef = ref(null);
let chartInstance = null;

const initChart = () => {
    if (!chartRef.value) return;
    chartInstance = echarts.init(chartRef.value);
    const option = {
        tooltip: { trigger: 'axis', backgroundColor: 'rgba(255, 255, 255, 0.9)', borderColor: '#e2e8f0', textStyle: { color: '#1e293b' } },
        grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
        xAxis: { 
            type: 'category', 
            boundaryGap: false, 
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
            axisLine: { lineStyle: { color: '#cbd5e1' } },
            axisLabel: { color: '#64748b', fontWeight: 'bold' }
        },
        yAxis: { 
            type: 'value',
            splitLine: { lineStyle: { type: 'dashed', color: '#f1f5f9' } },
            axisLabel: { color: '#64748b' }
        },
        series: [
            {
                name: '服务订单量',
                type: 'line',
                smooth: true,
                lineStyle: { width: 4, color: '#6366f1' },
                itemStyle: { color: '#6366f1', borderWidth: 3, borderColor: '#fff' },
                symbolSize: 8,
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: 'rgba(99, 102, 241, 0.4)' },
                        { offset: 1, color: 'rgba(99, 102, 241, 0)' }
                    ])
                },
                data: [120, 182, 191, 234, 290, 330, 310]
            }
        ]
    };
    chartInstance.setOption(option);
};

onMounted(() => {
    fetchData();
    nextTick(() => {
        setTimeout(initChart, 300);
    });
    window.addEventListener('resize', handleResize);
});

const handleResize = () => {
    chartInstance?.resize();
};

onBeforeUnmount(() => {
    window.removeEventListener('resize', handleResize);
    chartInstance?.dispose();
});
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.8s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(15px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
