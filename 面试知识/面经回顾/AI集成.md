

```java
package com.kcall.service.impl;

import cn.hutool.json.JSONObject;
import com.google.gson.Gson;
import com.kcall.dto.AIChatExecuterParamDto;
import com.kcall.entity.AiChatRecordEntity;
import com.kcall.entity.Result;
import com.kcall.entity.AIChat.ChatMessage;
import com.kcall.mapper.AiChatRecordMapper;
import com.kcall.service.IAiChatRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kcall.entity.AIChat.ApiRequest;
import com.kcall.utils.DingDingChatRobotUtil;
import com.kcall.utils.ResultUtil;
import com.kcall.websocket.NettyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AiChatRecordServiceImpl extends ServiceImpl<AiChatRecordMapper, AiChatRecordEntity> implements IAiChatRecordService {

    @Autowired
    DingDingChatRobotUtil dingChatRobotUtil;

    @Autowired
    private NettyHandler nettyHandler;

    @Autowired
    private IAiChatRecordService aiChatRecordService;

    @Override
    public Result saveChatRecord(AiChatRecordEntity chatRecord) {
        log.info("保存聊天记录", chatRecord);
        chatRecord.setCreateTime(new Date());
        boolean save = save(chatRecord);
        if (save) return ResultUtil.success();
        else return ResultUtil.failure();
    }

    @Override
    public Result selectAllChatRecord(String userUUid, Integer isPushed, Integer pageNum, Integer pageSize) {
        pageNum = Math.max(0, pageNum - 1) * pageSize;
        List<AiChatRecordEntity> allByIsPushedAndUserUuid = baseMapper.getAllByIsPushedAndUserUuid(isPushed, userUUid, pageNum, pageSize);
        return ResultUtil.success(allByIsPushedAndUserUuid);

    }

    @Override
    public Result selectAllChatRecord(List<String> userUUid, Integer isPushed, Integer pageNum, Integer pageSize) {
        pageNum = Math.max(0, pageNum - 1) * pageSize;
        List<AiChatRecordEntity> allByIsPushedAndUserUuid = baseMapper.getAllByIsPushedAndUserUuidList(isPushed, userUUid, pageNum, pageSize);
        return ResultUtil.success(allByIsPushedAndUserUuid);
    }


    /*调用智能机器人回答问题*/
    /*小嘿助手*/
    @Override
    public void chatToAI(AIChatExecuterParamDto paramDto) {

        String userId =paramDto.getUserId();
        String question = paramDto.getQuestion();

        // 构造 AI API 请求
        String response_mode = "streaming";
        String conversation_id = "";

        ApiRequest apiRequest = new ApiRequest(question, response_mode, conversation_id, userId); // 假设 AI API 接受 ApiRequest 对象

        // 调用 AI API，获取流式响应
        Flux<Object> apiResponse = dingChatRobotUtil.callAiApi(apiRequest);
        Gson gson = new Gson();

        // 处理 AI API 响应，并逐条发送给前端
        StringBuilder result = new StringBuilder();
        apiResponse.delayElements(Duration.ofMillis(50))
                .doOnComplete(() -> {
                    if (paramDto.getIsSaveRecord()){
                        AiChatRecordEntity aiChatRecordEntity = new AiChatRecordEntity(userId, question, result.toString(), paramDto.getIsPushed());
                        aiChatRecordService.saveChatRecord(aiChatRecordEntity);
                    }
                })
                .onErrorContinue((e,o)->{
                    System.err.println("Error occurred for item: " + o + ", error: " + e.getMessage());
                })
                .doFinally((Obj) -> {
                    JSONObject completeMsg = new JSONObject();
                    completeMsg.set("answer", result.toString());
                    completeMsg.set("status", "end");

                    nettyHandler.sendMsg(userId, completeMsg.toString());
                    log.info("Finally");

                })


                .subscribe(chunk -> {
                    String processedData = chunk.toString()
                            .replaceAll("(\\w+)=([^,}]*)", "\"$1\":\"$2\"") // 替换键和值，但注意这不会处理带有空格或特殊字符的值
                            .replaceAll(", *\"answer\":\" *\"", ", \"answer\":\"\""); // 特别处理answer字段，将其值替换为空字符串


                    log.info("chunk:" + processedData);


                    if (!processedData.contains("agent_thought")) {
                        System.out.println("转换前" + chunk.toString());
                        try {

                            ChatMessage chatMessage = gson.fromJson(processedData, ChatMessage.class);
                            //将数据结果流式推送给用户
                            log.info("推送数据：" + chatMessage.toString());
                                                      result.append(chatMessage.getAnswer());
                        } catch (Exception e) {
                            //给的Json有些问题，会出现answer为空格出现无法解析的问题，这里进行一个捕获，然后主动添加上空格
                            log.error("ai对话结果推送给用户发生错误"+e.getMessage());
                            result.append(" ");
                        }

                        JSONObject answer = new JSONObject();
                        answer.set("answer", result.toString());
                        answer.set("status", "continue");
                        nettyHandler.sendMsg(userId, answer.toString());
                    }
                });
    }


}



```



