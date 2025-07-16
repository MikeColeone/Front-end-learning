作为最常用的echarts的图形库，在使用的时候会出现以下问题：

- 复杂的option配置

- 默认主题不满足设计要求

所以我们经常要对echarts进行二次封装：

## 主题包封装

### 目标

1. 样式尽量和UX设计统一

2. 尽量解耦样式和数据

### 举例：

- 常规配置如下：

```TypeScript
// 常规的配置方式let option = {  backgroundColor: ,  color: [],  textStyle: {    color: ,    fontSize:,    fontFamily:  },  tooltip: {    trigger: ,    backgroundColor:,    textStyle:,    axisPointer: {      lineStyle:,      type:,      z:    }  },  legend: {    data:,    type: ,    pageIconColor:,    pageIconSize:,    pageTextStyle:,    textStyle: {      color: ,      fontSize:    }  },  yAxis: {    name: ,  },  series: [    {      type: ,      data: ,      name:     },  ]};
```

常见的option配置通常包括但不限于以上配置，并且样式和数据耦合，维护处理很麻烦，相同的主题风格还会导致代码大量重复，但是echarts提供注册主题的方法，基于封装主题包实现样式和配置解耦：

### 流程

1. 自定义主题

2. 注册主题： registerTheme("customTheme", customTheme);

3. 初始化： echarts.init(chartRef.current, theme);

4. setOption(注意option > 主题包 > echarts默认配置 所以option样式会覆盖掉主题包)

### 代码举例：

```TypeScript
// 自定义主题配置对象const customTheme = {  // 图表全局颜色列表，优先用于系列配色  color: baseColors,  // 图表背景色  backgroundColor: "#FFFFFF",  // 标题配置  title: {    // 主标题样式    textStyle: {},    // 副标题样式    subtextStyle: {},    // 标题水平位置    left: "center",    // 标题垂直位置    top: 20,  },  // 提示框配置  tooltip: {    // 提示框位置计算函数    position(point, params, dom, rect, size) {},    // 提示框内容格式化函数    formatter(params) {},  },  // 图例配置  legend: {    // 图例底部位置    bottom: 0,    // 图例标记形状    icon: "rect",    // 图例项间距    itemGap: 16,    // 图例高度    height: 16,    // 图例标记宽度    itemWidth: 10,    // 图例标记高度    itemHeight: 10,    // 图例文本格式化函数（超出长度省略）    formatter: function (name) {},    // 图例类型（可滚动）    type: "scroll",    // 翻页按钮间距    pageButtonItemGap: 5,    // 翻页按钮位置    pageButtonPosition: "end",    // 页码格式化    pageFormatter: "{current}/{total}",    // 页码图标颜色    pageIconColor: "#333",    // 页码图标非激活颜色    pageIconInactiveColor: "#aaa",    // 页码文本样式    pageTextStyle: {},    // 图例水平居中    left: "center",    // 图例排列方向    orient: "horizontal",    // 图例文本样式    textStyle: {},    // 图例提示框配置    tooltip: {      show: true,      formatter: function (name) {},    },  },  // 网格配置（用于直角坐标系图表）  grid: {    // 左内边距    left: "24px",    // 右内边距    right: "24px",    // 下内边距    bottom: "24px",    // 上内边距    top: "24px",    // 是否包含坐标轴标签    containLabel: true,  },  // 类目轴配置  categoryAxis: {    // 轴类型    type: "category",    // 是否显示坐标轴刻度    axisTick: { show: false },    // 坐标轴线样式    axisLine: {},    // 坐标轴标签样式    axisLabel: {},  },  // 数值轴配置  valueAxis: {    // 轴类型    type: "value",    // 坐标轴标签样式    axisLabel: {},    // 分隔线样式    splitLine: {},  },  // 数据区域缩放配置  dataZoom: [    {      // 缩放类型      type: "slider",      // 是否显示      show: true,      // 起始百分比      start: 0,      // 结束百分比      end: 100,      // 背景色      backgroundColor: "rgba(245, 245, 245, 0.8)",      // 边框色      borderColor: "#ddd",      // 填充色      fillerColor: "rgba(167, 190, 211, 0.5)",      // 滑块样式      handleStyle: {},    },  ],  // 系列默认配置（按图表类型区分 严格遵守官网类型 没有的只能自定义）  series: [    {      // 折线图配置      type: "line",      // 数据项样式      itemStyle: {},      // 线样式      lineStyle: {},      // 是否显示数据点标记      showSymbol: false,    },    {      // 柱状图配置      type: "bar",      // 数据项样式      itemStyle: {},      // 柱间距      barGap: "20%",    },    {      // 词云图配置      type: "wordCloud",      // 字体大小范围      sizeRange: [16, 48],      // 旋转角度范围      rotationRange: [0, 0],      // 文本样式      textStyle: {},      // 高亮样式      emphasis: {},      // 网格间距      gridSize: 6,      // 形状      shape: "cardioid",      // 布局动画      layoutAnimation: true,    },    {      // 饼图配置      type: "pie",      // 数据项样式      itemStyle: {},      // 标签配置      label: {},      // 标签连接线配置      labelLine: {},      // 中心位置      center: ["50%", "50%"],      // 半径      radius: "60%",    },    {      // 漏斗图配置      type: "funnel",      // 提示框配置      tooltip: {},      // 标签配置      label: {},      // 数据项样式      itemStyle: {},      // 高亮样式      emphasis: {},    },    {      // 散点图配置      type: "scatter",      // 数据项样式      itemStyle: {},      // 标记形状      symbol: "circle",      // 标记大小      symbolSize: 8,    },    {      // 雷达图配置      type: "radar",      // 轴线样式      axisLine: {},      // 分隔线样式      splitLine: {},      // 分隔区域样式      splitArea: {},      // 线样式      lineStyle: {},      // 数据项样式      itemStyle: {},    },    {      // 热力图配置      type: "heatmap",      // 数据项样式      itemStyle: {},      // 高亮样式      emphasis: {},    },    {      // 面积图配置      type: "area",      // 填充区域样式      areaStyle: {},    },  ],  //单独给图表配置样式  line:{  }};
```
