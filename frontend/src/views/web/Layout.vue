<template>
  <el-container class="h-screen bg-[#fdfdfd] overflow-hidden">
    <!-- Sophisticated Senior Sidebar -->
    <el-aside width="280px" class="relative z-10 flex flex-col h-full bg-white border-r border-slate-100 shadow-[10px_0_30px_rgba(0,0,0,0.02)]">
      <div class="p-10 flex flex-col items-center">
        <div class="w-16 h-16 bg-gradient-to-br from-orange-400 to-primary rounded-[22px] flex items-center justify-center shadow-2xl shadow-orange-200 mb-6 transition-all hover:scale-110 duration-500">
          <el-icon class="text-white text-3xl"><HomeFilled /></el-icon>
        </div>
        <div class="text-center">
           <h2 class="text-xl font-black text-slate-800 tracking-tight">和悦康养</h2>
           <p class="text-[10px] text-orange-400 font-bold tracking-[0.2em] uppercase mt-1">Senior Care Portal</p>
        </div>
      </div>

      <nav class="flex-1 px-6 overflow-y-auto">
        <el-menu 
            :default-active="$route.path" 
            router 
            class="senior-sidebar-menu border-none" 
            background-color="transparent" 
            text-color="#64748b" 
            active-text-color="#ffffff"
        >
          <el-menu-item index="/web/home" class="menu-item-senior">
            <el-icon class="menu-icon"><DataBoard /></el-icon> <span>首页概览</span>
          </el-menu-item>
          <el-menu-item index="/web/order" class="menu-item-senior">
            <el-icon class="menu-icon"><List /></el-icon> <span>服务预约</span>
          </el-menu-item>
          <el-menu-item index="/web/health" class="menu-item-senior">
            <el-icon class="menu-icon"><TrendCharts /></el-icon> <span>健康看板</span>
          </el-menu-item>
          <el-menu-item index="/web/activity" class="menu-item-senior">
            <el-icon class="menu-icon"><Calendar /></el-icon> <span>精彩活动</span>
          </el-menu-item>
          <el-menu-item index="/web/mall" class="menu-item-senior">
            <el-icon class="menu-icon"><ShoppingTrolley /></el-icon> <span>积分商城</span>
          </el-menu-item>
          <el-menu-item index="/web/treehole" class="menu-item-senior">
            <el-icon class="menu-icon"><ChatDotRound /></el-icon> <span>心情树洞</span>
          </el-menu-item>
        </el-menu>
      </nav>

      <!-- Account / Logout -->
      <div class="p-8 mt-auto border-t border-slate-100 bg-slate-50/30">
        <div class="flex items-center gap-4 group cursor-pointer" @click="logout">
           <el-avatar :size="48" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" class="border-2 border-orange-100 shadow-sm" />
           <div class="flex-1 min-w-0">
              <p class="text-sm font-bold text-slate-800 truncate group-hover:text-primary transition-colors">{{ userInfo.realName || '尊敬的长者' }}</p>
              <div class="flex items-center gap-1">
                 <el-icon class="text-orange-400 text-xs"><Coin /></el-icon>
                 <span class="text-[10px] text-orange-400 font-bold uppercase tracking-tighter">{{ userInfo.points || 0 }} 积分</span>
              </div>
           </div>
           <el-icon class="text-slate-300 group-hover:text-danger hover:scale-125 transition-all"><SwitchButton /></el-icon>
        </div>
      </div>
    </el-aside>

    <el-container direction="vertical" class="relative flex-col min-w-0 bg-[#fdfdfd]">
      <!-- Warm Glass Header -->
      <header class="h-20 flex items-center justify-between px-10 z-10 bg-white/70 backdrop-blur-xl border-b border-slate-100 sticky top-0">
        <div class="flex items-center gap-4">
          <el-breadcrumb separator="/" class="text-sm font-medium">
            <el-breadcrumb-item class="opacity-40">和悦智慧中心</el-breadcrumb-item>
            <el-breadcrumb-item><span class="text-slate-900 font-bold tracking-tight">{{ activeMenuName }}</span></el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="flex items-center gap-6">
          <div class="flex items-center gap-3 bg-white px-5 py-2 rounded-2xl border border-slate-100 shadow-sm transition-all hover:shadow-md cursor-help">
            <div class="w-1.5 h-1.5 rounded-full bg-success"></div>
            <span class="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em]">服务在线中</span>
          </div>
          <el-badge hidden class="cursor-pointer" @click="handleNotification">
             <el-icon class="text-slate-400 text-2xl hover:text-primary transition-colors"><Bell /></el-icon>
          </el-badge>
        </div>
      </header>

      <!-- Main Content Area -->
      <el-main class="flex-1 overflow-y-auto p-12 relative bg-transparent">
        <div class="absolute inset-0 pointer-events-none opacity-[0.05] select-none z-0">
           <div class="absolute top-[10%] right-[10%] w-[500px] h-[500px] bg-orange-200 rounded-full blur-[150px]"></div>
           <div class="absolute bottom-[10%] left-[10%] w-[300px] h-[300px] bg-primary rounded-full blur-[120px]"></div>
        </div>

        <div class="relative z-10 w-full max-w-[1200px] mx-auto min-h-full">
          <router-view v-slot="{ Component }">
            <transition name="web-fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessageBox, ElMessage } from 'element-plus';
import request from '../../utils/request';
import { 
  HomeFilled, DataBoard, List, TrendCharts, Calendar, ShoppingTrolley, ChatDotRound, SwitchButton, Bell, Coin
} from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'));

const activeMenuName = computed(() => {
  const path = route.path;
  if (path.includes('home')) return '我的和悦首页';
  if (path.includes('order')) return '服务预约下单';
  if (path.includes('health')) return '每日健康数据';
  if (path.includes('activity')) return '社区精彩活动';
  if (path.includes('mall')) return '积分兑换商城';
  if (path.includes('treehole')) return '匿名情感树洞';
  return '欢迎回来';
});

const logout = () => {
    ElMessageBox.confirm(
        '确定要退出当前账号并返回登录页吗？',
        '登出提示',
        { 
          confirmButtonText: '确定退出', 
          cancelButtonText: '再看看', 
          type: 'info',
          customClass: 'ultra-msg-box'
        }
    ).then(() => {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        router.push('/login');
    }).catch(() => {});
};

const handleNotification = () => {
    ElMessage.info('暂无新通知');
};

onMounted(async () => {
    try {
        const res = await request.get('/user/info');
        if (res.data) {
            userInfo.value = res.data;
            localStorage.setItem('userInfo', JSON.stringify(res.data));
        }
    } catch(e) {}
});
</script>

<style scoped>
.menu-item-senior {
  height: 60px !important;
  margin-bottom: 8px;
  border-radius: 18px !important;
  font-weight: 700 !important;
  letter-spacing: 0.025em;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.menu-item-senior:hover {
  background-color: rgba(249, 115, 22, 0.03) !important;
  color: var(--primary) !important;
}

.menu-item-senior.is-active {
  background: linear-gradient(135deg, var(--primary), #ef4444) !important;
  box-shadow: 0 10px 20px -5px rgba(249, 115, 22, 0.3);
  color: white !important;
}

.menu-icon {
  font-size: 18px !important;
  margin-right: 12px !important;
}

.web-fade-enter-active,
.web-fade-leave-active {
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.web-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.web-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
