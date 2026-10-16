<script setup>
/* =========================================================
   首页（门户版 · 风格对齐动漫共和国，视觉沿用登录页）
   - 导航栏由 App 壳统一渲染（含搜索栏）
   - 结构：Hero 轮播 → 视频播放器 → 热门推荐 + 番剧新番 →
           侧边栏（热播排行 + 番剧时间表）
   - 数据全部来自 mock API，后端就绪后页面零改动
   ========================================================= */
import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import characterImg from '../assets/character.jpg'
import { fetchVideos, fetchBangumi, fetchBangumiCategories, fetchRanks, fetchSchedule } from '../api'
import FeatherField from './FeatherField.vue'

const props = defineProps({
  user: { type: Object, required: true },
  q: { type: String, default: '' }
})

const videos = ref([])
const bangumi = ref([])
const ranks = ref([])
const schedule = ref({ weekdays: [], today: '', items: {} })
const current = ref(null)
const loading = ref(true)
const loadError = ref('')
const query = ref(props.q || '')
const videoEl = ref(null)

/* Hero 轮播 */
const heroItems = computed(() => bangumi.value.slice(0, 5))
const heroIndex = ref(0)
let heroTimer = null
function goHero(i) {
  heroIndex.value = (i + heroItems.value.length) % heroItems.value.length
}
function nextHero() {
  goHero(heroIndex.value + 1)
}
function prevHero() {
  goHero(heroIndex.value - 1)
}
function startHero() {
  stopHero()
  heroTimer = setInterval(nextHero, 5000)
}
function stopHero() {
  clearInterval(heroTimer)
}

/* 热门推荐：响应全局导航栏搜索 q */
const filtered = computed(() => {
  const s = query.value.trim().toLowerCase()
  if (!s) return videos.value
  return videos.value.filter(
    (v) =>
      v.title.toLowerCase().includes(s) ||
      v.uploader.toLowerCase().includes(s) ||
      v.tags.some((t) => t.toLowerCase().includes(s))
  )
})

/* 番剧板块：分类 Tab + 海报墙 */
/* Tab 优先取后端返回的实际分类，取不到时回落为内置列表 */
const CATS = ref(['全部', '日漫', '国漫', '剧场', '美漫'])
const cat = ref('全部')
const bangumiFiltered = computed(() =>
  cat.value === '全部' ? bangumi.value : bangumi.value.filter((b) => b.category === cat.value)
)

function playVideo(v) {
  current.value = v
  nextTick(() => videoEl.value && videoEl.value.load())
  document.getElementById('player')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}
function watchHero() {
  if (videos.value[0]) playVideo(videos.value[0])
}

watch(
  () => props.q,
  (val) => {
    query.value = val || ''
    nextTick(() =>
      document.getElementById('video-grid')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
    )
  }
)

const initial = computed(() => {
  const name = props.user.nickname || props.user.username || '?'
  return name.slice(0, 1).toUpperCase()
})

/* 任一接口失败时不再整页空白：用 allSettled 逐项落地，失败的板块单独提示 */
async function loadAll() {
  loading.value = true
  loadError.value = ''
  const [v, b, r, s] = await Promise.allSettled([
    fetchVideos(),
    fetchBangumi(),
    fetchRanks(),
    fetchSchedule()
  ])
  if (v.status === 'fulfilled') videos.value = v.value || []
  if (b.status === 'fulfilled') bangumi.value = b.value || []
  if (r.status === 'fulfilled') ranks.value = r.value || []
  if (s.status === 'fulfilled') schedule.value = s.value || { weekdays: [], today: '', items: {} }
  current.value = videos.value[0] || null
  startHero()

  const failed = [v, b, r, s].find((x) => x.status === 'rejected')
  loadError.value = failed ? failed.reason?.message || '数据加载失败' : ''

  fetchBangumiCategories()
    .then((list) => {
      if (list && list.length) CATS.value = ['全部', ...list]
    })
    .catch(() => {})
  loading.value = false
}

onMounted(loadAll)
onBeforeUnmount(stopHero)
</script>

