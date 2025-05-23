# VUE3的响应式原理和VUE2的区别

## vue3

- vue3是采用proxy和Reflect实现的

### Proxy代理模式

- proxy是代理模式，用于修改某些操作的默认行为，相当于于在编程语言上做修改属于元编程，可简单理解为在目标对象之前设置一层拦截，外界对该对象的访问都必须通过这层拦截，进而实现对外界的访问进行过滤和改写。

### 语法

- Proxy构造函数： var proxy = new Proxy(target,handler);
  
  - target：需要拦截的目标对象（不能是原始值）
  
  - handler：定制拦截行为


