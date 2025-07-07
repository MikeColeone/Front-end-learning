# `rem` 与 `em` 的区别总结

### 📌 基本定义

| 单位    | 相对对象               | 是否继承变化   | 常用于           |
| ----- | ------------------ | -------- | ------------- |
| `rem` | 根元素 `<html>` 的字体大小 | ❌ 不受嵌套影响 | 全局字体、间距、布局    |
| `em`  | 当前元素的字体大小（或父级）     | ✅ 随嵌套变化  | 按钮、局部缩放、图标等场景 |

---

### 默认设置

```css
html {
  font-size: 16px; /* 浏览器默认值 */
}
```

- `1rem = 16px`（始终参考 `<html>`）

- `1em = 父元素的 font-size`（用于设置字体时）

- 但 `em` 在设置边距、内边距等时，是**相对自身字体大小**

---

### 使用示例

#### `rem` 示例（始终参考根）

```css
p {
  font-size: 1.5rem; /* 1.5 × 16px = 24px */
}
```

#### `em` 示例（字体参考父，padding 参考自己）

```css
.parent {
  font-size: 20px;
}

.child {
  font-size: 2em;    /* 字体 = 2 × 20px = 40px */
  padding: 1em;      /* padding = 1 × 40px = 40px */
}
```

---

### 注意点

- `em` 是级联的，会“连锁放大”

- 多层嵌套 `font-size: em` 容易导致字体越来越大（或小）

- 使用 `rem` 更利于响应式布局的一致性控制

---

### 实战建议

- 全局字体大小和间距：推荐用 `rem`

- 局部内部样式（如按钮 padding、图标缩放）：可以用 `em`

- 若要适配不同屏幕，结合媒体查询动态修改 `html { font-size }`

---

## 代码举例

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>rem vs em 示例</title>
  <style>
    html {
      font-size: 16px; /* 默认 1rem = 16px */
    }

    body {
      padding: 20px;
      font-family: Arial, sans-serif;
    }

    h2 {
      margin-top: 40px;
    }

    .rem-box {
      font-size: 1.5rem; /* 1.5 × 16 = 24px */
      padding: 1rem;     /* 1 × 16 = 16px */
      background-color: #d0f0fd;
      margin-bottom: 20px;
    }

    .em-parent {
      font-size: 20px;
    }

    .em-box {
      font-size: 2em;    /* 2 × 20 = 40px */
      padding: 1em;      /* 1 × 40 = 40px */
      background-color: #ffe4b3;
      margin-bottom: 20px;
    }

    .nested {
      font-size: 1.5em;  /* 1.5 × 40 = 60px */
      background-color: #ffc0cb;
      padding: 0.5em;    /* 0.5 × 60 = 30px */
    }
  </style>
</head>
<body>

  <h1>rem vs em 示例</h1>

  <h2>1️⃣ 使用 rem</h2>
  <div class="rem-box">
    字体大小：1.5rem（= 24px）<br>
    Padding：1rem（= 16px）
  </div>

  <h2>2️⃣ 使用 em</h2>
  <div class="em-parent">
    父级字体大小：20px
    <div class="em-box">
      字体大小：2em（= 40px）<br>
      Padding：1em（= 40px）

      <div class="nested">
        嵌套字体大小：1.5em（= 60px）<br>
        Padding：0.5em（= 30px）
      </div>
    </div>
  </div>

</body>
</html>

```