<template>
  <div class="page home">
    <FeatherField />
    <!-- 欢迎条（轻量） -->
    <div class="greet">
      <span class="g-avatar">{{ initial }}</span>
      <div>
        <p class="g-hi">欢迎回来，{{ user.nickname || user.username }} ✦</p>
        <p class="g-sub">今天也想看点和什么呢？挑一部，把烦恼留在现实的车站就好。</p>
      </div>
    </div>

    <div v-if="loading" class="loading-wrap">
      <i class="loader-spin"></i><span>正在连接次元…</span>
    </div>

    <div v-else-if="loadError && !bangumi.length && !videos.length" class="err-bar">
      <span>⚠ 数据加载失败：{{ loadError }}</span>
      <button class="btn ghost" type="button" @click="loadAll">重新加载</button>
    </div>
    <div v-else-if="loadError" class="err-bar warn">
      <span>⚠ 部分板块没加载出来：{{ loadError }}</span>
      <button class="btn ghost" type="button" @click="loadAll">重试</button>
    </div>

    <template v-if="!loading">
      <!-- ============ Hero 轮播 ============ -->
      <section class="hero">
        <div class="slides">
          <div
            v-for="(h, i) in heroItems"
            :key="h.id"
            class="slide"
            :class="{ active: i === heroIndex }"
            :style="{ background: h.cover }"
          >
            <img v-if="h.illust" class="slide-art" :src="h.illust" :alt="h.title" />
            <div class="slide-mask"></div>
            <div class="slide-content">
              <span class="h-tag">
                <i class="h-dot"></i>{{ h.status }} · {{ h.studio }}
              </span>
              <h2 class="h-title">{{ h.title }}</h2>
              <p class="h-desc">{{ h.desc }}</p>
              <div class="h-meta">
                <span><b>★</b> {{ h.score.toFixed(1) }}</span>
                <span>全 {{ h.episodes }} 话</span>
                <span v-for="t in h.tags" :key="t" class="h-chip">{{ t }}</span>
              </div>
              <button class="btn primary" type="button" @click="watchHero">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M8 5v14l11-7z"/></svg>
                立即观看
              </button>
            </div>
          </div>
        </div>
        <button class="h-nav prev" type="button" aria-label="上一个" @click="prevHero">‹</button>
        <button class="h-nav next" type="button" aria-label="下一个" @click="nextHero">›</button>
        <div class="h-dots">
          <span
            v-for="(h, i) in heroItems"
            :key="h.id"
            :class="{ on: i === heroIndex }"
            @click="goHero(i)"
          ></span>
        </div>
      </section>

      <!-- ============ 视频播放器 ============ -->
      <section class="player-section" id="player" v-if="current">
        <div class="player-card">
          <div class="player-wrap">
            <video
              ref="videoEl"
              class="player"
              :src="current.src"
              :poster="current.coverUrl || current.img || characterImg"
              controls
              preload="metadata"
              playsinline
            ></video>
            <span class="live-badge">NOW PLAYING</span>
          </div>
          <div class="player-info">
            <div class="tags">
              <span v-for="t in current.tags" :key="t" class="tag">{{ t }}</span>
            </div>
            <h3 class="v-title">{{ current.title }}</h3>
            <div class="stats">
              <span><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z"/><circle cx="12" cy="12" r="3"/></svg>{{ current.views }} 播放</span>
              <span><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 11.5a8.38 8.38 0 0 1-9 8.3A8.5 8.5 0 1 1 12 3a8.38 8.38 0 0 1 9 5.8"/></svg>{{ current.danmaku }} 弹幕</span>
              <span><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>{{ current.duration }}</span>
            </div>
            <p class="v-desc">{{ current.desc }}</p>
            <div class="actions">
              <button class="btn primary" type="button" @click="videoEl && videoEl.play()">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M8 5v14l11-7z"/></svg>
                立即播放
              </button>
              <button class="btn ghost" type="button">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg>
                收藏
              </button>
              <button class="btn ghost" type="button">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="18" cy="5" r="3"/><circle cx="6" cy="12" r="3"/><circle cx="18" cy="19" r="3"/><path d="m8.6 13.5 6.8 4M15.4 6.5l-6.8 4"/></svg>
                分享
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- ============ 主区 + 侧栏 ============ -->
      <div class="layout">
        <div class="main-col">
          <section class="block" id="video-grid">
            <div class="sec-head">
              <h3>{{ query.trim() ? `“${query.trim()}” 的搜索结果` : '热门推荐' }}</h3>
              <span class="sec-sub">{{ filtered.length }} 个视频</span>
            </div>
            <div v-if="filtered.length" class="grid">
              <article
                v-for="v in filtered"
                :key="v.id"
                class="v-card"
                :class="{ on: current && current.id === v.id }"
                @click="playVideo(v)"
              >
                <div class="thumb" :style="{ background: v.grad }">
                  <img v-if="v.coverUrl || v.img" class="t-img" :src="v.coverUrl || v.img" :alt="v.title" loading="lazy" />
                  <span class="play"><svg width="22" height="22" viewBox="0 0 24 24" fill="#fff"><path d="M8 5v14l11-7z"/></svg></span>
                  <span class="dur">{{ v.duration }}</span>
                </div>
                <div class="v-body">
                  <h4 class="v-name">{{ v.title }}</h4>
                  <p class="v-up">{{ v.uploader }}</p>
                  <p class="v-count">{{ v.views }} 播放 · {{ v.danmaku }} 弹幕</p>
                </div>
              </article>
            </div>
            <div v-else class="empty">
              <p>没有找到相关视频，换个关键词试试吧～</p>
            </div>
          </section>

          <section class="block">
            <div class="sec-head">
              <h3>番剧新番</h3>
              <div class="cat-tabs">
                <span
                  v-for="c in CATS"
                  :key="c"
                  :class="{ on: cat === c }"
                  @click="cat = c"
                >{{ c }}</span>
              </div>
            </div>
            <div class="poster-grid">
              <article v-for="b in bangumiFiltered" :key="b.id" class="p-card">
                <div class="poster" :style="{ background: b.cover }">
                  <img v-if="b.illust" class="p-img" :src="b.illust" :alt="b.title" loading="lazy" />
                  <i class="p-shade"></i>
                  <span v-if="b.status === '连载中'" class="p-status">连载中</span>
                  <span class="p-score">{{ b.score.toFixed(1) }}分</span>
                </div>
                <p class="p-name">{{ b.title }}</p>
                <p class="p-sub">更新至第 {{ b.episodes }} 集 · {{ b.category }}</p>
              </article>
            </div>
            <div v-if="!bangumiFiltered.length" class="empty">
              <p>该分类下暂无番剧</p>
            </div>
          </section>
        </div>

        <aside class="side-col">
          <section class="block">
            <div class="sec-head"><h3>热播排行</h3></div>
            <div class="side-rank">
              <div
                v-for="v in ranks.slice(0, 6)"
                :key="v.id"
                class="sr-row"
                :class="`top${v.rank <= 3 ? v.rank : ''}`"
                @click="playVideo(v)"
              >
                <span class="sr-no">{{ v.rank }}</span>
                <div class="sr-info">
                  <p class="sr-name">{{ v.title }}</p>
                  <p class="sr-views">{{ v.views }} 播放</p>
                </div>
              </div>
            </div>
          </section>

          <section class="block">
            <div class="sec-head">
              <h3>番剧时间表</h3>
              <span class="sec-sub">今天 · {{ schedule.today }}</span>
            </div>
            <div class="week-tabs">
              <span
                v-for="d in schedule.weekdays"
                :key="d"
                class="wt"
                :class="{ on: d === schedule.today }"
              >{{ d.replace('周', '') }}</span>
            </div>
            <ul class="sched-list">
              <li v-for="(it, i) in (schedule.items[schedule.today] || [])" :key="i">
                <span class="sc-time">{{ it.time }}</span>
                <span class="sc-title">{{ it.title }}</span>
                <span class="sc-ep">第 {{ it.ep }} 话</span>
              </li>
              <li v-if="!(schedule.items[schedule.today] || []).length" class="sc-none">
                今天没有更新哦～
              </li>
            </ul>
          </section>
        </aside>
      </div>
    </template>

    <footer class="foot">桜次元 · Sakura — 仅前端演示 · 数据由 mock API 提供（后端就绪后无需改动页面）</footer>
  </div>
