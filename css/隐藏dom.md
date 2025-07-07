## ✅ 一、CSS 层面的隐藏

### 1. `display: none`

- **彻底隐藏**，不占空间

- DOM 仍在页面中，但不可见、不可交互

```css
.element {
  display: none;
}
```

### 2. `visibility: hidden`

- **隐藏但保留占位**

- 元素不可见、不可交互，但仍占据空间

```css
.element {
  visibility: hidden;
}
```

### 3. `opacity: 0`

- 元素完全透明但**仍占位**，**可被点击**

- 常用于动画或渐隐

```css
.element {
  opacity: 0;
}
```

---

## ✅ 二、JS 控制类名/属性隐藏

### 4. 动态控制类名

```js
element.classList.add("hidden"); // "hidden" 设置 display: none;
```

### 5. `hidden` 属性

- HTML5 原生属性

- 效果类似于 `display: none`

```html
<div hidden>不会显示</div>
```

或 JS 控制：

```js
element.hidden = true;
```

---

## ✅ 三、结构性遮挡（“显示”但用户看不见）

### 6. 使用覆盖层遮挡（面试官重点提示的）

- 常用于旧浏览器兼容、弹窗遮挡底层 DOM

```html
<div class="mask"></div> <!-- 一个全屏白色或黑色图层 -->
<div class="target">目标元素</div>
```

```css
.mask {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: white;
  z-index: 9999;
}
```

> ✔️ 目标元素仍在页面中，甚至可能能被访问，但视觉上被覆盖，用户无法“看到”它。

---

## ✅ 四、CSS 定位移出可视区域

### 7. `position: absolute; left: -9999px;`

- 把元素移出可视区域

- 常用于辅助技术（如屏幕阅读器仍能读取）

```css
.element {
  position: absolute;
  left: -9999px;
}
```

---

## ✅ 五、条件渲染控制（React / Vue）

### 8. 条件渲染（逻辑控制 DOM 是否出现在页面中）

```jsx
{show && <div>显示内容</div>}
```

> 虽然这不是“隐藏”，但面试中也可能讨论“DOM 是否存在于页面结构”。

---

## ✅ 六、用尺寸缩小隐藏

### 9. `width: 0; height: 0; overflow: hidden`

- 可配合视觉差或动画使用，但不推荐用于完全隐藏

```css
.element {
  width: 0;
  height: 0;
  overflow: hidden;
}
```

---

## ✅ 总结：方式速查表

| 方法                                  | 是否占位  | 是否在 DOM 中 | 可交互   | 兼容性说明      |
| ----------------------------------- | ----- | --------- | ----- | ---------- |
| `display: none`                     | ❌ 否   | ✅ 是       | ❌ 否   | 推荐         |
| `visibility: hidden`                | ✅ 是   | ✅ 是       | ❌ 否   | 推荐         |
| `opacity: 0`                        | ✅ 是   | ✅ 是       | ✅ 是   | 慎用，可能被点击   |
| `hidden` 属性                         | ❌ 否   | ✅ 是       | ❌ 否   | 推荐         |
| 遮罩遮挡层                               | ✅ 是   | ✅ 是       | ❌ 或 ✅ | 老浏览器常用技巧   |
| `position: absolute; left: -9999px` | ❌ 不可见 | ✅ 是       | ❌ 否   | SEO/辅助设备兼容 |
| 条件渲染（框架）                            | ❌ 否   | ❌ 否       | ❌ 否   | 框架特性       |

---

## 除了 CSS，还有别的方式吗

- 动态加遮罩层覆盖

- 用 `visibility`, `opacity`, `clip-path` 或 `z-index` 控制图层层级

- React/Vue 的控制渲染也可实现逻辑上的“隐藏”

好的！下面是一个经典面试实现：**用遮罩层覆盖一个 DOM，让它“存在但不可见”**。这是早期兼容 IE 或强制遮挡底层内容的常见手法。

---

## ✅ 场景：遮住一个 DOM 元素不让它显示

目标：

- **DOM 节点仍在文档中**

- **不能删它 / 设置 display: none**

- **不修改原节点样式**

- **实现“视觉上完全不可见”**

---

## 💡 方法：新建一个 `mask` 遮罩层，覆盖目标 DOM

### ✅ HTML 示例

```html
<div class="target">
  <p>这个元素需要被隐藏，但不能删</p>
</div>

<div class="mask"></div>
```

---

### ✅ CSS 实现遮挡

```css
.target {
  position: relative;
  width: 300px;
  height: 150px;
  background: orange;
  z-index: 1;
}

.mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 300px;
  height: 150px;
  background: white;
  z-index: 2;
}
```

> 📌 注意：
> 
> - `z-index` 确保遮罩层在目标元素之上
> 
> - `position: absolute` 可以覆盖任何文档流区域
> 
> - 如果页面有滚动，可用 `fixed` 或监听滚动跟随

---

## ✅ JS 动态创建遮罩层（增强写法）

```js
function coverElement(el) {
  const rect = el.getBoundingClientRect();

  const mask = document.createElement('div');
  Object.assign(mask.style, {
    position: 'fixed',
    top: rect.top + 'px',
    left: rect.left + 'px',
    width: rect.width + 'px',
    height: rect.height + 'px',
    background: '#fff',
    zIndex: 9999,
    pointerEvents: 'none' // 不挡住用户交互（可选）
  });

  document.body.appendChild(mask);
}
```

调用方式：

```js
const target = document.querySelector('.target');
coverElement(target);
```

---
