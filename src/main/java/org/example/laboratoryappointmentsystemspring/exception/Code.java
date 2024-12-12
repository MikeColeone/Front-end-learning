package org.example.laboratoryappointmentsystemspring.exception;


import lombok.*;

@Getter
@RequiredArgsConstructor
public enum Code {
    USER_NOT_FOUND(100,"未查询到用户"),
    LOGIN_ERROR(Code.ERROR, "用户名密码错误"),
    BAD_REQUEST(Code.ERROR, "请求错误"),
    UNAUTHORIZED(401, "未登录"),
    TOKEN_EXPIRED(403, "过期请重新登录"),
    FORBIDDEN(403, "无权限");
    public static final int ERROR = 400;  //通用业务码
    private final int code;
    private final String message; //异常信息
}
