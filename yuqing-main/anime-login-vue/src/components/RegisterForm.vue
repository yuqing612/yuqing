<script setup>
import { ref, computed, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { register } from '../api'

const emit = defineEmits(['switch'])

/* ---------- 表单状态 ---------- */
const username = ref('')
const email = ref('')
const nickname = ref('')
const password = ref('')
const confirmPwd = ref('')
const errorMessage = ref('')
const okMessage = ref('')
const loading = ref(false)
const showPassword = ref(false)

/* ---------- 抖动状态 ---------- */
const shakeKey = ref('')
let shakeTimers = []

function triggerShake(key) {
  shakeKey.value = ''
  nextTick(() => {
    shakeKey.value = key
    const t = setTimeout(() => {
      shakeKey.value = ''
    }, 460)
    shakeTimers.push(t)
  })
}

const passwordType = computed(() => (showPassword.value ? 'text' : 'password'))

/* ---------- 校验规则（与后端保持一致） ---------- */
const PASSWORD_RULE = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{6,15}$/
const EMAIL_RULE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

/* ---------- 图形验证码（前端校验，演示用） ---------- */
const captchaText = ref('')
const captchaInput = ref('')
const captchaCanvas = ref(null)
// 去掉易混淆字符 0/O/1/I/L
const CAPTCHA_CHARS = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
const CAPTCHA_COLORS = ['#b06ab3', '#c084fc', '#7dd3fc', '#ff6f9c', '#a78bfa']

function genCaptcha() {
  let s = ''
  for (let i = 0; i < 4; i++) {
    s += CAPTCHA_CHARS[Math.floor(Math.random() * CAPTCHA_CHARS.length)]
  }
  captchaText.value = s
  drawCaptcha()
}

function drawCaptcha() {
  const canvas = captchaCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  const w = canvas.width
  const h = canvas.height
  // 背景
  ctx.clearRect(0, 0, w, h)
  const grad = ctx.createLinearGradient(0, 0, w, h)
  grad.addColorStop(0, '#f4edff')
  grad.addColorStop(1, '#e9f5ff')
  ctx.fillStyle = grad
  ctx.fillRect(0, 0, w, h)
  // 字符
  ctx.textBaseline = 'middle'
  const text = captchaText.value
  for (let i = 0; i < text.length; i++) {
    ctx.save()
    ctx.font = '700 30px "Courier New", monospace'
    const x = 16 + i * 24
    const y = h / 2 + (Math.random() * 8 - 4)
    const angle = (Math.random() - 0.5) * 0.55
    ctx.translate(x, y)
    ctx.rotate(angle)
    ctx.fillStyle = CAPTCHA_COLORS[Math.floor(Math.random() * CAPTCHA_COLORS.length)]
    ctx.fillText(text[i], 0, 0)
    ctx.restore()
  }
  // 干扰线
  for (let i = 0; i < 4; i++) {
    ctx.strokeStyle = CAPTCHA_COLORS[Math.floor(Math.random() * CAPTCHA_COLORS.length)]
    ctx.globalAlpha = 0.35
    ctx.beginPath()
    ctx.moveTo(Math.random() * w, Math.random() * h)
    ctx.lineTo(Math.random() * w, Math.random() * h)
    ctx.stroke()
  }
  ctx.globalAlpha = 1
  // 干扰点
  for (let i = 0; i < 22; i++) {
    ctx.fillStyle = CAPTCHA_COLORS[Math.floor(Math.random() * CAPTCHA_COLORS.length)]
    ctx.beginPath()
    ctx.arc(Math.random() * w, Math.random() * h, 1.3, 0, Math.PI * 2)
    ctx.fill()
  }
}

/* ---------- 密码安全等级 ---------- */
const PWD_CRITERIA = [
  { label: '6-15 位', test: (v) => v.length >= 6 && v.length <= 15 },
  { label: '小写字母', test: (v) => /[a-z]/.test(v) },
  { label: '大写字母', test: (v) => /[A-Z]/.test(v) },
  { label: '数字', test: (v) => /\d/.test(v) },
  { label: '特殊字符', test: (v) => /[\W_]/.test(v) },
  { label: '≥ 12 位', test: (v) => v.length >= 12 }
]

const criteriaList = computed(() =>
  PWD_CRITERIA.map((c) => ({ label: c.label, met: c.test(password.value) }))
)

const strengthMeta = computed(() => {
  const v = password.value
  if (!v) return { score: 0, text: '', color: 'var(--line)' }
  const met = PWD_CRITERIA.filter((c) => c.test(v)).length
  let level
  if (met <= 2) level = 1
  else if (met === 3) level = 2
  else if (met <= 5) level = 3
  else level = 4
  const map = {
    1: { text: '弱', tip: '建议混合更多字符类型', color: '#f43f5e' },
    2: { text: '中', tip: '再加点复杂度就更稳了', color: '#fb923c' },
    3: { text: '强', tip: '不错，账号比较安全', color: '#38bdf8' },
    4: { text: '极强', tip: '铜墙铁壁，给你点赞', color: '#34d399' }
  }
  return { score: level, ...map[level] }
})
const strength = computed(() => strengthMeta.value.score)
const strengthText = computed(() => strengthMeta.value.text)
const strengthColor = computed(() => strengthMeta.value.color)
const strengthTip = computed(() => strengthMeta.value.tip)

function validate() {
  const name = username.value.trim()
  const mail = email.value.trim()
  const pwd = password.value

  if (!name) {
    errorMessage.value = '请输入用户名'
    triggerShake('user')
    return null
  }
  if (name.length < 2 || name.length > 32) {
    errorMessage.value = '用户名长度需在 2-32 位之间'
    triggerShake('user')
    return null
  }
  if (mail && !EMAIL_RULE.test(mail)) {
    errorMessage.value = '邮箱格式不太对哦～'
    triggerShake('email')
    return null
  }
  if (!PASSWORD_RULE.test(pwd)) {
    errorMessage.value = '密码需为 6-15 位，且包含数字、大小写字母和特殊字符'
    triggerShake('pass')
    return null
  }
  if (confirmPwd.value !== pwd) {
    errorMessage.value = '两次输入的密码不一致'
    triggerShake('confirm')
    return null
  }
  // 图形验证码校验
  if (!captchaInput.value.trim()) {
    errorMessage.value = '请输入图形验证码'
    triggerShake('captcha')
    return null
  }
  if (captchaInput.value.trim().toUpperCase() !== captchaText.value) {
    errorMessage.value = '验证码不正确，请重新输入'
    triggerShake('captcha')
    captchaInput.value = ''
    genCaptcha()
    return null
  }
  return { name, mail, pwd, nick: nickname.value.trim() }
}

/* ---------- 提交 ---------- */
async function onSubmit() {
  if (loading.value) return
  const payload = validate()
  if (!payload) return

  errorMessage.value = ''
  okMessage.value = ''
  loading.value = true

  try {
    await register({
      username: payload.name,
      email: payload.mail || null,
      password: payload.pwd,
      nickname: payload.nick || null
    })
    okMessage.value = '注册成功，次元门已为你打开 ✦'
  } catch (e) {
    errorMessage.value = e.message || '注册失败，请稍后再试'
    triggerShake(/密码/.test(errorMessage.value) ? 'pass' : 'user')
    genCaptcha()
    captchaInput.value = ''
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  genCaptcha()
})

onBeforeUnmount(() => {
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

    <h1>加入我们，旅人</h1>
    <p class="sub">注册一个新账号<br />开启属于你的二次元冒险 ✦</p>

    <p class="err" role="alert">{{ errorMessage }}</p>
    <p v-if="okMessage" class="ok">{{ okMessage }}</p>

    <div class="field" :class="{ shake: shakeKey === 'user' }">
      <input id="reg-user" v-model="username" type="text" placeholder=" " autocomplete="username" />
      <label for="reg-user">用户名</label>
      <span class="ico">
        <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
          <circle cx="12" cy="7" r="4" />
        </svg>
      </span>
    </div>

    <div class="field" :class="{ shake: shakeKey === 'email' }">
      <input id="reg-email" v-model="email" type="text" placeholder=" " autocomplete="email" />
      <label for="reg-email">邮箱（选填）</label>
      <span class="ico">
        <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="5" width="18" height="14" rx="2.5" />
          <path d="m3 7 9 6 9-6" />
        </svg>
      </span>
    </div>

    <div class="field">
      <input id="reg-nick" v-model="nickname" type="text" placeholder=" " />
      <label for="reg-nick">昵称（选填）</label>
      <span class="ico">
        <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M12 21s-8-4.9-8-10.3C4 7.7 6.7 5 10 5c1.5 0 2.9.7 2 2 .9-1.3 2.3-2 2-2 3.3 0 6 2.7 6 5.7C20 16.1 12 21 12 21z" />
        </svg>
      </span>
    </div>

    <div class="field" :class="{ shake: shakeKey === 'pass' }">
      <input id="reg-pass" v-model="password" :type="passwordType" placeholder=" " autocomplete="new-password" />
      <label for="reg-pass">密码（6-15 位，含数字/大小写/特殊字符）</label>
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

    <!-- 密码安全等级 -->
    <div v-if="password" class="pwd-strength" :style="{ '--sc': strengthColor }">
      <div class="pwd-bars">
        <span v-for="n in 4" :key="n" class="bar" :class="{ on: strength >= n }" :style="strength >= n ? { background: strengthColor } : null"></span>
      </div>
      <div class="pwd-meta">
        <span class="pwd-label" :style="{ color: strengthColor }">强度：{{ strengthText }}</span>
        <span class="pwd-tip">{{ strengthTip }}</span>
      </div>
      <ul class="pwd-criteria">
        <li v-for="c in criteriaList" :key="c.label" :class="{ ok: c.met }">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 6L9 17l-5-5" />
          </svg>
          <span>{{ c.label }}</span>
        </li>
      </ul>
    </div>

    <div class="field" :class="{ shake: shakeKey === 'confirm' }">
      <input id="reg-confirm" v-model="confirmPwd" :type="passwordType" placeholder=" " autocomplete="new-password" />
      <label for="reg-confirm">确认密码</label>
      <span class="ico">
        <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M20 6L9 17l-5-5" />
        </svg>
      </span>
    </div>

    <!-- 图形验证码 -->
    <div class="field captcha-field" :class="{ shake: shakeKey === 'captcha' }">
      <input id="reg-captcha" v-model="captchaInput" type="text" maxlength="4" placeholder=" " autocomplete="off" />
      <label for="reg-captcha">图形验证码</label>
      <span class="ico">
        <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
        </svg>
      </span>
      <canvas ref="captchaCanvas" class="captcha-img" width="120" height="48" title="点击图片刷新" @click="genCaptcha"></canvas>
      <button class="captcha-refresh" type="button" aria-label="刷新验证码" @click="genCaptcha">
        <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M21 12a9 9 0 1 1-2.64-6.36" />
          <path d="M21 3v6h-6" />
        </svg>
      </button>
    </div>

    <button class="btn" type="submit" :class="{ loading }">
      <span>
        注 册
        <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round">
          <path d="M5 12h14M13 6l6 6-6 6" />
        </svg>
      </span>
      <i class="spin"></i>
    </button>

    <p class="signup">已有账号？<a href="#" @click.prevent="emit('switch')">返回登录 →</a></p>
  </form>
</template>

<style scoped>
.panel {
  padding: 44px 56px 36px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 品牌 */
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 28px;
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
  margin-bottom: 24px;
  line-height: 1.7;
}

/* 提示 */
.err {
  color: #f43f5e;
  font-size: 12px;
  margin: -8px 0 10px;
  min-height: 16px;
  letter-spacing: 0.02em;
}
.ok {
  color: #10b981;
  font-size: 12.5px;
  margin: -8px 0 10px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

/* 输入框 */
.field {
  position: relative;
  margin-bottom: 15px;
}
.field input {
  width: 100%;
  height: 54px;
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
  top: 17px;
  font-size: 13.5px;
  color: var(--ink-soft);
  pointer-events: none;
  transition: 0.24s cubic-bezier(0.2, 0.8, 0.25, 1);
  letter-spacing: 0.02em;
  white-space: nowrap;
  overflow: hidden;
  max-width: calc(100% - 110px);
  text-overflow: ellipsis;
}
.field input:focus + label,
.field input:not(:placeholder-shown) + label {
  top: 7px;
  font-size: 11px;
  color: var(--accent);
  letter-spacing: 0.06em;
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
  top: 17px;
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
  top: 17px;
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

/* 图形验证码字段 */
.captcha-field input {
  padding-right: 182px;
}
.captcha-img {
  position: absolute;
  right: 52px;
  top: 3px;
  width: 120px;
  height: 48px;
  border-radius: 12px;
  cursor: pointer;
  border: 1px solid var(--line);
  user-select: none;
}
.captcha-refresh {
  position: absolute;
  right: 12px;
  top: 13px;
  width: 30px;
  height: 30px;
  border: 0;
  background: none;
  cursor: pointer;
  color: #b9aedd;
  border-radius: 8px;
  display: grid;
  place-items: center;
  transition: 0.25s;
}
.captcha-refresh:hover {
  color: var(--accent);
  background: rgba(192, 132, 252, 0.1);
  transform: rotate(-35deg);
}

/* 密码安全等级 */
.pwd-strength {
  margin: -6px 0 16px;
  padding: 12px 14px 13px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid var(--line);
}
.pwd-bars {
  display: flex;
  gap: 6px;
  margin-bottom: 9px;
}
.pwd-bars .bar {
  flex: 1;
  height: 5px;
  border-radius: 4px;
  background: var(--line);
  transition: background 0.3s ease, transform 0.2s ease;
}
.pwd-bars .bar.on {
  transform: scaleY(1.25);
}
.pwd-meta {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 9px;
}
.pwd-label {
  font-size: 12.5px;
  font-weight: 700;
  letter-spacing: 0.04em;
}
.pwd-tip {
  font-size: 11px;
  color: var(--ink-soft);
}
.pwd-criteria {
  list-style: none;
  display: flex;
  flex-wrap: wrap;
  gap: 6px 14px;
  margin: 0;
  padding: 0;
}
.pwd-criteria li {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: var(--ink-soft);
  transition: color 0.2s;
}
.pwd-criteria li svg {
  width: 13px;
  height: 13px;
  color: #cbbde8;
  transition: color 0.2s;
}
.pwd-criteria li.ok {
  color: #10b981;
}
.pwd-criteria li.ok svg {
  color: #10b981;
}

/* 主按钮 */
.btn {
  position: relative;
  width: 100%;
  height: 54px;
  margin-top: 8px;
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

.signup {
  margin-top: 22px;
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
    padding: 32px 26px 28px;
  }
  .brand {
    margin-bottom: 22px;
  }
  h1 {
    font-size: 23px;
  }
  .captcha-field input {
    padding-right: 168px;
  }
  .captcha-img {
    width: 108px;
    right: 50px;
  }
}
</style>
