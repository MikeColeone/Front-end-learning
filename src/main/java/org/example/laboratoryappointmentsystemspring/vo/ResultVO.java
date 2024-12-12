package org.example.laboratoryappointmentsystemspring.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.graduatemanage.exception.Code;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.graduatemanage.exception.Code;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultVO {
    private int code;//状态码
    private String message;//返回信息
    private Object data;//返回数据
    private long timestamp;// 时间戳
    //只返回成功的状态 不携带数据
    private static final ResultVO EMPTY = ResultVO.builder()
            .code(200)
            .timestamp(System.currentTimeMillis())
            .build();
    //每次请求的不必都创建对象 所以构建一个ResultVO对象做一个缓存 不必每次都创建
    public static ResultVO ok(){
        return EMPTY;
    }
    public static ResultVO success(Object data){

        return ResultVO.builder()
                .code(200)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static ResultVO success(String account){
        return ResultVO.builder()
                .code(200)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    //当存在通用异常码的时候
    public static ResultVO error(Code code){
        return ResultVO.builder().code(code.getCode())
                .timestamp(System.currentTimeMillis())
                .message(code.getMessage()).
                build();
    }

    //不存在通用异常码的时候
    public static ResultVO error(int code,String message){
        return ResultVO.builder()
                .code(code)
                .message(message)
                .build();
    }
}

