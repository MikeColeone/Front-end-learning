你的代码目前将图表逻辑直接写在业务组件中，要实现 echarts 的二次封装组件，可以按以下方案设计：

---

### 🔧 二次封装 echarts 组件设计方案

#### 1. 创建基础图表组件 `BaseChart.vue`

```vue
<template>
  <div ref="chartDom" :style="{ width: width, height: height }"></div>
</template>

<script>
import * as echarts from 'echarts'
import { debounce } from 'lodash'

export default {
  props: {
    width: { type: String, default: '100%' },
    height: { type: String, default: '400px' },
    option: { type: Object, required: true },
    theme: { type: [String, Object], default: 'default' },
    autoResize: { type: Boolean, default: true }
  },

  data() {
    return {
      chartInstance: null
    }
  },

  watch: {
    option: {
      handler(newVal) {
        this.updateChart(newVal)
      },
      deep: true
    }
  },

  mounted() {
    this.initChart()
    if (this.autoResize) {
      window.addEventListener('resize', this.handleResize)
    }
  },

  beforeDestroy() {
    if (this.chartInstance) {
      this.chartInstance.dispose()
    }
    window.removeEventListener('resize', this.handleResize)
  },

  methods: {
    initChart() {
      this.chartInstance = echarts.init(this.$refs.chartDom, this.theme)
      this.chartInstance.setOption(this.option)

      // 暴露事件
      this.chartInstance.on('click', params => {
        this.$emit('chart-click', params)
      })

      this.chartInstance.on('mouseover', params => {
        this.$emit('chart-mouseover', params)
      })
    },

    updateChart(newOption) {
      if (!this.chartInstance) return
      this.chartInstance.setOption(newOption, true)
    },

    handleResize: debounce(function() {
      this.chartInstance?.resize()
    }, 300)
  }
}
</script>
```

---

### 2. 创建专用图表组件（按类型封装）

#### 雷达图组件 `RadarChart.vue`

```vue
<template>
  <BaseChart
    ref="chart"
    :option="mergedOption"
    v-bind="$attrs"
    @chart-mouseover="handleMouseover"
  />
</template>

<script>
import BaseChart from './BaseChart.vue'

export default {
  components: { BaseChart },

  props: {
    indicators: { type: Array, required: true },  // 雷达图指标
    actualData: { type: Array, required: true }, // 实际数据
    maxData: { type: Array, default: () => [] }, // 最大值数据
    theme: { type: String, default: 'default' }
  },

  computed: {
    mergedOption() {
      return {
        color: ['#FFE434', '#660099'],
        radar: {
          indicator: this.indicators,
          radius: 150,
          axisName: {
            color: '#fff',
            backgroundColor: '#666',
            borderRadius: 3,
            padding: [3, 5]
          }
        },
        series: [{
          type: 'radar',
          data: [
            {
              name: '实际分数',
              value: this.actualData,
              areaStyle: { /* 样式配置 */ }
            },
            {
              name: '最大分数',
              value: this.maxData,
              lineStyle: { type: 'dashed' }
            }
          ]
        }]
      }
    }
  },

  methods: {
    handleMouseover(params) {
      if (params.componentType === 'series') {
        this.$emit('dimension-hover', {
          index: params.dataIndex,
          indicator: this.indicators[params.dataIndex]
        })
      }
    }
  }
}
</script>
```

---

### 3. 在业务组件中使用

```vue
<template>
  <RadarChart
    :indicators="radarChartData"
    :actual-data="scoreList2"
    :max-data="scoreList3"
    @dimension-hover="handleRadarHover"
  />
</template>

<script>
import RadarChart from '@/components/RadarChart.vue'

export default {
  components: { RadarChart },

  methods: {
    handleRadarHover({ index, indicator }) {
      const reason = this.scoreList.find(item => 
        item.title === indicator.text
      )?.reason

      this.centerText = `${indicator.text}: ${this.scoreList2[index]}\n${reason}`
    }
  }
}
</script>
```

---

### 🎯 设计要点说明

1. **分层架构**
   
   - **基础层** (`BaseChart`)：处理 echarts 实例生命周期、通用配置、事件绑定
   - **业务层** (`RadarChart/PieChart`)：封装特定图表类型的配置逻辑
   - **使用层**：只关注数据传递和业务交互

2. **响应式设计**
   
   - 通过 `watch` 自动响应 option 变化
   - 内置窗口 resize 监听
   - 支持动态主题切换

3. **事件机制**
   
   - 基础事件：click/mouseover 等原生事件
   - 业务事件：`dimension-hover` 等语义化事件

4. **性能优化**
   
   - 使用 debounce 控制 resize 频率
   - 按需更新配置（`setOption` 的合并策略）
   - 自动销毁图表实例

5. **扩展性**
   
   - 通过 `$attrs` 继承基础组件属性
   - 支持自定义主题
   - 可组合多种图表类型

---

### ⚡ 你的代码优化方向

1. **解耦数据获取与图表渲染**
   
   ```javascript
   // 修改后 fetchData
   async fetchData(uuid) {
     const resp = await service.post(`kcall/call-score/getScoreByUuid/${uuid}`)
     if (resp.code !== 200) {
       ElMessage.error("获取详情失败")
       return
     }
   
     this.prepareChartData(resp.data.scoreList)
     this.scoreDrawer = true
   }
   
   prepareChartData(rawData) {
     this.chartData = rawData.map(item => ({
       value: item.score,
       name: item.title
     }))
   
     this.radarChartData = rawData.map(item => ({
       text: item.title,
       max: 20
     }))
   
     this.scoreList2 = rawData.map(item => item.score)
     this.scoreList3 = Array(rawData.length).fill(20)
   }
   ```

2. **移除直接操作 DOM 的逻辑**
   
   ```javascript
   // 删除所有 $nextTick 和 initChart 调用
   // 依靠组件自动响应数据变化
   ```

3. **统一事件处理**
   
   ```javascript
   // 在业务组件中
   handleChartHover(params) {
     if (params.seriesType === 'radar') {
       // 处理雷达图悬浮
     } else if (params.seriesType === 'pie') {
       // 处理饼图悬浮
     }
   }
   ```

这样封装后，你的代码将获得更好的可维护性和复用性，同时降低业务组件复杂度。
