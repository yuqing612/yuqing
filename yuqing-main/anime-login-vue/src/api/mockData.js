/* =========================================================
   mock 数据层（仅前端演示用，后端就绪后可整体移除）
   所有函数返回 Promise，模拟网络延迟，数据结构与真实
   /api 返回保持一致，页面通过 api/index.js 门面调用，
   不感知数据来自 mock 还是后端。
   ========================================================= */

import { BANGUMI_ILLUST } from '../assets/bangumi'

const delay = (ms = 160) => new Promise((r) => setTimeout(r, ms))

/* ---------- 视频（首页推荐 / 收藏 / 排行共用） ---------- */
export const VIDEOS = [
  {
    id: 1,
    title: '【4K】樱花飘落的次元车站 · 治愈系混剪',
    uploader: '桜TV官方',
    views: '128.5万',
    play: 1285000,
    danmaku: '3.2万',
    duration: '04:12',
    tags: ['治愈', '混剪', '4K'],
    desc: '当最后一班列车驶离站台，花瓣与霓虹一同沉入夜色。一段献给所有旅人的安眠曲。',
    fav: true,
    grad: 'linear-gradient(135deg,#ff8fb1,#c084fc)'
  },
  {
    id: 2,
    title: '纸片人女友的一天 · 原创短篇动画',
    uploader: '星屑工坊',
    views: '56.1万',
    play: 561000,
    danmaku: '8742',
    duration: '08:47',
    tags: ['原创', '日常', '甜'],
    desc: '从清晨的第一缕光到深夜的台灯，她把平凡的日子过成了童话。',
    fav: false,
    grad: 'linear-gradient(135deg,#7dd3fc,#a78bfa)'
  },
  {
    id: 3,
    title: '雨夜便利店 · 慢节奏 ASMR 白噪音',
    uploader: '夜猫电台',
    views: '23.9万',
    play: 239000,
    danmaku: '1920',
    duration: '30:00',
    tags: ['ASMR', '助眠', '雨声'],
    desc: '关东煮的热气、窗外的雨、以及永远为你亮着的那盏灯。',
    fav: true,
    grad: 'linear-gradient(135deg,#c084fc,#7dd3fc)'
  },
  {
    id: 4,
    title: '超时空辉夜姬',
    uploader: '苍崎制作',
    views: '342.7万',
    play: 3427000,
    danmaku: '12.6万',
    duration: '02:58',
    tags: ['同人', '战斗', '燃'],
    desc: '为了夺回被夺走的星光，少女拔出了沉睡千年的剑。',
    fav: false,
    grad: 'linear-gradient(135deg,#ff6f9c,#c084fc)',
    coverUrl: '/kaguya.webp'
  },
  {
    id: 5,
    title: '猫咪咖啡馆营业中 · 治愈 vlog',
    uploader: '软糖日记',
    views: '78.4万',
    play: 784000,
    danmaku: '4531',
    duration: '12:33',
    tags: ['vlog', '猫', '治愈'],
    desc: '三只店长猫，一杯手冲，和刚好路过的你。',
    fav: false,
    grad: 'linear-gradient(135deg,#ffd98a,#ff8fb1)'
  },
  {
    id: 6,
    title: '虚拟歌姬演唱会 · 全息现场实录',
    uploader: '棱镜现场',
    views: '201.3万',
    play: 2013000,
    danmaku: '8.9万',
    duration: '15:20',
    tags: ['演唱会', '虚拟', 'Live'],
    desc: '当全场荧光棒亮起，她不再是代码，而是此刻真实的心跳。',
    fav: true,
    grad: 'linear-gradient(135deg,#a78bfa,#7dd3fc)'
  },
  {
    id: 7,
    title: '深夜画室 · 厚涂立绘绘制过程',
    uploader: '小满Official',
    views: '45.2万',
    play: 452000,
    danmaku: '3201',
    duration: '21:08',
    tags: ['绘画', '教程', '厚涂'],
    desc: '从线稿到高光，一笔一画把纸片人养成会呼吸的少女。',
    fav: false,
    grad: 'linear-gradient(135deg,#ff8fb1,#a78bfa)'
  },
  {
    id: 8,
    title: '次元歌单 · 春日恋爱循环',
    uploader: '棱镜现场',
    views: '167.9万',
    play: 1679000,
    danmaku: '6.4万',
    duration: '48:30',
    tags: ['歌单', '恋爱', '循环'],
    desc: '把整个春天都塞进这一张播放列表里，单曲循环到第几首了？',
    fav: true,
    grad: 'linear-gradient(135deg,#7dd3fc,#ff8fb1)'
  }
]

/* 公开示例视频，仅作播放占位，后端就绪后替换为真实地址 */
export const VIDEO_SRC = {
  1: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
  2: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',
  3: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4',
  4: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4',
  5: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
  6: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4',
  7: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',
  8: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4'
}

