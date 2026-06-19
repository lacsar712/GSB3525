<template>
  <div class="space-y-4">
    <div class="flex justify-between items-center bg-blue-50 p-4 rounded-lg border border-blue-100">
      <div>
         <h2 class="text-xl font-bold text-blue-800">积分商城</h2>
         <p class="text-sm text-blue-600 mt-1">多参加社区活动，赚取积分免费兑换心仪商品！</p>
      </div>
      <div class="text-center font-bold">
         <div class="text-sm text-gray-500">我的可用积分</div>
         <div class="text-2xl text-orange-500">{{ myPoints }}</div>
         <el-button link type="primary" size="small" @click="fetchMyPoints" icon="RefreshRight">刷新</el-button>
      </div>
    </div>

    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 mt-4">
       <el-card v-for="item in list" :key="item.id" shadow="hover" class="text-center pb-2">
           <img :src="item.imageUrl || 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png'" referrerpolicy="no-referrer" class="w-full h-32 object-cover rounded shadow-sm bg-gray-50 mb-3" />
           <h3 class="font-bold text-gray-800 truncate px-2">{{ item.name }}</h3>
           <div class="text-orange-500 font-bold mt-2">{{ item.pointsRequired }} 积分</div>
           <p class="text-xs text-gray-400 mt-1">剩余库存: {{ item.stock }}</p>
           <el-button class="mt-4 w-[80%]" type="warning" plain round @click="handleExchange(item)" :disabled="item.stock <= 0">免费兑换</el-button>
       </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '../../utils/request';

const list = ref([]);
const myPoints = ref(0);
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'));

const fetchMyPoints = async () => {
    // 重新获取用户信息刷新积分
    try {
        const res = await request.get('/user/info');
        if (res.data) {
            myPoints.value = res.data.points || 0;
            // 更新本地存储
            const updated = { ...userInfo.value, points: res.data.points };
            localStorage.setItem('userInfo', JSON.stringify(updated));
            userInfo.value = updated;
        }
    } catch(e) {}
};

const fetchData = async () => {
    const res = await request.get('/mall/list');
    list.value = res.data || [];
};

const handleExchange = (item) => {
    if (myPoints.value < item.pointsRequired) {
        ElMessage.warning('您的可用积分不足以兑换该商品');
        return;
    }
    ElMessageBox.confirm(`确认消耗 ${item.pointsRequired} 积分兑换【${item.name}】吗？`, '兑换确认', {
        type: 'warning',
        confirmButtonText: '立即兑换'
    }).then(async () => {
        await request.post('/mall/exchange', { mallId: item.id, seniorId: userInfo.value.id });
        ElMessage.success('兑换成功！工作人员将尽快配送到家');
        fetchMyPoints();
        fetchData();
    }).catch(()=>{});
};

onMounted(() => {
    myPoints.value = userInfo.value.points || 0;
    fetchMyPoints();
    fetchData();
});
</script>
