<template>
  <div class="space-y-4">
    <div class="flex justify-between items-center">
      <h2 class="text-xl font-bold">心情树洞（匿名社区）</h2>
      <el-button type="primary" @click="dialogVisible = true" icon="Edit">倾诉一下</el-button>
    </div>

    <!-- 树洞列表 -->
    <div class="space-y-4">
       <el-card v-for="item in list" :key="item.treeHole.id" shadow="hover" class="relative group">
           <div class="flex justify-between items-start">
               <div>
                   <p class="font-bold text-gray-800">{{ item.treeHole.nickname }} <span class="text-xs text-gray-400 font-normal ml-2">{{ item.treeHole.createTime }}</span></p>
                   <p class="mt-2 text-gray-600">{{ item.treeHole.content }}</p>
               </div>
               <div class="flex flex-col items-center min-w-[50px]">
                   <el-button link type="danger" @click="handleLike(item.treeHole.id)">
                      <el-icon class="mr-1"><Pointer /></el-icon> {{ item.treeHole.likes }}
                   </el-button>
               </div>
           </div>
           
           <!-- 评论区 -->
           <div class="mt-4 bg-gray-50 p-3 rounded-md text-sm">
               <div v-for="comment in item.comments" :key="comment.id" class="mb-2 last:mb-0">
                   <span class="font-bold text-gray-700">{{ comment.nickname }}</span>
                   <span class="text-xs text-gray-400 mx-1">{{ comment.createTime }}</span>: 
                   <span class="text-gray-600">{{ comment.content }}</span>
               </div>
               
               <div class="flex gap-2 mt-3 w-full">
                   <el-input size="small" v-model="commentForms[item.treeHole.id]" placeholder="我也来说一句匿名鼓励..." @keyup.enter="handleComment(item.treeHole.id)" />
                   <el-button size="small" type="primary" plain @click="handleComment(item.treeHole.id)">回复</el-button>
               </div>
           </div>
       </el-card>
    </div>

    <el-dialog v-model="dialogVisible" title="匿名倾诉" width="500px">
       <el-form label-width="80px" class="space-y-4">
           <el-form-item label="匿名昵称">
               <el-input v-model="form.nickname" placeholder="留空则显示为[匿名长者]" />
           </el-form-item>
           <el-form-item label="你想说的话" required>
               <el-input type="textarea" v-model="form.content" :rows="4" placeholder="写下你的心情或困惑..." />
           </el-form-item>
       </el-form>
       <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitTreeHole">发布</el-button>
       </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import request from '../../utils/request';

const list = ref([]);
const commentForms = ref({});
const dialogVisible = ref(false);
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
const form = ref({ nickname: '', content: '' });

const fetchData = async () => {
    const res = await request.get('/treehole/list');
    list.value = res.data || [];
};

const handleLike = async (id) => {
    await request.post(`/treehole/like/${id}`);
    ElMessage.success('点赞成功');
    fetchData();
};

const handleComment = async (holeId) => {
    const content = commentForms.value[holeId]?.trim();
    if (!content) return;
    await request.post('/treehole/comment', { treeHoleId: holeId, content, authorId: userInfo.id });
    ElMessage.success('回复成功');
    commentForms.value[holeId] = '';
    fetchData();
};

const submitTreeHole = async () => {
    if (!form.value.content) {
        ElMessage.warning('内容不能为空');
        return;
    }
    await request.post('/treehole/add', { ...form.value, authorId: userInfo.id });
    ElMessage.success('发布成功');
    dialogVisible.value = false;
    form.value = { nickname: '', content: '' };
    fetchData();
};

onMounted(() => fetchData());
</script>
