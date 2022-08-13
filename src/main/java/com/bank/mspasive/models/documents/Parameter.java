package com.bank.mspasive.models.documents;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data @NoArgsConstructor
public class Parameter {

    private String id;
    @NotNull(message = "code must not be null")
    private Integer code;

    private String value;

    private String name;

    private String argument;

}
