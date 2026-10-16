<script setup>
/* =========================================================
   同色系发光粒子星座特效
   配色完全复刻登录界面调色板：粉 / 青 / 紫
   - 粒子缓慢漂浮 + 呼吸式明灭
   - 邻近粒子自动连成细线（constellation）
   - 鼠标 / 触摸附近粒子被轻柔推开，并连线到指针
   - 尊重 prefers-reduced-motion（静止渲染一帧）
   ========================================================= */
import { ref, onMounted, onBeforeUnmount } from 'vue'

const canvasRef = ref(null)
const reduce =
  typeof window !== 'undefined' &&
  window.matchMedia('(prefers-reduced-motion: reduce)').matches

// 与界面一致的色系：粉、青(天空蓝)、紫
const PALETTE = [
  { r: 255, g: 143, b: 177 }, // --pink  #ff8fb1
  { r: 255, g: 111, b: 156 }, // --pink-deep #ff6f9c
  { r: 125, g: 211, b: 252 }, // --sky   #7dd3fc (青)
  { r: 192, g: 132, b: 252 }, // --violet #c084fc
  { r: 167, g: 139, b: 250 }  // --violet2 #a78bfa
]

const LINK_DIST = 132
const sprites = []
let ctx = null
let raf = 0
let particles = []
let w = 0
let h = 0
let dpr = 1
const mouse = { x: -9999, y: -9999, active: false }

const rnd = (min, max) => min + Math.random() * (max - min)

/* ---------- 预渲染每色发光精灵，循环内直接 drawImage，避免逐帧建渐变 ---------- */
function buildSprites() {
  sprites.length = 0
  for (const c of PALETTE) {
    const s = 64
    const off = document.createElement('canvas')
    off.width = s
    off.height = s
    const octx = off.getContext('2d')
    const g = octx.createRadialGradient(s / 2, s / 2, 0, s / 2, s / 2, s / 2)
    g.addColorStop(0, `rgba(${c.r},${c.g},${c.b},0.95)`)
    g.addColorStop(0.22, `rgba(${c.r},${c.g},${c.b},0.55)`)
    g.addColorStop(0.55, `rgba(${c.r},${c.g},${c.b},0.18)`)
    g.addColorStop(1, `rgba(${c.r},${c.g},${c.b},0)`)
    octx.fillStyle = g
    octx.fillRect(0, 0, s, s)
    sprites.push(off)
  }
}

/* ---------- 画布初始化 ---------- */
function resize() {
  const cv = canvasRef.value
  if (!cv) return
  dpr = Math.min(window.devicePixelRatio || 1, 2)
  w = window.innerWidth
  h = window.innerHeight
  cv.width = w * dpr
  cv.height = h * dpr
  cv.style.width = w + 'px'
  cv.style.height = h + 'px'
  ctx = cv.getContext('2d')
  ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
  buildParticles()
  if (reduce) renderStatic()
}

/* ---------- 生成粒子 ---------- */
function buildParticles() {
  particles = []
  const n = Math.max(34, Math.min(96, Math.round((w * h) / 15000)))
  for (let i = 0; i < n; i++) {
    const ci = (Math.random() * PALETTE.length) | 0
    particles.push({
      x: Math.random() * w,
      y: Math.random() * h,
      vx: rnd(-0.22, 0.22),
      vy: rnd(-0.22, 0.22),
      r: rnd(1.1, 3.0),
      ci,
      base: rnd(0.45, 0.9),
      tw: Math.random() * Math.PI * 2,
      tws: rnd(0.008, 0.026)
    })
  }
}

/* ---------- 画一帧 ---------- */
function renderStatic() {
  if (!ctx) return
  ctx.clearRect(0, 0, w, h)
  drawParticles(1)
  drawLinks()
}

