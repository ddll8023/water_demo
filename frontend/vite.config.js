import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vueDevTools from "vite-plugin-vue-devtools";

// https://vite.dev/config/
export default defineConfig({
	plugins: [vue(), vueDevTools()],
	resolve: {
		alias: {
			"@": fileURLToPath(new URL("./src", import.meta.url)),
		},
	},
	css: {
		preprocessorOptions: {
			scss: {
				// 关闭所有 Sass 警告
				silenceDeprecations: [
					"legacy-js-api",
					"import",
					"global-builtin",
					"color-functions",
				],
				quietDeps: true,
				// 完全关闭警告输出
				logger: {
					warn: () => {},
					debug: () => {},
					deprecation: () => {},
				},
				// 设置 API 类型为现代版本
				api: "modern-compiler",
			},
		},
	},
	server: {
		port: 5173,
		open: true,
		proxy: {
			"/api": {
				target: "http://localhost:8080",
				changeOrigin: true,
				secure: false,
			},
			"/uploads": {
				target: "http://localhost:8080",
				changeOrigin: true,
				secure: false,
			},
		},
	},
	build: {
		outDir: "dist",
		assetsDir: "assets",
		sourcemap: false,
		rollupOptions: {
			output: {
				chunkFileNames: "js/[name]-[hash].js",
				entryFileNames: "js/[name]-[hash].js",
				assetFileNames: "[ext]/[name]-[hash].[ext]",
			},
		},
	},
});
