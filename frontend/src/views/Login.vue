<template>
  <div class="min-h-screen relative flex items-center justify-center overflow-hidden bg-[#0a0c10]">
    <!-- Dynamic Ambient Background -->
    <div class="absolute inset-0 z-0">
      <div class="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-primary/20 rounded-full blur-[120px] animate-pulse"></div>
      <div class="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-indigo-900/30 rounded-full blur-[120px] animate-pulse" style="animation-delay: 2s;"></div>
      <img 
        src="/login_background_abstract.png" 
        class="w-full h-full object-cover opacity-20 mix-blend-overlay" 
        alt="Background" 
      />
    </div>

    <!-- Login Container -->
    <div class="relative z-10 w-full max-w-[480px] px-6 py-12">
      <div class="premium-login-card p-12 rounded-[32px] border border-white/10 backdrop-blur-3xl bg-white/[0.03] shadow-[0_32px_64px_-16px_rgba(0,0,0,0.6)] animate-slide-up">
        <div class="text-center mb-12">
          <div class="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-primary to-indigo-600 rounded-2xl mb-6 shadow-xl shadow-primary/20 transition-transform hover:scale-105 duration-500">
            <el-icon class="text-white text-4xl"><HomeFilled /></el-icon>
          </div>
          <h1 class="text-4xl font-extrabold tracking-tight text-white mb-3">
            和悦康养
          </h1>
          <p class="text-slate-400 font-medium tracking-wide">
            智慧守护 · 尊严养老服务系统
          </p>
        </div>

        <el-form :model="loginForm" :rules="rules" ref="loginFormRef" @submit.prevent="handleLogin" class="space-y-6">
          <el-form-item prop="username">
            <div class="w-full group">
              <label class="block text-[10px] font-bold uppercase tracking-[0.2em] text-slate-500 mb-2 ml-1 transition-colors group-focus-within:text-primary">User Access</label>
              <el-input 
                v-model="loginForm.username" 
                placeholder="账号名称" 
                size="large"
                class="ultra-input"
              >
                <template #prefix>
                  <el-icon class="text-slate-500"><User /></el-icon>
                </template>
              </el-input>
            </div>
          </el-form-item>
          
          <el-form-item prop="password">
            <div class="w-full group">
              <label class="block text-[10px] font-bold uppercase tracking-[0.2em] text-slate-500 mb-2 ml-1 transition-colors group-focus-within:text-primary">Security Key</label>
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="安全密码" 
                size="large"
                show-password
                class="ultra-input"
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <el-icon class="text-slate-500"><Lock /></el-icon>
                </template>
              </el-input>
            </div>
          </el-form-item>

          <div class="pt-6">
            <el-button 
              type="primary" 
              class="w-full h-14 text-lg font-bold rounded-2xl transition-all duration-500 hover:shadow-[0_0_30px_rgba(79,70,229,0.4)] hover:-translate-y-1 active:scale-95 border-none bg-gradient-to-r from-primary to-indigo-600" 
              :loading="loading"
              @click="handleLogin"
            >
              进 入 系 统
            </el-button>
          </div>
        </el-form>
      </div>
      
      <div class="mt-12 text-center animate-fade-in" style="animation-delay: 0.5s;">
        <p class="text-slate-500 text-sm font-medium">
          © 2026 和悦智慧养老 · 科技连接温情
        </p>
        <div class="mt-4 flex justify-center gap-6">
            <span class="w-1.5 h-1.5 rounded-full bg-slate-800"></span>
            <span class="w-1.5 h-1.5 rounded-full bg-slate-800"></span>
            <span class="w-1.5 h-1.5 rounded-full bg-slate-800"></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import request from '../utils/request';
import { User, Lock, HomeFilled } from '@element-plus/icons-vue';

const router = useRouter();
const loginFormRef = ref(null);
const loading = ref(false);

const loginForm = reactive({
  username: '',
  password: ''
});

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};

const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const res = await request.post('/user/login', loginForm);
        if (res.code === 200) {
          localStorage.setItem('token', res.data.token);
          localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo));
          
          const role = res.data.userInfo.role;
          if (role === 'SENIOR' || role === 'CHILD') {
            router.push('/web/home');
          } else if (role === 'CARER') {
            router.push('/app/home');
          } else if (role === 'ORG_ADMIN') {
            router.push('/org/home');
          } else if (role === 'PLATFORM_ADMIN') {
            router.push('/platform/home');
          }
        }
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped>
.animate-slide-up {
  animation: slideUp 1s cubic-bezier(0.16, 1, 0.3, 1);
}

.animate-fade-in {
  animation: fadeIn 1.2s ease-out;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

:deep(.ultra-input .el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.05) !important;
  box-shadow: none !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 16px !important;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  height: 56px;
  padding: 0 20px;
}

:deep(.ultra-input .el-input__wrapper.is-focus) {
  border-color: var(--primary) !important;
  background-color: rgba(255, 255, 255, 0.08) !important;
  box-shadow: 0 0 20px rgba(79, 70, 229, 0.1) !important;
}

:deep(.ultra-input .el-input__inner) {
  color: white !important;
  font-weight: 500;
  letter-spacing: 0.025em;
}

:deep(.ultra-input .el-input__inner::placeholder) {
  color: #64748b !important;
}

:deep(.el-button--primary) {
  box-shadow: 0 10px 15px -3px rgba(79, 70, 229, 0.3);
}
</style>
