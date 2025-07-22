const { defineConfig } = require('@vue/cli-service')
const webpack = require('webpack')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081
  },
  configureWebpack: {
    plugins: [
      new webpack.DefinePlugin({
        // 核心特性标志（必须定义）
        '__VUE_PROD_HYDRATION_MISMATCH_DETAILS__': true,
        // 其他常用标志（根据项目需求添加）
        '__VUE_OPTIONS_API__': true,        // 启用Options API支持
        '__VUE_PROD_DEVTOOLS__': false,     // 禁用生产环境DevTools
        '__VUE_PROD_PERF__': true,          // 启用生产性能优化
        // 如果你使用了Composition API，建议添加：
        '__VUE_COMPOSITION_API__': true
      })
    ]
  }
})