</template>

<style scoped>
.page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 26px 22px 60px;
  animation: rise 0.6s cubic-bezier(0.2, 0.8, 0.25, 1) both;
}

/* 欢迎条 */
.greet {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 22px;
}
.g-avatar {
  width: 46px;
  height: 46px;
  border-radius: 15px;
  background: var(--grad);
  display: grid;
  place-items: center;
  font-size: 19px;
  font-weight: 800;
  color: #fff;
  box-shadow: 0 10px 22px -10px rgba(192, 132, 252, 0.9);
  flex-shrink: 0;
}
.g-hi {
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 0.02em;
}
.g-sub {
  font-size: 12.5px;
  color: var(--ink-soft);
  margin-top: 3px;
}

/* ============ Hero 轮播 ============ */
.hero {
  position: relative;
  border-radius: var(--radius);
  overflow: hidden;
  height: 420px;
  margin-bottom: 30px;
  box-shadow: 0 30px 70px -34px rgba(90, 60, 140, 0.5);
  background: #1c1530;
}
.slides {
  position: absolute;
  inset: 0;
}
.slide {
  position: absolute;
  inset: 0;
  opacity: 0;
  transform: scale(1.06);
  transition: opacity 0.8s ease, transform 6s ease;
  display: flex;
  align-items: flex-end;
}
.slide.active {
  opacity: 1;
  transform: scale(1);
  z-index: 1;
}
/* 番剧插画：裁切贴右侧，与左侧文案形成留白 */
.slide-art {
  position: absolute;
  z-index: 1;
  top: 0;
  right: 0;
  width: 40%;
  height: 100%;
  object-fit: cover;
  object-position: center 28%;
  -webkit-mask-image: linear-gradient(90deg, transparent 0%, #000 30%, #000 100%);
  mask-image: linear-gradient(90deg, transparent 0%, #000 30%, #000 100%);
}
.slide-mask {
  position: absolute;
  z-index: 2;
  inset: 0;
  background: linear-gradient(90deg, rgba(28, 21, 48, 0.82) 0%, rgba(28, 21, 48, 0.35) 48%, transparent 78%),
    linear-gradient(0deg, rgba(28, 21, 48, 0.7), transparent 40%);
}
.slide-content {
  position: relative;
  z-index: 2;
  max-width: 620px;
  padding: 0 0 46px 44px;
  color: #fff;
}
.h-tag {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 6px 13px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(6px);
  margin-bottom: 14px;
}
.h-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #34d399;
  box-shadow: 0 0 0 3px rgba(52, 211, 153, 0.25);
}
.h-title {
  font-size: 32px;
  font-weight: 900;
  line-height: 1.25;
  text-shadow: 0 3px 18px rgba(0, 0, 0, 0.4);
}
.h-desc {
  font-size: 13.5px;
  line-height: 1.7;
  margin: 12px 0 14px;
  color: rgba(255, 255, 255, 0.9);
  max-width: 540px;
}
.h-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.95);
}
.h-meta b {
  color: #ffd98a;
}
.h-chip {
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 11.5px;
  background: rgba(255, 255, 255, 0.16);
}
.h-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 3;
  width: 44px;
  height: 44px;
  border: 0;
  border-radius: 50%;
  cursor: pointer;
  font-size: 26px;
  line-height: 1;
  color: #fff;
  background: rgba(28, 21, 48, 0.4);
  backdrop-filter: blur(4px);
  transition: 0.22s;
}
.h-nav:hover {
  background: rgba(28, 21, 48, 0.7);
}
.h-nav.prev {
  left: 16px;
}
.h-nav.next {
  right: 16px;
}
.h-dots {
  position: absolute;
  z-index: 3;
  right: 30px;
  bottom: 26px;
  display: flex;
  gap: 8px;
}
.h-dots span {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.45);
  cursor: pointer;
  transition: 0.25s;
}
.h-dots span.on {
  width: 26px;
  border-radius: 999px;
  background: #fff;
}

