<script setup>
import { ref, computed, nextTick } from 'vue'

const emit = defineEmits(['success'])

/* ---------- 表单状态 ---------- */
const username = ref('')
const password = ref('')
const errorMessage = ref('')
const loading = ref(false)
const showPassword = ref(false)
const keepLogin = ref(true)

/* ---------- 抖动状态 ---------- */
const shakeUser = ref(false)
const shakePass = ref(false)
let shakeTimers = []

function triggerShake(target) {
  const key = target === 'user' ? shakeUser : shakePass
  target === 'user' ? (shakePass.value = false) : (shakeUser.value = false)
  key.value = false
  nextTick(() => {
    key.value = true
    const t = setTimeout(() => {
      key.value = false
    }, 460)
    shakeTimers.push(t)
  })
}

const passwordType = computed(() => (showPassword.value ? 'text' : 'password'))

/* ---------- 校验规则 ---------- */
const RULES = {
  requiredUser: '请输入用户名或邮箱',
  badEmail: '邮箱格式不太对哦～',
  requiredPass: '请输入密码',
  shortPass: '密码至少需要 6 位字符'
}

function validate() {
  const name = username.value.trim()
  const pwd = password.value

  if (!name) {
    errorMessage.value = RULES.requiredUser
    triggerShake('user')
    return null
  }
  if (name.includes('@') && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(name)) {
    errorMessage.value = RULES.badEmail
    triggerShake('user')
    return null
  }
  if (!pwd) {
    errorMessage.value = RULES.requiredPass
    triggerShake('pass')
    return null
  }
  if (pwd.length < 6) {
    errorMessage.value = RULES.shortPass
    triggerShake('pass')
    return null
  }
  return { name, pwd }
}

/* ---------- 提交 ---------- */
let submitTimer = null
function onSubmit() {
  if (loading.value) return
  const payload = validate()
  if (!payload) return

  errorMessage.value = ''
  loading.value = true

  submitTimer = setTimeout(() => {
    loading.value = false
    const nickname = payload.name.includes('@') ? payload.name.split('@')[0] : payload.name
    emit('success', nickname, { remember: keepLogin.value })
  }, 1250)
}

/* ---------- 对外暴露：重置表单 ---------- */
function reset() {
  username.value = ''
  password.value = ''
  errorMessage.value = ''
  loading.value = false
  showPassword.value = false
  shakeUser.value = false
  shakePass.value = false
}
defineExpose({ reset })

/* ---------- 组件卸载清定时器 ---------- */
import { onBeforeUnmount } from 'vue'
onBeforeUnmount(() => {
  clearTimeout(submitTimer)
  shakeTimers.forEach(clearTimeout)
  shakeTimers = []
})
</script>

<template>
  <form class="panel" novalidate @submit.prevent="onSubmit">
    <div class="brand">
      <div class="logo">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="#fff">
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

    <h1>欢迎回来，旅人</h1>
    <p class="sub">登录以继续你的二次元旅程<br />尚未完成的冒险还在等着你 ✦</p>

    <p class="err" role="alert">{{ errorMessage }}</p>

    <div class="field" :class="{ shake: shakeUser }">
      <input id="user" v-model="username" type="text" placeholder=" " autocomplete="username" />
      <label for="user">用户名 / 邮箱</label>
      <span class="ico">
        <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
          <circle cx="12" cy="7" r="4" />
        </svg>
      </span>
    </div>

    <div class="field" :class="{ shake: shakePass }">
      <input id="pass" v-model="password" :type="passwordType" placeholder=" " autocomplete="current-password" />
      <label for="pass">密码</label>
      <span class="ico">
        <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="4" y="11" width="16" height="10" rx="2.5" />
          <path d="M8 11V7a4 4 0 0 1 8 0v4" />
        </svg>
      </span>
      <button class="toggle" type="button" aria-label="显示密码" @click="showPassword = !showPassword">
        <svg v-if="showPassword" width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M2 2l20 20" />
          <path d="M6.7 6.8C4 8.6 1.5 12 1.5 12s3.5 6.5 10.5 6.5c1.6 0 3-.4 4.2-1" />
          <path d="M9.9 5.5A9.6 9.6 0 0 1 12 5.5c7 0 10.5 6.5 10.5 6.5a19 19 0 0 1-2.7 3.6" />
        </svg>
        <svg v-else width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M1.5 12S5 5.5 12 5.5 22.5 12 22.5 12 19 18.5 12 18.5 1.5 12 1.5 12z" />
          <circle cx="12" cy="12" r="3.2" />
        </svg>
      </button>
    </div>

    <div class="row">
      <label class="check">
        <input v-model="keepLogin" type="checkbox" />
        <span class="box">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="3.4" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 6L9 17l-5-5" />
          </svg>
        </span>
        记住登录状态
      </label>
      <a class="forgot" href="#" @click.prevent>忘记密码？</a>
    </div>

    <button class="btn" type="submit" :class="{ loading }">
      <span>
        登 录
        <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round">
          <path d="M5 12h14M13 6l6 6-6 6" />
        </svg>
      </span>
      <i class="spin"></i>
    </button>

    <div class="divider">或 使用第三方账号</div>

    <div class="sso">
      <button type="button" title="QQ">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
          <path d="M12 2c2.6 0 4.6 2 4.6 4.6 0 .9-.2 1.7-.6 2.4.9.5 1.7 1.1 2.4 1.9-1 .3-2 .5-3 .6L12 22l-3.4-10.5c-1-.1-2-.3-3-.6.7-.8 1.5-1.4 2.4-1.9-.4-.7-.6-1.5-.6-2.4C7.4 4 9.4 2 12 2z" />
        </svg>
      </button>
      <button type="button" title="微信">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
          <path d="M9 3C5 3 2 5.7 2 9c0 1.9 1 3.6 2.6 4.7L4 16l2.7-1.4c.7.2 1.5.3 2.3.3h.6a5.6 5.6 0 0 1-.2-1.4c0-3.1 3-5.6 6.7-5.6h.6C15.8 5.1 12.8 3 9 3z" />
          <path d="M22 14.5c0-2.7-2.7-4.9-6-4.9s-6 2.2-6 4.9 2.7 4.9 6 4.9c.8 0 1.5-.1 2.2-.3L21 21l-.7-2.4c1-.8 1.7-2 1.7-3.1z" />
        </svg>
      </button>
      <button type="button" title="哔哩哔哩">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
          <path d="M6.5 3.5l3 2.2h5l3-2.2 2 2-1.7 1.6c1.3.9 2.2 2.3 2.2 4v6.4c0 2.5-2.7 4.5-6 4.5s-6-2-6-4.5V11c0-1.7.9-3.1 2.2-4L5.5 5.5z" />
          <path d="M8.5 11l-1.5-.9M15.5 11l1.5-.9" stroke="#fff" stroke-width="1.6" stroke-linecap="round" />
        </svg>
      </button>
    </div>

    <p class="signup">还没有账号？<a href="#" @click.prevent>立即注册 →</a></p>
  </form>
