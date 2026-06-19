<template>
  <el-container class="h-screen bg-[#0f1115] overflow-hidden">
    <!-- Ultra Premium Sidebar -->
    <el-aside width="280px" class="relative z-10 flex flex-col h-full bg-[#0a0c10] border-r border-white/5 shadow-[20px_0_50px_rgba(0,0,0,0.5)]">
      <div class="p-10 flex flex-col items-center">
        <div class="w-20 h-20 bg-gradient-to-br from-primary to-indigo-600 rounded-[24px] flex items-center justify-center shadow-2xl shadow-primary/20 mb-6 group hover:rotate-6 transition-transform duration-500">
          <el-icon class="text-white text-4xl"><Monitor /></el-icon>
        </div>
        <div class="text-center">
           <h2 class="text-xl font-black text-white tracking-widest uppercase">和悦监控</h2>
           <p class="text-[9px] text-slate-500 font-bold tracking-[0.3em] uppercase mt-1">Platform Control Center</p>
        </div>
      </div>

      <nav class="flex-1 px-6 overflow-y-auto">
        <el-menu 
            :default-active="$route.path" 
            router 
            class="ultra-sidebar-menu border-none" 
            background-color="transparent" 
            text-color="#64748b" 
            active-text-color="#ffffff"
        >
          <el-menu-item index="/platform/home" class="menu-item-premium">
            <el-icon class="menu-icon"><Odometer /></el-icon> <span>控制台大屏</span>
          </el-menu-item>
          <el-menu-item index="/platform/users" class="menu-item-premium">
            <el-icon class="menu-icon"><User /></el-icon> <span>准入权限审计</span>
          </el-menu-item>
          <el-menu-item index="/platform/complaints" class="menu-item-premium">
            <el-icon class="menu-icon"><Notification /></el-icon> <span>仲裁投诉中心</span>
          </el-menu-item>
          <el-menu-item index="/platform/logs" class="menu-item-premium">
            <el-icon class="menu-icon"><Document /></el-icon> <span>系统操作日志</span>
          </el-menu-item>
          <el-menu-item index="/platform/articles" class="menu-item-premium">
            <el-icon class="menu-icon"><Reading /></el-icon> <span>科普内容运营</span>
          </el-menu-item>
        </el-menu>
      </nav>

      <!-- Account / Logout -->
      <div class="p-8 mt-auto border-t border-white/5 bg-white/[0.02]">
        <div class="flex items-center gap-4 group cursor-pointer" @click="logout">
           <div class="relative">
              <el-avatar :size="48" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" class="border-2 border-primary/20 shadow-lg" />
              <div class="absolute bottom-0 right-0 w-3 h-3 bg-success rounded-full border-2 border-[#0a0c10]"></div>
           </div>
           <div class="flex-1 min-w-0">
              <p class="text-sm font-bold text-white truncate group-hover:text-primary transition-colors">{{ userInfo.realName || '超级管理员' }}</p>
              <p class="text-[10px] text-slate-500 font-bold uppercase tracking-tighter">System Superuser</p>
           </div>
           <el-icon class="text-slate-600 group-hover:text-danger hover:scale-125 transition-all"><SwitchButton /></el-icon>
        </div>
      </div>
    </el-aside>

    <el-container direction="vertical" class="relative flex-col min-w-0 bg-[#f8fafc]">
      <!-- Refined Header -->
      <header class="h-20 flex items-center justify-between px-10 z-10 bg-white/90 backdrop-blur-xl border-b border-slate-200/60 sticky top-0">
        <div class="flex items-center gap-4">
          <div class="bg-slate-100 p-2 rounded-xl">
             <el-icon class="text-slate-400 text-lg"><Grid /></el-icon>
          </div>
          <el-breadcrumb separator="/" class="text-sm font-medium">
            <el-breadcrumb-item class="opacity-50">系统首页</el-breadcrumb-item>
            <el-breadcrumb-item><span class="text-slate-900 font-bold tracking-tight">{{ activeMenuName }}</span></el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="flex items-center gap-8">
          <div class="flex items-center gap-4 bg-slate-50 px-5 py-2 rounded-2xl border border-slate-200/40">
            <div class="w-1.5 h-1.5 rounded-full bg-success animate-ping"></div>
            <span class="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em]">Core Service Active</span>
          </div>
          <div class="flex items-center gap-2 cursor-pointer hover:bg-slate-100 p-2 rounded-xl transition-colors" @click="handleNotification">
             <el-badge hidden class="cursor-pointer" @click="handleNotification">
                <el-icon class="text-slate-500 text-2xl"><Bell /></el-icon>
             </el-badge>
          </div>
        </div>
      </header>

      <!-- Main Master Area -->
      <el-main class="flex-1 overflow-y-auto p-12 bg-[#f8fafc] relative">
        <!-- Visual Decor -->
        <div class="absolute inset-0 pointer-events-none opacity-[0.03] select-none z-0">
           <div class="absolute top-0 right-0 w-[600px] h-[600px] bg-primary rounded-full blur-[200px]"></div>
           <div class="absolute bottom-0 left-0 w-[400px] h-[400px] bg-indigo-500 rounded-full blur-[150px]"></div>
        </div>

        <div class="relative z-10 w-full max-w-[1400px] mx-auto min-h-full">
          <router-view v-slot="{ Component }">
            <transition name="master-fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessageBox, ElMessage } from 'element-plus';
import { 
  Monitor, Odometer, User, Notification, Document, Reading, SwitchButton, Bell, Grid 
} from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'));

const activeMenuName = computed(() => {
  const path = route.path;
  if (path.includes('home')) return '数据监控看板';
  if (path.includes('users')) return '准入权限名单';
  if (path.includes('complaints')) return '仲裁纠纷处理';
  if (path.includes('logs')) return '核心审计记录';
  if (path.includes('articles')) return '知识科普中心';
  return '监控中心';
});

const logout = () => {
    ElMessageBox.confirm(
        '您正在执行登出操作，系统将清除当前对话凭证。',
        '安全会话提示',
        { 
          confirmButtonText: '确定登出', 
          cancelButtonText: '取消', 
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
</script>

<style scoped>
.menu-item-premium {
  height: 64px !important;
  margin-bottom: 8px;
  border-radius: 20px !important;
  font-weight: 700 !important;
  letter-spacing: 0.05em;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.menu-item-premium:hover {
  background-color: rgba(255, 255, 255, 0.03) !important;
}

.menu-item-premium.is-active {
  background: linear-gradient(135deg, var(--primary), #4338ca) !important;
  box-shadow: 0 12px 24px -6px rgba(79, 70, 229, 0.4);
  color: white !important;
}

.menu-icon {
  font-size: 20px !important;
  margin-right: 14px !important;
  transition: transform 0.4s ease;
}

.menu-item-premium:hover .menu-icon {
  transform: scale(1.2);
}

.master-fade-enter-active,
.master-fade-leave-active {
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.master-fade-enter-from {
  opacity: 0;
  transform: translateY(15px);
}

.master-fade-leave-to {
  opacity: 0;
  transform: translateY(-15px);
}
</style>
