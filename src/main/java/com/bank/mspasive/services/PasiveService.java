package com.bank.mspasive.services;

import com.bank.mspasive.handler.ResponseHandler;
import com.bank.mspasive.models.documents.Pasive;
import com.bank.mspasive.models.utils.Amount;
import reactor.core.publisher.Mono;

public interface PasiveService {
    Mono<ResponseHandler> create(Pasive p);

    Mono<ResponseHandler> setAmountData(String id, Amount m);

    Mono<ResponseHandler> findAll();

    Mono<ResponseHandler> find(String id);

    Mono<ResponseHandler> getAmountData(String id);

    Mono<ResponseHandler> update(String id, Pasive p);

    Mono<ResponseHandler> delete(String id);

    Mono<ResponseHandler> findByCode(String id);
}
