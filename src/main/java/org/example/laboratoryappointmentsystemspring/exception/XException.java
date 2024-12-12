package org.example.laboratoryappointmentsystemspring.exception;

import lombok.*;

//通用异常名 因为异常类是抽象的 所有类都可用

@EqualsAndHashCode(callSuper = false)
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
//继承自非受检异常
public class

XException extends RuntimeException {
    private Code code;
    private int number; //单独的业务码
    private String message;  //随之伴随的异常信息

    public XException(Code code) {
        this.code = code;
    }
}
