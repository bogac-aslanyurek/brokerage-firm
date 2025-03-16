package com.ing.brokeragefirm.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiException extends RuntimeException {

    private Integer code;
    private String message;
    private String[] args;

    public ApiException(Integer code, String message, String... args) {
        super(message);
        this.code = code;
        this.message = message;
        this.args = args;
    }
}
