<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import SakuraBackground from './components/SakuraBackground.vue'
import CharacterPanel from './components/CharacterPanel.vue'
import LoginForm from './components/LoginForm.vue'
import RegisterForm from './components/RegisterForm.vue'
import SuccessOverlay from './components/SuccessOverlay.vue'
import HomeView from './components/HomeView.vue'
import { getAuth, clearAuth, me, logout } from './api'

/* ---------- 视图状态 ---------- */
const mode = ref('login') // login / register
const user = ref(null) // 已登录用户（含 token）
const booting = ref(true) // 启动时恢复会话中
const done = ref(false)
const nickname = ref('')
const loginRef = ref(null)

let enterHomeTimer = null

/* ---------- 启动：尝试用本地 token 恢复会话 ---------- */
onMounted(async () => {
  const auth = getAuth()
  if (auth && auth.token) {
    try {
      const fresh = await me()
      user.value = { ...auth, ...fresh }
    } catch {
      /* token 失效，getAuth 已被 api 层清理，停留在登录页 */
    }
  }
  booting.value = false
})

onBeforeUnmount(() => clearTimeout(enterHomeTimer))

/* ---------- 登录成功：先展示成功页，再自动进入主页 ---------- */
function onSuccess(name, payload) {
  nickname.value = name
  done.value = true
  const u = payload && payload.user ? payload.user : null
  enterHomeTimer = setTimeout(() => {
    done.value = false
    if (u) user.value = u
    loginRef.value?.reset()
  }, 1400)
}

/* 成功页点击"返回登录"：取消跳转并登出 */
function onClose() {
  clearTimeout(enterHomeTimer)
  done.value = false
  doLogout()
}

async function doLogout() {
  try {
    await logout()
  } catch {
    /* 后端不可达也照常本地登出 */
  }
  clearAuth()
  user.value = null
  mode.value = 'login'
  loginRef.value?.reset()
}

function toRegister() {
  mode.value = 'register'
}

function toLogin() {
  mode.value = 'login'
}
</script>

<template>
  <SakuraBackground />

  <!-- 会话恢复中：保持背景，避免闪烁 -->
  <main v-if="booting" class="stage" />

  <!-- 已登录：主页 -->
  <HomeView v-else-if="user" :user="user" @logout="doLogout" />

  <!-- 未登录：登录 / 注册卡片 -->
  <main v-else class="stage">
    <div class="card">
      <CharacterPanel class="left" />

      <div class="right">
        <LoginForm
          v-if="mode === 'login'"
          ref="loginRef"
          @success="onSuccess"
          @switch="toRegister"
        />
        <RegisterForm v-else @switch="toLogin" />
      </div>

      <SuccessOverlay :show="done" :nickname="nickname" @close="onClose" />
    </div>
  </main>
</template>

<style scoped>
.stage {
  position: relative;
  z-index: 1;
  width: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px;
}

.card {
  position: relative;
  display: grid;
  grid-template-columns: 0.95fr 1.05fr;
  width: min(1040px, 96vw);
  min-height: 680px;
  border-radius: var(--radius);
  overflow: hidden;
  background: var(--glass);
  border: 1px solid rgba(255, 255, 255, 0.65);
  box-shadow: 0 34px 80px -34px rgba(90, 60, 140, 0.45);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  animation: rise 0.7s cubic-bezier(0.2, 0.8, 0.25, 1) both;
}

.left {
  min-height: 100%;
}

.right {
  display: flex;
  flex-direction: column;
}

.right > * {
  flex: 1;
}

@media (max-width: 920px) {
  .card {
    grid-template-columns: 1fr;
    min-height: auto;
  }
  .left {
    display: none;
  }
}
</style>
