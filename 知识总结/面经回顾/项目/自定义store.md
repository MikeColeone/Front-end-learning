```js
import type { User } from '@/types'

const textCoder = new TextEncoder()
const textDecoder = new TextDecoder()

const asc = (str: string) => str.replace(/20/g, '=').replace(/3/g, '/').replace(/6/g, 'x')
const dasc = (str: string) => str.replace(/=/g, '20').replace(/\//g, '3').replace(/x/g, '6')

const encode = (str: string) => {
  const deResult: string[] = []
  textCoder.encode(str).forEach((r) => {
    deResult.push(r.toString(17))
  })
  return asc(deResult.join(''))
}

const decode = (str: string) => {
  const dascStr = dasc(str)
  const deResult: number[] = []
  for (let i = 0; i < dascStr.length; i += 2) {
    const st0 = dasc(dascStr[i])
    const st1 = dasc(dascStr[i + 1])
    deResult.push(parseInt(`${st0}${st1}`, 17))
  }
  return textDecoder.decode(new Uint8Array(deResult))
}

const u = sessionStorage.getItem('user')
const userS = shallowRef<User>()
u && (userS.value = JSON.parse(decode(u)))

const setUserSessionStorage = (user: User, role: string) => {
  userS.value = user
  sessionStorage.setItem('role', role)
  sessionStorage.setItem('user', encode(JSON.stringify(user)))
}
const store = { userS, setUserSessionStorage }
export const useUserStore = () => {
  return store
}

```

很好，我们就以你这段代码为例，来**系统讲解一下 Vue 中如何实现一个“自定义 Store”（状态管理）**，不依赖 Pinia、Vuex，仅用 `ref` / `reactive` / `shallowRef` 等 Vue 核心 API 实现。

---

## 🧩 你这段代码：自定义 `User` 状态模块

```ts
const userS = shallowRef<User>()
```

- `userS` 是响应式用户状态（浅响应式，用于防止深层监听提高性能）

```ts
const setUserSessionStorage = (user: User, role: string) => {
  userS.value = user
  sessionStorage.setItem('role', role)
  sessionStorage.setItem('user', encode(JSON.stringify(user)))
}
```

- 你定义了操作函数（**行为**），用于修改状态并同步到 `sessionStorage`

```ts
const store = { userS, setUserSessionStorage }
export const useUserStore = () => store
```

- 把状态和方法都暴露出去，供外部组件访问

---

## ✅ 自定义 Store 的关键组成

| 组成部分             | 作用                                        | 你代码中体现                                    |
| ---------------- | ----------------------------------------- | ----------------------------------------- |
| **状态 (State)**   | 用 `ref`、`shallowRef` 或 `reactive` 定义响应式数据 | `const userS = shallowRef<User>()`        |
| **行为 (Actions)** | 封装状态修改函数                                  | `setUserSessionStorage()`                 |
| **持久化机制**        | 同步数据到 `sessionStorage` / `localStorage` 等 | 你 encode 之后写入 `sessionStorage`            |
| **封装函数**         | 使用 `useXXXStore()` 封装导出                   | `export const useUserStore = () => store` |

---

## 🛠️ 怎么从零实现一个自定义 store？

### 步骤 1：创建响应式状态

```ts
import { shallowRef } from 'vue'

const userS = shallowRef<User>()
```

- 使用 `shallowRef` / `ref` / `reactive`，根据需要选择

- `shallowRef` 更适合对象数据，不深度追踪子属性

---

### 步骤 2：添加行为函数（操作状态）

```ts
const setUserSessionStorage = (user: User, role: string) => {
  userS.value = user
  sessionStorage.setItem('role', role)
  sessionStorage.setItem('user', encode(JSON.stringify(user)))
}
```

- 这是你的“行为”函数，相当于 Vuex 中的 `mutations` 或 `actions`

---

### 步骤 3：从缓存中恢复状态（初始化）

```ts
const u = sessionStorage.getItem('user')
u && (userS.value = JSON.parse(decode(u)))
```

- 页面刷新后从缓存恢复状态，是常见的 store 初始化逻辑

---

### 步骤 4：统一导出（模块化）

```ts
const store = { userS, setUserSessionStorage }

export const useUserStore = () => store
```

- 用函数包装，使得每次导入都得到同一个响应式引用（共享状态）

---

## ✅ 优势（vs 不封装）

| 做法                  | 说明                           |
| ------------------- | ---------------------------- |
| 单独导出变量和函数           | 容易冲突、状态不集中                   |
| 封装成 `useXXXStore()` | 状态统一管理，函数和数据一体，易维护，便于组合式函数调用 |

---

## 🧠 延伸建议（进阶）

1. ✅ 添加 **`clearUserSession()`** 方法用于登出

2. ✅ 添加 **role 的响应式变量**

3. ⚠️ 考虑使用 `localStorage` 或 `IndexedDB` 替代 `sessionStorage`（看业务场景）

4. ✅ 可写个 `initUserStore()` 在 app 初始化时调用

---

如果你需要，我可以帮你把这个 `UserStore` 封装得更完整，或者用 Pinia 重写对比一下。需要吗？
