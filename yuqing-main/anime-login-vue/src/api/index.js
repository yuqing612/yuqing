/* ---------- 后端 API 封装 ----------
   开发环境通过 vite 代理将 /api 转发到 http://localhost:8081
   登录凭证（token + 用户信息）持久化：
   - 勾选"记住我" → localStorage（7 天，与后端 token 有效期一致）
   - 不勾选 → sessionStorage（12 小时，关闭标签页即失效） */

const BASE = '/api'
const AUTH_KEY = 'sakura_auth'

export function saveAuth(auth, remember) {
  clearAuth()
  const store = remember ? localStorage : sessionStorage
  store.setItem(AUTH_KEY, JSON.stringify(auth))
}

export function getAuth() {
  const raw = sessionStorage.getItem(AUTH_KEY) || localStorage.getItem(AUTH_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    clearAuth()
    return null
  }
}

export function clearAuth() {
  sessionStorage.removeItem(AUTH_KEY)
  localStorage.removeItem(AUTH_KEY)
}

async function request(path, { method = 'GET', body } = {}) {
  const headers = {}
  if (body) headers['Content-Type'] = 'application/json'
  const auth = getAuth()
  if (auth && auth.token) headers['Authorization'] = `Bearer ${auth.token}`

  let res
  try {
    res = await fetch(BASE + path, {
      method,
      headers: Object.keys(headers).length ? headers : undefined,
      body: body ? JSON.stringify(body) : undefined
    })
  } catch {
    throw new Error('无法连接服务器，请确认后端已启动 (¬_¬")')
  }

  let json = null
  try {
    json = await res.json()
  } catch {
    /* 非 JSON 响应 */
  }
  if (!res.ok || !json) {
    throw new Error((json && json.message) || `请求失败（${res.status}）`)
  }
  if (json.code === 401) {
    clearAuth()
    throw Object.assign(new Error(json.message || '登录状态已失效'), { code: 401 })
  }
  if (json.code !== 200) {
    throw new Error(json.message || '操作失败，请稍后再试')
  }
  return json.data
}

export const login = (data) => request('/auth/login', { method: 'POST', body: data })
export const register = (data) => request('/auth/register', { method: 'POST', body: data })
export const me = () => request('/auth/me')
export const logout = () => request('/auth/logout', { method: 'POST' })

/* =========================================================
   首页 / 视频 / 番剧 / 直播 相关接口
   - 页面统一调用下方方法，不感知数据来自 mock 还是真实后端
   - 默认走 src/api/mockData.js（仅前端演示）
   - 后端就绪后，将 vite 环境变量 VITE_API_REAL 设为 'true'，
     即可无缝切换到真实 /api 接口，页面代码无需任何改动
   ========================================================= */
import * as mock from './mockData'

// 是否使用真实后端（默认 false → mock）
const USE_REAL = import.meta.env.VITE_API_REAL === 'true'
// 番剧模块单独开关：后端已实现番剧接口，可先于其它模块切到真实数据。
// 视频模块单独开关：后端已实现首页每日推荐 / 排行 / 搜索 / 详情接口。
// 想整体切换时把 VITE_API_REAL 设为 'true' 即可，下面各模块开关会自动跟随。
const BANGUMI_REAL = USE_REAL || import.meta.env.VITE_API_BANGUMI === 'true'
const VIDEOS_REAL = USE_REAL || import.meta.env.VITE_API_VIDEOS === 'true'

export const fetchVideos = () =>
  VIDEOS_REAL ? request('/videos') : mock.mockFetchVideos()              // GET  /api/videos
export const fetchVideo = (id) =>
  VIDEOS_REAL ? request(`/videos/${id}`) : mock.mockFetchVideo(id)       // GET  /api/videos/:id
export const searchVideos = (q) =>
  VIDEOS_REAL
    ? request(`/videos/search?q=${encodeURIComponent(q)}`)
    : mock.mockSearchVideos(q)                                          // GET  /api/videos/search?q=
export const fetchBangumi = (category) =>
  BANGUMI_REAL
    ? request(category && category !== '全部'
        ? `/bangumi?category=${encodeURIComponent(category)}`
        : '/bangumi')
    : mock.mockFetchBangumi()                                           // GET  /api/bangumi
export const fetchLives = () =>
  USE_REAL ? request('/lives') : mock.mockFetchLives()                   // GET  /api/lives
export const fetchFavorites = () =>
  USE_REAL ? request('/favorites') : mock.mockFetchFavorites()          // GET  /api/favorites
export const fetchRanks = () =>
  VIDEOS_REAL ? request('/videos/rank') : mock.mockFetchRanks()         // GET  /api/videos/rank
export const fetchSchedule = () =>
  BANGUMI_REAL ? request('/schedule') : mock.mockFetchSchedule()        // GET  /api/schedule

/* 番剧分类 Tab 数据来源（后端按表中实际分类去重返回） */
const CATS_FALLBACK = ['日漫', '国漫', '剧场', '美漫']
export const fetchBangumiCategories = () =>
  BANGUMI_REAL
    ? request('/bangumi/categories')
    : Promise.resolve(CATS_FALLBACK)                                    // GET  /api/bangumi/categories
