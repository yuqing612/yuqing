<script setup>
/* 排行页：调用 mock API fetchRanks，按播放量倒序，带前三名奖牌样式 */
import { ref, onMounted } from 'vue'
import { fetchRanks } from '../api'

defineProps({ user: Object, q: String })
const list = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    list.value = await fetchRanks()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page">
    <div class="sec-head">
      <h2>热播排行</h2>
      <span class="sec-sub">{{ loading ? '加载中…' : `TOP ${list.length}` }}</span>
    </div>

    <div v-if="loading" class="loading-wrap">
      <i class="loader-spin"></i><span>正在连接次元…</span>
    </div>

    <div v-else-if="list.length" class="rank-list">
      <div
        v-for="v in list"
        :key="v.id"
        class="r-row"
        :class="`top${v.rank <= 3 ? v.rank : ''}`"
      >
        <span class="rank-no">{{ v.rank }}</span>
        <div class="r-thumb" :style="{ background: v.grad }">
          <img v-if="v.coverUrl || v.img" class="t-img" :src="v.coverUrl || v.img" :alt="v.title" loading="lazy" />
          <span class="play">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="#fff"><path d="M8 5v14l11-7z"/></svg>
          </span>
        </div>
        <div class="r-info">
          <h4 class="r-name">{{ v.title }}</h4>
          <p class="r-up">{{ v.uploader }} · {{ v.tags.join(' / ') }}</p>
        </div>
        <div class="r-metric">
          <span class="r-views">{{ v.views }}</span>
          <span class="r-label">播放量</span>
        </div>
      </div>
    </div>

    <div v-else class="empty">
      <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"><path d="M4 20V10M10 20V4M16 20v-7M22 20H2"/></svg>
      <p>暂无排行数据</p>
    </div>

    <footer class="foot">桜次元 · Sakura — 仅前端演示 · 数据由 mock API 提供</footer>
  </div>
</template>

<style scoped>
.rank-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.r-row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  border-radius: 16px;
  background: var(--glass);
  border: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: 0 14px 34px -28px rgba(90, 60, 140, 0.5);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  cursor: pointer;
  transition: transform 0.22s, box-shadow 0.22s, border-color 0.22s;
}
.r-row:hover {
  transform: translateX(4px);
  border-color: #e3d4ff;
}
.rank-no {
  width: 34px;
  flex-shrink: 0;
  text-align: center;
  font-size: 20px;
  font-weight: 900;
  font-style: italic;
  color: var(--ink-soft);
}
.r-row.top1 .rank-no {
  color: #ffb020;
}
.r-row.top2 .rank-no {
  color: #9ca3ff;
}
.r-row.top3 .rank-no {
  color: #ff9a76;
}
.r-row.top1 {
  box-shadow: 0 16px 38px -22px rgba(255, 176, 32, 0.6);
}
.r-thumb {
  position: relative;
  width: 96px;
  height: 54px;
  flex-shrink: 0;
  border-radius: 10px;
  display: grid;
  place-items: center;
  overflow: hidden;
}
.r-thumb .t-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.r-thumb .play {
  position: relative;
  z-index: 1;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.92);
  display: grid;
  place-items: center;
  transition: transform 0.24s;
}
.r-row:hover .play {
  transform: scale(1.12);
}
.r-info {
  flex: 1;
  min-width: 0;
}
.r-name {
  font-size: 14.5px;
  font-weight: 700;
  color: var(--ink);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.r-up {
  font-size: 12px;
  color: var(--accent);
  margin-top: 5px;
  font-weight: 600;
}
.r-metric {
  text-align: right;
  flex-shrink: 0;
}
.r-views {
  display: block;
  font-size: 15px;
  font-weight: 800;
  color: var(--ink);
}
.r-label {
  font-size: 11px;
  color: var(--ink-soft);
}
@media (max-width: 560px) {
  .r-metric {
    display: none;
  }
}
</style>
