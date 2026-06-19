<template>
  <div class="space-y-8 animate-fade-in relative z-10">
    <!-- Status Card -->
    <div class="bg-primary rounded-3xl p-8 text-white premium-shadow relative overflow-hidden group">
       <div class="relative z-10">
           <div class="text-white/70 text-xs font-bold uppercase tracking-widest">当前工作状态</div>
           <div class="text-3xl font-bold mt-2 flex items-center gap-3">
               准备接单
               <div class="w-2.5 h-2.5 rounded-full bg-white animate-pulse"></div>
           </div>
           <div class="mt-8 grid grid-cols-2 gap-4">
               <div class="bg-white/10 backdrop-blur-md rounded-2xl p-4 border border-white/10">
                   <p class="text-[10px] text-white/60 font-bold uppercase tracking-widest">今日完成</p>
                   <p class="text-xl font-bold mt-1">{{ todayCompleted }} <span class="text-xs font-medium opacity-60">单</span></p>
               </div>
               <div class="bg-white/10 backdrop-blur-md rounded-2xl p-4 border border-white/10">
                   <p class="text-[10px] text-white/60 font-bold uppercase tracking-widest">本月评分</p>
                   <p class="text-xl font-bold mt-1">{{ monthlyRating }} <span class="text-xs font-medium opacity-60">分</span></p>
               </div>
           </div>
       </div>
       <!-- Abstract decor -->
       <div class="absolute -right-12 -top-12 w-48 h-48 bg-white/10 rounded-full blur-3xl group-hover:scale-110 transition-transform duration-700"></div>
    </div>
    
    <!-- Action Grid -->
    <div class="grid grid-cols-2 gap-6">
       <div 
         v-for="action in carerActions" 
         :key="action.label"
         @click="action.route ? $router.push(action.route) : handleActionClick(action)"
         class="bg-white p-6 rounded-[28px] border border-slate-100 hover:border-primary/20 hover:shadow-xl transition-all text-center group active:scale-95"
       >
           <div :class="['w-14', 'h-14', action.bg, action.color, 'rounded-2xl flex items-center justify-center mx-auto mb-4 transition-transform group-hover:rotate-12 text-2xl shadow-sm']">
               <el-icon><component :is="action.icon" /></el-icon>
           </div>
           <div class="font-bold text-slate-800 text-sm tracking-wide">{{ action.label }}</div>
           <div class="text-[9px] text-slate-400 font-bold uppercase tracking-tighter mt-1">{{ action.desc }}</div>
       </div>
    </div>

    <!-- Quick Tips -->
    <div class="p-6 rounded-3xl bg-amber-50 border border-amber-100 flex items-start gap-4 shadow-sm">
        <el-icon class="text-amber-500 text-xl mt-0.5"><WarningFilled /></el-icon>
        <div>
            <p class="text-sm font-bold text-amber-900">接单小贴士</p>
            <p class="text-xs text-amber-700 mt-1 leading-relaxed">请确保手机实时开启推送通知，多一份关注，多一份收入。</p>
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import request from '../../utils/request';
import { 
  List, Wallet, WarningFilled, Promotion, Money 
} from '@element-plus/icons-vue';

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
const todayCompleted = ref(0);
const monthlyRating = ref('5.0');

const carerActions = [
  { label: '接单大厅', desc: '发现新工单', icon: Promotion, route: '/app/order', bg: 'bg-primary/10', color: 'text-primary' },
  { label: '收入结算', desc: '查看薪资', icon: Money, bg: 'bg-emerald-50', color: 'text-emerald-500' }
];

const handleActionClick = (action) => {
    ElMessage.info(`${action.label}功能模块正在建设中...`);
};

const fetchData = async () => {
    if(!userInfo.id) return;
    try {
        const res = await request.get(`/dashboard/carer?carerId=${userInfo.id}`);
        if(res.code === 200 && res.data) {
            todayCompleted.value = res.data.todayCompleted || 0;
            monthlyRating.value = res.data.monthlyRating || '5.0';
        }
    } catch(e) {
        console.error("Failed to fetch carer stats:", e);
    }
};

onMounted(() => {
    fetchData();
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
