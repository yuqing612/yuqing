<script setup>
/* 收藏页：调用 mock API fetchFavorites，网格展示已收藏视频 */
import { ref, onMounted } from 'vue'
import { fetchFavorites } from '../api'

defineProps({ user: Object, q: String })
const list = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    list.value = await fetchFavorites()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page">
    <div class="sec-head">
      <h2>我的收藏</h2>
      <span class="sec-sub">{{ loading ? '加载中…' : `${list.length} 个视频` }}</span>
    </div>

    <div v-if="loading" class="loading-wrap">
      <i class="loader-spin"></i><span>正在连接次元…</span>
    </div>

    <div v-else-if="list.length" class="grid">
      <article v-for="v in list" :key="v.id" class="f-card">
        <div class="thumb" :style="{ background: v.grad }">
          <img v-if="v.coverUrl || v.img" class="t-img" :src="v.coverUrl || v.img" :alt="v.title" loading="lazy" />
          <span class="play">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="#fff"><path d="M8 5v14l11-7z"/></svg>
          </span>
          <span class="dur">{{ v.duration }}</span>
          <span class="heart">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="#fff"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg>
          </span>
        </div>
        <div class="v-body">
          <h4 class="v-name">{{ v.title }}</h4>
          <p class="v-up">{{ v.uploader }}</p>
          <p class="v-count">{{ v.views }} 播放 · {{ v.danmaku }} 弹幕</p>
        </div>
      </article>
    </div>

    <div v-else class="empty">
      <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg>
      <p>还没有收藏，去首页挑一部喜欢的吧～</p>
    </div>

    <footer class="foot">桜次元 · Sakura — 仅前端演示 · 数据由 mock API 提供</footer>
  </div>
</template>

<style scoped>
.f-card {
  border-radius: 18px;
  overflow: hidden;
  background: var(--glass);
  border: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: 0 18px 40px -28px rgba(90, 60, 140, 0.5);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  cursor: pointer;
  transition: transform 0.26s cubic-bezier(0.2, 0.8, 0.25, 1), box-shadow 0.26s, border-color 0.26s;
}
.f-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 26px 50px -26px rgba(90, 60, 140, 0.6);
  border-color: #e3d4ff;
}
.thumb {
  position: relative;
  aspect-ratio: 16 / 9;
  display: grid;
  place-items: center;
  overflow: hidden;
}
.thumb .t-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.thumb .play {
  position: relative;
  z-index: 1;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.92);
  display: grid;
  place-items: center;
  box-shadow: 0 10px 24px -8px rgba(47, 36, 69, 0.5);
  transition: transform 0.26s, background 0.26s;
}
.f-card:hover .play {
  transform: scale(1.12);
  background: #fff;
}
.thumb .dur {
  position: absolute;
  right: 10px;
  bottom: 10px;
  padding: 3px 9px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 700;
  color: #fff;
  background: rgba(28, 21, 48, 0.7);
  backdrop-filter: blur(3px);
}
.thumb .heart {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: rgba(244, 63, 94, 0.9);
}
.v-body {
  padding: 14px 16px 16px;
}
.v-name {
  font-size: 14px;
  font-weight: 700;
  line-height: 1.5;
  color: var(--ink);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.v-up {
  font-size: 12px;
  color: var(--accent);
  margin-top: 8px;
  font-weight: 600;
}
.v-count {
  font-size: 11.5px;
  color: var(--ink-soft);
  margin-top: 4px;
}
</style>