</template>

<style scoped>
.panel {
  padding: 52px 56px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 品牌 */
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 36px;
}
.logo {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: var(--grad);
  display: grid;
  place-items: center;
  box-shadow: 0 10px 22px -8px rgba(192, 132, 252, 0.7);
  animation: logoFloat 4s ease-in-out infinite;
}
.brand-text b {
  font-size: 17px;
  letter-spacing: 0.04em;
  display: block;
}
.brand-text small {
  font-size: 11.5px;
  color: var(--ink-soft);
  letter-spacing: 0.24em;
  text-transform: uppercase;
}

h1 {
  font-size: 27px;
  letter-spacing: 0.02em;
  margin-bottom: 9px;
  font-weight: 800;
}
.sub {
  font-size: 13.5px;
  color: var(--ink-soft);
  margin-bottom: 30px;
  line-height: 1.7;
}

/* 错误提示 */
.err {
  color: #f43f5e;
  font-size: 12px;
  margin: -8px 0 12px;
  min-height: 16px;
  letter-spacing: 0.02em;
}

/* 输入框 */
.field {
  position: relative;
  margin-bottom: 18px;
}
.field input {
  width: 100%;
  height: 56px;
  padding: 22px 48px 8px 44px;
  border-radius: 16px;
  border: 1.5px solid var(--line);
  background: rgba(255, 255, 255, 0.75);
  font-size: 14.5px;
  color: var(--ink);
  outline: none;
  font-family: inherit;
  transition: border-color 0.25s, box-shadow 0.25s, background 0.25s;
}
.field input::placeholder {
  color: transparent;
}
.field label {
  position: absolute;
  left: 44px;
  top: 18px;
  font-size: 14px;
  color: var(--ink-soft);
  pointer-events: none;
  transition: 0.24s cubic-bezier(0.2, 0.8, 0.25, 1);
  letter-spacing: 0.02em;
}
.field input:focus + label,
.field input:not(:placeholder-shown) + label {
  top: 8px;
  font-size: 11px;
  color: var(--accent);
  letter-spacing: 0.1em;
  font-weight: 600;
}
.field input:focus {
  border-color: #c9a7ff;
  background: #fff;
  box-shadow: 0 0 0 5px rgba(192, 132, 252, 0.14);
}
.field .ico {
  position: absolute;
  left: 15px;
  top: 18px;
  color: #b9aedd;
  transition: color 0.25s, transform 0.25s;
}
.field input:focus ~ .ico {
  color: var(--accent);
  transform: scale(1.1);
}
.toggle {
  position: absolute;
  right: 14px;
  top: 18px;
  width: 30px;
  height: 30px;
  border: 0;
  background: none;
  cursor: pointer;
  color: #b9aedd;
  border-radius: 8px;
  display: grid;
  place-items: center;
  transition: 0.2s;
}
.toggle:hover {
  color: var(--accent);
  background: rgba(192, 132, 252, 0.1);
}

