<template>
  <el-container class="h-screen bg-[#fcfcfd] overflow-hidden">
    <!-- Sophisticated Enterprise Sidebar -->
    <el-aside width="270px" class="relative z-10 flex flex-col h-full bg-[#fdfdfd] border-r border-slate-200">
      <div class="p-10 flex flex-col items-center">
        <div class="w-16 h-16 bg-gradient-to-br from-indigo-500 to-primary rounded-[22px] flex items-center justify-center shadow-2xl shadow-indigo-100 mb-6 group hover:-rotate-3 transition-transform duration-500">
          <el-icon class="text-white text-3xl"><OfficeBuilding /></el-icon>
        </div>
        <div class="text-center">
           <h2 class="text-xl font-black text-slate-900 tracking-tight uppercase">机构中枢</h2>
           <p class="text-[9px] text-indigo-500 font-bold tracking-[0.2em] uppercase mt-1">Management Node</p>
        </div>
      </div>

      <nav class="flex-1 px-6 overflow-y-auto">
        <el-menu 
            :default-active="$route.path" 
            router 
            class="org-sidebar-menu border-none" 
            background-color="transparent" 
            text-color="#64748b" 
            active-text-color="#ffffff"
        >
          <el-menu-item index="/org/home" class="menu-item-org">
            <el-icon class="menu-icon"><DataBoard /></el-icon> <span>综合大盘</span>
          </el-menu-item>
          <el-menu-item index="/org/order" class="menu-item-org">
            <el-icon class="menu-icon"><List /></el-icon> <span>任务派发</span>
          </el-menu-item>
        </el-menu>
      </nav>

      <!-- Account switcher -->
      <div class="p-8 mt-auto border-t border-slate-100">
        <div class="flex items-center gap-4 group cursor-pointer" @click="logout">
           <el-avatar :size="48" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" class="border-2 border-slate-50 shadow-sm" />
           <div class="flex-1 min-w-0">
              <p class="text-sm font-bold text-slate-800 truncate group-hover:text-primary transition-colors">{{ userInfo.realName || '机构负责人' }}</p>
              <p class="text-[9px] text-slate-400 font-bold uppercase tracking-widest mt-0.5">Authorized Node</p>
           </div>
           <el-icon class="text-slate-300 group-hover:text-danger hover:scale-125 transition-all"><SwitchButton /></el-icon>
        </div>
      </div>
    </el-aside>

    <el-container direction="vertical" class="relative flex-col min-w-0">
      <!-- High-End Header -->
      <header class="h-20 flex items-center justify-between px-10 z-10 bg-white border-b border-slate-200 sticky top-0">
        <div class="flex items-center gap-6">
          <div class="flex items-center gap-3 bg-slate-50 px-4 py-1.5 rounded-xl border border-slate-100">
            <span class="text-indigo-600 text-[10px] font-black tracking-widest uppercase">授权认证机构</span>
          </div>
          <div class="w-px h-6 bg-slate-100"></div>
          <span class="text-sm font-bold text-slate-600 tracking-tight">组织看板 / {{ activeMenuName }}</span>
        </div>
        
        <div class="flex items-center gap-6">
          <el-badge hidden class="cursor-pointer" @click="handleNotification">
             <el-icon class="text-slate-400 text-2xl hover:text-primary transition-colors"><Bell /></el-icon>
          </el-badge>
        </div>
      </header>

      <!-- Main Area -->
      <el-main class="flex-1 overflow-y-auto p-12 bg-[#fcfcfd] relative">
        <div class="absolute inset-0 pointer-events-none opacity-[0.02] select-none z-0">
           <div class="absolute top-[20%] left-[20%] w-[400px] h-[400px] bg-indigo-600 rounded-full blur-[150px]"></div>
        </div>

        <div class="relative z-10 w-full max-w-[1300px] mx-auto min-h-full">
          <router-view v-slot="{ Component }">
            <transition name="org-slide" mode="out-in">
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
  OfficeBuilding, DataBoard, List, SwitchButton, Bell 
} from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'));

const activeMenuName = computed(() => {
  const path = route.path;
  if (path.includes('home')) return '数据中心';
  if (path.includes('order')) return '派单大厅';
  return '组织核心';
});

const logout = () => {
    ElMessageBox.confirm(
        '您正在执行组织后台退出操作。确认继续吗？',
        '安全操作确认',
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
.menu-item-org {
  height: 60px !important;
  margin-bottom: 8px;
  border-radius: 18px !important;
  font-weight: 700 !important;
  letter-spacing: 0.05em;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.menu-item-org:hover {
  background-color: rgba(79, 70, 229, 0.03) !important;
}

.menu-item-org.is-active {
  background: linear-gradient(135deg, var(--primary), #4338ca) !important;
  box-shadow: 0 10px 20px -5px rgba(79, 70, 229, 0.3);
  color: white !important;
}

.menu-icon {
  font-size: 18px !important;
  margin-right: 12px !important;
}

.org-slide-enter-active,
.org-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.org-slide-enter-from {
  opacity: 0;
  transform: translateY(15px);
}

.org-slide-leave-to {
  opacity: 0;
  transform: translateY(-15px);
}
</style>