/* ---------- 番剧时间表（每周更新表，mock） ---------- */
export const WEEKDAYS = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
export const SCHEDULE = {
  周一: [
    { title: '星屑列车开往黎明', ep: 8, time: '20:00' },
    { title: '便利屋猫咪日记', ep: 6, time: '22:30' }
  ],
  周二: [
    { title: '魔法少女小满的午后', ep: 19, time: '19:30' },
    { title: '深空抒情诗', ep: 5, time: '21:00' }
  ],
  周三: [
    { title: '晚晴剑歌', ep: 7, time: '20:30' },
    { title: '机甲学徒守则', ep: 4, time: '23:00' }
  ],
  周四: [
    { title: '樱色咖啡店物语', ep: 14, time: '20:00' },
    { title: '妖怪百物语·春', ep: 9, time: '22:00' }
  ],
  周五: [
    { title: '星屑列车开往黎明', ep: 9, time: '20:00' },
    { title: '深空抒情诗', ep: 6, time: '21:00' }
  ],
  周六: [
    { title: '魔法少女小满的午后', ep: 20, time: '18:00' },
    { title: '晚晴剑歌', ep: 8, time: '20:30' },
    { title: '机甲学徒守则', ep: 5, time: '23:00' }
  ],
  周日: [
    { title: '樱色咖啡店物语', ep: 15, time: '19:00' },
    { title: '妖怪百物语·春', ep: 10, time: '21:30' }
  ]
}
/* ---------- 番剧 ---------- */
export const BANGUMI = [
  { id: 101, title: '星屑列车开往黎明', cover: 'linear-gradient(135deg,#ff8fb1,#c084fc)', episodes: 12, status: '连载中', score: 9.2, studio: '桜动画', category: '日漫', tags: ['原创', '科幻', '治愈'], desc: '末日后的轨道上，少女与一台会说话的旧列车驶向传说的黎明站。' },
  { id: 102, title: '魔法少女小满的午后', cover: 'linear-gradient(135deg,#7dd3fc,#a78bfa)', episodes: 24, status: '已完结', score: 8.7, studio: '青空制作', category: '日漫', tags: ['日常', '魔法', '甜'], desc: '只要在下午三点许愿，任何小事都会变成闪闪发光的奇迹。' },
  { id: 103, title: '晚晴剑歌', cover: 'linear-gradient(135deg,#ff6f9c,#c084fc)', episodes: 13, status: '连载中', score: 9.0, studio: '苍崎制作', category: '国漫', tags: ['武侠', '古风', '燃'], desc: '一把断剑，一座空城，她在雨里等一个再也不会回来的人。' },
  { id: 104, title: '便利屋猫咪日记', cover: 'linear-gradient(135deg,#ffd98a,#ff8fb1)', episodes: 10, status: '已完结', score: 8.4, studio: '软糖动画', category: '日漫', tags: ['治愈', '猫', '日常'], desc: '帮邻居送牛奶、替老人修水管，顺手拯救一下世界也不错。' },
  { id: 105, title: '深空抒情诗', cover: 'linear-gradient(135deg,#a78bfa,#7dd3fc)', episodes: 12, status: '连载中', score: 8.9, studio: '棱镜动画', category: '日漫', tags: ['太空', '音乐', '群像'], desc: '一艘载满歌声的飞船，在星云间寻找失落的故乡频率。' },
  { id: 106, title: '樱色咖啡店物语', cover: 'linear-gradient(135deg,#ff8fb1,#a78bfa)', episodes: 24, status: '已完结', score: 8.6, studio: '桜动画', category: '日漫', tags: ['美食', '治愈', '群像'], desc: '转角那家咖啡店，每位客人都带着一段想被温柔接住的故事。' },
  { id: 107, title: '机甲学徒守则', cover: 'linear-gradient(135deg,#c084fc,#7dd3fc)', episodes: 13, status: '连载中', score: 8.8, studio: '青空制作', category: '国漫', tags: ['机甲', '校园', '热血'], desc: '入学第一天，她把全校最强的机甲拆成了零件标本。' },
  { id: 108, title: '妖怪百物语·春', cover: 'linear-gradient(135deg,#ff6f9c,#a78bfa)', episodes: 11, status: '已完结', score: 9.1, studio: '苍崎制作', category: '日漫', tags: ['妖怪', '单元剧', '和风'], desc: '春夜里，妖怪们排队来借一样人类弄丢的东西。' },
  { id: 109, title: '星海航路·剧场版', cover: 'linear-gradient(135deg,#7dd3fc,#ff8fb1)', episodes: 1, status: '已完结', score: 9.3, studio: '棱镜动画', category: '剧场', tags: ['剧场版', '科幻', '冒险'], desc: '一封信，一片海，和一场跨越十二年的告别。' },
  { id: 110, title: '樱之纪念日·剧场版', cover: 'linear-gradient(135deg,#ff8fb1,#c084fc)', episodes: 1, status: '已完结', score: 9.0, studio: '桜动画', category: '剧场', tags: ['剧场版', '恋爱', '催泪'], desc: '樱花落下的速度，刚好是我想起你的速度。' },
  { id: 111, title: '赛博小吃街', cover: 'linear-gradient(135deg,#c084fc,#ff8fb1)', episodes: 16, status: '连载中', score: 8.5, studio: '青空制作', category: '国漫', tags: ['美食', '科幻', '单元剧'], desc: '霓虹灯下的深夜食堂，每碗面都藏着一段心事。' },
  { id: 112, title: '星际巡逻队', cover: 'linear-gradient(135deg,#a78bfa,#7dd3fc)', episodes: 20, status: '连载中', score: 8.3, studio: '棱镜动画', category: '美漫', tags: ['科幻', '搞笑', '冒险'], desc: '宇宙边界的巡逻小队，今天的任务依然是——摸鱼失败。' }
]

