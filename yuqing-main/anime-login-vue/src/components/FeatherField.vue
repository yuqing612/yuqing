<script setup>
/* =========================================================
   羽毛漂浮特效层（首页专用）
   - 同色系柔和羽毛：粉 / 紫 / 青 / 白，低透明度 + 柔光
   - 运动：外层持续向下落（featherFall），内层左右轻摆 + 微旋（featherSway）
   - 层级：position:fixed + z-index:-1 → 固定在视口、落在樱花背景之上，
     但永远在所有内容（导航栏 + 正文）之下，绝不遮挡文字
   - 尊重 prefers-reduced-motion
   ========================================================= */
// x: 水平列；sz: 大小；dur: 下落时长；delay: 负延迟让羽毛错落分布在画面中
// sway: 摆动力度时长；rot: 基准旋转；color: 同色系柔色
const feathers = [
  { x: '5%', sz: 30, color: 'rgba(255,183,208,0.55)', dur: 17, delay: -2, sway: 4.2, rot: -12 },
  { x: '14%', sz: 22, color: 'rgba(231,198,255,0.55)', dur: 21, delay: -9, sway: 5.1, rot: 18 },
  { x: '23%', sz: 34, color: 'rgba(255,255,255,0.6)', dur: 15, delay: -5, sway: 3.8, rot: -22 },
  { x: '33%', sz: 18, color: 'rgba(207,234,255,0.6)', dur: 24, delay: -13, sway: 4.6, rot: 8 },
  { x: '42%', sz: 28, color: 'rgba(255,183,208,0.5)', dur: 19, delay: -1, sway: 5.4, rot: -8 },
  { x: '52%', sz: 24, color: 'rgba(231,198,255,0.5)', dur: 22, delay: -7, sway: 4.0, rot: 14 },
  { x: '61%', sz: 32, color: 'rgba(255,255,255,0.55)', dur: 16, delay: -11, sway: 4.8, rot: -16 },
  { x: '70%', sz: 20, color: 'rgba(207,234,255,0.55)', dur: 23, delay: -4, sway: 5.2, rot: 10 },
  { x: '79%', sz: 26, color: 'rgba(255,183,208,0.5)', dur: 18, delay: -14, sway: 3.9, rot: 6 },
  { x: '87%', sz: 22, color: 'rgba(231,198,255,0.5)', dur: 20, delay: -3, sway: 4.4, rot: -10 },
  { x: '93%', sz: 30, color: 'rgba(255,255,255,0.5)', dur: 25, delay: -8, sway: 5.0, rot: 20 },
  { x: '48%', sz: 16, color: 'rgba(207,234,255,0.6)', dur: 14, delay: -6, sway: 3.6, rot: -4 },
  { x: '29%', sz: 21, color: 'rgba(255,183,208,0.5)', dur: 26, delay: -16, sway: 5.3, rot: 12 },
  { x: '66%', sz: 27, color: 'rgba(231,198,255,0.5)', dur: 17, delay: -10, sway: 4.1, rot: -14 }
]
</script>

<template>
  <div class="feather-field" aria-hidden="true">
    <span
      v-for="(f, i) in feathers"
      :key="i"
      class="feather"
      :style="{
        left: f.x,
        animationDuration: f.dur + 's',
        animationDelay: f.delay + 's'
      }"
    >
      <span
        class="f-inner"
        :style="{
          width: f.sz + 'px',
          height: f.sz + 'px',
          '--rot': f.rot + 'deg',
          animationDuration: f.sway + 's'
        }"
      >
        <svg viewBox="0 0 32 32">
          <path
            class="plume"
            d="M26 4C14 6 7 14 5 26c0 0 2 2 4 2 10-2 16-9 18-19 1-4-1-5-1-5z"
            :fill="f.color"
          />
          <path
            class="rib"
            d="M22 9C16 13 11 18 8 25"
            stroke="rgba(255,255,255,0.75)"
            stroke-width="1"
            fill="none"
            stroke-linecap="round"
          />
        </svg>
      </span>
    </span>
  </div>
</template>

<style scoped>
.feather-field {
  position: fixed;
  inset: 0;
  z-index: -1;
  pointer-events: none;
  overflow: hidden;
}
.feather {
  position: absolute;
  top: 0;
  display: block;
  animation-name: featherFall;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
  will-change: transform;
}
.f-inner {
  display: block;
  animation-name: featherSway;
  animation-timing-function: ease-in-out;
  animation-iteration-count: infinite;
  animation-direction: alternate;
  filter: drop-shadow(0 6px 12px rgba(192, 132, 252, 0.25));
  transform-origin: center;
}
.f-inner svg {
  width: 100%;
  height: 100%;
  display: block;
}

/* 外层：持续从顶部向下落 */
@keyframes featherFall {
  0% {
    transform: translateY(-16vh);
  }
  100% {
    transform: translateY(116vh);
  }
}
/* 内层：左右轻摆 + 微旋，模拟羽毛飘落 */
@keyframes featherSway {
  0% {
    transform: translateX(-16px) rotate(calc(var(--rot) - 9deg));
  }
  100% {
    transform: translateX(16px) rotate(calc(var(--rot) + 9deg));
  }
}

@media (prefers-reduced-motion: reduce) {
  .feather,
  .f-inner {
    animation: none !important;
  }
}
</style>
