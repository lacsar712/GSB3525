<template>
  <div class="h-screen w-full bg-slate-100 flex justify-center overflow-hidden">
    <div class="w-full max-w-[480px] bg-[#fafafa] relative flex flex-col h-full shadow-[0_0_100px_rgba(0,0,0,0.1)]">
      <!-- Dynamic Header -->
      <header class="h-20 shrink-0 flex items-center justify-between px-6 bg-white/90 backdrop-blur-md border-b border-slate-100 z-50">
      <div class="flex items-center gap-4">
        <div class="w-12 h-12 bg-gradient-to-br from-sky-400 to-primary rounded-2xl flex items-center justify-center shadow-xl shadow-sky-100">
          <el-icon class="text-white text-xl"><Service /></el-icon>
        </div>
        <div>
           <h1 class="text-lg font-black text-slate-900 tracking-tight leading-none">服务站</h1>
           <p class="text-[9px] text-sky-500 font-black uppercase tracking-[0.2em] mt-1.5">Carer Service</p>
        </div>
      </div>
      
      <div class="flex items-center gap-4">
         <div class="relative cursor-pointer" @click="logout">
            <el-avatar :size="48" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" class="border-2 border-white shadow-xl" />
            <div class="absolute -bottom-1 -right-1 w-5 h-5 bg-white rounded-full flex items-center justify-center shadow-md">
               <el-icon class="text-danger text-[10px]"><SwitchButton /></el-icon>
            </div>
         </div>
      </div>
    </header>
    
      <!-- Fluid Content Area -->
      <main class="flex-1 overflow-y-auto p-6 pb-32 relative bg-transparent">
        <div class="absolute inset-0 pointer-events-none opacity-[0.03] overflow-hidden -z-10">
           <div class="absolute top-0 right-0 w-64 h-64 bg-primary rounded-full blur-[100px]"></div>
           <div class="absolute bottom-20 left-0 w-64 h-64 bg-sky-400 rounded-full blur-[100px]"></div>
        </div>

          <router-view v-slot="{ Component }">
            <transition name="app-slide" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
      </main>

      <!-- Master Bottom Navigation -->
      <div class="absolute bottom-6 left-1/2 -translate-x-1/2 flex items-center p-2.5 bg-slate-900/90 backdrop-blur-2xl rounded-[28px] shadow-2xl border border-white/10 z-50 w-[calc(100%-48px)]">
        <button 
          v-for="item in navItems" 
          :key="item.route"
          @click="$router.push(item.route)" 
          :class="[
            'flex-1 flex flex-col items-center justify-center gap-1.5 py-3 rounded-[20px] transition-all duration-500',
            $route.path === item.route ? 'bg-gradient-to-br from-primary to-indigo-600 text-white shadow-xl shadow-primary/30 scale-105' : 'text-slate-500 hover:text-slate-300'
          ]"
        >
          <el-icon :size="24" class="transition-transform group-active:scale-95"><component :is="item.icon" /></el-icon>
          <span class="text-[10px] font-black uppercase tracking-[0.1em]">{{ item.label }}</span>
        </button>
    </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessageBox } from 'element-plus';
import { 
  HomeFilled, List, Service, SwitchButton 
} from '@element-plus/icons-vue';

const router = useRouter();
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'));

const navItems = [
  { label: '服务大厅', route: '/app/home', icon: HomeFilled },
  { label: '我的任务', route: '/app/order', icon: List }
];

const logout = () => {
    ElMessageBox.confirm(
        '确认登出当前的护理工作站？未保存的待办项可能会丢失。',
        '安全离岗确认',
        { 
          confirmButtonText: '确定离岗', 
          cancelButtonText: '继续工作', 
          type: 'info',
          customClass: 'ultra-msg-box'
        }
    ).then(() => {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        router.push('/login');
    }).catch(() => {});
};
</script>

<style scoped>
.app-slide-enter-active,
.app-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.app-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.app-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