当然，我们来**详细剖析整个代码结构的模块设计**，尤其是重点部分 —— **AI 对话模块** 的设计原理和流程控制。我们将从全局架构、模块划分、核心逻辑（AI对话流式处理）三个层级逐步深入讲解。

---

## 🏛️ 一、整体架构概述

### 本类作用（`AiChatRecordServiceImpl`）：

这是一个 **服务层（Service Layer）实现类**，它位于三层架构的中间层：

```
Controller（接收请求） 
   ↓
Service（业务处理） ← 本类位置
   ↓
Mapper（数据库操作）
```

它主要协调以下四个任务：

1. 与数据库交互（记录保存、查询）

2. 调用外部 AI 接口（如“小嘿助手”）

3. 使用 WebSocket 将结果实时推送到前端

4. 错误处理与日志记录

---

## 🧱 二、模块划分说明

### 1. Entity 实体类（数据结构）

- `AiChatRecordEntity`：封装聊天记录，包含：
  
  - `userId`
  
  - `question`
  
  - `answer`
  
  - `createTime`
  
  - `isPushed` 等字段

- `ChatMessage`：封装从 AI 返回的数据结构（如 answer、agent_thought 等）

- `ApiRequest`：用于请求 AI 接口的数据结构，包括：
  
  - `prompt`（用户问题）
  
  - `response_mode`
  
  - `conversation_id`
  
  - `userId`

---

### 2. 工具类

#### `DingDingChatRobotUtil`

作用：封装 AI 对话接口调用

```java
Flux<Object> callAiApi(ApiRequest request)
```

- 返回的是一个 **Reactor 的 `Flux<Object>`**，表示“响应是以数据流形式返回的”，每次 `subscribe` 就像前端 SSE/WebSocket 接收响应片段。

- `response_mode = streaming` 表示 AI 会逐步响应，而不是一次性返回整个回答。

#### `ResultUtil`

作用：统一格式化接口返回结果。

#### `NettyHandler`

作用：封装 **WebSocket 消息推送**：

```java
nettyHandler.sendMsg(userId, jsonString);
```

- 用户ID是通道标识

- 内容是 JSON 格式字符串，前端接收到后更新 UI

---

## 🤖 三、AI对话核心逻辑详解（`chatToAI()`）

下面是最核心的模块，详细按流程讲解：

### Step 1：提取参数，构建请求

```java
String question = paramDto.getQuestion();
String userId = paramDto.getUserId();
ApiRequest apiRequest = new ApiRequest(question, "streaming", "", userId);
```

构建一个请求对象，传入 AI 服务（可能是 OpenAI、文心一言等包装后的小嘿助手 API）。

---

### Step 2：调用 AI 接口，获取流式响应

```java
Flux<Object> apiResponse = dingChatRobotUtil.callAiApi(apiRequest);
```

返回的是一个 `Flux<Object>`，表示“可订阅的数据流”。

---

### Step 3：处理响应（最复杂部分）

#### 3.1 创建 StringBuilder 收集回答

```java
StringBuilder result = new StringBuilder();
```

#### 3.2 启动流式消费（订阅）

```java
apiResponse
    .delayElements(Duration.ofMillis(50)) // 每条间隔发送
```

##### 中间流程：每条消息如何处理？

```java
.subscribe(chunk -> {
    String processedData = chunk.toString()
        .replaceAll("(\\w+)=([^,}]*)", "\"$1\":\"$2\"")
        .replaceAll(", *\"answer\":\" *\"", ", \"answer\":\"\"");

    ChatMessage chatMessage = gson.fromJson(processedData, ChatMessage.class);
    result.append(chatMessage.getAnswer());

    JSONObject answer = new JSONObject();
    answer.set("answer", result.toString());
    answer.set("status", "continue");

    nettyHandler.sendMsg(userId, answer.toString());
});
```

- 每次接收到数据片段：
  
  - 用正则把 chunk 转换为合法 JSON 字符串（容错处理）
  
  - 用 `Gson` 转为 `ChatMessage`
  
  - 把其中的 `answer` 部分加入总结果
  
  - 推送中间状态 `{ answer: xxx, status: "continue" }` 给前端，做到“边生成边显示”

---

#### 3.3 错误处理

