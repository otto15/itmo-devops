import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'happy-dom',
    exclude: [
      '**/node_modules/**',
      '**/dist/**',
      '**/src/api/generated/**',
      '**/coverage/**',
    ],
    setupFiles: ['./src/test/setup.js'],
  },
})