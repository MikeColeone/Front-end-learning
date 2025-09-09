# 手写代码-js

## 防抖

```ts
function debounce(fun, delay) {
    let timer = null;
    return function (...args) {
        clearTimeout(timer);         // 清除上次定时器
        timer = setTimeout(() => {
            fun.apply(this, args);
        }, delay);
    }
}
```

## 节流

```ts
function throttle(fun,delay){
    let timer = null;
    return function(...args){
        if(timer) return;
        timer = setTimeout(()=>{
            fun.apply(this,args);  
            timer = null;  
            },delay)
    }
}
```

## 设计一个 errorWrapper 函数，要求针对函数 function A 进行包装，当函数 A 执行报错的时候，可以用公共的 errorHandle 函数处理错误，并且把这个错误继续向上抛出，否则正常返回A 的执行结果。

```ts
//这是一个包装函数 包装函数返回函数 而不是立即执行
function errorWrapper(fun: Function) {
    if (typeof fun !== 'function') {
        throw new Error("is not function");
    }
    return function(...args: any[]) {
        try {
            return fun.apply(this, args);
        } catch (e) {
            // 统一错误处理，比如调用公共 errorHandle
            errorHandle(e);
            throw e;
        }
    }
}
```

## const add = curry((a,b,c)=>a+b+c); // fn = summation function

>  const add = curry((a,b,c)=>a+b+c); // fn = summation function

> console.log(add(1,2, 3)) // output: 6

> console.log(add(1)(2,3)) // output: 6

> console.log(add(1)(3)(2)) // output: 6

> console.log(add(1,2)(3)) // output: 6

```ts
const curry = (fn)=>{
    // console.log(fn.length)
    const length = fn.length
    return function(...args){
        if(args.length>=length) {
            return fn.apply(this, args)
        }else{
            console.log(args)
            return curry(fn.bind(this, ...args))
        }
    }
}
```

### 深拷贝

```ts

```

## 请用requestAnimationFrame实现一个心跳函数

```ts

```

## 实现一个开平方根的函数，结果精确到小数点后4位

// count 表示迭代次数，值越大越精准

```ts
const sqrt = (n,count){
    let i = 1;
    let a = 0;
    let b = 0;
    if(i * i === n){
        return i;
    }
}
```

## 实现一个useThrottle React Hook，要求其可接受一个Function函数和最短触发事件阈值Span，使得该函数在每次调用后的Span时间区间内不再触发重新调用

```javascript
function Button() {

const onClick = (_e) => console.log('Clicked');

const throttleClick = useThrottle(onClick, 1e3);

return <div onClick={throttleClick}></div>;

}
```

要求：渲染场景中，1s内多次点击该div，仅触发一次log

```ts

```

## MyReturnType<T>，正如起名所述，处理的是 function type T 的返回值 type

请自行实现 MyReturnType<T>

type Foo = () => {a: string}

type A = MyReturnType<Foo> // {a: string}

```ts

```

## 基本: 实现定时器 hooks 封装

加分：

1、增加参数 maxCallNum：最多调用 N 次数后自动关闭定时器

2、返回 清除定时器方法

3、返回错误标识

```ts

```

## JS的promise可用于封装各类异步调用场景，但多数接口都有访问qps的限制，要求实现一个ES6 class (或ES5函数类），该类可以接受并行执行promise的上限参数M作为构造参数，通过提供一个enqueue方法来接收promise生成器，并确保线程执行中的promise数量始终不超过M，调用样例参考：

## 

## 链式调用封装

## 简单的订阅发布模型

## 通过 setTimeout 实现 setInterval 和 clearInterval 函数。

## 解析JSON

```ts
function json(text) {
  let at = 0;
  let ch = '';
  ch = next();
  function value() {
    switch (ch) {
      case '{':
        return object();
        break;
      case '[':
        return array();
        break;
      case '"':
        return string();
        break;
      case '-':
        return number();
        break;
      default:
        return ch >= '0' && ch <= '9' ? number() : error();
    }
  }
  function error() {
    return '出了什么bug';
  }
  function next() {
    ch = text.charAt(at);
    at += 1;
    while (ch && ch <= ' ') {
      // 跳过空格
      next();
    }
    return ch;
  }
  function string() {
    let string = '';
    // 跳过双引号
    next('"');
    while (ch) {
      if (ch === '"') {
        // 跳过双引号
        next();
        return string;
      } else {
        string += ch;
      }
      next();
    }
  }
  function object() {
    let key = '';
    let object = {};
    // 跳过{
    next('{');
    while (ch) {
      key = string();
      // 跳过冒号
      next(':');
      object[key] = value();
      if (ch === '}') {
        // 跳过}
        next('}');
        return object;
      }
      next();
    }
  }
  function array() {
    let array = [];
    // 跳过[
    next('[');
    while (ch) {
      array.push(value());
      if (ch === ']') {
        // 跳过]
        next(']');
        return array;
      }
      next();
    }
  }
  function number() {
    let number, string = '';
    if (ch === '-') {
      string = '-';
      // 跳过-
      next('-');
    }
    while (ch >= '0' && ch <= '9') {
      string += ch;
      next();
    }
    if (ch === '.') {
      string += '.';
      // 跳过., 获取小数后的部分
      while (next() && ch >= '0' && ch <= '9') {
        string += ch;
      }
    }
    number = +string;
    return number;
  }
  return value();
}
```