/* ---------- 直播 ---------- */
export const LIVES = [
  { id: 201, title: '深夜绘画直播·厚涂立绘教学', streamer: '小满Official', category: '绘画', viewers: '1.2万', grad: 'linear-gradient(135deg,#ff8fb1,#c084fc)' },
  { id: 202, title: '歌回！点歌随便唱到天亮', streamer: '晚晴Official', category: '音乐', viewers: '8643', grad: 'linear-gradient(135deg,#a78bfa,#7dd3fc)' },
  { id: 203, title: '新番速看吐槽大会', streamer: '桜TV情报局', category: '杂谈', viewers: '5321', grad: 'linear-gradient(135deg,#7dd3fc,#a78bfa)' },
  { id: 204, title: '手作娘的黏土教室', streamer: '软糖日记', category: '手工', viewers: '3098', grad: 'linear-gradient(135deg,#ffd98a,#ff8fb1)' },
  { id: 205, title: '钢琴即兴· requests 开放', streamer: '棱镜现场', category: '音乐', viewers: '7120', grad: 'linear-gradient(135deg,#c084fc,#7dd3fc)' },
  { id: 206, title: '通宵通关老 RPG', streamer: '夜猫电台', category: '游戏', viewers: '4502', grad: 'linear-gradient(135deg,#ff6f9c,#c084fc)' },
  { id: 207, title: 'Vlog：逛了一整天的同人展', streamer: '星屑工坊', category: '生活', viewers: '2760', grad: 'linear-gradient(135deg,#a78bfa,#ff8fb1)' },
  { id: 208, title: '读书会·一起读轻小说', streamer: '青空书库', category: '阅读', viewers: '1984', grad: 'linear-gradient(135deg,#7dd3fc,#c084fc)' }
]

/* ===================== mock 异步接口 ===================== */
export async function mockFetchVideos() {
  await delay()
  return VIDEOS.map((v) => ({ ...v, src: VIDEO_SRC[v.id] }))
}

export async function mockFetchVideo(id) {
  await delay()
  const v = VIDEOS.find((x) => x.id === id)
  return v ? { ...v, src: VIDEO_SRC[v.id] } : null
}

export async function mockSearchVideos(q) {
  await delay()
  const s = (q || '').trim().toLowerCase()
  if (!s) return VIDEOS.map((v) => ({ ...v, src: VIDEO_SRC[v.id] }))
  return VIDEOS.filter(
    (v) =>
      v.title.toLowerCase().includes(s) ||
      v.uploader.toLowerCase().includes(s) ||
      v.tags.some((t) => t.toLowerCase().includes(s))
  ).map((v) => ({ ...v, src: VIDEO_SRC[v.id] }))
}

export async function mockFetchBangumi() {
  await delay()
  /* 挂上插画：illust 为静态资源 URL，缺图时回落为渐变封面 */
  return BANGUMI.map((b) => ({ ...b, illust: BANGUMI_ILLUST[b.id] || null }))
}

export async function mockFetchLives() {
  await delay()
  return LIVES
}

export async function mockFetchFavorites() {
  await delay()
  return VIDEOS.filter((v) => v.fav).map((v) => ({ ...v, src: VIDEO_SRC[v.id] }))
}

export async function mockFetchRanks() {
  await delay()
  return [...VIDEOS]
    .sort((a, b) => b.play - a.play)
    .map((v, i) => ({ ...v, src: VIDEO_SRC[v.id], rank: i + 1 }))
}

export async function mockFetchSchedule() {
  await delay()
  return { weekdays: WEEKDAYS, today: WEEKDAYS[new Date().getDay() === 0 ? 6 : new Date().getDay() - 1], items: SCHEDULE }
}