/* ============ 视频播放器 ============ */
.player-section {
  margin-bottom: 34px;
}
.player-card {
  display: grid;
  grid-template-columns: 1.55fr 1fr;
  border-radius: var(--radius);
  overflow: hidden;
  background: var(--glass);
  border: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: 0 30px 70px -34px rgba(90, 60, 140, 0.45);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}
.player-wrap {
  position: relative;
  background: #1c1530;
  min-height: 300px;
}
.player {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
  background: #1c1530;
}
.live-badge {
  position: absolute;
  top: 14px;
  left: 14px;
  padding: 5px 12px;
  border-radius: 999px;
  font-size: 10.5px;
  font-weight: 700;
  letter-spacing: 0.12em;
  color: #fff;
  background: rgba(244, 63, 94, 0.85);
  backdrop-filter: blur(4px);
}
.player-info {
  padding: 24px 26px;
  display: flex;
  flex-direction: column;
}
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}
.tag {
  padding: 5px 12px;
  border-radius: 999px;
  font-size: 11.5px;
  font-weight: 600;
  color: #8b5cf6;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(192, 132, 252, 0.3);
}
.v-title {
  font-size: 18px;
  font-weight: 800;
  line-height: 1.45;
  margin-bottom: 12px;
}
.stats {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 14px;
  color: var(--ink-soft);
  font-size: 12.5px;
}
.stats span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.v-desc {
  font-size: 13px;
  line-height: 1.8;
  color: var(--ink-soft);
  margin-bottom: 20px;
}
.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: auto;
}
.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 44px;
  padding: 0 22px;
  border-radius: 14px;
  border: 0;
  cursor: pointer;
  font-size: 13.5px;
  font-weight: 700;
  letter-spacing: 0.04em;
  transition: transform 0.22s, box-shadow 0.22s, background 0.22s;
}
.btn.primary {
  color: #fff;
  background: var(--grad);
  background-size: 220% 100%;
  box-shadow: 0 14px 28px -12px rgba(192, 132, 252, 0.95);
}
.btn.primary:hover {
  transform: translateY(-2px);
  background-position: 100% 0;
}
.btn.ghost {
  color: var(--accent);
  background: rgba(255, 255, 255, 0.8);
  border: 1.5px solid var(--line);
}
.btn.ghost:hover {
  transform: translateY(-2px);
  background: #fff;
  border-color: #e3d4ff;
}

