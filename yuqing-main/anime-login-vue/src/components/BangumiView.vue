<script setup>
/* 番剧页：调用 mock API fetchBangumi，网格展示新番 */
import { ref, onMounted } from 'vue'
import { fetchBangumi } from '../api'

defineProps({ user: Object, q: String })
const list = ref([])
const loading = ref(true)
const err = ref('')

async function load() {
  loading.value = true
  err.value = ''
  try {
    list.value = (await fetchBangumi()) || []
  } catch (e) {
    err.value = e.message || '番剧加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="sec-head">
      <h2>番剧新番</h2>
      <span class="sec-sub">{{ loading ? '加载中…' : `${list.length} 部作品` }}</span>
    </div>

    <div v-if="loading" class="loading-wrap">
      <i class="loader-spin"></i><span>正在连接次元…</span>
    </div>

    <div v-else-if="list.length" class="grid">
      <article v-for="b in list" :key="b.id" class="b-card">
        <div class="cover" :style="{ background: b.cover }">
          <img v-if="b.illust" class="b-img" :src="b.illust" :alt="b.title" loading="lazy" />
          <i class="b-shade"></i>
          <span class="status" :class="b.status === '连载中' ? 'on' : 'done'">{{ b.status }}</span>
          <span class="eps">{{ b.episodes }} 话</span>
          <span class="score">★ {{ b.score.toFixed(1) }}</span>
        </div>
        <div class="b-body">
          <h4 class="b-name">{{ b.title }}</h4>
          <p class="b-studio">{{ b.studio }} · {{ b.tags.join(' / ') }}</p>
          <p class="b-desc">{{ b.desc }}</p>
        </div>
      </article>
    </div>

    <div v-else-if="err" class="empty">
      <p>番剧加载失败：{{ err }}</p>
      <p class="tip">确认后端已启动并已执行 yuqing_db.sql 导入番剧数据</p>
    </div>

    <div v-else class="empty">
      <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="16" rx="2"/><path d="M3 9h18M8 4v5M16 4v5"/></svg>
      <p>暂无番剧数据</p>
    </div>

    <footer class="foot">桜次元 · Sakura — 番剧数据来自后端 /api/bangumi</footer>
  </div>
</template>

<style scoped>
.b-card {
  border-radius: 18px;
  overflow: hidden;
  background: var(--glass);
  border: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: 0 18px 40px -28px rgba(90, 60, 140, 0.5);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  transition: transform 0.26s cubic-bezier(0.2, 0.8, 0.25, 1), box-shadow 0.26s, border-color 0.26s;
  cursor: pointer;
}
.b-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 26px 50px -26px rgba(90, 60, 140, 0.6);
  border-color: #e3d4ff;
}
.cover {
  position: relative;
  aspect-ratio: 3 / 4;
  overflow: hidden;
}
/* 番剧插画 */
.cover .b-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.5s cubic-bezier(0.2, 0.8, 0.25, 1);
}
.b-card:hover .cover .b-img {
  transform: scale(1.06);
}
.cover .b-shade {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 34%;
  pointer-events: none;
  background: linear-gradient(0deg, rgba(28, 21, 48, 0.45), transparent);
}
.cover .status {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 11px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  color: #fff;
  backdrop-filter: blur(3px);
}
.cover .status.on {
  background: rgba(244, 63, 94, 0.85);
}
.cover .status.done {
  background: rgba(47, 36, 69, 0.55);
}
.cover .eps {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 11px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  color: var(--ink);
  background: rgba(255, 255, 255, 0.85);
}
.cover .score {
  position: absolute;
  left: 12px;
  bottom: 12px;
  padding: 4px 11px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
  color: #fff;
  background: rgba(28, 21, 48, 0.55);
  backdrop-filter: blur(3px);
}
.b-body {
  padding: 14px 16px 18px;
}
.b-name {
  font-size: 15px;
  font-weight: 800;
  line-height: 1.45;
  color: var(--ink);
}
.b-studio {
  font-size: 12px;
  color: var(--accent);
  margin-top: 7px;
  font-weight: 600;
}
.b-desc {
  font-size: 12px;
  color: var(--ink-soft);
  margin-top: 8px;
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.tip {
  margin-top: 8px;
  font-size: 12px;
  color: var(--ink-soft);
  opacity: 0.85;
}
</style>
