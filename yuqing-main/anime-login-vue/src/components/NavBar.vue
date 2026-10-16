<script setup>
/* 公共导航栏：品牌 + 导航链接 + 全局搜索 + 用户区
   - 由 App 壳统一渲染，所有子页共享
   - 搜索提交后由父级切回首页并透传关键词 */
import { ref, computed } from 'vue'

const props = defineProps({
  user: { type: Object, required: true },
  active: { type: String, default: '首页' }
})
const emit = defineEmits(['navigate', 'logout', 'search'])

const navItems = ['首页', '番剧', '排行', '收藏']
const query = ref('')

const initial = computed(() => {
  const name = props.user.nickname || props.user.username || '?'
  return name.slice(0, 1).toUpperCase()
})

function onSubmit() {
  emit('search', query.value.trim())
}
</script>

<template>
  <nav class="nav">
    <div class="nav-inner">
      <div class="brand" @click="emit('navigate', '首页')">
        <div class="logo">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="#fff">
            <path d="M12 2.6l1.7 3.2 3.6.5-2.6 2.5.6 3.6L12 10.8 8.7 12.4l.6-3.6L6.7 6.3l3.6-.5z" />
            <circle cx="5.5" cy="17" r="2.6" />
            <circle cx="18.5" cy="17" r="2.6" />
            <circle cx="12" cy="21" r="2.6" />
          </svg>
        </div>
        <div class="brand-text">
          <b>桜次元 · Sakura</b>
          <small>anime portal</small>
        </div>
      </div>

      <ul class="nav-links">
        <li
          v-for="item in navItems"
          :key="item"
          :class="{ active: active === item }"
          @click="emit('navigate', item)"
        >
          {{ item }}
        </li>
      </ul>

      <div class="nav-right">
        <div class="search">
          <svg class="s-ico" width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="7" />
            <path d="m21 21-4.3-4.3" />
          </svg>
          <input
            v-model="query"
            type="text"
            placeholder="搜索番剧 / 标签 / UP主…"
            @keyup.enter="onSubmit"
          />
          <button class="s-btn" type="button" @click="onSubmit">搜索</button>
        </div>

        <div class="user-chip">
          <span class="u-avatar">{{ initial }}</span>
          <div class="u-meta">
            <span class="u-name">{{ user.nickname || user.username }}</span>
            <span class="u-status"><i class="u-dot"></i>在线</span>
          </div>
          <button class="logout" type="button" title="退出登录" @click="emit('logout')">
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.1" stroke-linecap="round" stroke-linejoin="round">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
              <path d="m16 17 5-5-5-5M21 12H9" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.nav {
  flex-shrink: 0;
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  background: rgba(255, 255, 255, 0.55);
  border-bottom: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: 0 12px 30px -22px rgba(90, 60, 140, 0.6);
}
.nav-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 70px;
  padding: 0 22px;
  display: flex;
  align-items: center;
  gap: 22px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 11px;
  flex-shrink: 0;
  cursor: pointer;
}
.logo {
  width: 42px;
  height: 42px;
  border-radius: 13px;
  background: var(--grad);
  display: grid;
  place-items: center;
  box-shadow: 0 10px 22px -8px rgba(192, 132, 252, 0.7);
  animation: logoFloat 4s ease-in-out infinite;
}
.brand-text b {
  font-size: 16px;
  letter-spacing: 0.03em;
  display: block;
}
.brand-text small {
  font-size: 10.5px;
  color: var(--ink-soft);
  letter-spacing: 0.24em;
  text-transform: uppercase;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 4px;
  list-style: none;
  margin: 0 auto;
}
.nav-links li {
  padding: 8px 16px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 600;
  color: var(--ink-soft);
  cursor: pointer;
  transition: 0.22s;
  letter-spacing: 0.04em;
  user-select: none;
}
.nav-links li:hover {
  color: var(--accent);
  background: rgba(255, 255, 255, 0.6);
}
.nav-links li.active {
  color: #fff;
  background: var(--grad);
  box-shadow: 0 10px 20px -10px rgba(192, 132, 252, 0.9);
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.search {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 42px;
  padding: 0 6px 0 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.78);
  border: 1.5px solid var(--line);
  transition: 0.25s;
}
.search:focus-within {
  border-color: #c9a7ff;
  box-shadow: 0 0 0 5px rgba(192, 132, 252, 0.14);
  background: #fff;
}
.search .s-ico {
  color: #b9aedd;
  flex-shrink: 0;
}
.search input {
  width: 190px;
  border: 0;
  outline: none;
  background: transparent;
  font-size: 13.5px;
  color: var(--ink);
  font-family: inherit;
}
.search input::placeholder {
  color: var(--ink-soft);
}
.s-btn {
  border: 0;
  cursor: pointer;
  height: 30px;
  padding: 0 16px;
  border-radius: 999px;
  background: var(--grad);
  color: #fff;
  font-size: 12.5px;
  font-weight: 700;
  letter-spacing: 0.06em;
  transition: 0.22s;
}
.s-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 16px -8px rgba(192, 132, 252, 0.9);
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 10px;
}
.u-avatar {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  background: var(--grad);
  display: grid;
  place-items: center;
  font-size: 17px;
  font-weight: 800;
  color: #fff;
  box-shadow: 0 10px 22px -10px rgba(192, 132, 252, 0.9);
}
.u-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}
.u-name {
  font-size: 13.5px;
  font-weight: 700;
  color: var(--ink);
}
.u-status {
  font-size: 11px;
  color: #34d399;
  display: flex;
  align-items: center;
  gap: 5px;
}
.u-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #34d399;
  box-shadow: 0 0 0 3px rgba(52, 211, 153, 0.22);
  animation: pulse 2s infinite;
}
.logout {
  width: 38px;
  height: 38px;
  border: 1.5px solid var(--line);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.7);
  color: var(--ink-soft);
  cursor: pointer;
  display: grid;
  place-items: center;
  transition: 0.22s;
}
.logout:hover {
  color: #f43f5e;
  border-color: #ffc2d2;
  background: #fff;
  transform: translateY(-2px);
}

@media (max-width: 980px) {
  .nav-links {
    display: none;
  }
}
@media (max-width: 720px) {
  .search input {
    width: 110px;
  }
  .u-meta {
    display: none;
  }
}
</style>
