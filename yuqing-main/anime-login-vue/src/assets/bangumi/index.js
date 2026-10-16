/* =========================================================
   番剧插画资源表
   - 一图一作的矢量插画（3:4 竖版海报），与番剧 id 一一对应
   - 由 Vite 作为静态资源处理，import 得到的是打包后的 URL
   - 后端就绪后，此表可由后端返回的 coverUrl 直接替换，
     页面只读取 bangumi.illust 字段，无需改动
   ========================================================= */
import i101 from './101.svg'
import i102 from './102.svg'
import i103 from './103.svg'
import i104 from './104.svg'
import i105 from './105.svg'
import i106 from './106.svg'
import i107 from './107.svg'
import i108 from './108.svg'
import i109 from './109.svg'
import i110 from './110.svg'
import i111 from './111.svg'
import i112 from './112.svg'

export const BANGUMI_ILLUST = {
  101: i101, // 星屑列车开往黎明
  102: i102, // 魔法少女小满的午后
  103: i103, // 晚晴剑歌
  104: i104, // 便利屋猫咪日记
  105: i105, // 深空抒情诗
  106: i106, // 樱色咖啡店物语
  107: i107, // 机甲学徒守则
  108: i108, // 妖怪百物语·春
  109: i109, // 星海航路·剧场版
  110: i110, // 樱之纪念日·剧场版
  111: i111, // 赛博小吃街
  112: i112 // 星际巡逻队
}

export default BANGUMI_ILLUST
