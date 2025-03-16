package com.ing.brokeragefirm.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiError {

	@JsonProperty("code")
	private Integer code;

	@JsonProperty("message")
	private String message;

}
