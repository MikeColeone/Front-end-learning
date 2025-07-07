CSS 变量（又称 **自定义属性**）是 CSS 中一种强大的功能，用来在样式表中存储值，并在多个地方复用。这让样式更具可维护性、灵活性，适合做主题、响应式等动态样式控制。

---

## 🔧 1. CSS 变量语法

### ✅ 定义变量：

```css
:root {
  --primary-color: #007bff;
  --font-size-base: 16px;
}
```

- `--变量名` 是自定义变量的写法

- `:root` 是全局作用域（也可以在任何选择器内定义局部变量）

### ✅ 使用变量：

```css
body {
  color: var(--primary-color);
  font-size: var(--font-size-base);
}
```

---

## 🧠 2. 作用域与继承

- CSS 变量是**有作用域的**

- 子元素可以继承父元素定义的变量，也可以覆盖

```css
.container {
  --bg-color: white;
}
.child {
  background-color: var(--bg-color); /* 使用父作用域的变量 */
}
```

---

## 🎯 3. 动态修改变量（JS 控制样式）

CSS 变量可以在 JS 中通过 `style.setProperty` 动态修改，非常适合主题切换：

```js
document.documentElement.style.setProperty('--primary-color', 'green')
```

> 这样就能动态改变页面主色调了！

---

## 🧩 4. 与预处理器变量（如 SCSS）对比

| 特性         | CSS 变量      | SCSS 变量（`$color`） |
| ---------- | ----------- | ----------------- |
| 作用域        | 运行时，可继承     | 编译时，全局或局部作用域      |
| 是否支持 JS 操作 | ✅ 支持        | ❌ 不支持（编译后无法修改）    |
| 动态切换主题     | ✅ 非常适合      | ❌ 不适合             |
| 需要编译工具     | ❌ 原生 CSS 支持 | ✅ 需要 Sass 编译器     |

---

## 🪄 5. 示例：主题切换（浅色 / 深色）

```css
:root {
  --bg-color: #fff;
  --text-color: #000;
}
.dark-theme {
  --bg-color: #222;
  --text-color: #fff;
}
body {
  background: var(--bg-color);
  color: var(--text-color);
}
```

```js
document.body.classList.toggle('dark-theme')
```

---

## ✅ 总结：为什么使用 CSS 变量

- 💡 **增强复用性和一致性**

- 🎯 **便于维护和主题切换**

- 🧩 **适用于响应式、组件化系统**

- ⚡ **支持 JS 动态修改**

---

## 代码例子



```css

```
