<template>
  <div class="space-y-10 animate-fade-in relative z-10">
    <!-- Welcoming Hero -->
    <div class="flex justify-between items-center bg-white/40 backdrop-blur-xl p-10 rounded-3xl border border-white/60 premium-shadow">
        <div>
            <h2 class="text-3xl font-bold text-slate-800 tracking-tight">亲爱的 {{ userInfo.realName }}，欢迎回来</h2>
            <p class="text-slate-500 mt-2 font-medium">今天也是充满活力的一天，让我们一起关注健康与生活。</p>
        </div>
        <div class="hidden md:block">
            <el-button type="primary" bg class="rounded-2xl h-12 px-8 shadow-lg shadow-primary/20" @click="$router.push('/web/order')">
                立即预约服务
            </el-button>
        </div>
    </div>
    
    <!-- Quick Actions Grid -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
      <div 
        v-for="action in actions" 
        :key="action.title"
        class="group cursor-pointer bg-white p-8 rounded-3xl border border-slate-100 hover:border-primary/20 hover:shadow-2xl transition-all relative overflow-hidden"
        @click="$router.push(action.route)"
      >
        <div class="flex items-center gap-6 relative z-10">
          <div :class="['p-5', action.bg, action.textColor, 'rounded-2xl items-center justify-center flex shadow-sm transition-transform group-hover:scale-110 duration-500 text-3xl shadow-sm']">
            <el-icon><component :is="action.icon" /></el-icon>
          </div>
          <div>
            <div class="text-slate-400 text-xs font-bold uppercase tracking-widest">{{ action.subtitle }}</div>
            <div class="text-xl font-bold text-slate-800 mt-1">{{ action.title }}</div>
          </div>
        </div>
        <!-- Abstract shape decor -->
        <div class="absolute -right-4 -bottom-4 w-24 h-24 bg-slate-50 rounded-full group-hover:bg-primary/5 transition-colors"></div>
      </div>
    </div>

    <!-- Health Articles Section -->
    <div class="glass-card p-10 rounded-[32px] border border-white/40">
       <div class="flex items-center justify-between mb-8">
           <h3 class="text-2xl font-bold text-slate-800 flex items-center gap-3">
               <el-icon class="text-primary"><Reading /></el-icon>
               健康科普推荐
           </h3>
           <el-button link class="text-primary font-bold" @click="$router.push('/web/activity')">查看更多专题</el-button>
       </div>

       <el-skeleton :loading="loading" animated>
         <template #template>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              <el-skeleton-item variant="p" v-for="i in 3" :key="i" class="w-full h-64 rounded-3xl" />
            </div>
         </template>
         <template #default>
            <el-empty v-if="articles.length === 0" description="暂无内容"></el-empty>
            <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                <div 
                    v-for="(item, index) in articles" 
                    :key="item.id" 
                    class="bg-white p-6 rounded-3xl border border-slate-100 hover:border-primary/20 hover:shadow-xl transition-all flex flex-col h-full group"
                >
                    <div class="w-full h-40 bg-slate-100 rounded-2xl mb-6 overflow-hidden relative">
                        <!-- Placeholder for article image -->
                        <div class="absolute inset-0 bg-gradient-to-tr from-primary/10 to-transparent flex items-center justify-center">
                            <el-icon class="text-4xl text-white/50"><PictureFilled /></el-icon>
                        </div>
                    </div>
                    <h4 class="font-bold text-lg text-slate-800 mb-3 group-hover:text-primary transition-colors cursor-pointer line-clamp-1">{{ item.title }}</h4>
                    <p class="text-slate-500 text-sm line-clamp-2 leading-relaxed mb-6">{{ item.content }}</p>
                    
                    <div class="mt-auto pt-6 border-t border-slate-50 flex justify-between items-center text-xs text-slate-400">
                        <div class="flex items-center gap-2 font-medium">
                            <el-icon><Calendar /></el-icon>
                            <span>{{ item.publishTime }}</span>
                        </div>
                        <div class="flex gap-4">
                            <span class="flex items-center gap-1"><el-icon><View /></el-icon> {{ item.views }}</span>
                            <span class="flex items-center gap-1"><el-icon><Star /></el-icon> {{ item.likes }}</span>
                        </div>
                    </div>
                </div>
            </div>
         </template>
       </el-skeleton>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '../../utils/request';
import { 
  Service, FirstAidKit, Ticket, Reading, Calendar, View, Star, PictureFilled 
} from '@element-plus/icons-vue';

const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'));
const articles = ref([]);
const loading = ref(true);

const actions = [
  { title: '生活照料预约', subtitle: '快捷预约', icon: Service, route: '/web/order', bg: 'bg-indigo-50', textColor: 'text-indigo-600' },
  { title: '健康数据上传', subtitle: '数据监测', icon: FirstAidKit, route: '/web/health', bg: 'bg-emerald-50', textColor: 'text-emerald-600' },
  { title: '最新社区活动', subtitle: '社交心理', icon: Ticket, route: '/web/activity', bg: 'bg-orange-50', textColor: 'text-orange-600' }
];

const fetchArticles = async () => {
    loading.value = true;
    try {
        const res = await request.get('/article/list');
        articles.value = res.data || [];
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    fetchArticles();
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

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
