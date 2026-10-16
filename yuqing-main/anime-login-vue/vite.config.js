import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    /* 显式绑定 IPv4：默认 localhost 在 Node 17+ 会优先解析到 ::1，
       导致浏览器访问 127.0.0.1 时连不上（本机代理返回 502） */
    host: '127.0.0.1',
    port: 5173,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsInlineLimit: 4096
  }
})
