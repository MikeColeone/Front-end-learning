你这段代码是对 `axios` 的二次封装，主要目的是为了：

- 统一设置请求的基础地址（`baseURL`）。

- 统一处理请求头的 token 认证。

- 统一处理请求和响应的拦截逻辑（请求拦截器和响应拦截器）。

- 对响应数据做递归反序列化（把字符串类型的 JSON 结构转换为对象）。

- 对请求失败和响应错误做统一的错误处理。

- 提供了一套封装好的 HTTP 请求函数（`useGet`, `usePost` 等），方便调用并自动提取接口的 `data` 部分。

---

## 具体封装了什么：

### 1. **基础配置**

```ts
axios.defaults.baseURL = '/api/'
```

- 设置所有请求的基础地址，避免每次请求都写完整路径。

### 2. **请求拦截器**

```ts
axios.interceptors.request.use(
  (req) => {
    const auth = sessionStorage.getItem('token')
    if (auth && req.headers) {
      req.headers.token = auth
    }
    return req
  },
  (error) => {
    createMessageDialog(error.message)
    return Promise.reject()
  }
)
```

- 每个请求都会自动携带 `sessionStorage` 中存的 `token`，放在请求头 `token` 字段里。

- 请求出错时弹出错误提示。

### 3. **响应拦截器**

```ts
axios.interceptors.response.use(
  (resp) => {
    if (resp.config.responseType == 'blob') {
      return resp
    }

    const data: ResultVO<{}> = resp.data
    if (data.code < 300) {
      parseObject(resp.data)
      return resp
    }

    if (data.code >= 400) {
      return Promise.reject(data.message)
    }
    return resp
  },
  (error) => {
    return Promise.reject(error.message)
  }
)
```

- 对响应体进行判断：
  
  - 如果是文件流（`blob`）则直接返回。
  
  - 如果返回的 `code` 小于 300，认为请求成功，调用 `parseObject` 递归反序列化接口数据中的 JSON 字符串。
  
  - 如果 `code` >= 400，认为请求失败，返回失败信息。

- 统一处理 HTTP 请求异常，返回错误信息。

### 4. **递归反序列化函数 `parseObject`**

```ts
const parseObject = (data: any) => {
  let newValue = data

  for (const [key, value] of Object.entries(data)) {
    if (value instanceof Array) {
      value.forEach((d) => {
        parseObject(d)
      })
    }
    if (typeof value == 'object') {
      parseObject(value)
    }

    if (typeof value == 'string' && (value.includes('{"') || value.includes('['))) {
      try {
        newValue = JSON.parse(value)
        if (typeof newValue == 'object') {
          data[key] = parseObject(newValue)
        }
      } catch (error) {
        //
      }
    }
  }
  return newValue
}
```

- 递归地把接口返回中可能被多重序列化的 JSON 字符串转为对象。

### 5. **封装的请求函数**

```ts
export const useGet = async <T>(url: string) => {
  const resp = await axios.get<ResultVO<T>>(url)
  return resp.data.data
}

export const usePost = async <T>(url: string, data: unknown) => {
  const resp = await axios.post<ResultVO<T>>(url, data)
  return resp.data.data
}

export const usePut = async <T>(url: string) => {
  const resp = await axios.put<ResultVO<T>>(url)
  return resp.data.data
}

export const usePatch = async <T>(url: string, data: unknown) => {
  const resp = await axios.patch<ResultVO<T>>(url, data)
  return resp.data.data
}

export const useDelete = async <T>(url: string) => {
  const resp = await axios.delete<ResultVO<T>>(url)
  return resp.data.data
}
```

- 这些函数对 HTTP 方法做了封装，调用时直接得到后端接口响应中的 `data` 字段，简化了调用流程。

---

## 总结

这段代码是对 `axios` 的封装，主要封装了：

- **基础路径**（`baseURL`）

- **自动携带 Token 的请求头**

- **请求和响应的统一拦截处理**（带错误提示、统一错误抛出）

- **对接口返回数据中嵌套 JSON 字符串进行递归解析**

- **简化的请求调用接口**（`useGet`, `usePost` 等）





源码：

```js
import { createMessageDialog } from '@/components/message'
import type { ResultVO } from '@/types'
import axios from 'axios'

axios.defaults.baseURL = '/api/'

axios.interceptors.request.use(
  (req) => {
    const auth = sessionStorage.getItem('token')
    // 判断,用于避免header包含authorization属性但数据值为空
    if (auth && req.headers) {
      req.headers.token = auth
    }
    return req
  },
  (error) => {
    createMessageDialog(error.message)
    return Promise.reject()
  }
)

// 递归实现反序列化为JS对象
const parseObject = (data: any) => {
  let newValue = data

  for (const [key, value] of Object.entries(data)) {
    if (value instanceof Array) {
      value.forEach((d) => {
        parseObject(d)
      })
    }
    if (typeof value == 'object') {
      parseObject(value)
    }

    if (typeof value == 'string' && (value.includes('{"') || value.includes('['))) {
      try {
        newValue = JSON.parse(value)
        if (typeof newValue == 'object') {
          data[key] = parseObject(newValue)
        }
      } catch (error) {
        //
      }
    }
  }
  return newValue
}

axios.interceptors.response.use(
  (resp) => {
    if (resp.config.responseType == 'blob') {
      return resp
    }

    const data: ResultVO<{}> = resp.data
    if (data.code < 300) {
      parseObject(resp.data)
      return resp
    }

    if (data.code >= 400) {
      return Promise.reject(data.message)
    }
    return resp
  },
  // 全局处理异常信息。即，http状态码不是200
  (error) => {
    return Promise.reject(error.message)
  }
)

export const useGet = async <T>(url: string) => {
  const resp = await axios.get<ResultVO<T>>(url)
  return resp.data.data
}

export const usePost = async <T>(url: string, data: unknown) => {
  const resp = await axios.post<ResultVO<T>>(url, data)
  return resp.data.data
}

export const usePut = async <T>(url: string) => {
  const resp = await axios.put<ResultVO<T>>(url)
  return resp.data.data
}

export const usePatch = async <T>(url: string, data: unknown) => {
  const resp = await axios.patch<ResultVO<T>>(url, data)
  return resp.data.data
}

export const useDelete = async <T>(url: string) => {
  const resp = await axios.delete<ResultVO<T>>(url)
  return resp.data.data
}

export default axios

```
