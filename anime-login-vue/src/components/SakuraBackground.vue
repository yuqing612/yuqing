<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const canvasRef = ref(null)

const COLORS = ['#ffb7d0', '#ffc9e0', '#ffdfeb', '#e7c6ff', '#ffd6a8']
const reduce = window.matchMedia('(prefers-reduced-motion: reduce)').matches

let ctx = null
let raf = 0
let petals = []

/* ---------- 画布初始化 ---------- */
function resize() {
  const cv = canvasRef.value
  if (!cv) return
  const dpr = Math.min(window.devicePixelRatio || 1, 2)
  cv.width = window.innerWidth * dpr
  cv.height = window.innerHeight * dpr
  cv.style.width = window.innerWidth + 'px'
  cv.style.height = window.innerHeight + 'px'
  ctx = cv.getContext('2d')
  ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
  buildPetals()
}

/* ---------- 生成花瓣 ---------- */
function buildPetals() {
  petals = []
  const n = window.innerWidth < 760 ? 26 : 52
  for (let i = 0; i < n; i++) {
    petals.push({
      x: Math.random() * window.innerWidth,
      y: Math.random() * window.innerHeight,
      s: 5 + Math.random() * 8,
      vy: 0.28 + Math.random() * 0.7,
      vx: -0.25 + Math.random() * 0.5,
      rot: Math.random() * Math.PI * 2,
      vr: (-0.9 + Math.random() * 1.8) * 0.012,
      ph: Math.random() * Math.PI * 2,
      sw: 0.4 + Math.random() * 0.8,
      al: 0.45 + Math.random() * 0.45,
      c: COLORS[(Math.random() * COLORS.length) | 0]
    })
  }
}

/* ---------- 单片花瓣轮廓 ---------- */
function drawPetal(p) {
  const s = p.s
  ctx.beginPath()
  ctx.moveTo(0, 0)
  ctx.bezierCurveTo(s * 0.55, -s * 0.68, s * 1.45, -s * 0.22, s * 1.18, s * 0.52)
  ctx.bezierCurveTo(s * 0.72, s * 1.02, s * 0.12, s * 0.82, 0, 0)
  ctx.closePath()
}

/* ---------- 主循环 ---------- */
function loop() {
  const w = window.innerWidth
  const h = window.innerHeight
  ctx.clearRect(0, 0, w, h)

  for (let i = 0; i < petals.length; i++) {
    const p = petals[i]
    p.ph += 0.014
    p.x += p.vx + Math.sin(p.ph) * p.sw
    p.y += p.vy
    p.rot += p.vr

    if (p.y > h + 30) {
      p.y = -30
      p.x = Math.random() * w
    }
    if (p.x > w + 30) p.x = -30
    if (p.x < -30) p.x = w + 30

    ctx.save()
    ctx.translate(p.x, p.y)
    ctx.rotate(p.rot)
    ctx.scale(1, Math.max(0.25, Math.abs(Math.cos(p.ph * 1.6))))
    ctx.globalAlpha = p.al
    ctx.fillStyle = p.c
    drawPetal(p)
    ctx.fill()
    ctx.restore()
  }
  raf = requestAnimationFrame(loop)
}

onMounted(() => {
  window.addEventListener('resize', resize)
  resize()
  if (!reduce) loop()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  cancelAnimationFrame(raf)
})
</script>

<template>
  <div class="backdrop" aria-hidden="true">
    <span class="blob b1"></span>
    <span class="blob b2"></span>
    <span class="blob b3"></span>
    <span class="grid-lines"></span>
    <canvas ref="canvasRef" class="sakura"></canvas>
  </div>
</template>

<style scoped>
.backdrop {
  position: fixed;
  inset: 0;
  z-index: 0;
  overflow: hidden;
  background: radial-gradient(1200px 800px at 12% 8%, #f0e4ff 0%, rgba(240, 228, 255, 0) 60%),
    radial-gradient(1000px 900px at 88% 92%, #e0f2fe 0%, rgba(224, 242, 254, 0) 62%),
    linear-gradient(145deg, #fdf2ff 0%, #f6f2ff 45%, #f0faff 100%);
}

.blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.55;
  mix-blend-mode: multiply;
}
.b1 {
  width: 460px;
  height: 460px;
  background: #e4d0ff;
  left: -80px;
  top: -60px;
  animation: float1 18s ease-in-out infinite;
}
.b2 {
  width: 520px;
  height: 520px;
  background: #c7d2fe;
  right: -120px;
  bottom: -140px;
  animation: float2 22s ease-in-out infinite;
}
.b3 {
  width: 340px;
  height: 340px;
  background: #ffd6ec;
  right: 18%;
  top: -90px;
  animation: float1 26s ease-in-out infinite reverse;
}

.grid-lines {
  position: absolute;
  inset: 0;
  opacity: 0.35;
  background-image: linear-gradient(rgba(255, 255, 255, 0.6) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.6) 1px, transparent 1px);
  background-size: 64px 64px;
  mask-image: radial-gradient(circle at 50% 40%, #000 30%, transparent 78%);
  -webkit-mask-image: radial-gradient(circle at 50% 40%, #000 30%, transparent 78%);
}

.sakura {
  position: absolute;
  inset: 0;
  z-index: 2;
  pointer-events: none;
}
</style>