/* 记住我 / 忘记密码 */
.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 6px 0 26px;
  font-size: 13px;
}
.check {
  display: flex;
  align-items: center;
  gap: 9px;
  cursor: pointer;
  color: var(--ink-soft);
  user-select: none;
}
.check input {
  display: none;
}
.box {
  width: 19px;
  height: 19px;
  border-radius: 6px;
  border: 1.5px solid var(--line);
  background: rgba(255, 255, 255, 0.8);
  display: grid;
  place-items: center;
  transition: 0.22s;
}
.check input:checked + .box {
  background: var(--grad);
  border-color: transparent;
  box-shadow: 0 6px 14px -6px rgba(192, 132, 252, 0.8);
}
.box svg {
  opacity: 0;
  transform: scale(0.4);
  transition: 0.22s cubic-bezier(0.2, 1.4, 0.4, 1);
}
.check input:checked + .box svg {
  opacity: 1;
  transform: scale(1);
}
.check:hover .box {
  border-color: #c9a7ff;
}
.forgot {
  color: var(--accent);
  text-decoration: none;
  font-weight: 600;
  position: relative;
}
.forgot::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  bottom: -3px;
  height: 1.5px;
  background: currentColor;
  transform: scaleX(0);
  transform-origin: right;
  transition: transform 0.3s;
}
.forgot:hover::after {
  transform: scaleX(1);
  transform-origin: left;
}

/* 主按钮 */
.btn {
  position: relative;
  width: 100%;
  height: 54px;
  border: 0;
  border-radius: 16px;
  cursor: pointer;
  overflow: hidden;
  background: var(--grad);
  background-size: 220% 100%;
  color: #fff;
  font-size: 15.5px;
  font-weight: 700;
  letter-spacing: 0.16em;
  box-shadow: 0 16px 34px -14px rgba(192, 132, 252, 0.95);
  transition: transform 0.22s cubic-bezier(0.2, 0.8, 0.25, 1), box-shadow 0.22s, background-position 0.6s;
}
.btn:hover {
  transform: translateY(-2.5px);
  box-shadow: 0 22px 42px -14px rgba(192, 132, 252, 1);
  background-position: 100% 0;
}
.btn:active {
  transform: translateY(0) scale(0.985);
}
.btn::before {
  content: "";
  position: absolute;
  top: 0;
  left: -120%;
  width: 60%;
  height: 100%;
  background: linear-gradient(100deg, transparent, rgba(255, 255, 255, 0.55), transparent);
  transition: left 0.65s;
}
.btn:hover::before {
  left: 130%;
}
.btn span {
  position: relative;
  z-index: 2;
  display: inline-flex;
  align-items: center;
  gap: 9px;
}
.btn.loading {
  pointer-events: none;
  color: transparent;
}
.spin {
  position: absolute;
  inset: 0;
  margin: auto;
  width: 22px;
  height: 22px;
  border: 2.5px solid rgba(255, 255, 255, 0.35);
  border-top-color: #fff;
  border-radius: 50%;
  opacity: 0;
  animation: spin360 0.7s linear infinite;
}
.btn.loading .spin {
  opacity: 1;
}

/* 第三方登录 */
.divider {
  display: flex;
  align-items: center;
  gap: 14px;
  margin: 26px 0 20px;
  color: #a99fc4;
  font-size: 11.5px;
  letter-spacing: 0.1em;
}
.divider::before,
.divider::after {
  content: "";
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--line), transparent);
}
.sso {
  display: flex;
  gap: 12px;
}
.sso button {
  flex: 1;
  height: 48px;
  border-radius: 14px;
  border: 1.5px solid var(--line);
  background: rgba(255, 255, 255, 0.78);
  cursor: pointer;
  display: grid;
  place-items: center;
  color: #7a6f96;
  transition: transform 0.22s, box-shadow 0.22s, border-color 0.22s, background 0.22s;
}
.sso button:hover {
  transform: translateY(-3px);
  background: #fff;
  border-color: #e3d4ff;
  box-shadow: 0 14px 26px -14px rgba(126, 104, 180, 0.5);
  color: var(--accent);
}

.signup {
  margin-top: 26px;
  text-align: center;
  font-size: 13px;
  color: var(--ink-soft);
}
.signup a {
  color: var(--accent);
  font-weight: 700;
  text-decoration: none;
}
.signup a:hover {
  text-decoration: underline;
}

/* 抖动 */
.shake {
  animation: shake 0.42s;
}

/* ---------- 响应式 ---------- */
@media (max-width: 920px) {
  .panel {
    padding: 34px 26px 30px;
  }
  .brand {
    margin-bottom: 26px;
  }
  h1 {
    font-size: 23px;
  }
}
</style>
