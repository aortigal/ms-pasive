package com.bank.mspasive.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PasiveNameType {

    SAVING(1000),
    ACCOUNT(1001),
    FIXEDTERM(1002);

    private final int value;
}