```java
.onErrorContinue((e,o)->{
    System.err.println("Error occurred for item: " + o + ", error: " + e.getMessage());
})
```

- 某个响应数据解析失败时，不中断整体处理流，只报错并跳过。

---

#### 3.4 结束时处理

```java
.doOnComplete(() -> {
    if (paramDto.getIsSaveRecord()) {
        AiChatRecordEntity record = new AiChatRecordEntity(userId, question, result.toString(), paramDto.getIsPushed());
        aiChatRecordService.saveChatRecord(record);
    }
})
```

- 如果用户设置了“保存记录”，则保存完整对话到数据库。

---

#### 3.5 最终统一推送“结束标志”

```java
.doFinally(signal -> {
    JSONObject completeMsg = new JSONObject();
    completeMsg.set("answer", result.toString());
    completeMsg.set("status", "end");
    nettyHandler.sendMsg(userId, completeMsg.toString());
})
```

- 不管成功或失败，都会推送一次 `status=end`，前端用它来“关闭输入框/显示结束动画”。

---

## 📈 四、AI流式对话设计价值

| 设计点               | 好处               |
| ----------------- | ---------------- |
| 使用 `Flux<Object>` | 实现流式交互，边生成边推送    |
| WebSocket 推送      | 实时更新，减少轮询        |
| 字符串转 JSON 容错处理    | 保证不中断，即便某次响应格式出错 |
| 完成后写库             | 保证完整记录           |
| 异步非阻塞             | 高并发适配良好，性能优秀     |

---

## ✅ 总结一句话

这段代码实现了一个 **后端 AI 流式对话系统**，能够实时与 AI 通信、动态将结果推送给用户，并高效地管理对话记录存储，是现代智能客服、聊天机器人、AIGC系统的典型实现方式。



# 前端设计

## 1. 整体设计

#### **1.1 功能目标**

- 实现前端与后端 AI 服务的对接，支持用户输入问题并获取 AI 的回答。
- 支持实时通信（如流式回答）和聊天记录的管理。
- 提供良好的用户体验，包括状态显示、滚动控制和错误处理。

#### **1.2 设计架构**

1. **WebSocket 实现实时通信**：
   
   - 前端通过 WebSocket 与后端保持长连接，支持实时发送用户问题和接收 AI 的回答。
   - 支持流式回答（`status: "continue"`）和回答结束（`status: "end"`）。

2. **聊天记录管理**：
   
   - 聊天记录存储在 `aiChatRecordList` 中，支持动态更新和格式化显示。
   - 支持分页加载历史记录。

3. **用户交互**：
   
   - 输入框用于用户提问，支持按下回车键发送问题。
   - 显示 AI 的回答状态（如“正在思考中”）。
   - 滚动条自动滚动到最新消息。

4. **错误处理**：
   
   - 处理 WebSocket 连接失败、超时等异常情况。
   - 提示用户检查网络或重新连接。

---

### **2. 具体实现**

#### **2.1 WebSocket 连接**

通过 WebSocket 实现前后端的实时通信。

##### **初始化 WebSocket**



```js
initWs() {
  return new Promise((resolve, reject) => {
    console.log('--------------初始化连接--------------');
    const userId = this.$store.state.baseStore.userInfo.jti; // 获取用户 ID
    if (userId != '') {
      this.userId = userId;
      const wsUrl = 'ws://' + config.asr_ws + ':' + config.asr_ws_port;
      console.log("WSURL是:       ", `${wsUrl}/ws?id=${userId}`);
      this.webSocket = new WebSocket(`${wsUrl}/ws?id=${userId}`);
    } else {
      reject('请先登录');
    }
    this.webSocket.onmessage = this.getMessage; // 接收消息
    this.webSocket.onopen = () => {
      console.log('ws建立连接成功');
      this.ws_is_closed = false;
      resolve();
    };
    this.webSocket.onerror = () => {
      console.log('ws连接失败');
      this.ws_is_closed = true;
      reject();
    };
    this.webSocket.onclose = () => {
      this.ws_is_closed = true;
      this.closeWs();
      console.log('和ws服务器断开连接');
      reject();
    };
  });
}
```

##### **关闭 WebSocket**

```js
closeWs() {
  clearInterval(this.ping_inter);
  this.ws_is_closed = true;
  if (this.webSocket && this.webSocket.readyState === WebSocket.OPEN) {
    this.webSocket.close();
    console.log('关闭ws连接');
  }
}
```

##### **心跳检测**



```js
pingSend() {
  if (!this.ws_is_closed) {
    const data = {
      message: 'ping',
      type: 'ping'
    };
    this.ping_inter = setInterval(() => {
      this.webSocket.send(JSON.stringify(data));
    }, 5000);
  }
}
```

#### **2.2 发送消息**