function drawParticles(alphaScale) {
  for (let i = 0; i < particles.length; i++) {
    const p = particles[i]
    const sprite = sprites[p.ci]
    const size = p.r * 9
    ctx.globalAlpha = Math.min(1, p.base * alphaScale)
    ctx.drawImage(sprite, p.x - size / 2, p.y - size / 2, size, size)
  }
  ctx.globalAlpha = 1
}

function drawLinks() {
  for (let i = 0; i < particles.length; i++) {
    const a = particles[i]
    const ac = PALETTE[a.ci]
    for (let j = i + 1; j < particles.length; j++) {
      const b = particles[j]
      const dx = a.x - b.x
      const dy = a.y - b.y
      const d = Math.sqrt(dx * dx + dy * dy)
      if (d < LINK_DIST) {
        const o = (1 - d / LINK_DIST) * 0.16
        ctx.strokeStyle = `rgba(${ac.r},${ac.g},${ac.b},${o})`
        ctx.lineWidth = 1
        ctx.beginPath()
        ctx.moveTo(a.x, a.y)
        ctx.lineTo(b.x, b.y)
        ctx.stroke()
      }
    }
    if (mouse.active) {
      const dx = a.x - mouse.x
      const dy = a.y - mouse.y
      const d = Math.sqrt(dx * dx + dy * dy)
      const R = LINK_DIST * 1.5
      if (d < R) {
        const o = (1 - d / R) * 0.28
        ctx.strokeStyle = `rgba(192,132,252,${o})`
        ctx.lineWidth = 1
        ctx.beginPath()
        ctx.moveTo(a.x, a.y)
        ctx.lineTo(mouse.x, mouse.y)
        ctx.stroke()
      }
    }
  }
}

/* ---------- 主循环 ---------- */
function loop() {
  ctx.clearRect(0, 0, w, h)

  const R = 120
  for (let i = 0; i < particles.length; i++) {
    const p = particles[i]
    p.tw += p.tws
    const twA = p.base * (0.65 + 0.35 * Math.sin(p.tw))

    p.x += p.vx
    p.y += p.vy

    // 鼠标附近轻柔推开
    if (mouse.active) {
      const dx = p.x - mouse.x
      const dy = p.y - mouse.y
      const d2 = dx * dx + dy * dy
      if (d2 < R * R) {
        const d = Math.sqrt(d2) || 1
        const f = ((R - d) / R) * 0.8
        p.x += (dx / d) * f
        p.y += (dy / d) * f
      }
    }

    // 环绕边界
    if (p.x < -24) p.x = w + 24
    if (p.x > w + 24) p.x = -24
    if (p.y < -24) p.y = h + 24
    if (p.y > h + 24) p.y = -24

    const sprite = sprites[p.ci]
    const size = p.r * 9
    ctx.globalAlpha = Math.min(1, twA)
    ctx.drawImage(sprite, p.x - size / 2, p.y - size / 2, size, size)
  }
  ctx.globalAlpha = 1

  drawLinks()
  raf = requestAnimationFrame(loop)
}

/* ---------- 指针交互 ---------- */
function onMove(e) {
  const t = e.touches ? e.touches[0] : e
  mouse.x = t.clientX
  mouse.y = t.clientY
  mouse.active = true
}
function onLeave() {
  mouse.active = false
  mouse.x = -9999
  mouse.y = -9999
}

onMounted(() => {
  buildSprites()
  window.addEventListener('resize', resize)
  window.addEventListener('pointermove', onMove, { passive: true })
  window.addEventListener('pointerleave', onLeave)
  resize()
  if (!reduce) loop()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  window.removeEventListener('pointermove', onMove)
  window.removeEventListener('pointerleave', onLeave)
  cancelAnimationFrame(raf)
})
</script>

<template>
  <canvas ref="canvasRef" class="particle-field" aria-hidden="true" />
</template>

<style scoped>
.particle-field {
  position: absolute;
  inset: 0;
  z-index: 3; /* 位于樱花画布(z2)之上、卡片(z1, 整体 backdrop 之下) */
  pointer-events: none;
  mix-blend-mode: screen;
}
</style>