## 计算文本行数,计算给定宽度的 div 元素中，文本字符占多少行？

```ts

```

## 实现Promise.all

```ts

```

## 手写promise

```ts

```

## 手写Array的map方法

```ts
Array.prototype.myMap = function(callback, thisArg) {
  if (typeof callback !== 'function') {
    throw new TypeError(callback + ' is not a function');
  }

  const result = [];
  const arr = this;

  for (let i = 0; i < arr.length; i++) {
    // 只对存在的索引调用（跳过稀疏数组的空位）
    if (i in arr) {
      result[i] = callback.call(thisArg, arr[i], i, arr);
    }
  }

  return result;
};
```

## 手写n个2的n次方之和

## 通过 setTimeout 实现 setInterval 和 clearInterval 函数。

## JS实现一个带并发限制的异步调度器Scheduler，保证同时运行的任务最多有两个。完善代码中Scheduler类，使得以下程序能正确输出

class Scheduler {

add(promiseCreator) { ... }

// ...

}

const timeout = (time) => new Promise(resolve => {

setTimeout(resolve, time)

})

const scheduler = new Scheduler()

const addTask = (time, order) => {

scheduler.add(() => timeout(time))

.then(() => console.log(order))

}

addTask(1000, '1')

addTask(500, '2')

addTask(300, '3')

addTask(400, '4')

// output: 2 3 1 4// 一开始，1、2两个任务进入队列// 500ms时，2完成，输出2，任务3进队// 800ms时，3完成，输出3，任务4进队// 1000ms时，1完成，输出1// 1200ms时，4完成，输出

## 版本号排序

## 实现一个repeat方法，要求如下：需要实现的函数function repeat (func, times, wait) { // 补全}使下面调用代码能正常工作const repeatFunc = repeat(console.log, 4, 3000);repeatFunc("hello world"); //会输出4次 hello world, 每次间隔3秒

```ts

```

## 判断是不是空对象

```ts
function isEmpty(obj){
    for (let key in obj){
        return false;
    }
    return true;
}
```

## 模拟实现loadash中的_.get()函数，实现如下传入参数取值效果

## 不借助变量交换两个数

```ts
function swap(a,b){
  a=a^b;
  b=b^a;
  a=a^b;
    return [a,b]
}

funtion swap(a,b){
    return [a,b] = [b,a];
}

function swap(a,b){
    b = b - a;
    a = a + b;
    b = a - b;
    return [a,b];
}
```

## diff算法

## 实现一个JSONP

> 主要考察如何处理第二个参数 `callback` 的问题，
> 加分项比如超时处理 onerror 的处理, xss 考虑等等

## NodeJS实现简单的HTTP代理和隧道代理

## 二进制数相加

## 数组清空 尽量多的方法

## js实现多重继承

## 用 IIFE 模式实现一个模块（对象）

## 实现简单的MVC,例如HH:mm:ss的展示

## 统计标签数量,考虑iframe

```ts
function countTagsInDocument(doc) {
  const tagCount = {};

  function traverse(root) {
    const allElements = root.querySelectorAll('*');
    allElements.forEach(el => {
      const tag = el.tagName.toLowerCase();
      tagCount[tag] = (tagCount[tag] || 0) + 1;

      // 处理 shadow DOM
      if (el.shadowRoot) {
        traverse(el.shadowRoot);
      }

      // 处理 iframe
      if (el.tagName === 'IFRAME') {
        try {
          if (el.contentDocument) {
            traverse(el.contentDocument);
          }
        } catch (e) {
          // 跨域 iframe 无法访问
          console.warn('跨域 iframe 无法访问:', el.src);
        }
      }
    });
  }

  traverse(doc);
  return tagCount;
}

console.table(countTagsInDocument(document));
```

## memoize函数

## 观察者模式

## 数组拍平

## 列表元素反转

## 能够实现xxx时间之前函数后，让其稍加改造，如：输入日期与当前时间相差超过2天就显示该日期等

## 数组深度

## Js原子锁

## 实现JS限流调度器，方法add接收一个返回Promise的函数，同时执行的任务数量不能超过两个。

## 写一段JS程序提取URL中的各个GET参数

有这样一个URL：http://item.taobao.com/item.htm?a=1&b=2&c=&d=xxx&e，请写一段JS程序提取URL中的各个GET参数(参数名和参数个数不确定)，将其按key-value形式返回到一个json结构中，如{a:'1', b:'2', c:'', d:'xxx', e:undefined}

```ts
function serilizeUrl(url) {
  var result = {};
  url = url.split("?")[1];
  var map = url.split("&");
  for(var i = 0, len = map.length; i < len; i++) {
    result[map[i].split("=")[0]] = map[i].split("=")[1];
  }
  return result;
}
```

## 模拟js数组中的reduce和reduceRight方法