用户输入问题后，通过 WebSocket 将消息发送到后端。



```js
sendMessageToWs() {
  if (this.webSocket && this.webSocket.readyState === WebSocket.OPEN) {
    const msg = {
      userUuid: this.userId,
      question: this.question,
      isSaveRecord: true // 是否保存聊天记录
    };
    this.webSocket.send(JSON.stringify(msg)); // 发送消息
    this.aiChatRecordList.push({
      id: 0,
      question: this.question,
      answer: "正在思考中..."
    }); // 添加用户问题到聊天记录
    this.querstionStatus = 1; // 设置状态为“AI 正在思考”
    this.inputDisabled = false; // 禁用输入框
    this.scrollToText(); // 滚动到聊天底部
    this.question = ''; // 清空输入框
  } else {
    console.log('ws未连接');
  }
}
```

#### **2.3 接收消息**

通过 `getMessage` 方法处理后端返回的消息。



```js
getMessage(msg) {
  if (this.timer != null) {
    clearTimeout(this.timer);
  }
  var data = JSON.parse(msg.data);
  let flag = 0;
  // 为 continue 时，表示流式回答还在继续
  if (data.status == "continue") {
    data.answer != "" ? flag = 1 : "";
    if (flag == 1) {
      this.aiChatRecordList[this.aiChatRecordList.length - 1].answer = data.answer;
      console.log("聊天记录" + this.aiChatRecordList);
      this.$nextTick(() => {
        this.$refs.myScrollbar.setScrollTop(this.$refs.chatContainer.scrollHeight); // 滚动到页面底部
      });
    }
  } else if (data.status == "end") {
    // 为 end 时，表示流式回答已经结束
    this.querstionStatus = 0;
    this.inputDisabled = true;
  }
}
```





#### **2.4 聊天记录管理**

聊天记录存储在 `aiChatRecordList` 中，并通过 `formattedMessages` 计算属性格式化显示。





```js
computed: {
  formattedMessages() {
    var array = this.checkShowRule(this.aiChatRecordList, 'createTime');
    console.log(array);
    return this.checkShowRule(this.aiChatRecordList, 'createTime');
  }
}
```



#### **2.5 滚动到最新消息**

在发送或接收消息后，滚动到最新消息的位置。



```js
scrollToText() {
  this.$nextTick(() => {
    this.$refs.myScrollbar.setScrollTop(this.$refs.chatContainer.scrollHeight);
  });
}
```



### **3. API 设计**

#### **3.1 WebSocket 消息格式**

- **发送消息**：

```js
{
  "userUuid": "用户ID",
  "question": "用户输入的问题",
  "isSaveRecord": true
}
```

- **接收消息**：



```js
{
  "status": "continue", // 或 "end"
  "answer": "AI 的回答"
}
```

#### **3.2 聊天记录接口**

- **获取聊天记录**：
  - **URL**：`/kcall/ai-chat-record/listPageUserUUidList`
  - **请求参数**：

```js
{
  "userUuids": ["用户ID"],
  "isPushed": 0,
  "pageNum": 1,
  "pageSize": 10
}
```

- **响应数据**：

```js
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "question": "用户问题",
      "answer": "AI 回答",
      "createTime": "2025-05-23T10:00:00"
    }
  ]
}
```

### **4. 易踩坑的点**

#### **4.1 WebSocket 连接问题**

- **问题**：WebSocket 可能因网络问题断开连接。
- **解决**：
  - 实现自动重连机制。
  - 使用心跳检测保持连接活跃。

#### **4.2 流式回答处理**

- **问题**：流式回答可能出现数据丢失或顺序错误。
- **解决**：
  - 确保 `status: "continue"` 的回答被正确拼接到最后一条记录中。
  - 在 `status: "end"` 时停止拼接。

#### **4.3 聊天记录分页加载**

- **问题**：分页加载可能导致重复数据或漏数据。
- **解决**：
  - 确保分页参数（`pageNum` 和 `pageSize`）正确递增。
  - 去重处理，避免重复加载相同的记录。

#### **4.4 超时处理**

- **问题**：AI 服务可能因网络或后端问题超时。
- **解决**：
  - 设置超时时间（如 60 秒），超时后提示用户检查网络。

---

### **5. 总结**

- **整体设计**：通过 WebSocket 实现实时通信，结合聊天记录管理和用户交互，提供完整的 AI 问答功能。
- **具体实现**：包括 WebSocket 的初始化、消息发送与接收、聊天记录管理和滚动控制。
- **易踩坑的点**：重点关注 WebSocket 连接问题、流式回答处理、分页加载和超时处理。

这种设计能够高效对接后端 AI 服务，同时保证用户体验流畅。
