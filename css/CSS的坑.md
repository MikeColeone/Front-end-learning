## CSS的坑

1. 给一个div设置它父级div的宽度是100px，然后再设置它的padding-top为20%。  
   问现在的div有多高？如果父级元素定位是absolute呢？
   
   - 都不变，不管是padding-top，bottom，left，right都是相对父级计算，元素的高度可能由子元素撑开决定，如果子元素的padding and margin 基于父元素的高度来判断的话 会陷入死循环
   
   - 宽度计算
     
     - 收缩包裹型：`inline-block`、`float`、`position: absolute`
       
       - 计算潜在宽度
       
       - 实际子元素宽度
       
       - 最终父元素
     
     - 普通块级盒子
       
       - 依赖父元素100%

2. 


