<script setup>
defineProps({
  show: { type: Boolean, default: false },
  nickname: { type: String, default: '' }
})

const emit = defineEmits(['close'])
</script>

<template>
  <Transition name="fade">
    <div v-if="show" class="done">
      <div class="inner">
        <div class="ring">
          <svg width="42" height="42" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 6L9 17l-5-5" />
          </svg>
        </div>
        <h2>登录成功</h2>
        <p>
          欢迎回来，<b>{{ nickname }}</b
          ><br />次元门已开启，正在跳转…
        </p>
        <button class="back" type="button" @click="emit('close')">返回登录</button>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.done {
  position: absolute;
  inset: 0;
  z-index: 9;
  display: grid;
  place-items: center;
  border-radius: var(--radius);
  background: linear-gradient(150deg, rgba(255, 255, 255, 0.92), rgba(255, 255, 255, 0.82));
  backdrop-filter: blur(14px);
}
.done .inner {
  text-align: center;
  transform: translateY(20px);
  transition: transform 0.6s 0.1s cubic-bezier(0.2, 0.9, 0.25, 1);
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
.fade-enter-active .inner {
  transform: none;
}
.ring {
  width: 96px;
  height: 96px;
  margin: 0 auto 22px;
  border-radius: 50%;
  background: var(--grad);
  display: grid;
  place-items: center;
  box-shadow: 0 20px 44px -16px rgba(192, 132, 252, 0.95);
  animation: pop 0.6s cubic-bezier(0.2, 1.5, 0.4, 1) both;
}
.done h2 {
  font-size: 23px;
  margin-bottom: 10px;
}
.done p {
  font-size: 13.5px;
  color: var(--ink-soft);
  line-height: 1.8;
}
.done p b {
  color: var(--accent);
}
.done .back {
  margin-top: 24px;
  padding: 11px 26px;
  border-radius: 12px;
  border: 0;
  cursor: pointer;
  background: var(--ink);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.08em;
  transition: 0.24s;
}
.done .back:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 28px -12px rgba(47, 36, 69, 0.7);
}
</style>
