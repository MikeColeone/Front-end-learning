按照 content-type 分为常见的四种：

1. `application/x-www-form-urlencoded`, 即 a=b&c=d 的形式
2. `application/json`，即 json 字符串的形式
3. `multipart/form-data`，通常用于上传文件
4. `application/xml`，即 xml 的形式，比较少见
   以 express 为例，
   前两种通过 bodyParse 中间件处理，然后通过 req.body 获取请求体； 
   第三种，可以通过 multer 模块处理请求体，然后通过 req.files 获取文件 
   第四种，可以获取原始字符串，拼接后转化为 json （比如利用 xml2json）再进行处理
