package com.bank.mspasive.services;

import com.bank.mspasive.models.documents.Parameter;
import com.bank.mspasive.models.utils.ResponseParameter;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ParameterService {

    Mono<ResponseParameter> findByCode(Integer code);

    List<Parameter> getParameter(List<Parameter> listParameter, Integer code);
}