/* ============ 主区 + 侧栏 ============ */
.layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 26px;
  align-items: start;
}
.block {
  margin-bottom: 30px;
}
.main-col .grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 18px;
}

/* 视频卡 */
.v-card {
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
.v-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 26px 50px -26px rgba(90, 60, 140, 0.6);
  border-color: #e3d4ff;
}
.v-card.on {
  border-color: transparent;
  box-shadow: 0 0 0 2px #c084fc, 0 22px 46px -22px rgba(192, 132, 252, 0.8);
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
  transition: transform 0.5s cubic-bezier(0.2, 0.8, 0.25, 1);
}
.v-card:hover .thumb .t-img {
  transform: scale(1.06);
}
.thumb .play {
  position: relative;
  z-index: 1;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.92);
  display: grid;
  place-items: center;
  box-shadow: 0 10px 24px -8px rgba(47, 36, 69, 0.5);
  transition: transform 0.26s, background 0.26s;
}
.v-card:hover .play {
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
.v-body {
  padding: 13px 15px 15px;
}
.v-name {
  font-size: 13.5px;
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
  margin-top: 7px;
  font-weight: 600;
}
.v-count {
  font-size: 11.5px;
  color: var(--ink-soft);
  margin-top: 4px;
}

/* 番剧分类 Tab */
.cat-tabs {
  display: flex;
  gap: 6px;
  margin-left: auto;
}
.cat-tabs span {
  padding: 6px 14px;
  border-radius: 999px;
  font-size: 12.5px;
  font-weight: 600;
  color: var(--ink-soft);
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid var(--line);
  cursor: pointer;
  user-select: none;
  transition: 0.2s;
}
.cat-tabs span:hover {
  color: var(--accent);
  border-color: #e3d4ff;
}
.cat-tabs span.on {
  color: #fff;
  background: var(--grad);
  border-color: transparent;
  box-shadow: 0 8px 18px -8px rgba(192, 132, 252, 0.9);
}

/* 番剧海报墙（竖版海报 + 下方标题） */
.poster-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(148px, 1fr));
  gap: 16px;
}
.p-card {
  cursor: pointer;
  transition: transform 0.26s cubic-bezier(0.2, 0.8, 0.25, 1);
}
.p-card:hover {
  transform: translateY(-5px);
}
.poster {
  position: relative;
  aspect-ratio: 3 / 4;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 14px 30px -18px rgba(90, 60, 140, 0.6);
  transition: box-shadow 0.26s;
}
.p-card:hover .poster {
  box-shadow: 0 20px 38px -16px rgba(90, 60, 140, 0.75);
}
/* 番剧插画 */
.poster .p-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.5s cubic-bezier(0.2, 0.8, 0.25, 1);
}
.p-card:hover .poster .p-img {
  transform: scale(1.06);
}
/* 底部压暗，保证分数可读 */
.poster .p-shade {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 42%;
  pointer-events: none;
  background: linear-gradient(0deg, rgba(28, 21, 48, 0.5), transparent);
}
.p-status {
  position: absolute;
  top: 9px;
  left: 9px;
  padding: 3px 9px;
  border-radius: 999px;
  font-size: 10px;
  font-weight: 700;
  color: #fff;
  background: rgba(244, 63, 94, 0.88);
  backdrop-filter: blur(3px);
}
.p-score {
  position: absolute;
  left: 9px;
  bottom: 9px;
  padding: 3px 9px;
  border-radius: 999px;
  font-size: 10.5px;
  font-weight: 800;
  color: #fff;
  background: rgba(28, 21, 48, 0.6);
  backdrop-filter: blur(3px);
}
.p-name {
  font-size: 13px;
  font-weight: 700;
  color: var(--ink);
  margin-top: 9px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.p-sub {
  font-size: 11px;
  color: var(--ink-soft);
  margin-top: 3px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 侧栏 */
.side-col .block {
  background: var(--glass);
  border: 1px solid rgba(255, 255, 255, 0.7);
  border-radius: 18px;
  padding: 16px 16px 18px;
  box-shadow: 0 18px 40px -30px rgba(90, 60, 140, 0.5);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
}
.side-rank {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.sr-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: 0.2s;
}
.sr-row:hover {
  background: rgba(255, 255, 255, 0.55);
}
.sr-no {
  width: 22px;
  text-align: center;
  font-size: 16px;
  font-weight: 900;
  font-style: italic;
  color: var(--ink-soft);
  flex-shrink: 0;
}
.sr-row.top1 .sr-no {
  color: #ffb020;
}
.sr-row.top2 .sr-no {
  color: #9ca3ff;
}
.sr-row.top3 .sr-no {
  color: #ff9a76;
}
.sr-name {
  font-size: 13px;
  font-weight: 700;
  color: var(--ink);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.sr-views {
  font-size: 11px;
  color: var(--ink-soft);
  margin-top: 2px;
}

.week-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 12px;
}
.wt {
  flex: 1;
  text-align: center;
  padding: 6px 0;
  border-radius: 9px;
  font-size: 12px;
  font-weight: 600;
  color: var(--ink-soft);
  background: rgba(255, 255, 255, 0.5);
  cursor: default;
}
.wt.on {
  color: #fff;
  background: var(--grad);
}
.sched-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.sched-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 6px;
  border-bottom: 1px dashed var(--line);
  font-size: 12.5px;
}
.sched-list li:last-child {
  border-bottom: 0;
}
.sc-time {
  font-weight: 800;
  color: var(--accent);
  width: 44px;
  flex-shrink: 0;
}
.sc-title {
  flex: 1;
  color: var(--ink);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.sc-ep {
  font-size: 11px;
  color: var(--ink-soft);
  flex-shrink: 0;
}
.sc-none {
  color: var(--ink-soft);
  justify-content: center;
}

/* 加载失败提示条 */
.err-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 14px;
  margin: 0 0 22px;
  padding: 14px 18px;
  border-radius: 16px;
  font-size: 13px;
  color: #b42318;
  background: rgba(255, 228, 230, 0.9);
  border: 1px solid rgba(244, 63, 94, 0.35);
}
.err-bar.warn {
  color: #92400e;
  background: rgba(254, 243, 199, 0.9);
  border-color: rgba(245, 158, 11, 0.4);
}
.err-bar .btn {
  height: 36px;
  padding: 0 16px;
  font-size: 12.5px;
  flex-shrink: 0;
}

.empty {
  text-align: center;
  padding: 50px 0;
  color: var(--ink-soft);
}
.empty p {
  font-size: 14px;
}
.foot {
  text-align: center;
  font-size: 11.5px;
  color: var(--ink-soft);
  margin-top: 20px;
  letter-spacing: 0.04em;
}

/* ============ 响应式 ============ */
@media (max-width: 980px) {
  .layout {
    grid-template-columns: 1fr;
  }
  .player-card {
    grid-template-columns: 1fr;
  }
  .player-wrap {
    min-height: 220px;
  }
  .sec-head {
    flex-wrap: wrap;
  }
  .cat-tabs {
    margin-left: 0;
    width: 100%;
  }
}
@media (max-width: 720px) {
  .hero {
    height: 320px;
  }
  .slide-content {
    padding: 0 0 30px 22px;
  }
  .h-title {
    font-size: 24px;
  }
  .h-nav {
    display: none;
  }
  .slide-art {
    width: 72%;
    opacity: 0.7;
  }
}
@media (prefers-reduced-motion: reduce) {
  .page,
  .slide {
    animation: none !important;
    transition: opacity 0.3s !important;
  }
}
</style>
