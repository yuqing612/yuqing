<script setup>
import characterImg from '../assets/character.jpg'
</script>

<template>
  <div class="visual">
    <img class="photo" :src="characterImg" alt="动漫立绘" draggable="false" />

    <div class="vtag">
      <span class="dot"></span>
      ONLINE · 次元链接已建立
    </div>

    <div class="floaters">
      <span class="f1">
        <svg width="26" height="26" viewBox="0 0 24 24" fill="#ffb3d1">
          <path d="M12 21s-8-4.9-8-10.3C4 7.7 6.7 5 10 5c1.5 0 2.9.7 2 2 .9-1.3 2.3-2 2-2 3.3 0 6 2.7 6 5.7C20 16.1 12 21 12 21z" />
        </svg>
      </span>
      <span class="f2">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="#ffd98a">
          <path d="M12 2l2.4 6.9L21 12l-6.6 3.1L12 22l-2.4-6.9L3 12l6.6-3.1z" />
        </svg>
      </span>
      <span class="f3">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="#a5c9ff">
          <circle cx="12" cy="12" r="3.2" />
          <circle cx="19" cy="6" r="1.7" />
          <circle cx="5.5" cy="17.5" r="2.1" />
        </svg>
      </span>
    </div>

    <div class="quote">
      <p class="jp">夢 幻 の 次 元</p>
      <p class="cn">在梦与现实的夹缝间，与你再次相遇</p>
    </div>
  </div>
</template>

<style scoped>
.visual {
  position: relative;
  overflow: hidden;
  background: #e9e2ff;
  border-right: 1px solid rgba(255, 255, 255, 0.7);
}

.photo {
  position: absolute;
  inset: 0;
  z-index: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center top;
  transform: translate3d(var(--px, 0px), var(--py, 0px), 0);
  transition: transform 1.4s cubic-bezier(0.2, 0.8, 0.25, 1);
}
.visual:hover .photo {
  transform: translate3d(var(--px, 0px), var(--py, 0px), 0) scale(1.04);
}

.visual::after {
  content: "";
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;
  background: linear-gradient(to top, rgba(104, 72, 160, 0.42), transparent 36%),
    linear-gradient(to bottom, rgba(104, 72, 160, 0.18), transparent 20%),
    linear-gradient(to right, rgba(255, 255, 255, 0.12), transparent 24%);
}

.vtag {
  position: absolute;
  z-index: 2;
  left: 26px;
  top: 24px;
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 8px 15px;
  border-radius: 999px;
  font-size: 12.5px;
  letter-spacing: 0.06em;
  font-weight: 600;
  color: #8b5cf6;
  background: rgba(255, 255, 255, 0.75);
  border: 1px solid rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 20px -8px rgba(139, 92, 246, 0.35);
}
.vtag .dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #34d399;
  box-shadow: 0 0 0 4px rgba(52, 211, 153, 0.22);
  animation: pulse 2s infinite;
}

.floaters span {
  position: absolute;
  z-index: 2;
  animation: sway 7s ease-in-out infinite;
  filter: drop-shadow(0 6px 10px rgba(255, 111, 156, 0.35));
}
.f1 {
  left: 12%;
  top: 20%;
  animation-delay: 0.4s;
}
.f2 {
  right: 14%;
  top: 32%;
  animation-delay: 1.6s;
}
.f3 {
  right: 22%;
  bottom: 16%;
  animation-delay: 2.6s;
}

.quote {
  position: absolute;
  z-index: 2;
  left: 0;
  right: 0;
  bottom: 26px;
  text-align: center;
  padding: 0 30px;
}
.quote .jp {
  font-size: 15px;
  letter-spacing: 0.22em;
  color: #fff;
  font-weight: 700;
  text-shadow: 0 2px 14px rgba(90, 50, 140, 0.5);
}
.quote .cn {
  font-size: 12.5px;
  color: rgba(255, 255, 255, 0.88);
  margin-top: 8px;
  letter-spacing: 0.1em;
  text-shadow: 0 1px 10px rgba(90, 50, 140, 0.5);
}
</style>
