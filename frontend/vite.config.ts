import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server:{
    watch:{
      usePolling:true,
      interval:100,
    },
    allowedHosts: ["frontend"],
    host: '0.0.0.0',
    port: 5173,
    hmr: {
      protocol: 'ws',
      host: 'localhost',
      port: 5173,
      clientPort:5173,
    }
  },
  optimizeDeps: {
    include: ["@stomp/stompjs","sockjs-client"],
  },
  define: {
    global: "window",
  },
})
